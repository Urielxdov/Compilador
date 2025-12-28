package parser.analysis;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import data_structures.Set;
import parser.grammar.*;

public class GrammarAnalysis {
    private Grammar grammar;



    private final String VACIO = "ε";

    public GrammarAnalysis(Grammar grammar) {
        this.grammar = grammar;
    }


    public void calcularFirst() {
        Lista<Production> producciones = grammar.getProducciones();
        boolean cambios;
        // Inicializamos los campos vacios
        for(NoTerminal nt : grammar.getNoTerminales()) {
            grammar.getFirst().put(nt, new Conjunto<>());
        }

        do {
            // cambios globales
            cambios = false;

            for (Production p : producciones) {
                boolean cambio = procesarProduccionFirst(p);
                cambios |= cambio;
            }
        } while (cambios);
    }

    private boolean procesarProduccionFirst (Production p) {
        boolean cambio = false;
        Lista<Symbol> rhs = p.getDerecha();
        NoTerminal noTerminal = p.getIzquierda();

        for (Symbol s : rhs) {
            if (s instanceof Terminal) {
                cambio |= grammar.getFirst().get(p.getIzquierda()).agregar(s.equals(Epsilon.getInstance()) ? Epsilon.getInstance() : (Terminal) s);
                break;
            } else {
                Conjunto<Terminal> fistS = grammar.getFirst().get((NoTerminal) s);

                cambio |= grammar.getFirst().get(noTerminal).agregar(fistS.obtenerSinVacio());

                if (!fistS.contiene(new Epsilon())) break;
            }
        }
        return cambio;
    }

    public void calcularFollow() {
        Lista<Production> productions = grammar.getProducciones();

        for (Production p : productions) {
            grammar.getFollow().put(p.getIzquierda(), new Conjunto<>());
        }

        grammar.getFollow().get(grammar.getSimboloInicial()).agregar(new Terminal("$"));
        boolean cambios;

        do {
            cambios = false;

            for (Production p : productions) {
                boolean cambio = procesarProduccionFollow(p);
                cambios |= cambio;
            }
        }while (cambios);

    }


    public boolean procesarProduccionFollow (Production p) {
        boolean cambio = false;
        Lista<Symbol> rhs = p.getDerecha();
        NoTerminal lhs = p.getIzquierda();

        for (int i = 0; i < rhs.nodosExistentes(); i++) {
            Symbol s = rhs.obtener(i);

            if (!(s instanceof NoTerminal)) continue; // Los no terminales no importan

            NoTerminal ntActual = (NoTerminal) s;

            if (i + 1 < rhs.nodosExistentes()) {
                Symbol siguiente = rhs.obtener(i + 1);

                if (siguiente instanceof Terminal) {
                    // caso 1 A -> aB
                    cambio |= grammar.getFollow().get(ntActual).agregar((Terminal) siguiente);
                } else {
                    NoTerminal ntSiguiente = (NoTerminal) siguiente;

                    cambio |= grammar.getFollow().get(ntActual).agregar(grammar.getFirst().get(ntSiguiente).obtenerSinVacio());

                    // Si First(b) contiene vacio, agregamos Follow(A)
                    if (grammar.getFirst().get(ntSiguiente).contiene(Epsilon.getInstance())) {
                        cambio |= grammar.getFollow().get(ntActual).agregar(grammar.getFollow().get(lhs));
                    }
                }
            } else {
                cambio |= grammar.getFollow().get(ntActual).agregar(grammar.getFollow().get(lhs));
            }
        }
        return cambio;
    }

}
