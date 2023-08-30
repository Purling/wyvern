package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.BreakException;
import wyvern.tools.errors.HasLocation;

public interface IASTNode extends HasLocation {
    <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) throws BreakException;
}
