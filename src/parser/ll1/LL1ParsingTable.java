package parser.ll1;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import lexer.Token;
import lexer.constants.TablaCaracteresSimples;
import lexer.constants.TablaPalabrasReservadas;
import lexer.constants.TiposTokens;
import parser.grammar.*;


public class LL1ParsingTable {

    private int[][] matriz; // Matriz LL(1) de producciones

    // Mapeos entre simbolos y posiciones de la tabla
    private Map<NoTerminal, Integer> ubicacionesNoTerminales = new Map<>();
    private Map<Terminal, Integer> ubicacionesTerminales = new Map<>();

    private Grammar grammar; // gramatica asociada

    private static final String[] ORDEN_NO_TERMINALES = {
            "programa","lista_sent","sent_final","sentencia","lista_id",
            "id_final","lista_expr","lista_exprfinal","expr_bool","expresion",
            "expr_final","expr_arit","tipo","operador","operel","inicio"
    };

    private static final String[] ORDEN_TERMINALES = {
            "Programa","id","Inicio","Fin",";","=","Leer","(",")","Escribir",
            "Si","Entonces","Sino",",","literalentera","literalreal",
            "Entero","Real","+","-","*",
            ">","<","<>","=="
    };


    private final int ID = 295;

    /**
     * Construye la tabla LL(1) a partir de una gramatica
     *
     * @param grammar gramatica LL(1) con FIRST y FOLLOW ya calculados
     */
    public LL1ParsingTable(Grammar grammar) {
        this.grammar = grammar;


        // codigo duro
        for (int i = 0; i < ORDEN_TERMINALES.length; i++) {
            ubicacionesTerminales.put(new Terminal(ORDEN_TERMINALES[i]), i);
        }

        for (int i = 0; i < ORDEN_NO_TERMINALES.length; i++) {
            ubicacionesNoTerminales.put(new NoTerminal(ORDEN_NO_TERMINALES[i]), i);
        }

        this.matriz = new int[ORDEN_NO_TERMINALES.length][ORDEN_TERMINALES.length];

        llenarTabla();

    }


//    /**
//     * Para cada A -> a:
//     *  - Se agrega entradas para FIRST(a)
//     *  - sI first(A) contiene epsilon, se agregan entradas para FOLLOW(A)
//     */
//    private void llenarTabla() {
//        Lista<Production> producciones = grammar.getProducciones();
//
//        for (Production p : producciones) {
//            NoTerminal A = p.getIzquierda();
//            Lista<Symbol> alpha = p.getDerecha();
//
//            Conjunto<Terminal> firstAlpha = new Conjunto<>();
//
//            for (Terminal t : firstAlpha) {
//                if (!(t instanceof Epsilon)) {
//                    agregarUbicacion(A, t, p.getId());
//                }
//            }
//
//            if (firstAlpha.contiene(Epsilon.getInstance())) {
//                Conjunto<Terminal> followA = new Conjunto<>();
//
//                for (Terminal t : followA) {
//                    agregarUbicacion(A, t, p.getId());
//                }
//            }
//        }
//    }


