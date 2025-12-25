package parser.analysis;

import data_structures.Lista;
import data_structures.Map;
import parser.grammar.*;

public class GrammarAnalysis {
    private Grammar grammar;

    private Map<NoTerminal, Lista<Terminal>> first;
    private Map<NoTerminal, Lista<Terminal>> follow;

    private final String VACIO = "ε";

    public GrammarAnalysis(Grammar grammar) {
        this.grammar = grammar;
        this.first = new Map<>();
        this.follow = new Map<>();
    }


    public void calcularFirst() {
        Lista<Production> producciones = grammar.getProducciones();
        for(Production p : producciones) {
            Lista<Symbol> rhs = p.getDerecha();
            if (rhs.iterator().next() instanceof Terminal) {
                // Abarca primer y segundo caso dado que ε es terminal
                Lista<Terminal> pivot = new Lista<>();
                pivot.agregar((Terminal) rhs.iterator().next());
                first.put(p.getIzquierda(), pivot);
            } else {
                // No hay terminales

            }
        }
    }

    public void calcularFollow() {

    }



}
