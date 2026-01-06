package lexer.constants;

import data_structures.Set;

public class TablaCaracteresSimples{
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
    public static boolean existe(String s) {
        for (Elemento elemento : CARACTERES_SIMPLES)
            if (elemento.clave.equals(s)) return true;
        return false;
    }

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
