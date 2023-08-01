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
import wyvern.tools.errors.FileLocation;

public class Try extends Expression {

    private BindingSite selfSite;
    private IExpr objectExpr;
    List<? extends IExpr> expressions;

    public Try(ValueType exprType, FileLocation loc, BindingSite selfSite, IExpr objectExpr, List<? extends IExpr> expressions) {
        super(exprType, loc);
        this.selfSite = selfSite;
        this.objectExpr = objectExpr;
        this.expressions = expressions;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
//        System.out.println("acceptVisitor");
        return visitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
//        System.out.println("typeCheck");
        return objectExpr.typeCheck(ctx, effectAccumulator);
    }

    @Override
    public Value interpret(EvalContext ctx) {
//        System.out.println("interpret");
        return null;
    }

    @Override
    public Set<String> getFreeVariables() {
//        System.out.println("getFreeVariables");
        return null;
    }
}
