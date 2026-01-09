package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Set
 *
 * Implementacion propia de un conjunto basado en una tabla hash
 * con direccionamiento abierto y resolucion de coalisiones mediante
 * probing lineal
 *
 * Caracteristicas:
 * - No permite valores duplicados
 * - No permite valores null
 * - Redimensiona automaticamente segun el factor de carga
 * - Iteracion eficiente sin exponer posiciones vacias
 *
 * Diseño:
 * - Open Addressing
 * - Linear Probing
 * - Reashing al superar el factor de carga
 *
 * Complejidad esperada:
 * - Insersion O(1) promedio
 * - Busqueda O(1) promedio
 * - Eliminacion O(1) promedio (con reinsercion de cluster)
 *
 * Nota:
 * Esta implementacion prioriza calidad y control de algoritmos
 * sobre optimizaciones extremas
 * @param <T>
 */
public class Set <T> implements Iterable<T>{
    /**
     * Arreglo interno que almacena los valores del conjunto
     * Las posiciones null representan espacios vacios
     */
    private T[] valores;
    /**Capacidad actual de la tabla hash*/
    private int longitud;
    /**Numero de elementos almacenados*/
    private int elementos;
    /**Factor de carga maximo antes de redimensionar*/
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

    /**
     * Agrega un valor al conjunto si no existe previamente
     *
     * @param valor elemnto a insertar
     */
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

    /**
     * Obtiene la posicion interna del valor dentro de la tabla hash
     *
     * @param valor elemnto a buscar
     * @return indice interno o -1 si no existe
     */
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

    public int size () {
        return elementos;
    }


    /**
     * Eliminar un elemnto del conjunto y restructura
     * el cluster generado por probing lineal
     *
     * @param valor elemento a eliminar
     */
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
            removerElemento(v);
        }
    }

    public boolean contains (T valor) { return get(valor) != -1; }

    /**
     * Duplica la capacidad del conjunto y reubica todos
     * los elemntos usando la nueva funcion hash
     */
    @SuppressWarnings("unchecked")
    private void redimensionar () {
        int nuevaLongitud = longitud * 2;
        T[] nuevoSet = (T[]) new Object[nuevaLongitud];
        int elementosActuales = 0;

        for(T valor : this) {
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

        valores = nuevoSet;
        longitud = nuevaLongitud;
        elementos = elementosActuales;
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
        String s = "";
        for(T v : this) {
           s += v.toString() + '\n';
        };
        return s;
    }
}