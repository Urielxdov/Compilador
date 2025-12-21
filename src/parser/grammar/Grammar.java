package parser.grammar;

import data_structures.Lista;
import data_structures.Map;
import io.FileReaderManager;
import io.RutaArchivos;

public class Grammar {
    // Gramatica completa
    private Lista<Terminal> terminales;
    private Lista<NoTerminal> noTerminales;
    private Map<NoTerminal, Production> producciones;

    // Gramatica separada por conjuntos
    private Map<NoTerminal, Terminal> conjuntoFirst;
    private Map<NoTerminal, Lista<Terminal>> conjuntoFollow;

    public Grammar() {
        terminales = new Lista<>();
        noTerminales = new Lista<>();
        producciones = new Map<>();

        conjuntoFirst = new Map<>();
        conjuntoFollow = new Map<>();
    }



    private void inicializar() {

    }

    private void obtenerNoTerminales() {
        FileReaderManager lector = new FileReaderManager();
        Lista<String> gramatica = lector.leerArchivo(RutaArchivos.GRAMATICA);

        for (int i = 0; i < gramatica.nodosExistentes(); i++) {
            String linea = gramatica.obtener(i);

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
            }
        }
    }
}
