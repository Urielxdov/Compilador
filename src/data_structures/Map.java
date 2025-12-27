package data_structures;

import java.util.Iterator;

public class Map <K, V> implements Iterable<K>{
    private Nodo<Par<K, V>> cabeza;

    public void put (K clave, V valor) {
        Nodo<Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) {
                actual.dato.valor = valor; // se actualiza el valor
                return;
            }
            actual = actual.siguiente;
        }
        // Insertamos nuevo
        Nodo<Par<K, V>> nodo = new Nodo<>(new Par<>(clave, valor));
        nodo.siguiente = cabeza;
        cabeza = nodo;
    }


    public V get (K clave) {
        Nodo <Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) {
                return actual.dato.valor;
            }
            actual = actual.siguiente;
        }
        return null;
    }


    public boolean existKey (K clave) {
        Nodo <Par<K, V>> actual = cabeza;

        while (actual != null) {
            if (actual.dato.clave.equals(clave)) return true;
            actual = actual.siguiente;
        }

        return false;
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private Nodo<Par<K, V>> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public K next() {
                K clave = actual.dato.clave;
                actual = actual.siguiente;
                return clave;
            }
        };
    }


    private class Nodo <T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }

        @Override
        public String toString() {
            return dato.toString();
        }
    }

    private class Par <K, V> {
        K clave;
        V valor;

        Par (K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "Par{" +
                    "clave=" + clave +
                    ", valor=" + valor +
                    '}';
        }
    }

    @Override
    public String toString() {
        String sb = "{\n";
        Nodo<Par<K, V>> actual = cabeza;

        while (actual != null) {
            sb += actual.dato.clave;
            sb += " " + '=';
            sb += actual.dato.valor;

            if (actual.siguiente != null) sb += "\n";

            actual = actual.siguiente;
        }

        sb += "\n}";
        return sb;
    }
}
