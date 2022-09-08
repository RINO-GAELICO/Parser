package visitor;

import ast.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.*;

public class DrawOffsetVisitor extends ASTVisitor {

  private final int NODE_WIDTH = 100;
  private final int NODE_HEIGHT = 30;
  private final int VERTICAL_GAP = 40;
  private final int HORIZONTAL_GAP = 10;
  private final int SCALE_FACTOR = 2;
  private final int PADDING = 50;

  private int widthWindow;
  private int heightWindow;

  private HashMap<Integer, ArrayList<Integer>> hashMapOffset;
  private int depth = 0;
  private int maxDepth;
  private int maxOffset;
  private BufferedImage bimg;
  private Graphics2D g2;

  public DrawOffsetVisitor(HashMap<Integer, ArrayList<Integer>> hashMap) {

    hashMapOffset = new HashMap<>(hashMap);

    maxDepth = hashMapOffset.entrySet()
        .stream()
        .max(Comparator.comparingInt((entry) -> entry.getValue().get(1)))
        .get()
        .getValue()
        .get(1);

    maxOffset = hashMapOffset.entrySet()
        .stream()
        .max(Comparator.comparingInt((entry) -> entry.getValue().get(0)))
        .get()
        .getValue()
        .get(0);

    widthWindow = (((maxOffset / 2)+1) * (NODE_WIDTH + HORIZONTAL_GAP))+ NODE_WIDTH;
    heightWindow = (maxDepth+1) * (NODE_HEIGHT + VERTICAL_GAP)+ PADDING;

    g2 = createGraphics2D(widthWindow, heightWindow);
  }

  public void draw(String symbol, AST treeNode) {

    int horizontalStep = NODE_WIDTH + HORIZONTAL_GAP;
    int verticalStep = NODE_HEIGHT + VERTICAL_GAP;

    
    int x = (hashMapOffset.get(treeNode.getNodeNum()).get(0) * horizontalStep/SCALE_FACTOR);
    int y = depth * verticalStep;

    g2.setColor(Color.black);
    g2.drawOval(x, y, NODE_WIDTH, NODE_HEIGHT);
    g2.setColor(Color.BLACK);
    g2.drawString(symbol, x + 10, y + 2 * NODE_HEIGHT / 3);

    int startx = x + NODE_WIDTH / 2;
    int starty = y + NODE_HEIGHT;
    int endx;
    int endy;
    g2.setColor(Color.black);

    if(!treeNode.getKids().isEmpty()){
      for (int i = 0; i < treeNode.kidCount(); i++) {
        endx = ((hashMapOffset.get(treeNode.getKid(i+1).getNodeNum()).get(0) * horizontalStep)/SCALE_FACTOR) + PADDING;
        endy = (depth + 1) * verticalStep;
        g2.drawLine(startx, starty, endx, endy);
      }

    }
    
    depth++;
    visitKids(treeNode);
    depth--;
    return;
    

    
  }

  private Graphics2D createGraphics2D(int width, int height) {
    Graphics2D g2;

    if (bimg == null || bimg.getWidth() != width || bimg.getHeight() != height) {
      bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    g2 = bimg.createGraphics();
    g2.setBackground(Color.WHITE);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2.clearRect(0, 0, width, height);
    return g2;
  }

  public BufferedImage getImage() {
    return bimg;
  }

  public Object visitProgramTree(AST t) {
    draw("Program", t);
    return null;
  }

  public Object visitBlockTree(AST t) {
    draw("Block", t);
    return null;
  }

  public Object visitFunctionDeclTree(AST t) {
    draw("FunctionDecl", t);
    return null;
  }

  public Object visitCallTree(AST t) {
    draw("Call", t);
    return null;
  }

  public Object visitDeclTree(AST t) {
    draw("Decl", t);
    return null;
  }

  public Object visitIntTypeTree(AST t) {
    draw("IntType", t);
    return null;
  }

  public Object visitBoolTypeTree(AST t) {
    draw("BoolType", t);
    return null;
  }

  public Object visitFloatTypeTree(AST t) {
    draw("FloatType", t);
    return null;
  }

  public Object visitVoidTypeTree(AST t) {
    draw("VoidType", t);
    return null;
  }

  public Object visitFormalsTree(AST t) {
    draw("Formals", t);
    return null;
  }

  public Object visitActualArgsTree(AST t) {
    draw("ActualArgs", t);
    return null;
  }

  public Object visitIfTree(AST t) {
    draw("If", t);
    return null;
  }

  public Object visitUnlessTree(AST t) {
    draw("Unless", t);
    return null;
  }

  public Object visitWhileTree(AST t) {
    draw("While", t);
    return null;
  }

  public Object visitForTree(AST t) {
    draw("For", t);
    return null;
  }

  public Object visitReturnTree(AST t) {
    draw("Return", t);
    return null;
  }

  public Object visitAssignTree(AST t) {
    draw("Assign", t);
    return null;
  }

  public Object visitIntTree(AST t) {
    draw("Int: " + ((IntTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitUtfStringLitTree(AST t) {
    draw("Utf16String: " + ((UtfStringLitTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitTimeStampLitTree(AST t) {
    draw("TimeStamp: " + ((TimeStampLitTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitIdTree(AST t) {
    draw("Id: " + ((IdTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitRelOpTree(AST t) {
    draw("RelOp: " + ((RelOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitAddOpTree(AST t) {
    draw("AddOp: " + ((AddOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitMultOpTree(AST t) {
    draw("MultOp: " + ((MultOpTree) t).getSymbol().toString(), t);
    return null;
  }

  public Object visitStringTypeTree(AST t) {
    draw("StringType", t);
    return null;
  }

  public Object visitCharTypeTree(AST t) {
    draw("CharType", t);
    return null;
  }

  public Object visitSwitchTree(AST t) {
    draw("Switch", t);
    return null;
  }

  public Object visitSwitchBlockTree(AST t) {
    draw("SwitchBlock", t);
    return null;
  }

  public Object visitCaseBlockTree(AST t) {
    draw("CaseBlock", t);
    return null;
  }

  public Object visitCaseListTree(AST t) {
    draw("CaseList", t);
    return null;
  }

  public Object visitCaseStatementTree(AST t) {
    draw("CaseStatement", t);
    return null;
  }

  public Object visitDefaultStatementTree(AST t) {
    draw("DefaultStatement", t);
    return null;
  }

  public Object visitUstringTypeTree(AST t) {
    draw("UstringType", t);
    return null;
  }

  public Object visitTimeStampTypeTree(AST t) {
    draw("TimeStampType", t);
    return null;
  }

}