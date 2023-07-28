package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

public class TryStatement extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
    private final List<ExpressionAST> expressions;
    private final ExpressionAST handler;
    private final Type type;
    private final String tryObj;
    private final String with;

    @Override
    public FileLocation getLocation() {
        return this.location;
    }

    public TryStatement(Type type, List<ExpressionAST> expressions, TypedAST handler,
                        String tryObj, String with, FileLocation location) {
        this.type = type;
        this.expressions = expressions;
        this.handler = (ExpressionAST) handler;
        this.tryObj = tryObj;
        this.with = with;
        this.location = location;
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        // The names have to match
        if (!tryObj.equals(with)) {
            ToolError.reportError(ErrorMessage.IDENTIFIER_NOT_SAME, handler, tryObj, with);
        }

        // Adding new object to the context
        IExpr obj = handler.generateIL(ctx, null, dependencies);
        obj.typeCheck(ctx, null);

        BindingSite site = new BindingSite(with);
//        GenContext thisContext = ctx.extend(
//                site,
//                new Variable(site),
//                type
//        );

//        System.out.println("1");

        // Type check each of the expressions which
        for (ExpressionAST expression : expressions) {
            IExpr expr = expression.generateIL(ctx, null, dependencies);
            System.out.println("After");
            ValueType expectedExprType = expr.typeCheck(ctx, null);
        }

//        System.out.println("2");

        return null;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
