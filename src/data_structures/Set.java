package data_structures;

public class Set <T> {
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

    public T get(T valor){
        if(valor == null) return null;

        int indice = hash(valor);
        int i = 0;

        while (i < longitud) {
            T elemento = valores[indice];

            if(elemento == null) return  null;
            else if (elemento.equals(valor)) return valor;

            indice = (indice + 1) % longitud;
            i++;
        }

        return null;
    }

    public int obtenerPosicion(T valor) {
        if(valor == null) return -1;

        int indice = hash(valor);
        int i = 0;

        while (i < longitud) {
            T elemento = valores[indice];

            if(elemento == null) return  -1;
            else if (elemento.equals(valor)) return i;

            indice = (indice + 1) % longitud;
            i++;
        }

        return -1;
    }

    public boolean contains (T valor) { return get(valor) != null; }

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

    @SuppressWarnings("unchecked")
    public T[] obtenerArreglo () {
        T[] arreglo = (T[]) new Object[elementos];
        int lugar = 0;
        for(T valor : valores) {
            if(valor != null) arreglo[lugar++] = valor;
        }
        return arreglo;
    }

    public int datosRegistrados(){
        return this.elementos;
    }



    @SuppressWarnings("unchecked")
    public T[] toArray(T[] plantilla) {
        int n = elementos;
        // Si la plantilla es lo bastante grande, la usamos; si no, creamos otra
        T[] resultado = plantilla.length >= n
                ? plantilla
                : (T[]) java.lang.reflect.Array.newInstance(
                plantilla.getClass().getComponentType(), n);

        int idx = 0;
        for (T v : valores) {
            if (v != null) {
                resultado[idx++] = v;
            }
        }
        // Marcamos el fin si sobra espacio
        if (resultado.length > n) {
            resultado[n] = null;
        }
        return resultado;
    }






}