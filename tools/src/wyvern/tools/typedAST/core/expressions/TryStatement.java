package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

public class TryStatement extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
    private final List<ExpressionAST> expressions;
    private final TypedAST handlerObject;
    private final TypedAST objectFields;
    private final Type type;
    private final String tryObj;
    private final String with;

    @Override
    public FileLocation getLocation() {
        return this.location;
    }

    public TryStatement(Type type, List<ExpressionAST> expressions, TypedAST handlerObject, TypedAST objectFields,
                        String tryObj, String with, FileLocation location) {
        this.type = type;
        this.expressions = expressions;
        this.handlerObject = handlerObject;
        this.objectFields = objectFields;
        this.tryObj = tryObj;
        this.with = with;
        this.location = location;
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        // The names have to match
        if (!tryObj.equals(with)) {
            ToolError.reportError(ErrorMessage.IDENTIFIER_NOT_SAME, handlerObject, tryObj, with);
        }

        // Type check the newly created object first


        // Type check each of the expressions which
        for (ExpressionAST expression : expressions) {
            IExpr expr = expression.generateIL(ctx, null, dependencies);
            ValueType expectedExprType = expr.typeCheck(ctx, null);
        }
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
