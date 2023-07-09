package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class WyvernException extends AbstractExpressionAST implements CoreAST {
    private DeclSequence exp;
    private DeclSequence handler;
    private FileLocation location;

    public WyvernException(TypedAST exp, TypedAST handler, FileLocation location) {
        this.exp = (DeclSequence) exp;
        this.handler = (DeclSequence) handler;
        this.location = location;
    }
    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return null;
    }

    @Override
    public StringBuilder prettyPrint() {
        return super.prettyPrint();
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public FileLocation getLocation() {
        return this.location;
    }
}
