import java.util.List;

import io.FileReaderManager;
import io.RutaArchivos;
import lexer.Lexer;
import lexer.constants.TiposTokens;
import lexer.tokens.NodoLineaToken;
import semantic.SemanticAnalyzer;
import semantic.SemanticReport;
import semantic.operadores.MapeadorCaracteresSimples;

public class Main {
    public static void main(String[] args) {
//         Grammar grammar = new Grammar();
//         GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
//         gp.ejecutar();
//         System.out.println("Simbolso terminales:\n " + grammar.getTerminales());
//         System.out.println("Simbolos no terminales:\n" + grammar.getNoTerminales());
//         System.out.println("Lados derechos:\n");
//         for(Production p : grammar.getProducciones()) {
//             System.out.println(p.getDerecha());
//         }
// //        System.out.println("Terminales");
// //        System.out.println(grammar.getTerminales());
// //        System.out.println("No terminales");
// //        System.out.println(grammar.getNoTerminales());
// //        System.out.println(grammar.getProducciones());
// //        GrammarAnalysis ga = new GrammarAnalysis(grammar);
// //        ga.calcularFirst();
// //        ga.calcularFollow();
//         LL1ParsingTable l = new LL1ParsingTable(grammar);
//         System.out.println(l);
//         LL1Parser lp = new LL1Parser(grammar, l, new Lexer());
//         lp.execute();
        Lexer lex = new Lexer();
        lex.all();

        List<NodoLineaToken> tokens = lex.obtenerTokensLinea();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        SemanticReport semanticReport = semanticAnalyzer.analyze(tokens);

        System.out.println("ARCHIVO DE PRUEBA");
        List<String> programa = new FileReaderManager().leerArchivo(RutaArchivos.PROGRAMA);
        for (int i = 0; i < programa.size(); i++) {
            System.out.printf("%-4d %s%n", i + 1, programa.get(i));
        }
        System.out.println();

        System.out.printf("%-10s %-10s %-25s%n", "NUMERO LINEA", "LEXEMA", "TIPO");
        System.out.println("----------------------------------------------------");

        for(NodoLineaToken token : tokens) {

            String tipoFinal;

            if (token.getToken().getTipo() == TiposTokens.CARACTER_SIMPLE) {
                tipoFinal = MapeadorCaracteresSimples.obtenerTipoOperador(token.getToken().getLexema());
            } else {
                tipoFinal = token.getToken().getTipo().toString();
            }

            System.out.printf(
                    "%-10s %-10s %-25s%n",
                    token.getLinea(),
                    token.getToken().getLexema(),
                    tipoFinal
            );


        }
        System.out.println();
        System.out.println("TABLA DE SIMBOLOS");
        System.out.println(semanticReport.renderSymbolTable());
        System.out.println("VERIFICACION DE TIPOS");
        System.out.println(semanticReport.renderTypeVerification());
    }
}
