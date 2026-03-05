package parser.grammar;

import data_structures.Lista;

public class Grammar {
    // Gramatica completa
    private Lista<Terminal> terminales;
    private Lista<NoTerminal> noTerminales;
    private Lista<Production> producciones;

    private NoTerminal simboloInicial;


    public Grammar() {
        terminales = new Lista<>();
        noTerminales = new Lista<>();
        producciones = new Lista<>();

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

    public Production getProduccion(int numeroProduccion) {
        for (Production p : producciones) {
            if (p.getId() == numeroProduccion) return p;
        }
        return null;
    }

    public NoTerminal getSimboloInicial() {
        return simboloInicial;
    }


    public Lista<NoTerminal> getNoTerminales () { return noTerminales; }

}
