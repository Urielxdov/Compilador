package parser.ll1;

import data_structures.Map;
import parser.grammar.Grammar;
import parser.grammar.NoTerminal;
import parser.grammar.Production;
import parser.grammar.Terminal;

public class LL1ParsingTable {
    private int[][] matriz;

    private Map<NoTerminal, Integer> ubicacionesNoTerminales = new Map<>();
    private Map<Terminal, Integer> ubicacionesTerminales = new Map<>();

    private Grammar grammar;

    public LL1ParsingTable(Grammar grammar) {
        this.grammar = grammar;

        crearTabla();
    }

    public void crearTabla() {
        int altura = grammar.getNoTerminales().nodosExistentes();
        int anchura = grammar.getTerminales().nodosExistentes();

        for (int i = 0; i < altura; i++) {
            ubicacionesNoTerminales.put(grammar.getNoTerminales().obtener(i), i);
        }

        for (int i = 0; i < anchura; i++) {
            ubicacionesTerminales.put(grammar.getTerminales().obtener(i), i);
        }

        this.matriz = new int[anchura][altura];
    }



}
