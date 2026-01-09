package lexer.constants;

import data_structures.Set;

import java.util.Objects;

/**
 * TablaPalabras reservadas
 *
 * Tabla lexica que define las palabras reservadas
 * reconocidas por el lenguaje
 *
 * Una palabra reservada es un identificador con
 * significado semantico especial, que no puede
 * ser utilizado como nombre definido por el usuario.
 *
 * Esta tabla es utilizada por el analizador lexico
 * para diferenciar entre identificadores y palabras reservadas
 */
public class TablaPalabrasReservadas {
    /**Conjunto de palabras reservadas dentro del ellenguaje*/
    static final Set<Elemento> PALABRAS_RESERVADAS = new Set<>();

    static {
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

    private TablaPalabrasReservadas() {}

    /**
     * Verifica si un lexema corresponde a una palabra reservada
     *
     * @param value lexema a evaluar
     * @return true si el lexema es una palara reservada
     */
    public static boolean existe (String value) {
        for (Elemento elemento : PALABRAS_RESERVADAS)
            if (elemento.clave.equals(value)) return true;
        return false;
    }

    /**
     * Representa una palabra reservada y su codigo lexico asociado
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
