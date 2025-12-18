package data_structures;

public class Map <K, V>{
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


    private class Nodo <T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private class Par <K, V> {
        K clave;
        V valor;

        Par (K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }
}
