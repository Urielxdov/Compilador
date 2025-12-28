package parser.analysis;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import data_structures.Set;
import parser.grammar.*;

public class GrammarAnalysis {
    private Grammar grammar;

    private Map<NoTerminal, Conjunto<Terminal>> first;
    private Map<NoTerminal, Conjunto<Terminal>> follow;

    private final String VACIO = "ε";

    public GrammarAnalysis(Grammar grammar) {
        this.grammar = grammar;
        this.first = new Map<>();
        this.follow = new Map<>();
    }


    public void calcularFirst() {
        Lista<Production> producciones = grammar.getProducciones();
        boolean cambios;
        // Inicializamos los campos vacios
        for(NoTerminal nt : grammar.getNoTerminales()) {
            first.put(nt, new Conjunto<>());
        }

        do {
            // cambios globales
            cambios = false;

            for (Production p : producciones) {
                boolean cambio = procesarProduccionFirst(p);
                cambios |= cambio;
            }
        } while (cambios);
        System.out.println(first);
    }

    private boolean procesarProduccionFirst (Production p) {
        boolean cambio = false;
        Lista<Symbol> rhs = p.getDerecha();
        NoTerminal noTerminal = p.getIzquierda();

        for (Symbol s : rhs) {
            if (s instanceof Terminal) {
                cambio |= first.get(p.getIzquierda()).agregar((Terminal) s);
                break;
            } else {
                Conjunto<Terminal> fistS = first.get((NoTerminal) s);
                cambio |= first.get(noTerminal).agregar(fistS.obtenerSinVacio());

                if (!fistS.contiene(new Epsilon())) break;
            }
        }
        return cambio;
    }

    public void calcularFollow() {
        Lista<Production> productions = grammar.getProducciones();

        for (Production p : productions) {
            follow.put(p.getIzquierda(), new Conjunto<>());
        }

        follow.get(grammar.getSimboloInicial()).agregar(new Terminal("$"));
        boolean cambios;

        do {
            cambios = false;

            for (Production p : productions) {
                boolean cambio = procesarProduccionFollow(p);
                cambios |= cambio;
            }
        }while (cambios);

        System.out.println(follow);
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
                    cambio |= follow.get(ntActual).agregar((Terminal) siguiente);
                } else {
                    NoTerminal ntSiguiente = (NoTerminal) siguiente;

                    cambio |= follow.get(ntActual).agregar(first.get(ntSiguiente).obtenerSinVacio());

                    // Si First(b) contiene vacio, agregamos Follow(A)
                    if (first.get(ntSiguiente).contiene(new Epsilon())) {
                        cambio |= follow.get(ntSiguiente).agregar(follow.get(lhs));
                    }
                }
            }
        }
        return cambio;
    }

}
