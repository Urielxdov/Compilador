package assembler.registers;

public class RegisterFile {
    public static final int TOTAL_REGISTROS = 8;

    // Registros de propósito general (16-bit con divisiones de 8-bit)
    private final GeneralRegister ax = new GeneralRegister("AX");
    private final GeneralRegister bx = new GeneralRegister("BX");
    private final GeneralRegister cx = new GeneralRegister("CX");
    private final GeneralRegister dx = new GeneralRegister("DX");

    // Registro de banderas
    private final FlagRegister flags = new FlagRegister();

    // Registros apuntadores
    private final PointerRegister ip = new PointerRegister("IP"); // instruction pointer
    private final PointerRegister sp = new PointerRegister("SP"); // stack pointer
    private final PointerRegister bp = new PointerRegister("BP"); // base pointer

    public GeneralRegister getAX() { return ax; }
    public GeneralRegister getBX() { return bx; }
    public GeneralRegister getCX() { return cx; }
    public GeneralRegister getDX() { return dx; }
    public FlagRegister    getFlags() { return flags; }
    public PointerRegister getIP() { return ip; }
    public PointerRegister getSP() { return sp; }
    public PointerRegister getBP() { return bp; }

    public int registrosEnUso() {
        int count = 0;
        if (ax.isEnUso()) count++;
        if (bx.isEnUso()) count++;
        if (cx.isEnUso()) count++;
        if (dx.isEnUso()) count++;
        return count;
    }

    public int totalRegistros() { return TOTAL_REGISTROS; }

    public GeneralRegister registroLibre() {
        if (!ax.isEnUso()) return ax;
        if (!bx.isEnUso()) return bx;
        if (!cx.isEnUso()) return cx;
        if (!dx.isEnUso()) return dx;
        throw new RuntimeException("No hay registros generales disponibles");
    }

    public void reiniciarGenerales() {
        ax.liberar();
        bx.liberar();
        cx.liberar();
        dx.liberar();
    }

    @Override
    public String toString() {
        return "=== REGISTROS ===\n" +
                "  Generales:\n" +
                "    " + ax + "\n" +
                "    " + bx + "\n" +
                "    " + cx + "\n" +
                "    " + dx + "\n" +
                "  Apuntadores:\n" +
                "    " + ip + "\n" +
                "    " + sp + "\n" +
                "    " + bp + "\n" +
                "  " + flags + "\n" +
                "  En uso: " + registrosEnUso() + "/" + TOTAL_REGISTROS + "\n";
    }
}
