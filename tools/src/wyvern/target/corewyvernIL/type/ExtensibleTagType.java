package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class ExtensibleTagType extends TagType {

    public ExtensibleTagType(CaseType caseType) {
        super(caseType);
    }

    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor,
                                  S state) {
        return emitILVisitor.visit(state, this);
    }
}
