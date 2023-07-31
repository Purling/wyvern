package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.expression.Expression;
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
    private final ExpressionAST handler;
    private final Type type;
    private final String tryObj;
    private final String with;
    private BindingSite site;

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
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        // The names have to match
        if (!tryObj.equals(with)) {
            ToolError.reportError(ErrorMessage.IDENTIFIER_NOT_SAME, handler, tryObj, with);
        }

        // Create the binding site
        if (site == null) {
            site = new BindingSite(with);
        }

        // Creating new object
        IExpr obj = handler.generateIL(ctx, null, dependencies);

        // Adding the object to the context
        GenContext thisContext = ctx.extend(site, obj.typeCheck(ctx, null));

        // List to add all of the expression to
        List<wyvern.target.corewyvernIL.expression.IExpr> exprs = new LinkedList<>();

        // Type check each of the expressions which
        for (ExpressionAST expression : expressions) {
            IExpr expr = expression.generateIL(thisContext, null, dependencies);
            ValueType expectedExprType = expr.typeCheck(thisContext, null);
            exprs.add(expr);
        }

        return new wyvern.target.corewyvernIL.Try();
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
