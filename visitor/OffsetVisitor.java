package visitor;
import java.util.*;

import ast.AST;


public class OffsetVisitor extends ASTVisitor {

    private int nextAvailableOffset[] = new int[100];
    private int depth = 0;
    private int maxDepth = depth;
    private int maxOffset = 0;
    private HashMap<Integer,ArrayList<Integer>> hashMapOffset = new HashMap<>();

    private void offset(AST treeNode) {
        if(treeNode.getKids().isEmpty()){
            ArrayList<Integer> valuesXandY = new ArrayList<>();
            valuesXandY.add(0, nextAvailableOffset[depth]);
            valuesXandY.add(1, depth);
            hashMapOffset.put(treeNode.getNodeNum(),valuesXandY);
            nextAvailableOffset[depth] = nextAvailableOffset[depth]+2;
            if (depth > maxDepth) {
                maxDepth = depth;
              }
            return; 
        }
        if (depth > maxDepth) {
            maxDepth = depth;
          }
        depth++;
        visitKids(treeNode);
        depth--;
        if(!treeNode.getKids().isEmpty()){
            int offset = ( hashMapOffset.get(treeNode.getKid(1).getNodeNum()).get(0) );
            offset = offset + hashMapOffset.get(treeNode.getKid(treeNode.getKids().size()).getNodeNum()).get(0);
            offset = offset/2;
            if(offset > nextAvailableOffset[depth]){
                ArrayList<Integer> valuesXandY = new ArrayList<>();
                valuesXandY.add(0, offset);
                valuesXandY.add(1, depth);
                hashMapOffset.put(treeNode.getNodeNum(), valuesXandY);
                if (offset > maxOffset) {
                    maxOffset = offset;
                  }
                nextAvailableOffset[depth] = offset + 2; 
            }else{
                ArrayList<Integer> valuesXandY = new ArrayList<>();
                valuesXandY.add(0, nextAvailableOffset[depth]);
                valuesXandY.add(1, depth);
                hashMapOffset.put(treeNode.getNodeNum(), valuesXandY);
                if (nextAvailableOffset[depth] > maxOffset) {
                    maxOffset = nextAvailableOffset[depth];
                  }
                int shift =  nextAvailableOffset[depth] - offset;
                nextAvailableOffset[depth] = nextAvailableOffset[depth] + 2;
                adjustChildrenOffset(treeNode, shift);
            }
        }
        
        
    }

    private void adjustChildrenOffset(AST treeNode, int shift){
        ArrayList<AST> children = treeNode.getKids();
        for(AST node : children){
            
            adjustChildrenOffset(node, shift);
            int newValue = shift + hashMapOffset.get(node.getNodeNum()).get(0) ;
            hashMapOffset.get(node.getNodeNum()).set(0, newValue);
            if (newValue > maxOffset) {
                maxOffset = newValue;
            }
            int shiftDepth = hashMapOffset.get(node.getNodeNum()).get(1);
            nextAvailableOffset[shiftDepth] = newValue + 2;
        }
        return;
    };

    public void printHashMap(){
        hashMapOffset.entrySet()
    			 .stream()
    			 .forEach(e-> System.out.println(e));
    }

    public HashMap<Integer,ArrayList<Integer>> getHashMap(){
        HashMap<Integer,ArrayList<Integer>> tmpHashMap = new HashMap<>(hashMapOffset);
        return tmpHashMap;
    }

    public int getMaxDepth(){
       
        int tmpMaxDepth = maxDepth;
        return tmpMaxDepth;

    }

    public int getMaxOffset(){

        int tmpMaxOffset = maxOffset;
        return tmpMaxOffset;
    }

    @Override
    public Object visitProgramTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitBlockTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitFunctionDeclTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitCallTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitDeclTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitIntTypeTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitBoolTypeTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitFormalsTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitActualArgsTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitIfTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitWhileTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitSwitchBlockTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitCaseBlockTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitCaseStatementTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitCaseListTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitDefaultStatementTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitSwitchTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitReturnTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitAssignTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitIntTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitUtfStringLitTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitTimeStampLitTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitIdTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitRelOpTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitAddOpTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitMultOpTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitUstringTypeTree(AST t) {
        offset(t);
        return null;
    }

    @Override
    public Object visitTimeStampTypeTree(AST t) {
        offset(t);
        return null;
    }
    
}
