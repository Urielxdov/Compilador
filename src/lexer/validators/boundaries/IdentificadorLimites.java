package lexer.validators.boundaries;

/**
 * Define un criterio para identificar limites lexicos
 * durane el analisis de tokens
 *
 * Las implementaciones determinan si un caracter marca el fin de un lexema
 */
public interface IdentificadorLimites {
    boolean verificar(char c);
}
