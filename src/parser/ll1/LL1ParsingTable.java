package parser.ll1;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import lexer.Token;
import lexer.constants.TablaCaracteresSimples;
import lexer.constants.TablaPalabrasReservadas;
import parser.grammar.*;

public class LL1ParsingTable {

    private int[][] matriz;

    private Map<NoTerminal, Integer> ubicacionesNoTerminales = new Map<>();
    private Map<Terminal, Integer> ubicacionesTerminales = new Map<>();

    private Grammar grammar;


    private final int ID = 295;

    public LL1ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        crearTabla();
        llenarTabla();
    }

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

    private void agregarUbicacion(NoTerminal nt, Terminal t, int valor) {
        int fila = ubicacionesNoTerminales.get(nt);
        int columna = ubicacionesTerminales.get(t);



        matriz[fila][columna] = valor;
    }

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

    // No pregunten, este metodo si se lo hecho chatgpt
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
