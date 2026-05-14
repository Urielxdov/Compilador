package parser.ll1;

import data_structures.Pila;
import lexer.Lexer;
import lexer.Token;
import parser.SemanticListener;
import parser.grammar.*;
import semantic.ast.ASTNode;

/**
 * Implementacion de un LL(1)
 *
 * Este parser analiza una secuencia de tokens producida por un {@link Lexer}
 * utilizado una gramatica LL(1) previamente procesada y una tabla de analisis sintactico
 *
 * El algoritmo se basa en:
 * - Una pila de simbolos (terminales y no terminales)
 * - Un token de entrada actual
 * - Una tabla LL(1) que decide que produccion aplicar
 *
 * Flujo:
 * 1. Se inicializa la pila con el simbolo inicial de la gramatica
 * 2. Se obtiene el primer token de entrada
 * 3. Mientras la pila no este vacia:
 *  - Si el tope es un no terminal, se consulta la tabla LL(1)
 *  - Si el tope es un terminal, se compara con el token actual.
 *   - Se manejan producciones epsilon explicitamente
 *
 * Errores:
 *  - Un error sintactico ocurre si no existe una entrada valida en la tabla
 *    o si un terminal no coincide con el token actual
 */
public class LL1Parser {
    private Grammar grammar; // Gramatica LL(1)
    private LL1ParsingTable tabla; // Tabla de analisis predictivo LL(1)
    private Pila<Symbol> pila; // Pila de simbolos del parser
    private Pila<ASTNode> pilaAST;
    private Lexer lexer; // Fuente de tokens
    private SemanticListener semanticListener; // nullable

    /**
     * Construye un parser LL(1)
     *
     * @param grammar gramatica previamente definida
     * @param tabla tabla de analisis LL(1) generada a partir de la gramatica
     * @param lexer analizador lexico que produce los tokens de entrada
     */
    public LL1Parser(Grammar grammar, LL1ParsingTable tabla, Lexer lexer) {
        this(grammar, tabla, lexer, null);
    }

    /**
     * Construye un parser LL(1) con un listener semantico opcional
     *
     * @param grammar gramatica previamente definida
     * @param tabla tabla de analisis LL(1) generada a partir de la gramatica
     * @param lexer analizador lexico que produce los tokens de entrada
     * @param listener listener semantico que recibe eventos del parser (puede ser null)
     */
    public LL1Parser(Grammar grammar, LL1ParsingTable tabla, Lexer lexer, SemanticListener listener) {
        this.grammar = grammar;
        this.tabla = tabla;
        this.pila = new Pila<>();
        this.pilaAST = new Pila<>();
        this.lexer = lexer;
        this.semanticListener = listener;
    }


    /**
     * Ejecuta el analisis sintactico
     *
     * - Se apila el simbolo inicial
     * - Se obtiene el token de entrada actual
     * - Mientras la pila no este vacia:
     *  - Si el tope es un no terminal
     *      - Se consulta la tabla LL(1) con (No terminal, Token)
     *      - Se remplazada el no terminal por la parte derecha de la produccion
     *  - Si el tope es un terminal:
     *      - Debe coincidir con el token actual
     *      - Avanza en la entrada
     *  - Si es epsilon:
     *      - Se elimina de la pila sin consumir token
     *
     * En caso de error sintactico
     */
    public void execute() {
        pila.push(grammar.getSimboloInicial());
        Symbol x = pila.peek();
        Token a = lexer.next(); // inicializa pa
        int paso = 1;
        while (!pila.esVacia()) {
            System.out.println(String.valueOf(paso)+ ".- Token actual: " + a.getLexema() + "\nContenido de la pila:"+ pila.toString());
            if (x instanceof NoTerminal) {
                int posicion = tabla.getNumeroProduccion((NoTerminal) x, a);

                if (posicion != 0) {
                    Production p = grammar.getProduccion(posicion);
                    if (semanticListener != null) semanticListener.onProductionApplied(p, a);
                    pila.pop();
                    System.out.println("Produccion a utilizar: " + p.toString() + " en paso " + String.valueOf(paso));
                    if (p == null) {
                        System.out.println("dime");
                    }
                    for (int i = p.getDerecha().nodosExistentes() - 1; i >= 0; i--) {
                        pila.push(p.getDerecha().obtener(i));
                    }
                    x = pila.peek();
                } else {
                    System.out.println("Se produjo un error sintacto, se esperaba un '" + x.getNombre() + "' y se obtuvo un '" + a.getLexema() + "'");
                    if (semanticListener != null) semanticListener.onParseComplete(false);
                    return;
                }
            } else {
                if (Comparator.comapare(a, x)) {
                    Symbol t = pila.pop();
                    if (semanticListener != null) semanticListener.onTerminalMatched(a);
                    // Aqui debemos implementar un FActory para la creacion del nodo
                    // se compara lo que se espera para el sintactico, con lo que hay en el programa
                    // esto lo logramos mediante token que posee un atributo TipoToken
                    try {
                        x = pila.peek();
                        System.out.println("Simbolos en paso '" + String.valueOf(paso) + "':\n" + lexer.obtenerSimbolos());
                    } catch (Exception e) {
                        System.out.println("aca");
                        System.out.println(t);
                    }
                    a = lexer.next();
                } else if (x instanceof Epsilon) {
                    pila.pop();
                    x = pila.peek();
                }
                else {
                    System.out.println("Error sintactico 2");
                }
            }
            paso++;
        }
        if (semanticListener != null) semanticListener.onParseComplete(true);
    }




}
