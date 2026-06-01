package semantic.ast;

import assembler.DataType;
import lexer.Token;
import lexer.constants.TiposTokens;
import parser.SemanticListener;
import parser.grammar.Production;
import semantic.operations.*;

import java.util.*;

public class ASTBuilder implements SemanticListener {

    // ---- Root ----
    private final ProgramNode program = new ProgramNode();
    private boolean parseOk = false;

    // ---- State stack ----
    private enum State {
        IDLE, READING_PROG_NAME, IN_PROGRAM,
        DECLARING, ASSIGNING, READING, WRITING,
        IF_COND, IF_THEN, IF_ELSE
    }
    private final Deque<State> stateStack = new ArrayDeque<>();
    private State state = State.IDLE;

    // ---- Declaration context ----
    private DataType declType;
    private final List<String> declIds = new ArrayList<>();

    // ---- Assignment context ----
    private String assignTarget;
    private boolean assignGotTarget = false;

    // ---- Read context ----
    private final List<String> readVars = new ArrayList<>();

    // ---- Write context ----
    private final List<ExprNode> writeExprs = new ArrayList<>();
    private int writeParenDepth = 0; // tracks nested ( ) inside Escribir arguments

    // ---- Expression context (shared across assign/write/bool) ----
    private ExprNode currentExpr = new ExprNode();

    // ---- Bool expr context ----
    private ExprNode boolLeft;
    private String boolOperator;

    // ---- Statement line tracking (REQ-03) ----
    private int currentStmtLine = 0;

    // ---- If context stack (supports nested ifs) ----
    private static class IfFrame {
        BoolExprNode condition;
        StatementNode thenBranch;
        int lineNumber;
    }
    private final Deque<IfFrame> ifStack = new ArrayDeque<>();

    // ---- Statement delivery ----
    private void deliverStatement(StatementNode stmt) {
        if (state == State.IF_THEN) {
            // Grammar guarantees exactly one then-branch statement
            if (ifStack.peek().thenBranch != null)
                throw new IllegalStateException("ASTBuilder: thenBranch already set — parser delivered two then-branch statements");
            ifStack.peek().thenBranch = stmt;
        } else if (state == State.IF_ELSE) {
            IfFrame frame = ifStack.pop();
            IfNode ifNode = new IfNode(frame.condition, frame.thenBranch, stmt);
            ifNode.setLineNumber(frame.lineNumber);
            popState(); // pop IF_ELSE -> back to parent context
            deliverStatement(ifNode);
        } else {
            program.addStatement(stmt);
        }
    }

    private void pushState(State newState) {
        stateStack.push(state);
        state = newState;
    }

    private void popState() {
        state = stateStack.isEmpty() ? State.IN_PROGRAM : stateStack.pop();
    }

    // ---- SemanticListener ----

    @Override
    public void onProductionApplied(Production p, Token lookahead) {
        switch (p.getId()) {
            case 1  -> state = State.READING_PROG_NAME;
            case 5  -> { pushState(State.DECLARING); declType = null; declIds.clear(); currentStmtLine = lookahead.getLineNumber(); }
            case 6  -> { pushState(State.ASSIGNING); assignGotTarget = false; assignTarget = null; currentExpr = new ExprNode(); currentStmtLine = lookahead.getLineNumber(); }
            case 7  -> { pushState(State.READING); readVars.clear(); currentStmtLine = lookahead.getLineNumber(); }
            case 8  -> { pushState(State.WRITING); writeExprs.clear(); currentExpr = new ExprNode(); writeParenDepth = 0; currentStmtLine = lookahead.getLineNumber(); }
            case 9  -> {
                pushState(State.IF_COND);
                IfFrame frame = new IfFrame();
                frame.lineNumber = lookahead.getLineNumber();
                ifStack.push(frame);
                boolLeft = null; boolOperator = null;
                currentExpr = new ExprNode();
            }
            case 24 -> { if (state == State.DECLARING) declType = DataType.ENTERO; }
            case 25 -> { if (state == State.DECLARING) declType = DataType.REAL; }
        }
    }

