package semantic;

import data_structures.Map;
import lexer.constants.TiposTokens;

public class SymbolTable {
    private Map<String, TiposTokens> table = new Map<>();

    public void declare(String id, TiposTokens tipo) {
        if (table.existKey(id))
            throw new SemanticException("Variable ya declarada");
        table.put(id, tipo);
    }

    public TiposTokens get (String id) {
        if (!table.existKey(id))
            throw new SemanticException("Variable no declarada");
        return table.get(id);
    }
}
