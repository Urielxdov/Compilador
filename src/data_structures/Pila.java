package data_structures;

public class Pila <T> {
    private Nodo<T> tope;
    private int size = 0;

    public Pila() {
        tope = null;
    }

    public boolean esVacia() {
        return tope == null;
    }

    public void push(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    public T pop() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        T valor = tope.valor;
        tope = tope.siguiente;
        return valor;
    }

    public T peek() {
        if (esVacia()) throw new RuntimeException("La pila se encuentra vacia");
        return tope.valor;
    }

    private class Nodo<T> {
        Nodo siguiente;
        T valor;

        public Nodo (T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }
}
