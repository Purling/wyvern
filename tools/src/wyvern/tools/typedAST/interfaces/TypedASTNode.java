package wyvern.tools.typedAST.interfaces;

import wyvern.target.corewyvernIL.support.BreakException;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public interface TypedASTNode {
    <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) throws BreakException;
}
