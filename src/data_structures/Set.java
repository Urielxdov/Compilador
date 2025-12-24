package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Set <T> implements Iterable<T>{
    private T[] valores;
    private int longitud;
    private int elementos;
    private final float CARGA_MAXIMA = 0.75F;

    @SuppressWarnings("unchecked")
    public Set() {
        this.longitud = 10;
        this.valores = (T[]) new Object[longitud];
        this.elementos = 0;
    }


    private int hash (T valor) {
        return Math.abs(valor.hashCode() % longitud);
    }

    public void add(T valor) {
        if(valor == null) return;

        if(((float) elementos / longitud) >= CARGA_MAXIMA ) redimensionar();

        int indice = hash(valor);
        int i = 0;

        while(i < longitud) {
            T elemento = valores[indice];

            if(elemento == null) {
                valores[indice] = valor;
                elementos++;
                return;
            }
            else if(valor.equals(valores[indice])) return;

            indice = (indice + 1) % longitud;
            i++;
        }
    }

    public int get(T valor){
        if(valor == null) return -1;

        int indice = hash(valor);
        int i = 0;

        while (i < longitud) {
            T elemento = valores[indice];

            if(elemento == null) return  -1;
            else if (elemento.equals(valor)) return indice;

            indice = (indice + 1) % longitud;
            i++;
        }

        return -1;
    }

    public T get(int posicion) {
        return this.valores[posicion];
    }


    public int size () {
        return elementos;
    }


    public void removerElemento(T valor) {
        if (valor == null) return;

        int posicion = get(valor);
        if (posicion == -1) return;

        valores[posicion] = null;
        elementos--;

        // Reinsertar la cadena
        int siguiente = (posicion + 1) % longitud;

        while (valores[siguiente] != null) {
            T valorRegistrar = valores[siguiente];
            valores[siguiente] = null;
            elementos--;
            add(valorRegistrar);

            siguiente = (siguiente + 1) % longitud;
        }
    }



    public void removerTodo(Set<T> valores) {
        for (T v : valores) {
            int posicion;
            while ((posicion = get(v)) != -1) {
                removerElemento(v);
            }
        }
    }

    public boolean contains (T valor) { return get(valor) != -1; }

    @SuppressWarnings("unchecked")
    private void redimensionar () {
        int nuevaLongitud = longitud * 2;
        T[] nuevoSet = (T[]) new Object[nuevaLongitud];
        int elementosActuales = 0;

        for(T valor : valores) {
            if(valor != null) {
                int nuevoIndice = Math.abs(valor.hashCode() % nuevaLongitud);
                int i = 0;

                while(i < nuevaLongitud) {
                    if(nuevoSet[nuevoIndice] == null) {
                        nuevoSet[nuevoIndice] = valor;
                        elementosActuales++;
                        break;
                    }
                    nuevoIndice = (nuevoIndice + 1) % nuevaLongitud;
                    i++;
                }
            }
        }

        valores = nuevoSet;
        longitud = nuevaLongitud;
        elementos = elementosActuales;
    }

    /**
     * Este metodo tiene como finalidad eliminar los espacios en blanco del arreglo
     * Dado que quitara los espcios vacios, es importante usarlo unicamente cuando tu estructura
     * dejara de modificarse. De hacerlo se perdera rendimiento dado que redimensionara nuevamente
     * el arreglo y valores del objeto
     */
    @SuppressWarnings("unchecked")
    public void limpiarArreglo() {
        int nuevaLongitud = elementos;
        T[]nuevoSet = (T[]) new Object[nuevaLongitud];
        int reubicados= 0;

        for(T valor : valores) {
            if(valor != null) {
                int nuevoIndice = Math.abs(valor.hashCode() % nuevaLongitud);
                int i = 0;
                while(i < nuevaLongitud) {
                    if(nuevoSet[nuevoIndice] == null) {
                        nuevoSet[nuevoIndice] = valor;
                        reubicados++;
                        break;
                    }
                    nuevoIndice = (nuevoIndice + 1) % nuevaLongitud;
                    i++;
                }
            }
        }

        this.valores = nuevoSet;
        this.longitud = nuevaLongitud;
        this.elementos = reubicados; // Redundancia para asegurar consistencia
    }


    public int datosRegistrados(){
        return this.elementos;
    }





    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int indice = 0; // posicion actual
            private int restantes = elementos; // cuantos faltan por devolver?

            @Override
            public boolean hasNext() {
                return restantes > 0;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();

                while (indice < longitud && valores[indice] == null) indice++;

                if (indice >= longitud) throw new NoSuchElementException();

                T valor = valores[indice++];
                restantes--;
                return valor;
            }
        };
    }


    @Override
    public String toString() {
        return super.toString();
    }
}