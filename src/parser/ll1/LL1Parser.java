package parser.ll1;

import data_structures.Lista;
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


    public void execute() {
        pila.push(grammar.getSimboloInicial());
        Symbol x = pila.peek();
        Token a = lexer.next(); // inicializa pa
        while (!pila.esVacia()) {
            if (x instanceof NoTerminal) {
                int posicion = tabla.getNumeroProduccion((NoTerminal) x, a);
                if (posicion != -1) {
                    Production p = grammar.getProduccion(posicion);
                    pila.pop();
                    for (int i = p.getDerecha().nodosExistentes() - 1; i >= 0; i--) {
                        pila.push(p.getDerecha().obtener(i));
                    }
                    x = pila.peek();
                } else {
                    System.out.println("Error sintactico");
                }
            } else {
                if (Comparator.comapare(a, x)) {
                    pila.pop();
                    x = pila.peek();
                    a = lexer.next();
                } else if (x instanceof Epsilon) {
                    pila.pop();
                    x = pila.peek();
                }
                else {
                    System.out.println("Error sintactico");
                }
            }
        }
    }




}
