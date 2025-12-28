package parser.grammar;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;

public class Grammar {
    // Gramatica completa
    private Lista<Terminal> terminales;
    private Lista<NoTerminal> noTerminales;
    private Lista<Production> producciones;

    private NoTerminal simboloInicial;

    // Gramatica separada por conjuntos
    private Map<NoTerminal, Conjunto<Terminal>> first;
    private Map<NoTerminal, Conjunto<Terminal>> follow;

    public Grammar() {
        terminales = new Lista<>();
        noTerminales = new Lista<>();
        producciones = new Lista<>();

        first = new Map<>();
        follow = new Map<>();
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

    public NoTerminal getSimboloInicial() {
        return simboloInicial;
    }


    public Lista<NoTerminal> getNoTerminales () { return noTerminales; }


    public void agregarFirst(NoTerminal nt, Conjunto<Terminal> conjunto) {
        first.put(nt, conjunto);
    }


    public void agregarFollow(NoTerminal nt, Conjunto<Terminal> conjunto) {
        follow.put(nt, conjunto);
    }

    public Map<NoTerminal, Conjunto<Terminal>> getFirst () {
        return first;
    }

    public Map<NoTerminal, Conjunto<Terminal>> getFollow() {
        return follow;
    }
    
    
    public Conjunto<Terminal> firstDeSecuencia (Lista<Symbol> alfa) {
        Conjunto<Terminal> conjunto = new Conjunto<>();
        for (Symbol s : alfa) {
            if (s instanceof Terminal) conjunto.agregar((Terminal) s);
            else if (s instanceof NoTerminal) conjunto.agregar(first.get((NoTerminal) s));
        }
        return conjunto;
    }
}