    private void llenarTabla() {

        // programa
        agregarUbicacion("programa", "Programa", 1);

        // lista_sent
        agregarUbicacion("lista_sent", "id", 2);
        agregarUbicacion("lista_sent", "Leer", 2);
        agregarUbicacion("lista_sent", "Escribir", 2);
        agregarUbicacion("lista_sent", "Entero", 2);
        agregarUbicacion("lista_sent", "Real", 2);
        agregarUbicacion("lista_sent", "Si", 2);

        // sent_final
        agregarUbicacion("sent_final", "id", 3);
        agregarUbicacion("sent_final", "Fin", 4);
        agregarUbicacion("sent_final", "Leer", 3);
        agregarUbicacion("sent_final", "Escribir", 3);
        agregarUbicacion("sent_final", "Entero", 3);
        agregarUbicacion("sent_final", "Real", 3);
        agregarUbicacion("sent_final", "Si", 3);

        // sentencia
        agregarUbicacion("sentencia", "id", 6);
        agregarUbicacion("sentencia", "Leer", 7);
        agregarUbicacion("sentencia", "Escribir", 8);
        agregarUbicacion("sentencia", "Entero", 5);
        agregarUbicacion("sentencia", "Real", 5);
        agregarUbicacion("sentencia", "Si", 9);

        // lista_id
        agregarUbicacion("lista_id", "id", 10);

        // id_final
        agregarUbicacion("id_final", ",", 11);
        agregarUbicacion("id_final", ";", 12);
        agregarUbicacion("id_final", ")", 12);

        // lista_expr
        agregarUbicacion("lista_expr", "id", 13);
        agregarUbicacion("lista_expr", "(", 13);
        agregarUbicacion("lista_expr", "literalentera", 13);
        agregarUbicacion("lista_expr", "literalreal", 13);

        // lista_exprfinal - CORREGIDO
        agregarUbicacion("lista_exprfinal", ",", 14);  // CAMBIO: era "=" ahora ","
        agregarUbicacion("lista_exprfinal", ")", 15);

        // expr_bool
        agregarUbicacion("expr_bool", "id", 16);
        agregarUbicacion("expr_bool", "literalentera", 16);
        agregarUbicacion("expr_bool", "literalreal", 16);
        agregarUbicacion("expr_bool", "(", 16);

        // expresion
        agregarUbicacion("expresion", "id", 17);
        agregarUbicacion("expresion", "literalentera", 17);
        agregarUbicacion("expresion", "literalreal", 17);
        agregarUbicacion("expresion", "(", 17);

        // expr_final - CORREGIDO
        agregarUbicacion("expr_final", ";", 19);
        agregarUbicacion("expr_final", ")", 19);      // AGREGADO
        agregarUbicacion("expr_final", ",", 19);      // AGREGADO
        agregarUbicacion("expr_final", "Entonces", 19); // AGREGADO
        agregarUbicacion("expr_final", "Sino", 19);   // AGREGADO
        agregarUbicacion("expr_final", "+", 18);
        agregarUbicacion("expr_final", "-", 18);
        agregarUbicacion("expr_final", "*", 18);
        agregarUbicacion("expr_final", ">", 19);      // CAMBIO: era 18 ahora 19
        agregarUbicacion("expr_final", "<", 19);      // CAMBIO: era 18 ahora 19
        agregarUbicacion("expr_final", "<>", 19);     // CAMBIO: era 18 ahora 19
        agregarUbicacion("expr_final", "==", 19);     // CAMBIO: era 18 ahora 19

        // expr_arit
        agregarUbicacion("expr_arit", "id", 21);
        agregarUbicacion("expr_arit", "literalentera", 22);
        agregarUbicacion("expr_arit", "literalreal", 23);
        agregarUbicacion("expr_arit", "(", 20);

        // tipo
        agregarUbicacion("tipo", "Entero", 24);
        agregarUbicacion("tipo", "Real", 25);

        // operador
        agregarUbicacion("operador", "+", 26);
        agregarUbicacion("operador", "-", 27);
        agregarUbicacion("operador", "*", 28);

        // operel
        agregarUbicacion("operel", ">", 29);
        agregarUbicacion("operel", "<", 30);
        agregarUbicacion("operel", "<>", 31);
        agregarUbicacion("operel", "==", 32);

        // inicio
        agregarUbicacion("inicio", "Programa", 33);
    }


    /**
     * Asigna una produccion a una celda especifica de la tabla
     *
     * @param nt no terminal (fila)
     * @param t terminal (columna)
     * @param valor identificador de la produccion
     */
    private void agregarUbicacion(String nt, String t, int valor) {
        int fila = ubicacionesNoTerminales.get(new NoTerminal(nt));
        int columna = ubicacionesTerminales.get(new Terminal(t));

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
        else if (t.getTipo() == TiposTokens.NUMERO_NATURAL) {
            terminal = new Terminal("literalentera");
        } else if (t.getTipo() == TiposTokens.NUMERO_FLOTANTE) {
            terminal = new Terminal("literalreal");
        }

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
