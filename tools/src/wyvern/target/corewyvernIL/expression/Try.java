package wyvern.target.corewyvernIL.expression;

import java.util.List;
import java.util.Set;

import org.junit.internal.runners.statements.Fail;
import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class Try extends Expression {

    private final String tryIdentifier;
    private final String withIdentifier;
    private final IExpr objectExpr;
    private final ValueType tryType;
    private final ValueType withType;
    List<? extends IExpr> expressions;
    private final IExpr returnExpr;
    private final ValueType returnType;

    public Try(IExpr objectExpr, List<? extends IExpr> expressions,
               String tryIdentifier, String withIdentifier, ValueType tryType, ValueType withType, IExpr returnExpr,
               ValueType returnType) {
        this.objectExpr = objectExpr;
        this.expressions = expressions;
        this.tryIdentifier = tryIdentifier;
        this.withIdentifier = withIdentifier;
        this.tryType = tryType;
        this.withType = withType;
        this.returnExpr = returnExpr;
        this.returnType = returnType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        // The names have to match or else throw error
        if (!tryIdentifier.equals(withIdentifier)) {
            ToolError.reportError(ErrorMessage.IDENTIFIER_NOT_SAME, objectExpr, tryIdentifier, withIdentifier);
        }

        // Make sure the type of the handler object being created and the return type of the try block are the same
        if (!tryType.equalsInContext(withType, ctx, new FailureReason())) {
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr, tryType.desugar(ctx), withType.desugar(ctx));
        }

        // Type check the object being defined in the handler
        if (!tryType.equalsInContext(objectExpr.typeCheck(ctx, effectAccumulator), ctx, new FailureReason())) {
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr, tryType.desugar(ctx), withType.desugar(ctx));
        }

        // Check if the return type matches
        if (returnExpr != null &&
                !returnExpr.typeCheck(ctx, effectAccumulator).equalsInContext(returnType, ctx, new FailureReason())) {
            // TODO make a new and more accurate error message & make sure it actually works
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr, tryType.desugar(ctx), withType.desugar(ctx));
        } else if (!expressions.get(expressions.size() - 1).typeCheck(ctx, effectAccumulator).
                               equalsInContext(returnType, ctx, new FailureReason())) {
            // TODO make a new and more accurate error message & make sure it actually works
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr, tryType.desugar(ctx), withType.desugar(ctx));
        }

        return objectExpr.typeCheck(ctx, effectAccumulator);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
        return null;
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        return super.emitBytecode();
    }
}
