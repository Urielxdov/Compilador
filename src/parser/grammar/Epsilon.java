package parser.grammar;

public class Epsilon extends Terminal{
    private String nombre;

    public Epsilon (String nombre) {
        super(nombre);
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

        Epsilon other = (Epsilon) o;
        return this.nombre.equals(other.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
