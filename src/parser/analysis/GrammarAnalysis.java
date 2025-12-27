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

    }



}
