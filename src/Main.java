import parser.analysis.GrammarAnalysis;
import parser.grammar.Grammar;
import parser.GrammarParser;
import parser.ll1.LL1ParsingTable;
import parser.reader.GrammarReader;
public class Main {
    public static void main(String[] args) {
//        Lexer lexer = new Lexer();
//        lexer.ejecutar();
        Grammar grammar = new Grammar();
        GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
        gp.ejecutar();
        GrammarAnalysis ga = new GrammarAnalysis(grammar);
        ga.calcularFirst();
        ga.calcularFollow();
        LL1ParsingTable l = new LL1ParsingTable(grammar);
        System.out.println(l);
    }
}