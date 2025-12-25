package parser.ll1;

import data_structures.Map;
import parser.grammar.NoTerminal;
import parser.grammar.Production;
import parser.grammar.Terminal;

public class LL1ParsingTable {
    private Map<NoTerminal, Map<Terminal, Production>> tabla;
}
