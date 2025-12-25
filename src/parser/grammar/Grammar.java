package parser.grammar;

import data_structures.Lista;
import data_structures.Map;

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


    public void setSimboloInicial(NoTerminal s) {
        simboloInicial = s;
    }

    public void agregarNoTerminal(NoTerminal s) {
        if (this.noTerminales.existe(s)) return;
        this.noTerminales.agregar(s);
    }


    public void agregarProduccion(Production p) {
        this.producciones.agregar(p);
    }

    public Lista<Terminal> getTerminales() {
        return terminales;
    }


    public boolean terminalExiste(Terminal t) {
        return terminales.existe(t);
    }

    public void agregarTerminal(Terminal t) {
        if (t == null) return;
        if (terminalExiste(t)) return;
        terminales.agregar(t);
    }

    public Lista<Production> getProducciones() {
        return producciones;
    }



}
