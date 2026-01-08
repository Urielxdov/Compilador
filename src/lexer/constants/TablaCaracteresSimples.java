package lexer.constants;

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
    final static Set<Elemento> CARACTERES_SIMPLES = new Set<>();

    static {
        CARACTERES_SIMPLES.add(new Elemento(";", ';'));
        CARACTERES_SIMPLES.add(new Elemento("=", '='));
        CARACTERES_SIMPLES.add(new Elemento("+", '+'));
        CARACTERES_SIMPLES.add(new Elemento("-", '-'));
        CARACTERES_SIMPLES.add(new Elemento("*", '*'));
        CARACTERES_SIMPLES.add(new Elemento("(", '('));
        CARACTERES_SIMPLES.add(new Elemento(")", ')'));
        CARACTERES_SIMPLES.add(new Elemento(",", ','));
        CARACTERES_SIMPLES.add(new Elemento("<", '<'));
        CARACTERES_SIMPLES.add(new Elemento(">", '>'));
        CARACTERES_SIMPLES.add(new Elemento("==", '='));
        CARACTERES_SIMPLES.add(new Elemento("<>", '<'));
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
        for (Elemento elemento : CARACTERES_SIMPLES)
            if (elemento.clave.equals(s)) return true;
        return false;
    }

    /**
     * Representa un simbolo lexico simple
     * Asocia la representacion textual con su valor interno
     */
    static class Elemento {
        private String clave;
        private int valor;

        public Elemento (String clave, int valor) {
            this.clave = clave;
            this.valor = valor;
        }

        public String getClave() {
            return clave;
        }


        public int getValor () {
            return valor;
        }
    }
}
