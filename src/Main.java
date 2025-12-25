import parser.grammar.Grammar;
import parser.GrammarParser;
import parser.reader.GrammarReader;
public class Main {
    public static void main(String[] args) {
//        Lexer lexer = new Lexer();
//        lexer.ejecutar();
        GrammarParser g = new GrammarParser(new Grammar(), new GrammarReader());
        g.ejecutar();
    }
}