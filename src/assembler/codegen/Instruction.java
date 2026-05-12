package assembler.codegen;

public class Instruction {
    public static final int MODO_INMEDIATO = 0x00; // #valor
    public static final int MODO_DIRECTO   = 0x01; // @direccion
    public static final int MODO_REGISTRO  = 0x02; // Rn
    public static final int BYTES          = 4;    // tamano fijo por instruccion

    private final Opcode opcode;
    private final int modo;
    private int operando;
    private String etiqueta; // simbolo pendiente de resolver (modo reubicable)

    public Instruction(Opcode opcode, int modo, int operando) {
        this.opcode = opcode;
        this.modo = modo;
        this.operando = operando;
    }

    public Instruction(Opcode opcode) {
        this(opcode, MODO_INMEDIATO, 0);
    }

    public Opcode getOpcode()  { return opcode; }
    public int    getModo()    { return modo; }
    public int    getOperando(){ return operando; }
    public String getEtiqueta(){ return etiqueta; }

    public void setOperando(int operando) { this.operando = operando; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public byte[] getBytes() {
        return new byte[]{
                (byte) opcode.getCodigo(),
                (byte) modo,
                (byte) (operando & 0xFF),
                (byte) ((operando >> 8) & 0xFF)
        };
    }

    @Override
    public String toString() {
        String prefijo = switch (modo) {
            case MODO_INMEDIATO -> "#";
            case MODO_DIRECTO   -> "@";
            case MODO_REGISTRO  -> "R";
            default             -> "?";
        };
        return String.format("%-6s %s%04X", opcode.name(), prefijo, operando);
    }
}
