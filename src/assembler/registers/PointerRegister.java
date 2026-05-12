package assembler.registers;

public class PointerRegister extends Register {
    public PointerRegister(String nombre) {
        super(nombre, 16);
        this.enUso = true; // pointer registers always active
    }

    public void incrementar(int cantidad) {
        setValor(valor + cantidad);
    }

    public void decrementar(int cantidad) {
        setValor(valor - cantidad);
    }

    @Override
    public String toString() {
        return String.format("%s=0x%04X", nombre, valor);
    }
}
