package parser.analysis;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
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

    }

    private boolean procesarProduccionFirst (Production p) {
        boolean cambio = false;
        Lista<Symbol> rhs = p.getDerecha();

        for (Symbol s : rhs) {
            if (s instanceof Terminal) {
                cambio |= first.get(p.getIzquierda()).agregar((Terminal) s);
                break;
            }
        }
    }

    public void calcularFollow() {

    }



}
