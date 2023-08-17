package wyvern.target.corewyvernIL.expression;

import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class Break extends Expression {
    private final IExpr ret;
    public Break(IExpr ret) {
        this.ret = ret;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return ret.typeCheck(ctx, effectAccumulator); // Add the file to the context
    }

    @Override
    public Value interpret(EvalContext ctx) {
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
        return null;
    }
}
