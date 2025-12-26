package data_structures;

import java.util.Iterator;

public class Conjunto<T> implements Iterable{
    private T[] datos;
    private int size;

    public Conjunto() {
        datos = (T[]) new Object[8];
        size = 0;
    }

    public boolean contiene (T elemento) {
        for (int i = 0; i < size; i++) {
            if (datos[i].equals(elemento)) return true;
        }
        return false;
    }

    public boolean agregar(T elemento) {
        if (contiene(elemento)) return false;

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
        boolean cambio = false;
        for (T elemento : otro.getDatos()) {
            cambio |= agregar(elemento);
        }
        return cambio;
    }


    @Override
    public Iterator iterator() {
        return new Iterator() {
            private int i = 0;
            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Object next() {
                return datos[i++];
            }
        };
    }
}
