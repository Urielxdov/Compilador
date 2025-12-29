package parser.grammar;

public class Terminal implements Symbol {
    private final String nombre;
    private final TerminalMatcher matcher;

    public Terminal (String nombre, TerminalMatcher matcher) {
        this.matcher = matcher;
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
