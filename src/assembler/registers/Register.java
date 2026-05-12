package assembler.registers;

public abstract class Register {
    protected final String nombre;
    protected final int tamano; // bits: 8 o 16
    protected int valor;
    protected boolean enUso;

    public Register(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.valor = 0;
        this.enUso = false;
    }

    public String getNombre() { return nombre; }
    public int getTamano() { return tamano; }
    public int getValor() { return valor; }
    public boolean isEnUso() { return enUso; }

    public void setValor(int valor) {
        int mascara = tamano >= 16 ? 0xFFFF : 0xFF;
        this.valor = valor & mascara;
        this.enUso = true;
    }

    public void liberar() {
        this.valor = 0;
        this.enUso = false;
    }

    @Override
    public String toString() {
        return String.format("%s=0x%0" + (tamano / 4) + "X%s",
                nombre, valor, enUso ? "" : "(libre)");
    }
}
