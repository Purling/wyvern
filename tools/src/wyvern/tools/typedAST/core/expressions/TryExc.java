package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class TryExc extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
    @Override
    public FileLocation getLocation() {
        return this.location;
    }

    public TryExc(FileLocation location) {
        this.location = location;
    }


    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return null;
    }
}
