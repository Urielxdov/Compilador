package assembler;

public class SymbolRecord {
    private String nombre;
    private DataType tipo;
    private int direccion;
    private int tamano;
    private String contenido;
    private boolean inicializado;

    public SymbolRecord(String nombre, DataType tipo, int direccion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
        this.tamano = tipo.getTamano();
        this.inicializado = false;
    }

    public String getNombre() { return nombre; }
    public DataType getTipo() { return tipo; }
    public int getDireccion() { return direccion; }
    public int getTamano() { return tamano; }
    public String getContenido() { return contenido; }
    public boolean isInicializado() { return inicializado; }

    public void setDireccion(int direccion) { this.direccion = direccion; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setInicializado(boolean inicializado) { this.inicializado = inicializado; }

    @Override
    public String toString() {
        return String.format("%-15s %-8s %04X     %-6d %s",
                nombre, tipo, direccion, tamano,
                inicializado ? contenido : "---");
    }
}
