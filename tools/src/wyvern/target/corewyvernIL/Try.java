package wyvern.target.corewyvernIL;

import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class Try extends Expression {

    private String tryIdentifier;
    private String withIdentifier;
    private IExpr objectExpr;
    List<? extends IExpr> expressions;

    public Try(IExpr objectExpr, List<? extends IExpr> expressions,
               String tryIdentifier, String withIdentifier) {
        this.objectExpr = objectExpr;
        this.expressions = expressions;
        this.tryIdentifier = tryIdentifier;
        this.withIdentifier = withIdentifier;
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

        return objectExpr.typeCheck(ctx, effectAccumulator);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
//        System.out.println("getFreeVariables");
        return null;
    }
}
