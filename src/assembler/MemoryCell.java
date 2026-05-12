package assembler;

public class MemoryCell {
    private final int direccion;
    private int contenido;

    public MemoryCell(int direccion, int contenido) {
        this.direccion = direccion;
        this.contenido = contenido;
    }

    public int getDireccion() { return direccion; }
    public int getContenido() { return contenido; }

    @Override
    public String toString() {
        return String.format("[%04X] = %02X", direccion, contenido);
    }
}
