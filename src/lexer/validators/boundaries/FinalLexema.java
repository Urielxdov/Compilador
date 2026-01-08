package lexer.validators.boundaries;

import data_structures.Set;

/**
 * Identificador de limites lexico en carcteres de espacio en blanco
 *
 * Esta implementacion define como final de lexema cualquier caracter
 * considerado whitespace, permitiendo separar tokens correctamente
 * durante el analisis lexico
 */
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

    /**
     * Cerifica si el carcter recibido marca el fin de un lexema
     *
     * @param c carcter a evaluar
     * @return true si el carcter es considerado un limite lexico,
     *          false en caso contrario
     */
    @Override
    public boolean verificar(char c) {
        return ESPACIOS_BLANCOS.contains(c);
    }
}
