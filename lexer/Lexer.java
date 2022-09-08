package lexer;

import java.io.IOException;


/**
 * The Lexer class is responsible for scanning the source file
 * which is a stream of characters and returning a stream of
 * tokens; each token object will contain the string (or access
 * to the string) that describes the token along with an
 * indication of its location in the source program to be used
 * for error reporting; we are tracking line numbers; white spaces
 * are space, tab, newlines
 */
public class Lexer {
  private boolean atEOF = false;
  // next character to process
  private char ch;
  private SourceReader source;
  private String fullString, subString, lastLine;
  private int startPosition, endPosition, lineFound, oldLineRead;

  private final int MAX_YEARS = 9999;
  private final int MINIMUM_ZERO = 0;
  private final int MINIMUM_ONE = 1;
  private final int MAX_MONTHS = 12;
  private final int MAX_DAYS = 31;
  private final int MAX_HOURS = 23;
  private final int MAX_MINUTES = 59;
  private final int MAX_SECONDS = 59;
  private final int LENGTH_STANDARD = 2;

  /**
   * Lexer constructor
   * 
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer(String sourceFile) throws Exception {
    // init token table
    new TokenType();
    source = new SourceReader(sourceFile);
    ch = source.read();
    lastLine = source.getNextLine();
  }

  /**
   * newIdTokens are either ids or reserved words; new id's will be inserted
   * in the symbol table with an indication that they are id's
   * 
   * @param id            is the String just scanned - it's either an id or
   *                      reserved word
   * @param startPosition is the column in the source file where the token begins
   * @param endPosition   is the column in the source file where the token ends
   * @return the Token; either an id or one for the reserved words
   */
  public Token newIdToken(String id, int startPosition, int endPosition) {
    return new Token(
        startPosition,
        endPosition,
        lineFound,
        Symbol.symbol(id, Tokens.Identifier));
  }

  /**
   * number tokens are inserted in the symbol table; we don't convert the
   * numeric strings to numbers until we load the bytecodes for interpreting;
   * this ensures that any machine numeric dependencies are deferred
   * until we actually run the program; i.e. the numeric constraints of the
   * hardware used to compile the source program are not used
   * 
   * @param number        is the int String just scanned
   * @param startPosition is the column in the source file where the int begins
   * @param endPosition   is the column in the source file where the int ends
   * @return the int Token
   */
  public Token newNumberToken(String number, int startPosition, int endPosition) {
    return new Token(
        startPosition,
        endPosition,
        lineFound,
        Symbol.symbol(number, Tokens.INTeger));
  }

  public Token newTimeStampToken(int startPosition, int endPosition) {
    return new Token(
        startPosition,
        endPosition,
        lineFound,
        Symbol.symbol(fullString, Tokens.TimeStampLit));
  }

  public Token newULiteralToken(int startPosition, int endPosition) {
    return new Token(
        startPosition,
        endPosition,
        lineFound,
        Symbol.symbol(fullString, Tokens.Utf16StringLit));
  }

  /**
   * build the token for operators (+ -) or separators (parens, braces)
   * filter out comments which begin with two slashes
   * 
   * @param s             is the String representing the token
   * @param startPosition is the column in the source file where the token begins
   * @param endPosition   is the column in the source file where the token ends
   * @return the Token just found
   */
  public Token makeToken(String s, int startPosition, int endPosition) {
    // filter comments
    if (s.equals("//")) {
      try {
        int oldLine = source.getLineno();

        do {
          ch = source.read();
        } while (oldLine == source.getLineno());
      } catch (Exception e) {
        atEOF = true;
      }

      return nextToken();
    }

    // ensure it's a valid token
    Symbol sym = Symbol.symbol(s, Tokens.BogusToken);

    this.lineFound = this.source.getLineno();
    if (this.lineFound > oldLineRead) {
      lastLine = this.source.getNextLine();
      oldLineRead = this.lineFound;
    }

    if (sym == null) {
      System.out.println("******** illegal character: " + s);
      atEOF = true;
      return nextToken();
    }

    return new Token(startPosition, endPosition, lineFound, sym);
  }

