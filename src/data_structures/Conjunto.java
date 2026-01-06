package data_structures;

import parser.grammar.Epsilon;

import java.util.Iterator;

/**
 * Se realiza una implementacion propia de conjunto por fines academicos
 * @param <T> - Tipo de dato
 */
public class Conjunto<T> implements Iterable<T>{
    private T[] datos;
    private int size;

    public Conjunto() {
        datos = (T[]) new Object[8];
        size = 0;
    }

    public boolean contiene (T elemento) {
        if (elemento == null || size == 0) return false;
        for (int i = 0; i < size; i++) {
            if (datos[i].equals(elemento)) return true;
        }
        return false;
    }

    public boolean agregar(T elemento) {
        if (contiene(elemento)) return false;
        if (elemento == null) return false;

        if (size == datos.length) redimensionar();

        datos[size++] = elemento;
        return true;
    }


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

    public boolean agregar(Conjunto<T> otro) {
        if (otro == null) return false;
        boolean cambio = false;
        for (T elemento : otro.getDatos()) {
            cambio |= agregar(elemento);
        }
        return cambio;
    }


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
