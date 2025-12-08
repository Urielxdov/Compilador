package lexer.validators.boundaries;

import data_structures.Lista;
import data_structures.Set;
import lexer.Context;

public class FinalLexema implements IdentificadorLimites{
    private static final Set<Character> ESPACIOS_BLANCOS = new Set<>();
    public FinalLexema() {
        ESPACIOS_BLANCOS.add(' '); // Espacio
        ESPACIOS_BLANCOS.add('\t'); // tabulador
        ESPACIOS_BLANCOS.add('\n'); // nueva linea
        ESPACIOS_BLANCOS.add('\r'); // retorno de carro
        ESPACIOS_BLANCOS.add('\f'); // salto de pagina
        ESPACIOS_BLANCOS.add('\u000B'); // tabulador vertical
    }
    @Override
    public boolean verificar(char c) {
        return ESPACIOS_BLANCOS.contains(c);
    }
}
