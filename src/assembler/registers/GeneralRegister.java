package assembler.registers;

public class GeneralRegister extends Register {
    private int parteAlta; // XH: bits 8-15
    private int parteBaja; // XL: bits 0-7

    public GeneralRegister(String nombre) {
        super(nombre, 16);
    }

    @Override
    public void setValor(int valor) {
        super.setValor(valor);
        parteBaja = this.valor & 0xFF;
        parteAlta = (this.valor >> 8) & 0xFF;
    }

    public int getParteAlta() { return parteAlta; }
    public int getParteBaja() { return parteBaja; }

    public String getNombreAlto() { return nombre.charAt(0) + "H"; }
    public String getNombreBajo() { return nombre.charAt(0) + "L"; }

    public void setParteAlta(int val) {
        parteAlta = val & 0xFF;
        this.valor = (parteAlta << 8) | parteBaja;
        this.enUso = true;
    }

    public void setParteBaja(int val) {
        parteBaja = val & 0xFF;
        this.valor = (parteAlta << 8) | parteBaja;
        this.enUso = true;
    }

    @Override
    public String toString() {
        return String.format("%s=0x%04X (%s=0x%02X, %s=0x%02X)%s",
                nombre, valor,
                getNombreAlto(), parteAlta,
                getNombreBajo(), parteBaja,
                enUso ? "" : " [libre]");
    }
}
