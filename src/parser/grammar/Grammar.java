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


    /**
     * Calcula FIRST(α) para una secuencia de simbolos α = X1, X2 ... Xn.
     *
     * Reglas:
     * - Si X1 es terminal de ε → FIRST(α) = {X1}
     * - Si X1 es no terminal:
     *  - Se agrega FIRST(X1) \ {ε}
     *  - Si ε pertenece a FIRST(X1), se continua con X2
     *  - Si todos los Xi pueden producir ε -> ε  perteneciente a FIRST(α)
     *
     * Precondicion:
     * - FIRST(nt) debe estar previamente calculado para todo no terminal
     *
     * @param alfa
     * @return
     */
    public Conjunto<Terminal> firstDeSecuencia (Lista<Symbol> alfa) {
        Conjunto<Terminal> conjunto = new Conjunto<>();
        for (Symbol s : alfa) {
            if (s instanceof Terminal) {
                conjunto.agregar((Terminal) s);
                return conjunto;
            }

            NoTerminal nt = (NoTerminal) s;
            Conjunto<Terminal> firstNt = first.get(nt);

            conjunto.agregar(firstNt.obtenerSinVacio());

            if (!firstNt.contiene(Epsilon.getInstance())) {
                return conjunto;
            }
        }

        conjunto.agregar(Epsilon.getInstance());
        return conjunto;
    }
}
