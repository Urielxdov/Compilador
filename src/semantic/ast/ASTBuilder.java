package semantic.ast;

import lexer.Token;
import lexer.constants.TiposTokens;
import semantic.SemanticException;
import semantic.SymbolTable;

import java.util.List;

public class ASTBuilder {
    private List<Token> tokens;
    private int pos;
    private SymbolTable symbolTable;

    public ASTBuilder() {
        this.symbolTable = null;
    }

    public ASTBuilder(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    // Legacy: kept for ASTMain compatibility
    public AST construirAST(data_structures.Lista<Token> listaTokens) {
        throw new UnsupportedOperationException("Usa construirAST(List<Token>)");
    }

    public ASTNode construirAST(List<Token> tokenList) {
        this.tokens = tokenList;
        this.pos = 0;
        return parsePrograma();
    }

    private Token current() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos);
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private void expect(String lexema) {
        Token t = current();
        if (t == null || !t.getLexema().equals(lexema))
            throw new RuntimeException("Se esperaba '" + lexema + "' pero se obtuvo '"
                    + (t != null ? t.getLexema() : "EOF") + "'");
        consume();
    }

    private boolean currentIs(String lexema) {
        Token t = current();
        return t != null && t.getLexema().equals(lexema);
    }

    // programa → Iniciar lista_sent Finalizar
    private ASTNode parsePrograma() {
        expect("Iniciar");
        ASTNode root = new ASTNode(NodeKind.PROGRAMA);
        while (!currentIs("Finalizar") && current() != null) {
            root.agregarHijo(parseSentencia());
        }
        expect("Finalizar");
        return root;
    }

    // sentencia → decl | asign | leer | mostrar | si
    private ASTNode parseSentencia() {
        Token t = current();
        if (t == null) throw new RuntimeException("Sentencia inesperada: EOF");
        String lex = t.getLexema();

        if (lex.equals("Int") || lex.equals("Entero"))
            return parseDecl(NodeKind.DECL_ENTERA);
        if (lex.equals("Real"))
            return parseDecl(NodeKind.DECL_REAL);
        if (lex.equals("Leer"))
            return parseLeer();
        if (lex.equals("Mostrar") || lex.equals("Escribir"))
            return parseMostrar();
        if (lex.equals("Si"))
            return parseSiSino();
        if (t.getTipo() == TiposTokens.IDENTIFICADOR)
            return parseAsignacion();

        throw new RuntimeException("Sentencia no reconocida: '" + lex + "'");
    }

    // tipo id (, id)* ;
    private ASTNode parseDecl(NodeKind kind) {
        consume(); // tipo
        TiposTokens tipoVar = (kind == NodeKind.DECL_ENTERA)
                ? TiposTokens.NUMERO_NATURAL : TiposTokens.NUMERO_REAL;
        ASTNode decl = new ASTNode(kind);
        Token first = current();
        decl.agregarHijo(parseId());
        if (symbolTable != null) symbolTable.declare(first.getLexema(), tipoVar);
        while (currentIs(",")) {
            consume();
            Token next = current();
            decl.agregarHijo(parseId());
            if (symbolTable != null) symbolTable.declare(next.getLexema(), tipoVar);
        }
        expect(";");
        return decl;
    }

    // id = expresion ;
    private ASTNode parseAsignacion() {
        Token id = consume();
        if (symbolTable != null) symbolTable.get(id.getLexema()); // valida declarada
        expect("=");
        ASTNode expr = parseExpresion();
        expect(";");
        ASTNode asign = new ASTNode(NodeKind.ASIGNACION);
        asign.agregarHijo(new ASTNode(NodeKind.ID, id));
        asign.agregarHijo(expr);
        return asign;
    }

    // Leer id ;
    private ASTNode parseLeer() {
        consume(); // Leer
        ASTNode leer = new ASTNode(NodeKind.LEER);
        Token id = current();
        leer.agregarHijo(parseId());
        if (symbolTable != null) symbolTable.get(id.getLexema()); // valida declarada
        expect(";");
        return leer;
    }

