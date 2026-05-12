package assembler.codegen;

import assembler.AssemblerException;
import assembler.AssemblerSymbolTable;
import assembler.MemoryMatrix;
import data_structures.Map;

/**
 * Genera código reutilizable: organiza el código en segmentos nombrados
 * (subrutinas/funciones) que pueden ser invocados con CALL/RET,
 * permitiendo reutilizar bloques de código desde múltiples puntos.
 */
public class ReusableCodeGenerator extends MachineCodeGenerator {
    // nombre de segmento -> índice de primera instrucción
    private final Map<String, Integer> segmentos = new Map<>();
    private String segmentoActual = null;

    public ReusableCodeGenerator(AssemblerSymbolTable tablaSimbolos, MemoryMatrix memoria) {
        super(tablaSimbolos, memoria);
    }

    public void iniciarSegmento(String nombre) {
        segmentos.put(nombre, instrucciones.nodosExistentes());
        segmentoActual = nombre;
    }

    public void terminarSegmento() {
        segmentoActual = null;
    }

    public String getSegmentoActual() { return segmentoActual; }

    public int getDireccionSegmento(String nombre) {
        Integer idx = segmentos.get(nombre);
        if (idx == null) throw new AssemblerException("Segmento no registrado: " + nombre);
        return CODE_BASE + idx * Instruction.BYTES;
    }

    public boolean existeSegmento(String nombre) {
        return segmentos.existKey(nombre);
    }

    public void emitirCallSegmento(String nombreSegmento) {
        emitir(new Instruction(Opcode.CALL, Instruction.MODO_DIRECTO,
                getDireccionSegmento(nombreSegmento)));
    }

    @Override
    public String getMachineCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CODIGO MAQUINA REUTILIZABLE ===\n");
        sb.append("Segmentos registrados:\n");
        for (String seg : segmentos) {
            int dir = CODE_BASE + segmentos.get(seg) * Instruction.BYTES;
            sb.append(String.format("  %-20s -> PC=%04X\n", seg, dir));
        }
        sb.append("\n");
        sb.append(String.format("%-6s %-15s %s\n", "PC", "INSTRUCCION", "BYTES"));
        sb.append("-".repeat(50)).append("\n");
        int pc = CODE_BASE;
        for (Instruction inst : instrucciones) {
            byte[] bytes = inst.getBytes();
            sb.append(String.format("%04X   %-15s  ", pc, inst.toString()));
            for (byte b : bytes) sb.append(String.format("%02X ", b));
            sb.append("\n");
            pc += Instruction.BYTES;
        }
        return sb.toString();
    }
}
