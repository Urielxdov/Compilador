package parser.grammar;

import java.util.ArrayList;
import java.util.List;

public class Grammar {
    // Gramatica completa
    private List<Terminal> terminales;
    private List<NoTerminal> noTerminales;
    private List<Production> producciones;

    private NoTerminal simboloInicial;


    public Grammar() {
        terminales = new ArrayList<>();
        noTerminales = new ArrayList<>();
        producciones = new ArrayList<>();

    }


    public void setSimboloInicial(NoTerminal s) {
        simboloInicial = s;
    }

    public void agregarNoTerminal(NoTerminal s) {
        if (this.noTerminales.contains(s)) return;
        this.noTerminales.add(s);
    }


    public void agregarProduccion(Production p) {
        this.producciones.add(p);
    }

    public List<Terminal> getTerminales() {
        return terminales;
    }


    public boolean terminalExiste(Terminal t) {
        return terminales.contains(t);
    }

    public void agregarTerminal(Terminal t) {
        if (t == null) return;
        if (terminalExiste(t)) return;
        terminales.add(t);
    }

    public List<Production> getProducciones() {
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


    public List<NoTerminal> getNoTerminales () { return noTerminales; }

}