    // Mostrar expresion ;
    private ASTNode parseMostrar() {
        consume(); // Mostrar / Escribir
        ASTNode escribir = new ASTNode(NodeKind.ESCRIBIR);
        escribir.agregarHijo(parseExpresion());
        expect(";");
        return escribir;
    }

    // Si expr_bool Entonces sentencia Sino sentencia
    private ASTNode parseSiSino() {
        consume(); // Si
        ASTNode cond = parseExprBool();
        expect("Entonces");
        ASTNode sent1 = parseSentencia();
        expect("Sino");
        ASTNode sent2 = parseSentencia();
        ASTNode siSino = new ASTNode(NodeKind.SI_SINO);
        siSino.agregarHijo(cond);
        siSino.agregarHijo(sent1);
        siSino.agregarHijo(sent2);
        return siSino;
    }

    // expr_arit oprel expr_arit
    private ASTNode parseExprBool() {
        ASTNode left = parseExprArit();
        Token oprel = current();
        if (oprel == null) throw new RuntimeException("Se esperaba operador relacional");
        String lex = oprel.getLexema();
        if (!lex.equals(">") && !lex.equals("<") && !lex.equals("<>") && !lex.equals("=="))
            throw new RuntimeException("Operador relacional no reconocido: '" + lex + "'");
        consume();
        ASTNode right = parseExprArit();
        ASTNode bool = new ASTNode(NodeKind.EXPR_BOOL, oprel);
        bool.agregarHijo(left);
        bool.agregarHijo(right);
        return bool;
    }

    // expr_arit (op expr_arit)*  — asociatividad izquierda
    private ASTNode parseExpresion() {
        ASTNode left = parseExprArit();
        while (isOperador(current())) {
            Token op = consume();
            ASTNode right = parseExprArit();
            ASTNode bin = new ASTNode(NodeKind.EXPR_BIN, op);
            bin.agregarHijo(left);
            bin.agregarHijo(right);
            left = bin;
        }
        return left;
    }

    // (expr) | -atom | id | num
    private ASTNode parseExprArit() {
        Token t = current();
        if (t == null) throw new RuntimeException("Expresion vacia inesperada");

        if (t.getLexema().equals("(")) {
            consume();
            ASTNode expr = parseExpresion();
            expect(")");
            return expr;
        }

        if (t.getLexema().equals("-")) {
            consume();
            ASTNode neg = new ASTNode(NodeKind.NEG);
            neg.agregarHijo(parseAtomo());
            return neg;
        }

        return parseAtomo();
    }

    private ASTNode parseAtomo() {
        Token t = current();
        if (t == null) throw new RuntimeException("Se esperaba un valor");
        if (t.getTipo() == TiposTokens.IDENTIFICADOR) {
            if (symbolTable != null) symbolTable.get(t.getLexema()); // valida declarada
            return new ASTNode(NodeKind.ID, consume());
        }
        if (t.getTipo() == TiposTokens.NUMERO_NATURAL)
            return new ASTNode(NodeKind.NUM_INT, consume());
        if (t.getTipo() == TiposTokens.NUMERO_REAL)
            return new ASTNode(NodeKind.NUM_REAL, consume());
        throw new RuntimeException("Valor inesperado: '" + t.getLexema() + "'");
    }

    private ASTNode parseId() {
        Token t = current();
        if (t == null || t.getTipo() != TiposTokens.IDENTIFICADOR)
            throw new RuntimeException("Se esperaba identificador");
        return new ASTNode(NodeKind.ID, consume());
    }

    private boolean isOperador(Token t) {
        if (t == null) return false;
        String lex = t.getLexema();
        return lex.equals("+") || lex.equals("-") || lex.equals("*") || lex.equals("/");
    }
}
