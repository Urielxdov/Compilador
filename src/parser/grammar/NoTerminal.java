package parser.grammar;

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
}
