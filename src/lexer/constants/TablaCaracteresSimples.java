package lexer.constants;

import data_structures.Set;

public class TablaCaracteresSimples {
    final static Set<Elemento> CARACTERES_SIMPLES = new Set<>();

    public TablaCaracteresSimples () {
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
