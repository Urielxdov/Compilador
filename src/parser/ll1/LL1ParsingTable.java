package parser.ll1;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import parser.grammar.*;

public class LL1ParsingTable {

    private int[][] matriz;

    private Map<NoTerminal, Integer> ubicacionesNoTerminales = new Map<>();
    private Map<Terminal, Integer> ubicacionesTerminales = new Map<>();

    private Grammar grammar;

    public LL1ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        crearTabla();
        llenarTabla();
    }

    private void crearTabla() {
        int altura = grammar.getNoTerminales().nodosExistentes();
        int anchura = grammar.getTerminales().nodosExistentes();

        for (int i = 0; i < altura; i++) {
            ubicacionesNoTerminales.put(
                    grammar.getNoTerminales().obtener(i), i
            );
        }

        for (int i = 0; i < anchura; i++) {
            ubicacionesTerminales.put(
                    grammar.getTerminales().obtener(i), i
            );
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

    // No pregunten, este metodo si se lo hecho chatgpt
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append(String.format("%-20s", ""));
        for (int j = 0; j < grammar.getTerminales().nodosExistentes(); j++) {
            sb.append(String.format(
                    "%-15s",
                    grammar.getTerminales().obtener(j)
            ));
        }
        sb.append("\n");

        // Filas
        for (int i = 0; i < grammar.getNoTerminales().nodosExistentes(); i++) {
            NoTerminal nt = grammar.getNoTerminales().obtener(i);
            sb.append(String.format("%-20s", nt));

            for (int j = 0; j < grammar.getTerminales().nodosExistentes(); j++) {
                int valor = matriz[i][j];
                if (valor == 0) {
                    sb.append(String.format("%-15s", "—"));
                } else {
                    sb.append(String.format("%-15d", valor));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
