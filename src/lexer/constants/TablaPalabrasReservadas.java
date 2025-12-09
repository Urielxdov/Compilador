package lexer.constants;

import data_structures.Set;

import java.util.Objects;

public class TablaPalabrasReservadas {
    static final Set<Elemento> PALABRAS_RESERVADAS = new Set<>();

    public TablaPalabrasReservadas () {
        PALABRAS_RESERVADAS.add(new Elemento("Programa", 400));
        PALABRAS_RESERVADAS.add(new Elemento("Real", 401));
        PALABRAS_RESERVADAS.add(new Elemento("Entero", 402));
        PALABRAS_RESERVADAS.add(new Elemento("Leer", 403));
        PALABRAS_RESERVADAS.add(new Elemento("Escribir", 404));
        PALABRAS_RESERVADAS.add(new Elemento("Si", 405));
        PALABRAS_RESERVADAS.add(new Elemento("Entonces", 406));
        PALABRAS_RESERVADAS.add(new Elemento("Sino", 407));
        PALABRAS_RESERVADAS.add(new Elemento("Inicio", 408));
        PALABRAS_RESERVADAS.add(new Elemento("Fin", 409));
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
