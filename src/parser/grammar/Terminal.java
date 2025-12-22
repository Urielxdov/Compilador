package parser.grammar;

public class Terminal implements Symbol{
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
}
