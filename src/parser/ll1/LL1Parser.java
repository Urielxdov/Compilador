package parser.ll1;

import data_structures.Lista;
import data_structures.Map;
import data_structures.Pila;
import lexer.Lexer;
import lexer.Token;
import parser.grammar.*;

public class LL1Parser {
    private Grammar grammar;
    private LL1ParsingTable tabla;
    private Pila<Symbol> pila;
    private Lexer lexer;

    public LL1Parser(Grammar grammar, LL1ParsingTable tabla, Lexer lexer) {
        this.grammar = grammar;
        this.tabla = tabla;
        this.pila = new Pila<>();
        this.lexer = lexer;
    }


    public void parse(Lista<Terminal> input) {
        pila.push(grammar.getSimboloInicial());
        Symbol x = pila.peek();
        Token a = null; // inicializa pa
        while (!pila.esVacia()) {
            if (x instanceof NoTerminal) {
                int posicion = tabla.getNumeroProduccion((NoTerminal) x, a);
                if (posicion != 0) {
                    Production p = grammar.getProduccion(posicion);
                    pila.pop();
                    for (Symbol s : p.getDerecha()) {
                        pila.push(s);
                    }
                } else {
                    System.out.println("Error sintactico");
                }
            } else {
                if (x.equals(a)) {
                    pila.pop();
                    a = lexer.getNextToken();
                } else {
                    System.out.println("Error sintactico");
                }
            }
        }
    }




}
