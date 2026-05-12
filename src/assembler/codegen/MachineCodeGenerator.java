package assembler.codegen;

import assembler.AssemblerException;
import assembler.AssemblerSymbolTable;
import assembler.MemoryMatrix;
import assembler.SymbolRecord;
import data_structures.Lista;

public class MachineCodeGenerator {
    public static final int CODE_BASE = 0x0000;

    protected final Lista<Instruction> instrucciones = new Lista<>();
    protected int contadorPC = CODE_BASE;
    protected final AssemblerSymbolTable tablaSimbolos;
    protected final MemoryMatrix memoria;

    public MachineCodeGenerator(AssemblerSymbolTable tablaSimbolos, MemoryMatrix memoria) {
        this.tablaSimbolos = tablaSimbolos;
        this.memoria = memoria;
    }

    public void emitir(Instruction inst) {
        instrucciones.agregar(inst);
        byte[] bytes = inst.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            memoria.escribir(contadorPC + i, bytes[i] & 0xFF);
        }
        contadorPC += Instruction.BYTES;
    }

    public void emitirLoad(String nombreVar) {
        SymbolRecord r = tablaSimbolos.obtener(nombreVar);
        emitir(new Instruction(Opcode.LOAD, Instruction.MODO_DIRECTO, r.getDireccion()));
    }

    public void emitirStore(String nombreVar) {
        SymbolRecord r = tablaSimbolos.obtener(nombreVar);
        emitir(new Instruction(Opcode.STORE, Instruction.MODO_DIRECTO, r.getDireccion()));
    }

    public void emitirMov(int valor) {
        emitir(new Instruction(Opcode.MOV, Instruction.MODO_INMEDIATO, valor));
    }

    public void emitirAdd() { emitir(new Instruction(Opcode.ADD)); }
    public void emitirSub() { emitir(new Instruction(Opcode.SUB)); }
    public void emitirMul() { emitir(new Instruction(Opcode.MUL)); }
    public void emitirDiv() { emitir(new Instruction(Opcode.DIV)); }

    public void emitirCmp(String nombreVar) {
        SymbolRecord r = tablaSimbolos.obtener(nombreVar);
        emitir(new Instruction(Opcode.CMP, Instruction.MODO_DIRECTO, r.getDireccion()));
    }

    public void emitirJmp(int destino) {
        emitir(new Instruction(Opcode.JMP, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirJgt(int destino) {
        emitir(new Instruction(Opcode.JGT, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirJlt(int destino) {
        emitir(new Instruction(Opcode.JLT, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirJeq(int destino) {
        emitir(new Instruction(Opcode.JEQ, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirJne(int destino) {
        emitir(new Instruction(Opcode.JNE, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirRead(String nombreVar) {
        SymbolRecord r = tablaSimbolos.obtener(nombreVar);
        emitir(new Instruction(Opcode.READ, Instruction.MODO_DIRECTO, r.getDireccion()));
    }

    public void emitirWrite(String nombreVar) {
        SymbolRecord r = tablaSimbolos.obtener(nombreVar);
        emitir(new Instruction(Opcode.WRITE, Instruction.MODO_DIRECTO, r.getDireccion()));
    }

    public void emitirCall(int destino) {
        emitir(new Instruction(Opcode.CALL, Instruction.MODO_DIRECTO, destino));
    }

    public void emitirRet() {
        emitir(new Instruction(Opcode.RET));
    }

    public void emitirHalt() {
        emitir(new Instruction(Opcode.HALT));
    }

    // Parchea el operando de una instrucción ya emitida (para backpatching de saltos)
    public void parchear(int pcInstruccion, int nuevoDest) {
        int indice = (pcInstruccion - CODE_BASE) / Instruction.BYTES;
        Instruction inst = instrucciones.obtener(indice);
        if (inst == null) throw new AssemblerException("Instruccion no encontrada en PC: " + pcInstruccion);
        inst.setOperando(nuevoDest);
        // Actualiza bytes en memoria también
        byte[] bytes = inst.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            memoria.escribir(pcInstruccion + i, bytes[i] & 0xFF);
        }
    }

    public int getPC() { return contadorPC; }
    public Lista<Instruction> getInstrucciones() { return instrucciones; }

    public String getMachineCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CODIGO MAQUINA ABSOLUTO ===\n");
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

    public String getDumpMemoria() {
        if (contadorPC <= CODE_BASE) return "Sin codigo generado.\n";
        return memoria.dump(CODE_BASE, contadorPC - 1);
    }
}
