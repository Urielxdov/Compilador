import java.util.List;

import lexer.Lexer;
import lexer.constants.TiposTokens;
import lexer.tokens.NodoLineaToken;
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

        System.out.printf("%-10s %-10s %-25s%n", "NUMERO LINEA", "LEXEMA", "TIPO");
        System.out.println("----------------------------------------------------");

        for(NodoLineaToken token : tokens) {

            String tipoFinal;

            if (token.getToken().getTipo() == TiposTokens.CARACTER_SIMPLE) {

                switch (token.getToken().getLexema()) {

                    case "+":
                        tipoFinal = "OPERADOR_SUMA";
                        break;

                    case "-":
                        tipoFinal = "OPERADOR_RESTA";
                        break;

                    case "*":
                        tipoFinal = "OPERADOR_MULTIPLICACION";
                        break;

                    case "=":
                        tipoFinal = "ASIGNACION";
                        break;

                    case ";":
                    case ",":
                    case "(":
                    case ")":
                        tipoFinal = "SIMBOLO_ESPECIAL";
                        break;

                    default:
                        tipoFinal = "DESCONOCIDO";
                }

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
    }
}