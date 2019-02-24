package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.EffectDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.metadata.IsTailCall;
import wyvern.target.corewyvernIL.metadata.Metadata;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.support.ViewExtension;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;

public class MethodCall extends Expression {

    private IExpr objectExpr;
    private String methodName;
    private List<? extends IExpr> args;
    private ValueType receiverType;
    private boolean isTailCall;

    public MethodCall(IExpr receiverExpr, String methodName,
                      List<? extends IExpr> args2, HasLocation location) {
        this(receiverExpr, methodName, args2, location, false);
    }

    public MethodCall(IExpr receiverExpr, String methodName,
            List<? extends IExpr> args2, HasLocation location, boolean isTailCall) {
        super(location != null ? location.getLocation() : null);
        //        if (getLocation() == null || getLocation().line == -1)
        //            throw new RuntimeException("missing location");
        this.objectExpr = receiverExpr;
        this.methodName = methodName;
        this.args = args2;
        this.isTailCall = isTailCall;
        // sanity check
        if (args2.size() > 0 && args2.get(0) == null) {
            throw new NullPointerException("invariant: no null args");
        }
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        objectExpr.doPrettyPrint(dest, indent);
        dest.append('.').append(methodName).append('(');
        boolean first = true;
        for (IExpr arg : args) {
            if (first) {
                first = false;
            } else {
                dest.append(", ");
            }
            arg.doPrettyPrint(dest, indent);
        }
        dest.append(')');
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        // Should match primitives in Globals.java
        String[] primitives = {"Int", "Float", "String", "Character", "Boolean"};
        if (receiverType instanceof NominalType && Arrays.asList(primitives).contains(((NominalType) receiverType).getTypeMember())) {
            BytecodeOuterClass.Expression.StaticCallExpression.Builder pce = BytecodeOuterClass.Expression.StaticCallExpression.newBuilder()
                    .setMethod(methodName)
                    .setReceiverType(((NominalType) receiverType).getTypeMember())
                    .setReceiver(((Expression) objectExpr).emitBytecode());

            for (IExpr expr : args) {
                Expression e = (Expression) expr;
                pce.addArguments(e.emitBytecode());
            }
            return BytecodeOuterClass.Expression.newBuilder().setStaticCallExpression(pce).build();
        } else {
            BytecodeOuterClass.Expression.CallExpression.Builder ce = BytecodeOuterClass.Expression.CallExpression.newBuilder()
                    .setMethod(methodName)
                    .setIsTailCall(isTailCall)
                    .setReceiver(((Expression) objectExpr).emitBytecode());

            for (IExpr expr : args) {
                Expression e = (Expression) expr;
                ce.addArguments(e.emitBytecode());
            }
            return BytecodeOuterClass.Expression.newBuilder().setCallExpression(ce).build();

        }
    }

    public IExpr getObjectExpr() {
        return objectExpr;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<? extends IExpr> getArgs() {
        return args;
    }

    private ValueType getReceiverType(TypeContext ctx) {
        if (receiverType == null) {
            receiverType = objectExpr.typeCheck(ctx, null);
        }
        return receiverType;
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        // If calling on a dynamic receiver, it types to Dyn (provided the args typecheck)
        if (Util.isDynamicType(getReceiverType(ctx))) {
            for (IExpr arg : args) {
                arg.typeCheck(ctx, effectAccumulator);
            }
            return Util.dynType();
        }
        typeMethodDeclaration(ctx, effectAccumulator);
        return getType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Invokable receiver = (Invokable) objectExpr.interpret(ctx);
        List<Value> argValues = new ArrayList<Value>(args.size());
        for (int i = 0; i < args.size(); ++i) {
            IExpr e = args.get(i);
            argValues.add(e.interpret(ctx));
        }
        if (isTailCall()) {
            return new SuspendedTailCall(this.getType(), this.getLocation()) {

                @Override
                public Value interpret(EvalContext ignored) {
                    return receiver.invoke(methodName, argValues, getLocation());
                }

                @Override
                public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
                    // TODO Auto-generated method stub
                    return null;
                }

            };
        }
        return trampoline(receiver.invoke(methodName, argValues, getLocation()));
    }

    public static Value trampoline(Value v) {
        while (v instanceof SuspendedTailCall) {
            v = v.interpret(null);
        }
        return v;
    }

