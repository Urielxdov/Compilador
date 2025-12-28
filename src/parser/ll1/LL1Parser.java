package parser.ll1;

import data_structures.Lista;
import data_structures.Map;
import data_structures.Pila;
import parser.grammar.Grammar;
import parser.grammar.NoTerminal;
import parser.grammar.Symbol;
import parser.grammar.Terminal;

public class LL1Parser {
    private Grammar grammar;
    private LL1ParsingTable tabla;
    private Pila<Symbol> pila;

    public LL1Parser(Grammar grammar, LL1ParsingTable tabla) {
        this.grammar = grammar;
        this.tabla = tabla;
        this.pila = new Pila<>();
    }


    public void parse(Lista<Terminal> input) {
        pila.push(grammar.getSimboloInicial());
        Symbol x = pila.peek();
        String a = ""; // inicializa pa
        while (!pila.esVacia()) {
            if (x instanceof NoTerminal) {

            } else {
                if (x.equals(a)) {
                    pila.pop();

                } else {
                    // error sintactico
                }
            }
        }
    }


}
