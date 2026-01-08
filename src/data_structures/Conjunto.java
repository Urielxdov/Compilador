package data_structures;

import parser.grammar.Epsilon;

import java.util.Iterator;

/**
 * Conjunto
 *
 * Implementacion academica de un conjunto matematico basada
 * en un arreglo dinamico
 *
 * Incariantes:
 * - No admite elementos duplicados
 * - No admite valores null
 * - El orden de insrcion se conserva
 *
 * Uso principal
 * - Soporte para estructuras gramaticas (FIRST, FOLLOW, etc)
 *
 * Caracteristicas:
 * - Crecimiento dinamico
 * - Iteracion segura mediante Iterator
 * - Operaciones de union
 *
 * Nota:
 * Esta implementacion carece de optimizacion para busqueda dado que
 * la notacion es O(n), y ya prioriza claridad y control del comportamiento
 */
public class Conjunto<T> implements Iterable<T>{
    private T[] datos;
    private int size;

    public Conjunto() {
        datos = (T[]) new Object[8];
        size = 0;
    }

    /**
     * Verifica si el conjunto contiene el elemnto indicado
     * @param elemento - elemento a buscar
     * @return true si el elemento existe en el conjunto
     */
    public boolean contiene (T elemento) {
        if (elemento == null || size == 0) return false;
        for (int i = 0; i < size; i++) {
            if (datos[i].equals(elemento)) return true;
        }
        return false;
    }

    /**
     * Agrega un elemento al conjunto si no existe previamente
     * @param elemento - elemento a agregar
     * @return true si el conjunto fue modificado
     */
    public boolean agregar(T elemento) {
        if (contiene(elemento)) return false;
        if (elemento == null) return false;

        if (size == datos.length) redimensionar();

        datos[size++] = elemento;
        return true;
    }


    /**
     *
     */
    private void redimensionar() {
        T[] nuevo = (T[]) new Object[datos.length * 2];
        for (int i = 0; i < size; i++) {
            nuevo[i] = datos[i];
        }
        datos = nuevo;
    }

    public T[] getDatos() {
        return datos;
    }

    /**
     * Realiza la union de este conjunto con otro
     * @param otro conjunto a unir
     * @return true si el conjunto fue modificado
     */
    public boolean agregar(Conjunto<T> otro) {
        if (otro == null) return false;
        boolean cambio = false;
        for (T elemento : otro.getDatos()) {
            cambio |= agregar(elemento);
        }
        return cambio;
    }


    /**
     * Retorna un nuevo conjutno excluyendo el simbolo vacio (Epsilon)
     *
     * Uso tipico:
     * - Calculo de FIRST y FOLLOW en gramatcias
     *
     * @return conjunto sin simbolos Epsilon o null si esta vacio
     */
    public Conjunto<T> obtenerSinVacio() {
        if (size == 0) return null;
        Conjunto<T> aux = new Conjunto<>();
        for (T s : this) {
            if (!(s instanceof Epsilon)) aux.agregar(s);
        }
        return aux;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;
            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                return datos[i++];
            }
        };
    }


    @Override
    public String toString() {
        String sb = "";
        for (T v : this) {
            sb += " " + v.toString() + " ";
        }
        return sb;
    }
}