    @Override
    public void onTerminalMatched(Token token) {
        String lex = token.getLexema();

        switch (state) {
            case READING_PROG_NAME -> {
                if (token.getTipo() == TiposTokens.IDENTIFICADOR) {
                    program.setName(lex);
                    state = State.IN_PROGRAM;
                }
            }
            case DECLARING -> {
                if (token.getTipo() == TiposTokens.IDENTIFICADOR) {
                    declIds.add(lex);
                } else if (lex.equals(";")) {
                    DeclarationNode node = new DeclarationNode(declType, new ArrayList<>(declIds));
                    node.setLineNumber(currentStmtLine);
                    popState();
                    deliverStatement(node);
                }
            }
            case ASSIGNING -> {
                if (!assignGotTarget && token.getTipo() == TiposTokens.IDENTIFICADOR) {
                    assignTarget = lex;
                    assignGotTarget = true;
                } else if (lex.equals("=")) {
                    currentExpr = new ExprNode();
                } else if (isExprToken(token)) {
                    currentExpr.addToken(tokenToOperationToken(token));
                } else if (lex.equals(";")) {
                    AssignmentNode node = new AssignmentNode(assignTarget, currentExpr);
                    node.setLineNumber(currentStmtLine);
                    popState();
                    deliverStatement(node);
                }
            }
            case READING -> {
                if (token.getTipo() == TiposTokens.IDENTIFICADOR) {
                    readVars.add(lex);
                } else if (lex.equals(";")) {
                    ReadNode node = new ReadNode(new ArrayList<>(readVars));
                    node.setLineNumber(currentStmtLine);
                    popState();
                    deliverStatement(node);
                }
            }
            case WRITING -> {
                if (lex.equals("(")) {
                    writeParenDepth++;
                    currentExpr.addToken(tokenToOperationToken(token));
                } else if (lex.equals(")")) {
                    if (writeParenDepth > 0) {
                        writeParenDepth--;
                        currentExpr.addToken(tokenToOperationToken(token));
                    } else {
                        // closing ) of Escribir(...) — flush current expression
                        if (!currentExpr.getTokens().isEmpty()) writeExprs.add(currentExpr);
                    }
                } else if (lex.equals(",")) {
                    writeExprs.add(currentExpr);
                    currentExpr = new ExprNode();
                } else if (lex.equals(";")) {
                    WriteNode node = new WriteNode(new ArrayList<>(writeExprs));
                    node.setLineNumber(currentStmtLine);
                    popState();
                    deliverStatement(node);
                } else if (isExprToken(token)) {
                    currentExpr.addToken(tokenToOperationToken(token));
                }
            }
            case IF_COND -> {
                if (isRelOp(lex)) {
                    boolLeft = currentExpr;
                    boolOperator = lex;
                    currentExpr = new ExprNode();
                } else if (isExprToken(token)) {
                    currentExpr.addToken(tokenToOperationToken(token));
                } else if (lex.equals("Entonces")) {
                    if (boolLeft == null || boolOperator == null)
                        throw new IllegalStateException("ASTBuilder: bool expression incomplete before Entonces");
                    ExprNode right = currentExpr;
                    ifStack.peek().condition = new BoolExprNode(boolLeft, boolOperator, right);
                    popState();
                    pushState(State.IF_THEN);
                }
            }
            case IF_THEN -> {
                if (lex.equals("Sino")) {
                    popState();
                    pushState(State.IF_ELSE);
                }
            }
            case IF_ELSE -> {
                // sub-statement finishes via deliverStatement when its ';' fires in child state
            }
            default -> { /* IDLE, IN_PROGRAM */ }
        }
    }

    @Override
    public void onParseComplete(boolean success) {
        this.parseOk = success;
    }

    // ---- Helpers ----

    private boolean isExprToken(Token t) {
        TiposTokens tipo = t.getTipo();
        String lex = t.getLexema();
        if (tipo == TiposTokens.IDENTIFICADOR) return true;
        if (tipo == TiposTokens.NUMERO_NATURAL) return true;
        if (tipo == TiposTokens.NUMERO_FLOTANTE) return true;
        return lex.equals("+") || lex.equals("-") || lex.equals("*") || lex.equals("(");
    }

    private boolean isRelOp(String lex) {
        return lex.equals(">") || lex.equals("<") || lex.equals("<>") || lex.equals("==");
    }

    private OperationToken tokenToOperationToken(Token t) {
        String lex = t.getLexema();
        TiposTokens tipo = t.getTipo();
        if (tipo == TiposTokens.IDENTIFICADOR) return new IdentifierToken(lex);
        if (tipo == TiposTokens.NUMERO_NATURAL || tipo == TiposTokens.NUMERO_FLOTANTE) return new NumberToken(lex);
        return new OperatorToken(lex);
    }

    public ProgramNode getProgram() { return program; }
    public boolean isParseOk() { return parseOk; }
}
