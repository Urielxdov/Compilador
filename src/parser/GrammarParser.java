package parser;

import java.util.ArrayList;
import java.util.List;

import data_structures.Set;
import lexer.constants.TablaCaracteresSimples;
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
        List<String> gramatica = grammarReader.leerGramatica();

        int contador = 1;
        for (String s : gramatica) {
            NoTerminal lhs = obtenerLHS(s);
            grammar.agregarProduccion(new Production(lhs, new ArrayList<Symbol>(), contador++));

        }
        contador = 1;
        for (String s : gramatica) {
            NoTerminal lhs = obtenerLHS(s);
            int inicioRHS = inicioProduccion(s);

            if (lhs != null && inicioRHS != -1) {
                List<Symbol> rhs = obtenerRHS(s, inicioRHS);

                Production p = grammar.getProduccion(contador);

                for (Symbol symbol : rhs) {
                    p.getDerecha().add(symbol);
                }

                for (Symbol sym : rhs) {
                    if (sym instanceof  Terminal && !grammar.terminalExiste((Terminal) sym)) {
                        grammar.agregarTerminal((Terminal) sym);
                    }
                }
            }
            contador++;
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
    private List<Symbol> obtenerRHS (String linea, int inicio) {
        List<Symbol> rhs = new ArrayList<>();

        int fin = inicio;

        while (fin < linea.length()) {

            if (linea.charAt(fin) == ' ') {
                fin++;
                inicio = fin;
                continue;
            }

            if (isEpsilon(linea.charAt(fin))) {
                rhs.add(new Epsilon());
                fin++;
                inicio = fin;
                continue;
            } else if (TablaCaracteresSimples.existe(String.valueOf(linea.charAt(fin)))) {
                    char actual = linea.charAt(fin);

                    // Mira si existe un segundo carácter
                    if (fin + 1 < linea.length()) {
                        char siguiente = linea.charAt(fin + 1);
                        String doble = "" + actual + siguiente;

                        if (doble.equals("==") || doble.equals("<>")) {
                            rhs.add(new Terminal(doble));
                            fin += 2;
                            inicio = fin;
                            continue;
                        }
                    }

                    // Si no es doble, es simple: ')', ';', etc.
                    rhs.add(new Terminal(String.valueOf(actual)));
                    fin++;
                    inicio = fin;
                    continue;
            }

            inicio = fin;
            while (fin < linea.length() && linea.charAt(fin) != ' ' && !TablaCaracteresSimples.existe(String.valueOf(linea.charAt(fin)))) fin++;

            String palabra = linea.substring(inicio, fin);
            boolean encontrado = false;

            for (NoTerminal nt : grammar.getNoTerminales()) {
                if (nt.equals(palabra)) {
                    rhs.add(new NoTerminal(palabra));
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                rhs.add(new Terminal(palabra));
            }

            inicio = fin;
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

        for (int i = 0; i < grammar.getProducciones().size(); i++) {
            lhs.add(grammar.getProducciones().get(i).getIzquierda());
            for (int j = 0; j < grammar.getProducciones().get(i).getDerecha().size(); j++) {
                Symbol symbol = grammar.getProducciones().get(i).getDerecha().get(j);
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
    private NoTerminal obtenerLHS(String linea) {
        int inicio = -1;
        int fin = -1;

        for (int j = 0; j < linea.length(); j++) {
            if (!Character.isWhitespace(linea.charAt(j))) {
                inicio = j;
                break;
            }
        }

        if (inicio != -1) {
            fin = inicio;
            while (fin < linea.length() && !Character.isWhitespace(linea.charAt(fin))) {
                fin++;
            }

            NoTerminal noTerminal = new NoTerminal(linea.substring(inicio, fin));
            grammar.agregarNoTerminal(noTerminal);
            return noTerminal;
        }

        return null;
    }


}
