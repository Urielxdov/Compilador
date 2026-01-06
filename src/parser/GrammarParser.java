package parser;

import data_structures.Lista;
import data_structures.Set;
import parser.grammar.*;
import parser.reader.GrammarReader;

public class GrammarParser {
    private Grammar grammar;
    private GrammarReader grammarReader;


    public GrammarParser(Grammar grammar, GrammarReader grammarReader) {
        this.grammar = grammar;
        this.grammarReader = grammarReader;
    }

    private boolean isSimpleCharacter(char c) {
        return (c == 59) || (c == 61) || (c == 43) || (c == 45)
                || (c == 42) || (c == 40) || (c == 41) || (c == 44)
                || (c == 60) || (c == 62);
    }

    private boolean isEpsilon (char c) {
        return c == 'ε';
    }


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
