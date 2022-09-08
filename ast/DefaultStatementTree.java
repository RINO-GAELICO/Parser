package ast;

import visitor.*;

public class DefaultStatementTree extends AST {

    public DefaultStatementTree() {
    }

    public Object accept(ASTVisitor v) {
        return v.visitDefaultStatementTree(this);
    }

}