    public boolean isTailCall() {
        for (Metadata m : getMetadata()) {
            if (m instanceof IsTailCall) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getFreeVariables() {
        Set<String> freeVars = objectExpr.getFreeVariables();
        for (IExpr arg : args) {
            freeVars.addAll(arg.getFreeVariables());
        }
        return freeVars;
    }

    public static List<ValueType> getArgTypes(TypeContext ctx, List<? extends IExpr> args) {
        return args.stream()
                .map(arg -> arg.typeCheck(ctx, null))
                .collect(Collectors.toList());
    }

    public static class MatchResult {
        //CHECKSTYLE:OFF
        public final boolean succeeded;
        public final List<ValueType> formalArgTypes;
        public final TypeContext newCtx;
        public final TypeContext calleeCtx;
        public final String failureReason;
        public final View view;
        //CHECKSTYLE:ON
        public MatchResult(List<ValueType> fat, TypeContext newCtx, TypeContext calleeCtx, String fr, View v, boolean succeeded) {
            formalArgTypes = fat;
            this.newCtx = newCtx;
            this.calleeCtx = calleeCtx;
            this.failureReason = fr;
            view = v;
            this.succeeded = succeeded;
        }
        
    }
    
    public static MatchResult matches(TypeContext newCtx, ValueType receiver, DeclType declType, List<? extends IExpr> args, IExpr objectExpr) {
        List<ValueType> actualArgTypes = getArgTypes(newCtx, args);
        StructuralType receiverType = receiver.getStructuralType(newCtx);
        List<ValueType> formalArgTypes = new LinkedList<ValueType>();
        String failureReason = null;

        // Ignore non-methods.
        TypeContext calleeCtx = newCtx.extend(receiverType.getSelfSite(), receiver);
        if (!(declType instanceof DefDeclType)) {
            return new MatchResult(formalArgTypes, newCtx, calleeCtx, failureReason, null, false);
        }
        DefDeclType defDeclType = (DefDeclType) declType;

        // Check it has correct number of arguments.
        List<FormalArg> formalArgs = defDeclType.getFormalArgs();
        if (args.size() != formalArgs.size()) {
            return new MatchResult(formalArgTypes, newCtx, calleeCtx, failureReason, null, false);
        }

        // Typecheck actual args against formal args of this declaration.
        boolean argsTypechecked = true;
        View v = View.from(objectExpr, newCtx);
        for (int i = 0; i < args.size(); ++i) {
            // Get info about the formal arguments.
            FormalArg formalArg = formalArgs.get(i);
            ValueType formalArgType = formalArg.getType();
            if (objectExpr.isPath()) {
                formalArgType = formalArgType.adapt(v);
            } else {
                // adaptation for the receiver won't work, so try avoiding "this"
                try {
                    formalArgType = formalArgType.avoid(receiverType.getSelfName(), calleeCtx);
                } catch (RuntimeException e) {
                    if (!e.getMessage().contains("not found")) {
                        throw e;
                    }
                    // else: avoiding didn't succeed, but just try to continue for now
                }
            }
            formalArgTypes.add(formalArgType);
            ValueType actualArgType = actualArgTypes.get(i);

            // Check actual argument type accords with formal argument type.
            FailureReason r = new FailureReason();
            try {
                if (!actualArgType.isSubtypeOf(formalArgType, newCtx, r)) {
                    argsTypechecked = false;
                    if (failureReason == null) {
                        failureReason = r.getReason();
                    }
                    break;
                }
            } catch (RuntimeException e) {
                if (e.getMessage().contains("not found")) {
                    argsTypechecked = false;
                } else {
                    throw e;
                }
            }

            // Update context and view.
            newCtx = newCtx.extend(formalArg.getSite(), actualArgType);
            calleeCtx = calleeCtx.extend(formalArg.getSite(), actualArgType);
            IExpr e = args.get(i);
            if (e instanceof Variable) {
                v = new ViewExtension(new Variable(defDeclType.getFormalArgs().get(i).getSite()), (Variable) e, v);
            }
        }
        
        return new MatchResult(formalArgTypes, newCtx, calleeCtx, failureReason, v, argsTypechecked);
    }
    
    /**
     * Type the declaration for the method being invoked.
     * @param ctx: ctx in which invocation happens.
     * @return the declaration of the method.
     */
    public DefDeclType typeMethodDeclaration(TypeContext ctx, EffectAccumulator effectAccumulator) {
        // Typecheck receiver.
        ValueType receiver = getReceiverType(ctx);
        StructuralType receiverType = receiver.getStructuralType(ctx);

        // Sanity check: make sure it has declarations.
        List<DeclType> declarationTypes = receiverType.findDecls(methodName, ctx);
        if (declarationTypes.isEmpty()) {
            ToolError.reportError(ErrorMessage.NO_SUCH_METHOD, this, methodName, receiver.desugar(ctx));
        }

        // Go through all declarations, typechecking against the actual types passed in...
        List<ValueType> actualArgTypes = getArgTypes(ctx, args);
        List<ValueType> formalArgTypes = null;

        // ...use this context to do that.
        TypeContext newCtx = null;
        TypeContext calleeCtx = null;
        String failureReason = null;
        for (DeclType declType : declarationTypes) {
            MatchResult mr = matches(ctx, receiver, declType, args, objectExpr);
            
            newCtx = mr.newCtx;
            calleeCtx = mr.calleeCtx;
            formalArgTypes = mr.formalArgTypes;
            if (mr.failureReason != null) {
                failureReason = mr.failureReason;
            }
            if (!mr.succeeded) {
                continue;
            }
            DefDeclType defDeclType = (DefDeclType) declType;
            List<FormalArg> formalArgs = defDeclType.getFormalArgs();
            View v = mr.view;
            
            // We were able to typecheck; figure out the return type, and set the method declaration.
            if (mr.succeeded) {

                // accumulate effects from method calls
                if (effectAccumulator != null) {
                    // get the effects through the current view
                    EffectSet methodCallE = defDeclType.getEffectSet(v);

                    if ((methodCallE != null) && (methodCallE.getEffects() != null)) {
                        Set<Effect> concreteEffects = new HashSet<>();
                        for (Effect e : methodCallE.getEffects()) {
                            // If the effect is dependent on the arguments, set its path correctly
                            boolean dependent = false;
                            for (int i = 0; i < args.size(); i++) {
                                final FormalArg formalArg = formalArgs.get(i);

                                if (e.getPath() == null || !formalArg.getName().equals(e.getPath().toString())) {
                                    continue;
                                }
                                dependent = true;

                                final IExpr arg = args.get(i);

                                if (arg instanceof New) {
                                    // Polymorphic dependent effect
                                    for (Declaration d : ((New) arg).getDecls()) {
                                        if (d instanceof EffectDeclaration && d.getName().equals(e.getName())) {
                                            concreteEffects.addAll(((EffectDeclaration) d).getEffectSet().getEffects());
                                        }
                                    }
                                }
                            }
                            // Otherwise, just add it
                            if (!dependent) {
                                setPathIfNecessary(e);
                                concreteEffects.add(e);
                            }
                        }
                        effectAccumulator.addEffects(concreteEffects);
                    }
                }

                ctx = newCtx;
                ValueType resultType = defDeclType.getResultType(v);
                if (!objectExpr.isPath()) {
                    // adaptation for the receiver couldn't have worked, so try avoiding "this"
                    resultType = resultType.avoid(receiverType.getSelfName(), calleeCtx);
                }
                for (int i = args.size() - 1; i >= 0; --i) {
                    resultType = resultType.avoid(formalArgs.get(i).getName(), calleeCtx);
                }
                setExprType(resultType);
                return defDeclType;
            }
        }
        // Couldn't find an appropriate method declaration. Build up a nice error message.
        String errorMessage = methodDeclarationNotFoundMsg(ctx, declarationTypes, actualArgTypes, formalArgTypes, failureReason);
        ToolError.reportError(ErrorMessage.NO_METHOD_WITH_THESE_ARG_TYPES, this, errorMessage);
        return null;
    }

    private void setPathIfNecessary(Effect e) {
        if (e.getPath() == null) {
            // TODO: should not set path to objectExpr
            if (objectExpr instanceof Variable) {
                e.setPath((Variable) objectExpr);
            }
        }
    }

    private String methodDeclarationNotFoundMsg(
            TypeContext ctx, List<DeclType> declarationTypes, List<ValueType> actualArgTypes, List<ValueType> formalArgTypes, String failureReason
    ) {
        StringBuilder errMsg = new StringBuilder();
        //errMsg.append(methodName);
        //errMsg.append("(");
        for (int i = 0; i <= args.size() - 2; ++i) {
            // add the argument only if it's not a generic
            if (!((DefDeclType) declarationTypes.get(0)).getFormalArgs().get(i).getName().startsWith(DefDeclaration.GENERIC_PREFIX)) {
                errMsg.append(actualArgTypes.get(i).desugar(ctx));
                errMsg.append(", ");
            }
        }
        if (args.size() > 0) {
            errMsg.append(actualArgTypes.get(args.size() - 1).desugar(ctx));
        }
        errMsg.append("; expected types ");
        DefDeclType ddt = (DefDeclType) declarationTypes.get(0);
        for (int i = 0; i <= formalArgTypes.size() - 2; ++i) {
            // add the argument only if it's not a generic
            if (!ddt.getFormalArgs().get(i).getName().startsWith(DefDeclaration.GENERIC_PREFIX)) {
                errMsg.append(formalArgTypes.get(i).desugar(ctx));
                errMsg.append(", ");
            }
        }
        if (formalArgTypes.size() > 0) {
            errMsg.append(formalArgTypes.get(formalArgTypes.size() - 1).desugar(ctx));
        }
        //errMsg.append(")");
        if (failureReason != null) {
            errMsg.append("; argument subtyping failed because " + failureReason);
        }
        return errMsg.toString();
    }
}
