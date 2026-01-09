package parser.grammar;

/**
 * Representa un simbolo no terminal dentro de una gramatica
 * libre de contexto
 *
 * Un no terminal puede ser remplazado por una o mas
 * producciones y se utiliza para definir la estructura
 * del lenguaje
 *
 * La igualdad entre no terminales se define por su nombre
 */
public class NoTerminal implements Symbol {
    private String nombre;

    public NoTerminal(String nombre) {
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

        NoTerminal other = (NoTerminal) o;
        return this.nombre.equals(other.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
