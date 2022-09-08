package ast;

import visitor.*;

public class UstringTypeTree extends AST {

    public UstringTypeTree() {
    }

    public Object accept(ASTVisitor v) {
        return v.visitUstringTypeTree(this);
    }

}
