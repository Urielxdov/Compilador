package parser.analysis;

import data_structures.Conjunto;
import data_structures.Lista;
import data_structures.Map;
import data_structures.Set;
import parser.grammar.*;

/**
 * Implementa el calculo de los conjuntos FIRT y FOLLOW
 * para una gramatica libre de contexto
 *
 * Utiliza algoritmos iterativos hasta alcanzar punto fijo,
 * siguiendo las reglas formales para el analisis LL(1)
 *
 * Esta clase no valida si la gramatica es LL(1)
 * unicamente calcula los conjuntos necesarios
 */
public class GrammarAnalysis {
    private Grammar grammar;



    private final String VACIO = "ε";

    public GrammarAnalysis(Grammar grammar) {
        this.grammar = grammar;
    }


    /**
     * Calcula el conjunto FIRST para todos los no terminales
     * de la gramatica mediante iteraciones sucesivas hasta
     * que no existan cambios
     */
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

    /**
     * Procesa una produccion para actualizar el conjunto FIRST
     * del no terminal izquierdo
     *
     * Aplica las reglas:
     * - Si el simbolo es terminal se agrega directamente
     * - Si es no terminal, se propaha FIRST sin epsilon
     * - Si FIRST conteiene epsilon, continua evaluando
     *
     * @return true si hubo cambios en FIRST, flase en caso contrario
     */
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

    /**
     * Calcula el conjunto FOLLOW para todos los no terminales
     * de la gramatica
     *
     * El simbolo inicial recibe el terminal '$'
     * El calculo se realiza de forma iterativa hasta punto fijo
     */
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

    /**
     * Procesa una producción para actualizar los conjuntos FOLLOW
     * según las reglas formales:
     *
     * 1) A → α B a     → a ∈ FOLLOW(B)
     * 2) A → α B C     → FIRST(C) - ε ⊆ FOLLOW(B)
     * 3) Si FIRST(C) contiene ε o B es el último símbolo,
     *    FOLLOW(A) ⊆ FOLLOW(B)
     *
     * @return true si hubo cambios en FOLLOW, false en caso contrario
     */
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
                    //Posibles cambios
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
