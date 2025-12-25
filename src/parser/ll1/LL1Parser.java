package parser.ll1;

import data_structures.Lista;
import parser.grammar.Grammar;
import parser.grammar.Terminal;

public class LL1Parser {
    private Grammar grammar;
    private LL1ParsingTable tabla;

    public LL1Parser(Grammar grammar, LL1ParsingTable tabla) {
        this.grammar = grammar;
        this.tabla = tabla;
    }


    public void parse(Lista<Terminal> input) {

    }
}
