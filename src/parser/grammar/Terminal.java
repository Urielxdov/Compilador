package parser.grammar;

/**
 * Representa un simbolo terminal dentro de una gramatica
 * libre de conteto
 *
 * Un terminal es un simbolo que no puede ser remplazado
 * por ninguna produccion y aparece directamente
 * en la cadena de entrada
 *
 * La igualdad entre terminales se define por su nombre
 */
public class Terminal implements Symbol {
    private final String nombre;

    public Terminal (String nombre) {
        this.nombre = nombre;
    }


    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return getNombre();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Terminal other = (Terminal) o;
        return this.nombre.equals(other.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
