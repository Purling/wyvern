package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Break extends AbstractExpressionAST implements CoreAST {
    private FileLocation location = FileLocation.UNKNOWN;
    private ExpressionAST returnExpr;

    public Break(TypedAST returnExpr, FileLocation location) {
        this.returnExpr = (ExpressionAST) returnExpr;
        this.location = location;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        IExpr ret = returnExpr.generateIL(ctx, expectedType, dependencies);
        return new wyvern.target.corewyvernIL.expression.Break(ret);
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
