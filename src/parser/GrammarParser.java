package parser;

import data_structures.Lista;
import data_structures.Set;
import parser.grammar.*;
import parser.reader.GrammarReader;

/**
 * Analizador de gramaticas
 *
 * Esta clase se encarga de:
 *  - Leer una gramatica desde un archvio de texto
 *  - Parsear producciones en formato BNF simplificado
 *  - Construir la estructura {@link Grammar}
 *  - Detectar automaticamente el simbolo inicial
 *
 *
 * No valia propiedades LL(1); unicamente contruye la gramatica
 */
public class GrammarParser {
    private Grammar grammar; // Gramatica que se contruye
    private GrammarReader grammarReader; // Lector de la gramatica desde archivo


    /**
     * Crea un parser de gramática.
     *
     * @param grammar estructura de gramática a construir
     * @param grammarReader lector del archivo de gramática
     */
    public GrammarParser(Grammar grammar, GrammarReader grammarReader) {
        this.grammar = grammar;
        this.grammarReader = grammarReader;
    }

    /**
     * Determina si un carácter corresponde a un terminal simple
     * definido por el lenguaje.
     */
    private boolean isSimpleCharacter(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }

    /**
     * Verifica si el carácter representa la producción vacía (ε).
     */
    private boolean isEpsilon (char c) {
        return c == 'ε';
    }


    /**
     * Ejecuta el análisis de la gramática.
     *
     * Lee cada línea del archivo de gramática, extrae producciones,
     * registra terminales y no terminales, y finalmente determina
     * el símbolo inicial.
     */
    public void ejecutar() {
        Lista<String> gramatica = grammarReader.leerGramatica();

        for (int i = 0; i < gramatica.nodosExistentes(); i++) {
            String linea = gramatica.obtener(i);
            NoTerminal lhs = obtenerLHS(linea);
            int inicioRHS = inicioProduccion(linea);

            if (lhs != null && inicioRHS != -1) {
                Lista<Symbol> rhs = obtenerRHS(linea, inicioRHS);
                grammar.agregarProduccion(new Production(lhs, rhs, i+1));
                for (Symbol s : rhs) {
                    if (s instanceof Terminal && !grammar.terminalExiste((Terminal) s)) {
                        grammar.agregarTerminal((Terminal) s);
                    }
                }
            }
        }
        definirPrincipal();

    }


    /**
     * Extrae la parte derecha de una producción gramatical.
     *
     * @param linea línea completa de la gramática
     * @param inicio índice donde inicia el RHS
     * @return lista de símbolos que conforman la producción
     */
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
                if (fin+1 < linea.length() && linea.charAt(fin + 1) != ' ' && linea.charAt(fin + 1) != '>') {
                    inicio = fin;
                    while (fin < linea.length() && linea.charAt(fin) != '>') {
                        fin++;
                    }
                    rhs.agregar(new NoTerminal(linea.substring(inicio, fin + 1)));
                } else {
                    rhs.agregar(new Terminal(linea.substring(inicio, fin + 1)));
                }
                fin++;
                inicio = fin;
            } else if (isEpsilon(linea.charAt(fin))) {
                rhs.agregar(new Epsilon());
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

    /**
     * Localiza el inicio del lado derecho de una producción.
     *
     * @return índice posterior a '->' o -1 si no existe
     */
    private int inicioProduccion(String linea) {
        for (int i = 0; i < linea.length() - 1; i++) {
            if (linea.charAt(i) == '-' && linea.charAt(i + 1) == '>') {
                return i + 2;
            }
        }
        return -1;
    }


    /**
     * Determina el símbolo inicial de la gramática.
     *
     * El símbolo inicial es aquel no terminal que aparece en el LHS
     * pero nunca en ningún RHS.
     */
    private void definirPrincipal() {
        Set<NoTerminal> lhs = new Set<>();
        Set<NoTerminal> rhs = new Set<>();

        for (int i = 0; i < grammar.getProducciones().nodosExistentes(); i++) {
            lhs.add(grammar.getProducciones().obtener(i).getIzquierda());
            for (int j = 0; j < grammar.getProducciones().obtener(i).getDerecha().nodosExistentes(); j++) {
                Symbol symbol = grammar.getProducciones().obtener(i).getDerecha().obtener(j);
                if (symbol instanceof NoTerminal) {
                    rhs.add((NoTerminal) symbol);
                }
            }
        }

        lhs.removerTodo(rhs);

        if (lhs.size() == 1) {
            grammar.setSimboloInicial(lhs.iterator().next());
        } else {
            System.out.println("No hay simbolo inicial");
        }
    }

    /**
     * Obtiene el no terminal del lado izquierdo de una producción.
     *
     * @param linea línea de la gramática
     * @return no terminal encontrado o null si no existe
     */
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
            grammar.agregarNoTerminal(noTerminal);
            return noTerminal;
        }
        return null;
    }

}
