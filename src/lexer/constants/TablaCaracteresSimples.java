package lexer.constants;

import java.util.HashMap;
import java.util.Map;

import data_structures.Set;

/**
 * TablaCaracteresSimples
 *
 * Tabla lexica que define los caracteres y operadores simples
 * reconocidos por el analizador lexico
 *
 * Contiene simbolos que pueden ser identificados sin contexto
 * adicional (ej. delimitadores y operadores basicos)
 *
 * Esta clase centraliza la definicion de simbolos del lenguaje,
 * evitando valores harcodeados dentro del lexer
 */
public class TablaCaracteresSimples{
    /**Conjunto de simbolos simples reconocidos por el lenguaje*/
    //final static Set<Elemento> CARACTERES_SIMPLES = new Set<>();
    final static Map<String, Integer> CARACTERES_SIMPLES = new HashMap<>();

    static {
        CARACTERES_SIMPLES.put(";", (int) ';');
        CARACTERES_SIMPLES.put("=", (int) '=');
        CARACTERES_SIMPLES.put("+", (int) '+');
        CARACTERES_SIMPLES.put("-", (int) '-');
        CARACTERES_SIMPLES.put("*", (int) '*');
        CARACTERES_SIMPLES.put("/", (int) '/');
        CARACTERES_SIMPLES.put("(", (int) '(');
        CARACTERES_SIMPLES.put(")", (int) ')');
        CARACTERES_SIMPLES.put(",", (int) ',');
        CARACTERES_SIMPLES.put("<", (int) '<');
        CARACTERES_SIMPLES.put(">", (int) '>');
        CARACTERES_SIMPLES.put("==", (int) '=');
        CARACTERES_SIMPLES.put("<>", (int) '<');
    }
    private TablaCaracteresSimples() {}

    /**
     * Verifica si una cadena corresponde a un simbolo simple
     * definido en la tabla lexica
     *
     * @param s lexema a verificar
     * @return true si el simbolo existe
     */
    public static boolean existe(String s) {
        return CARACTERES_SIMPLES.containsKey(s);
    }

}
