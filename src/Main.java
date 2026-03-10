import lexer.Lexer;
import parser.grammar.Grammar;
import parser.GrammarParser;
import parser.grammar.Production;
import parser.ll1.LL1Parser;
import parser.ll1.LL1ParsingTable;
import parser.reader.GrammarReader;
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
    }
}