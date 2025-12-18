package parser;

import data_structures.Map;
import data_structures.Pila;
import lexer.Lexer;
import lexer.Token;
import parser.matrix.NoTerminal;
import parser.matrix.Production;
import parser.matrix.Terminal;

public class Parser {
    private Lexer lexer;
    private Token tokenActual;
    private Pila<String> pila;
    // Map<NoTerminal, Map<Terminal, Produccion>>
    Map<NoTerminal, Map<Terminal, Production>>

    public void LLDriver() {

    }
}
