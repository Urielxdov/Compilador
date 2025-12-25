import grammar.Grammar;
import grammar.GrammarParser;
import grammar.GrammarReader;
public class Main {
    public static void main(String[] args) {
//        Lexer lexer = new Lexer();
//        lexer.ejecutar();
        GrammarParser g = new GrammarParser(new Grammar(), new GrammarReader());
        g.ejecutar();
    }
}