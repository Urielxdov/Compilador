package parser;

import data_structures.Map;
import data_structures.Pila;
import lexer.Lexer;
import lexer.Token;
import parser.grammar.NoTerminal;
import parser.grammar.Production;
import parser.grammar.Terminal;

public class Parser {
    private Lexer lexer;
    private Token tokenActual;
    private Pila<String> pila;
    // Map<NoTerminal, Map<Terminal, Produccion>>
    private Map<NoTerminal, Map<Terminal, Production>> producciones;

    public void LLDriver() {

    }
}
