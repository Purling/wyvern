package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.util.GetterAndSetterGeneration;

public class Try extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
    private final List<ExpressionAST> expressions;
    private final ExpressionAST handler;
    private final Type returnType;
    private final Type type;
    private final String with;
    private final ExpressionAST breakExpr;
    private BindingSite site;

    @Override
    public FileLocation getLocation() {
        return this.location;
    }

    public Try(Type returnType, Type type, List<ExpressionAST> expressions, TypedAST handler,
               String with, TypedAST breakExpr, FileLocation location) {
        this.returnType = returnType;
        this.type = type;
        this.expressions = expressions;
        this.handler = (ExpressionAST) handler;
        this.with = with;
        this.breakExpr = (ExpressionAST) breakExpr;
        this.location = location;
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        // Create the binding site
        if (site == null) {
            site = new BindingSite(with);
        }

        // Creating new object
        IExpr obj = handler.generateIL(ctx, null, dependencies);
        ValueType objectType = obj.typeCheck(ctx, null);

        // Adding the object and variable to the context
        GenContext thisContext = ctx.extend(site, objectType);

        GenContext newContext = thisContext.extend(with, new Variable(site), objectType);

        // List to add all of the expression to
        List<wyvern.target.corewyvernIL.expression.IExpr> exprs = new LinkedList<>();

        // Generate IL for each of the expressions
        for (ExpressionAST expression : expressions) {
            IExpr expr = expression.generateIL(newContext, null, dependencies);
            exprs.add(expr);
        }

        IExpr returnExpr = null;

        // Generate the IL for the break return expression if one exists
        if (breakExpr != null) {
            returnExpr = breakExpr.generateIL(newContext, null, dependencies);
        }

        return new wyvern.target.corewyvernIL.expression.Try(site, obj, exprs, type.getILType(newContext), returnExpr,
                returnType.getILType(newContext));
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
