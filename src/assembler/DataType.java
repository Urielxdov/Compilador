package assembler;

public enum DataType {
    ENTERO(2),
    REAL(4),
    CHAR(1),
    CADENA(0);

    private final int tamano;

    DataType(int tamano) { this.tamano = tamano; }

    public int getTamano() { return tamano; }
}
