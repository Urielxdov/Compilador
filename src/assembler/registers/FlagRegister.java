package assembler.registers;

public class FlagRegister extends Register {
    private boolean zeroFlag;     // ZF: resultado == 0
    private boolean signFlag;     // SF: resultado < 0
    private boolean carryFlag;    // CF: desbordamiento sin signo
    private boolean overflowFlag; // OF: desbordamiento con signo

    public FlagRegister() {
        super("FLAGS", 16);
    }

    public void actualizar(int resultado) {
        zeroFlag = (resultado == 0);
        signFlag = (resultado < 0);
        enUso = true;
    }

    public void actualizarConAcarreo(long resultado, int tamano) {
        long mascara = tamano >= 16 ? 0xFFFFL : 0xFFL;
        long max = tamano >= 16 ? 0x7FFFL : 0x7FL;
        zeroFlag = ((resultado & mascara) == 0);
        signFlag = (resultado < 0);
        carryFlag = (resultado > mascara);
        overflowFlag = (resultado > max);
        enUso = true;
    }

    public boolean isZeroFlag()    { return zeroFlag; }
    public boolean isSignFlag()    { return signFlag; }
    public boolean isCarryFlag()   { return carryFlag; }
    public boolean isOverflowFlag(){ return overflowFlag; }

    public void setZeroFlag(boolean v)    { this.zeroFlag = v; }
    public void setSignFlag(boolean v)    { this.signFlag = v; }
    public void setCarryFlag(boolean v)   { this.carryFlag = v; }
    public void setOverflowFlag(boolean v){ this.overflowFlag = v; }

    @Override
    public String toString() {
        return String.format("FLAGS [ZF=%d SF=%d CF=%d OF=%d]",
                zeroFlag ? 1 : 0, signFlag ? 1 : 0,
                carryFlag ? 1 : 0, overflowFlag ? 1 : 0);
    }
}
