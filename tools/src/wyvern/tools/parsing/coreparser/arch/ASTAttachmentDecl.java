/* Generated By:JJTree: Do not edit this line. ASTAttachmentDecl.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package wyvern.tools.parsing.coreparser.arch;

import java.util.HashSet;

public class ASTAttachmentDecl extends SimpleNode {
    private HashSet<String> ports = new HashSet<>();
    private String connector;

    public ASTAttachmentDecl(int id) {
        super(id);
    }

    public ASTAttachmentDecl(ArchParser p, int id) {
        super(p, id);
    }

    public void setConnector(String c) {
        connector = c;
    }

    public String getConnector() {
        return connector;
    }

    public void addPort(String p) {
        ports.add(p);
    }

    public HashSet<String> getAllPorts() {
        return ports;
    }

    public String toString() {
        String s = "connect ";
        for (String p : ports) {
            s += p;
            s += " and ";
        }
        s += "with ";
        s += connector;
        return s;
    }

    /**
     * Accept the visitor.
     **/
    public Object jjtAccept(ArchParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
/*
 * JavaCC - OriginalChecksum=f62a9444b7b5a75e21fe82e98f697281 (do not edit this
 * line)
 */
