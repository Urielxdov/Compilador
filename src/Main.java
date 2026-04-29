import codegen.GeneradorCodigo;
import codegen.INotacion;
import codegen.NotacionCuadruplo;
import codegen.NotacionPostfija;
import codegen.NotacionPrefija;
import codegen.Cuadruplo;
import lexer.Lexer;
import lexer.Token;
import parser.GrammarParser;
import parser.grammar.Grammar;
import parser.ll1.LL1Parser;
import parser.ll1.LL1ParsingTable;
import parser.reader.GrammarReader;
import semantic.SemanticException;
import semantic.SymbolTable;
import semantic.ast.ASTBuilder;
import semantic.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class Main {
    // Cambia para elegir notacion: CUADRUPLO | POSTFIJA | PREFIJA
    private static final String NOTACION = "CUADRUPLO";

    public static void main(String[] args) {

        // ── 1. ANALISIS LEXICO + SINTACTICO (LL1) ──────────────────────────
        System.out.println("=== Analisis Sintactico LL(1) ===");
        try {
            Grammar grammar = new Grammar();
            GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
            gp.ejecutar();

            LL1ParsingTable tabla = new LL1ParsingTable(grammar);
            LL1Parser lp = new LL1Parser(grammar, tabla, new Lexer());
            lp.execute();
        } catch (Exception e) {
            System.out.println("[LL1] Error: " + e.getMessage());
        }

        // ── 2. LEXICO (segunda pasada para AST) ────────────────────────────
        System.out.println("\n=== Construccion AST ===");
        Lexer lex = new Lexer();
        lex.all();

        List<Token> tokenList = new ArrayList<>();
        for (Token t : lex.obtenerTokens()) {
            tokenList.add(t);
        }

        // ── 3. ANALISIS SEMANTICO + AST ────────────────────────────────────
        SymbolTable symbolTable = new SymbolTable();
        ASTBuilder builder = new ASTBuilder(symbolTable);
        ASTNode raiz;

        try {
            raiz = builder.construirAST(tokenList);
        } catch (SemanticException e) {
            System.out.println("[Semantico] " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.out.println("[Sintactico/AST] " + e.getMessage());
            return;
        }

        // ── 4. GENERACION DE CODIGO INTERMEDIO ─────────────────────────────
        System.out.println("\n=== Codigo Intermedio [" + NOTACION + "] ===");
        INotacion notacion = seleccionarNotacion(NOTACION);
        GeneradorCodigo gen = new GeneradorCodigo(notacion);
        List<Cuadruplo> codigo = gen.generar(raiz);
        gen.imprimir(codigo);
    }

    private static INotacion seleccionarNotacion(String tipo) {
        switch (tipo.toUpperCase()) {
            case "POSTFIJA": return new NotacionPostfija();
            case "PREFIJA":  return new NotacionPrefija();
            default:         return new NotacionCuadruplo();
        }
    }
}
