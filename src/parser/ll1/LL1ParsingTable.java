package parser.ll1;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import lexer.Token;
import lexer.constants.TablaCaracteresSimples;
import lexer.constants.TablaPalabrasReservadas;
import parser.grammar.*;

/**
 * Representa la tabal de analisis sintactico LL(1)
 *
 * La tabla se construye a partir de:
 * - Las producciones de la gramatica
 * - los conjuntos FIRST y FOLLOW previamente calculados
 *
 * Cada celda M[A, a] contiene el identificador de la produccion
 * que debe aplicarse cuando:
 *  - A es el no terminal en el tope de la pila
 *  - a es el simbolo terminal de entrada actual
 *
 * Reglas de contruccion:
 * 1. Para cada produccion A -> a:
 *      - Para todo terminal t perteneciente First(a) \ {epsilon}:
 *          M[A, t] = produccion
 * 2. Si epsilon pertenece a First(a):
 *  - Para todo terminal t perteneciente a FOLLOW(A)
 *      M[A, t* = produccion
 *
 * Las posiciones sin produccion valida permanecen vacias (error sintactico)
 *
 * Precondiciones:
 *  - La gramatica debe de ser LL(1)
 *  - FIRST y FOLLOW deben de haber sido calculados previamente
 */
public class LL1ParsingTable {

    private int[][] matriz; // Matriz LL(1) de producciones

    // Mapeos entre simbolos y posiciones de la tabla
    private Map<NoTerminal, Integer> ubicacionesNoTerminales = new Map<>();
    private Map<Terminal, Integer> ubicacionesTerminales = new Map<>();

    private Grammar grammar; // gramatica asociada


    private final int ID = 295;

    /**
     * Construye la tabla LL(1) a partir de una gramatica
     *
     * @param grammar gramatica LL(1) con FIRST y FOLLOW ya calculados
     */
    public LL1ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        crearTabla();
        llenarTabla();
    }

    /**
     * Inicializa la estructura de la tabla LL(1)
     *
     * - Asigna una fila a cada no terminal
     * - Asigna una columna a cada terminal (excepto epsilon)
     * - Crea la matriz de enteros para almacenar las producciones
     */
    private void crearTabla() {
        int altura = grammar.getNoTerminales().nodosExistentes();
        int anchura = grammar.getTerminales().nodosExistentes();

        for (int i = 0; i < altura; i++) {
            ubicacionesNoTerminales.put( grammar.getNoTerminales().obtener(i), i);
        }

        for (int i = 0; i < anchura; i++) {
            if (grammar.getTerminales().obtener(i) instanceof Epsilon) continue;
            ubicacionesTerminales.put(grammar.getTerminales().obtener(i), i);
        }

        this.matriz = new int[altura][anchura];
    }

    /**
     * Para cada A -> a:
     *  - Se agrega entradas para FIRST(a)
     *  - sI first(A) contiene epsilon, se agregan entradas para FOLLOW(A)
     */
    private void llenarTabla() {
        Lista<Production> producciones = grammar.getProducciones();

        for (Production p : producciones) {
            NoTerminal A = p.getIzquierda();
            Lista<Symbol> alpha = p.getDerecha();

            Conjunto<Terminal> firstAlpha = grammar.firstDeSecuencia(alpha);

            for (Terminal t : firstAlpha) {
                if (!(t instanceof Epsilon)) {
                    agregarUbicacion(A, t, p.getId());
                }
            }

            if (firstAlpha.contiene(Epsilon.getInstance())) {
                Conjunto<Terminal> followA = grammar.getFollow().get(A);

                for (Terminal t : followA) {
                    agregarUbicacion(A, t, p.getId());
                }
            }
        }
    }

    /**
     * Asigna una produccion a una celda especifica de la tabla
     *
     * @param nt no terminal (fila)
     * @param t terminal (columna)
     * @param valor identificador de la produccion
     */
    private void agregarUbicacion(NoTerminal nt, Terminal t, int valor) {
        int fila = ubicacionesNoTerminales.get(nt);
        int columna = ubicacionesTerminales.get(t);



        matriz[fila][columna] = valor;
    }

    /**
     * Obtiene el numero de produccion a aplicar dado un no terminal y un token
     *
     * Realiza la traduccion del token lexico a su correspondiente
     * simbolo terminal de la gramatica (por ejemplo, identificadores)
     *
     * @param nt no terminal en el tope de la pila
     * @param t token de entrada actual
     * @return numero de produccion o -1 si no existe entrada valida
     */
    public int getNumeroProduccion (NoTerminal nt, Token t) {
        if (nt == null) return 0;
        Terminal terminal = new Terminal(t.getLexema());
        if (t.getAtributo() == ID) terminal = new Terminal("id");
        else if (
                TablaPalabrasReservadas.existe(t.getLexema())
                        || TablaCaracteresSimples.existe(t.getLexema())
            )
            terminal = new Terminal(t.getLexema());

        int posicion;
        try {
            posicion = matriz[
                    ubicacionesNoTerminales.get(nt)
                    ][
                    ubicacionesTerminales.get(terminal)
                    ];
        } catch (NullPointerException e) {
            posicion = -1;
        }
        return posicion;
    }

    /**
     * Devuelve una representacion tabular de la tabla LL(1)
     * util para depuracion
     * @return
     */
    @Override
    public String toString() {
        String salida = "";

        // Encabezado
        salida += String.format("%-20s", "");
        for (Terminal t : ubicacionesTerminales) {
            salida += String.format("%-15s", t);
        }
        salida += "\n";

        // Filas
        for (NoTerminal nt : ubicacionesNoTerminales) {
            int fila = ubicacionesNoTerminales.get(nt);
            salida += String.format("%-20s", nt);

            for (Terminal t : ubicacionesTerminales) {
                int columna = ubicacionesTerminales.get(t);
                int valor = matriz[fila][columna];

                if (valor == 0) {
                    salida += String.format("%-15s", "—");
                } else {
                    salida += String.format("%-15d", valor);
                }
            }
            salida += "\n";
        }

        return salida;
    }

}
