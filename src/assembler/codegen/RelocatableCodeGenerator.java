package assembler.codegen;

import assembler.AssemblerSymbolTable;
import assembler.MemoryMatrix;
import data_structures.Map;

/**
 * Genera código reubicable: las referencias a símbolos se resuelven
 * en tiempo de carga mediante una tabla de reubicación, permitiendo
 * cargar el código en cualquier dirección base.
 */
public class RelocatableCodeGenerator extends MachineCodeGenerator {
    // índice de instrucción -> nombre de símbolo pendiente de resolver
    private final Map<Integer, String> tablaReubicacion = new Map<>();
    private int baseOffset = 0;

    public RelocatableCodeGenerator(AssemblerSymbolTable tablaSimbolos, MemoryMatrix memoria) {
        super(tablaSimbolos, memoria);
    }

    public void setBaseOffset(int offset) {
        this.baseOffset = offset;
        this.contadorPC = CODE_BASE + offset;
    }

    @Override
    public void emitirLoad(String nombreVar) {
        int indice = instrucciones.nodosExistentes();
        Instruction inst = new Instruction(Opcode.LOAD, Instruction.MODO_DIRECTO, 0);
        inst.setEtiqueta(nombreVar);
        instrucciones.agregar(inst);
        tablaReubicacion.put(indice, nombreVar);
        contadorPC += Instruction.BYTES;
    }

    @Override
    public void emitirStore(String nombreVar) {
        int indice = instrucciones.nodosExistentes();
        Instruction inst = new Instruction(Opcode.STORE, Instruction.MODO_DIRECTO, 0);
        inst.setEtiqueta(nombreVar);
        instrucciones.agregar(inst);
        tablaReubicacion.put(indice, nombreVar);
        contadorPC += Instruction.BYTES;
    }

    // Resuelve todas las referencias de símbolos aplicando el offset base
    public void resolver() {
        for (Integer idx : tablaReubicacion) {
            String simbolo = tablaReubicacion.get(idx);
            int dirBase = tablaSimbolos.obtener(simbolo).getDireccion();
            instrucciones.obtener(idx).setOperando(dirBase + baseOffset);
        }
    }

    @Override
    public String getMachineCode() {
        resolver();
        StringBuilder sb = new StringBuilder();
        sb.append("=== CODIGO MAQUINA REUBICABLE ===\n");
        sb.append(String.format("Base offset: 0x%04X\n\n", baseOffset));
        sb.append(String.format("%-6s %-15s %-12s %s\n", "PC", "INSTRUCCION", "SIMBOLO", "BYTES"));
        sb.append("-".repeat(60)).append("\n");
        int pc = CODE_BASE + baseOffset;
        for (Instruction inst : instrucciones) {
            byte[] bytes = inst.getBytes();
            String etiq = inst.getEtiqueta() != null ? inst.getEtiqueta() : "";
            sb.append(String.format("%04X   %-15s %-12s ", pc, inst.toString(), etiq));
            for (byte b : bytes) sb.append(String.format("%02X ", b));
            sb.append("\n");
            pc += Instruction.BYTES;
        }
        sb.append("\n=== TABLA DE REUBICACION ===\n");
        sb.append(String.format("  %-6s %-12s %s\n", "IDX", "SIMBOLO", "DIR_RESUELTA"));
        for (Integer idx : tablaReubicacion) {
            String simbolo = tablaReubicacion.get(idx);
            int dir = tablaSimbolos.obtener(simbolo).getDireccion() + baseOffset;
            sb.append(String.format("  %-6d %-12s %04X\n", idx, simbolo, dir));
        }
        return sb.toString();
    }
}
