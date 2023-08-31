package wyvern.target.corewyvernIL.expression;

import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.BreakException;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class Try extends Expression {

    private final BindingSite site;
    private final IExpr objectExpr;
    private final ValueType type;
    List<? extends IExpr> expressions;
    private final IExpr returnExpr;
    private final ValueType returnType;

    public Try(BindingSite site, IExpr objectExpr, List<? extends IExpr> expressions, ValueType type, IExpr returnExpr,
               ValueType returnType) {
        this.site = site;
        this.objectExpr = objectExpr;
        this.expressions = expressions;
        this.type = type;
        this.returnExpr = returnExpr;
        this.returnType = returnType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {

        ctx = ctx.extend(site, type);

        // Type check the object being defined in the handler
        if (!type.equalsInContext(objectExpr.typeCheck(ctx, effectAccumulator), ctx, new FailureReason())) {
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr, type.desugar(ctx),
                    objectExpr.typeCheck(ctx, effectAccumulator).desugar(ctx));
        }

        // Check if the return type matches
        if (returnExpr != null &&
                !returnExpr.typeCheck(ctx, effectAccumulator).equalsInContext(returnType, ctx, new FailureReason())) {
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr,
                    returnExpr.typeCheck(ctx, effectAccumulator).desugar(ctx), returnType.desugar(ctx));
        } else if (!expressions.get(expressions.size() - 1).typeCheck(ctx, effectAccumulator).
                               equalsInContext(returnType, ctx, new FailureReason())) {
            ToolError.reportError(ErrorMessage.TRY_TYPE_MISMATCH, objectExpr,
                    expressions.get(expressions.size() - 1).typeCheck(ctx, effectAccumulator).
                               desugar(ctx), returnExpr.typeCheck(ctx, effectAccumulator).desugar(ctx));
        }

        return objectExpr.typeCheck(ctx, effectAccumulator);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        try {
            ctx = ctx.extend(site, type.getMetadata(ctx));

            for (IExpr i : expressions) {
                if (!i.equals(expressions.get(expressions.size() - 1))) {
                    i.interpret(ctx);
                }
            }

            if (returnExpr != null) {
                throw new BreakException(BreakException.GetNextID(), returnExpr.interpret(ctx));
            } else {
                return expressions.get(expressions.size() - 1).interpret(ctx);
            }
        } catch (
                BreakException e) {
            if (e.ID != 1) {
                return (Value) e.retVal;
            } else {
//                throw e;
//            }
            }
        }
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
        return null;
    }
}
