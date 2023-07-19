package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class TryExp extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
//    private final DeclSequence exp;
//    private final DeclSequence handler;
    private final VarDeclaration exp;
    private final VarDeclaration handler;

    @Override
    public FileLocation getLocation() {
        return this.location;
    }

//    public TryExp(TypedAST exp, TypedAST handler, FileLocation location) {
//        this.exp = (DeclSequence) exp;
//        this.handler = (DeclSequence) handler;
//        this.location = location;
//    }

    public TryExp(TypedAST exp, TypedAST handler, FileLocation location) {
        this.exp = (VarDeclaration) exp;
        this.handler = (VarDeclaration) handler;
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