  /**
   * @return the next Token found in the source file
   */
  public Token nextToken() {

    // ch is always the next char to process
    if (atEOF) {

      if (source != null) {
        source.close();
        source = null;
      }

      return null;
    }

    try {
      // scan past whitespace
      while (Character.isWhitespace(ch)) {
        ch = source.read();
      }
    } catch (Exception e) {
      atEOF = true;
      return nextToken();
    }

    lineFound = source.getLineno();
    if (lineFound > oldLineRead) {
      lastLine = source.getNextLine();
      oldLineRead = lineFound;
    }

    startPosition = this.source.getPosition();
    endPosition = startPosition - 1;

    if (Character.isJavaIdentifierStart(ch)) {
      // return tokens for ids and reserved words
      String id = "";

      try {
        do {
          endPosition++;
          id += ch;
          ch = source.read();
        } while (Character.isJavaIdentifierPart(ch));
      } catch (Exception e) {
        atEOF = true;
      }

      return newIdToken(id, startPosition, endPosition);
    }

    if (Character.isDigit(ch)) {
      // return number tokens
      String number = "";
      try {
        do {
          endPosition++;
          number += ch;
          ch = source.read();
        } while (Character.isDigit(ch));
        // copy number into fullString
        fullString = new String(number);
        if (checkDelimiter('~')
            && endPosition - startPosition == 3
            && Integer.parseInt(fullString) <= MAX_YEARS
            && checkMoreThanZero()) {
          advanceOnePosition();
          // check the substring of months
          if (!checkDigits()) {
            return illegalCharacter();
          }
          if (!checkLimits(MAX_MONTHS, MINIMUM_ONE)) {
            return illegalSubString();
          }
          if (!checkDelimiter('~')) {
            return illegalCharacter();
          }
          advanceOnePosition();
          // check substring of days
          if (!checkDigits()) {
            return illegalCharacter();
          }
          if (!checkLimits(MAX_DAYS, MINIMUM_ONE)) {
            return illegalSubString();
          }
          if (!checkDelimiter('~')) {
            return illegalCharacter();
          }
          advanceOnePosition();
          // check subbstring of hours
          if (!checkDigits()) {
            return illegalCharacter();
          }
          if (!checkLimits(MAX_HOURS, MINIMUM_ZERO)) {
            return illegalSubString();
          }
          if (!checkDelimiter(':')) {
            return illegalCharacter();
          }
          advanceOnePosition();
          // check substring of minutes
          if (!checkDigits()) {
            return illegalCharacter();
          }
          if (!checkLimits(MAX_MINUTES, MINIMUM_ONE)) {
            return illegalSubString();
          }
          if (!checkDelimiter(':')) {
            return illegalCharacter();
          }
          advanceOnePosition();
          // check substring of seconds
          if (!checkDigits()) {
            return illegalCharacter();
          }
          if (!checkLimits(MAX_SECONDS, MINIMUM_ZERO)) {
            return illegalSubString();
          }
          if (!checkDelimiter(' ')) {
            return illegalCharacter();
          }
          return newTimeStampToken(startPosition, endPosition);

        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
        atEOF = true;
      }
      return newNumberToken(number, startPosition, endPosition);
    }
    
    //could be only utf16String
    if (ch == '\\') {

      fullString = "";
      fullString += ch;
      endPosition++;
      try {
        if (!checkULiterals()) {
          return illegalCharacter();
        }
        endPosition++;
        ch = source.read();
        fullString += ch;
        if (ch != '\\') {
          return illegalCharacter();
        }
        if (!checkULiterals()) {
          return illegalCharacter();
        }
        ch = source.read();
        if(!Character.isWhitespace(ch)){
          return illegalCharacter();
        }
        return newULiteralToken(startPosition, endPosition);

      } catch (Exception e) {
        System.out.println(e.getMessage());
        atEOF = true;
      }

    }

    // At this point the only tokens to check for are one or two
    // characters; we must also check for comments that begin with
    // 2 slashes
    String charOld = "" + ch;
    String op = charOld;
    Symbol sym;
    try {
      endPosition++;
      ch = source.read();
      op += ch;

      // check if valid 2 char operator; if it's not in the symbol
      // table then don't insert it since we really have a one char
      // token
      sym = Symbol.symbol(op, Tokens.BogusToken);
      if (sym == null) {
        // it must be a one char token
        return makeToken(charOld, startPosition, endPosition);
      }

      endPosition++;
      ch = source.read();

      return makeToken(op, startPosition, endPosition);
    } catch (Exception e) {
      /* no-op */ }

    atEOF = true;
    if (startPosition == endPosition) {
      op = charOld;
    }

    return

    makeToken(op, startPosition, endPosition);
  }

  private Token illegalCharacter() {
    lineFound = this.source.getLineno();
    if (this.lineFound > oldLineRead) {
      lastLine = this.source.getNextLine();
      oldLineRead = lineFound;
    }
    System.out.println("******** illegal character: " + ch);
    atEOF = true;
    return nextToken();
  }

  private Token illegalSubString() {
    lineFound = this.source.getLineno();
    if (this.lineFound > oldLineRead) {
      lastLine = this.source.getNextLine();
      oldLineRead = lineFound;
    }
    System.out.println("******** illegal substring: " + subString.substring(subString.length() - LENGTH_STANDARD));
    atEOF = true;
    return nextToken();
  }

  public String getLastLine() {
    String lastLineCopy = new String(lastLine);
    return lastLineCopy;
  }

  public int getOldLineRead() {
    int oldLineReadCopy = oldLineRead;
    return oldLineReadCopy;
  }

  public int getLineFound() {
    int lineFoundCopy = lineFound;
    return lineFoundCopy;
  }

  private boolean advanceOnePosition() throws IOException {
    subString = "";
    endPosition++;
    fullString += ch;
    ch = source.read();
    return true;
  }

  private boolean checkDigits() throws IOException {
    for (int i = 0; i < LENGTH_STANDARD; i++) {
      subString += ch;
      fullString += ch;
      if (!Character.isDigit(ch)) {
        return false;
      }
      ch = source.read();
      endPosition++;
    }
    return true;
  }

  private boolean checkLimits(int max, int min) throws IOException {

    return (Integer.parseInt(subString) >= min
        && Integer.parseInt(subString) <= max);
  }

  private boolean checkULiterals() throws IOException {
    endPosition++;
    ch = source.read();
    fullString += ch;
    if (ch == 'u') {
      return checkHexadecimal();
    } else {
      return false;
    }

  }

  private boolean checkHexadecimal() throws IOException {

    for (int i = 0; i < 4; i++) {
      endPosition++;
      ch = source.read();
      fullString += ch;
      if (Character.digit(ch, 16) == -1) {
        return false;
      }
    }
    return true;
  }

  private boolean checkDelimiter(char delimiter) {
    return ch == delimiter;
  }

  private boolean checkMoreThanZero() {
    return Integer.parseInt(fullString) >= MINIMUM_ZERO;
  }


}