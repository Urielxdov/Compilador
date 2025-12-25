package parser;

import data_structures.Map;
import data_structures.Pila;
import lexer.Lexer;
import lexer.Token;
import grammar.utils.NoTerminal;
import grammar.utils.Production;
import grammar.utils.Terminal;

public class Parser {
    private Lexer lexer;
    private Token tokenActual;
    private Pila<String> pila;
    // Map<NoTerminal, Map<Terminal, Produccion>>
    private Map<NoTerminal, Map<Terminal, Production>> producciones;

    public void LLDriver() {

    }
}
