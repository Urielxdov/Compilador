package lexer.validators.pointer;

import lexer.Context;

/**
 * Define un contrato para componentes que ajustan los punteros
 * del contexto lexico antes o despues del reconocimiento de tokens
 *
 * Las implementaciones pueden modificar el puntero inicial y/o final,
 * pero no pueden consumir caracteres que pertenezcan al lenguaje
 */
public interface AjustadorPuntero {
    /**
     * Aplica el ajuste de punteros sobre el contexto lexico actual
     *
     * @param ctx contexto del analisis lexico a modificar
     */
    void aplicar(Context ctx);

}
