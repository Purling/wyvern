/* Generated By:JJTree: Do not edit this line. ASTArchDesc.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package wyvern.tools.parsing.coreparser.arch;

public class ASTArchDesc extends SimpleNode {
    public ASTArchDesc(int id) {
        super(id);
    }

    public ASTArchDesc(ArchParser p, int id) {
        super(p, id);
    }


    /**
     * Accept the visitor.
     **/
    public Object jjtAccept(ArchParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=13ccb152384636ceed6c7f0c848f18f6 (do not edit this line) */
