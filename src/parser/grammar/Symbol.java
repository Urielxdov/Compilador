package parser.grammar;

/**
 * Representa un simbolo de una gramatica libre de contexto
 *
 * Un simbolo puede ser un {@link Terminal} o un {@link NoTerminal},
 * y constituye los elementos que conforman las producciones
 * de la gramatica
 */
public interface Symbol {
    String getNombre();
}
