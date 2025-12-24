package parser.grammar;

import data_structures.Lista;
import data_structures.Map;
import data_structures.Set;
import io.FileReaderManager;
import io.RutaArchivos;

public class Grammar {
    // Gramatica completa
    private Lista<Terminal> terminales;
    private Lista<NoTerminal> noTerminales;
    private Lista<Production> producciones;

    private NoTerminal simboloInicial;

    // Gramatica separada por conjuntos
    private Map<NoTerminal, Terminal> conjuntoFirst;
    private Map<NoTerminal, Lista<Terminal>> conjuntoFollow;

    public Grammar() {
        terminales = new Lista<>();
        noTerminales = new Lista<>();
        producciones = new Lista<>();

        conjuntoFirst = new Map<>();
        conjuntoFollow = new Map<>();
    }


    private boolean isSimpleCharacter(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }



    private void inicializar() {

    }

    public void obtenerTodo () {
        FileReaderManager lector = new FileReaderManager();
        Lista<String> gramatica = lector.leerArchivo(RutaArchivos.GRAMATICA);

        for (int i = 0; i < gramatica.nodosExistentes(); i++) {
            String linea = gramatica.obtener(i);
            NoTerminal lhs = obtenerLHS(linea);
            int inicioRHS = inicioProduccion(linea);

            if (lhs != null && inicioRHS != -1) {
                Lista<Symbol> rhs = obtenerRHS(linea, inicioRHS);
                producciones.agregar(new Production(lhs, rhs));
            }
        }
        definirPrincipal();
    }

    private Lista<Symbol> obtenerRHS (String linea, int inicio) {
        Lista<Symbol> rhs = new Lista<>();

        int fin = inicio;

        while (fin < linea.length()) {

            if (linea.charAt(fin) == ' ') {
                fin ++;
                inicio = fin;
                continue;
            }


            if (linea.charAt(fin) == '<') {
                inicio = fin;
                while (fin < linea.length() && linea.charAt(fin) != '>') {
                    fin++;
                }
                rhs.agregar(new NoTerminal(linea.substring(inicio, fin + 1)));
                fin++;
                inicio = fin;
            } else if (isSimpleCharacter(linea.charAt(fin))) {
                rhs.agregar(new Terminal(String.valueOf(linea.charAt(fin))));
                fin++;
                inicio = fin;
            } else {
                inicio = fin;
                while (fin < linea.length() && linea.charAt(fin) != ' ' && !isSimpleCharacter(linea.charAt(fin))) {
                    fin++;
                }
                rhs.agregar(new Terminal(linea.substring(inicio, fin)));
                inicio = fin;
            }
        }
        return rhs;
    }

    private int inicioProduccion(String linea) {
        for (int i = 0; i < linea.length() - 1; i++) {
            if (linea.charAt(i) == '-' && linea.charAt(i + 1) == '>') {
                return i + 2;
            }
        }
        return -1;
    }

    private void definirPrincipal() {
        Set<NoTerminal> lhs = new Set<>();
        Set<NoTerminal> rhs = new Set<>();

        for (int i = 0; i < producciones.nodosExistentes(); i++) {
            lhs.add(producciones.obtener(i).getIzquierda());
            for (int j = 0; j < producciones.obtener(i).getDerecha().nodosExistentes(); j++) {
                Symbol symbol = producciones.obtener(i).getDerecha().obtener(j);
                if (symbol instanceof NoTerminal) {
                    rhs.add((NoTerminal) symbol);
                }
            }
        }

        lhs.removerTodo(rhs);

        System.out.println(lhs.size());
        if (lhs.size() == 1) {
            System.out.println(lhs);
        } else {
            System.out.println("No la armas");
        }
    }

    private NoTerminal obtenerLHS (String linea) {
        int inicio = -1;
        int fin = -1;
        // Los no terrminales se encuentran encerrados por <>
        for (int j = 0; j < linea.length(); j++) {
            if (linea.charAt(j) == '<') inicio = j;
            else if (linea.charAt(j) == '>') {
                fin = j;
                break;
            }
        }

        if (inicio != -1 && fin != -1) {
            NoTerminal noTerminal = new NoTerminal(linea.substring(inicio, fin + 1));

            if (!noTerminales.existe(noTerminal)) {
                noTerminales.agregar(noTerminal);
            }
            return noTerminal;
        }
        return null;
    }
}
