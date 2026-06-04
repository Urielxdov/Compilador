package assembler;

import assembler.codegen.MachineCodeGenerator;
import intermediate.IntermediateCode;
import intermediate.Triplet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Traduce IntermediateCode (tripletas optimizadas) a NASM x86-64 ejecutable.
 * Conserva el Assembler educativo interno para no romper los reportes existentes.
 */
public class TripletAssemblerDriver {

    private final Assembler asm;
    private final AssemblerSymbolTable tabla;
    private final MachineCodeGenerator gen;
    private final String programName;

    private final List<String> textLines = new ArrayList<>();
    private final List<String> bssLines = new ArrayList<>();
    private final Map<String, List<Integer>> pending = new LinkedHashMap<>();
    private final Map<String, String> numericConstants = new LinkedHashMap<>();
    private final Set<String> declaredNames = new LinkedHashSet<>();

    public TripletAssemblerDriver(String programName) {
        this.asm = new Assembler();
        this.tabla = asm.getTablaSimbolos();
        this.gen = asm.getMachineCodeGenerator();
        this.programName = programName;
    }

    public void generate(IntermediateCode ic) {
        List<Triplet> triplets = ic.getTriplets();

        declareAll(triplets);
        asm.iniciarPrograma(programName);

        for (Triplet t : triplets) {
            emitTrip(t);
        }

        emitProgramExit();
        asm.terminarPrograma(programName);
    }

    private void declareAll(List<Triplet> triplets) {
        for (Triplet t : triplets) {
            switch (t.getInstruccion()) {
                case "Int" -> declareVar(t.getOp1(), DataType.ENTERO);
                case "Real" -> declareVar(t.getOp1(), DataType.REAL);
            }
        }

        for (Triplet t : triplets) {
            String instr = t.getInstruccion();
            if ("Int".equals(instr) || "Real".equals(instr)) continue;

            autoDecl(t.getOp1(), instr, false);
            autoDecl(t.getOp2(), instr, true);
        }

        if (!tabla.existe("__rhs")) {
            tabla.declarar("__rhs", DataType.REAL);
            declaredNames.add("__rhs");
            bssLines.add(formatBss("__rhs"));
        }
    }

    private void declareVar(String name, DataType tipo) {
        if (name == null || declaredNames.contains(name)) return;
        tabla.declarar(name, tipo);
        declaredNames.add(name);
        bssLines.add(formatBss(name));
    }

    private void autoDecl(String name, String instr, boolean isOp2) {
        if (name == null) return;
        if (isLabel(name) || isNumeric(name) || name.contains(" ")) return;
        if ("JMP".equals(instr) && !isOp2) return;
        if ("LABEL".equals(instr)) return;
        if (declaredNames.contains(name)) return;

        tabla.declarar(name, DataType.REAL);
        declaredNames.add(name);
        bssLines.add(formatBss(name));
    }

    private String formatBss(String name) {
        return String.format("    %-16s resq 1", name);
    }

    private void emitTrip(Triplet t) {
        String instr = t.getInstruccion();
        String op1 = t.getOp1();
        String op2 = t.getOp2();

        switch (instr) {
            case "Int", "Real" -> {
                // Declaraciones ya procesadas.
            }
            case "=", "mov" -> {
                loadOp(op2);
                storeVar(op1);
            }
            case "+", "-", "*", "/" -> {
                loadVar(op1);
                emitLegacyLoadOp(op2);
                loadOperandToXmm("xmm1", op2);
                emitArith(instr);
                storeVar(op1);
            }
            case "Leer" -> emitRead(op1);
            case "Mostrar" -> emitWrite(op1);
            case "JMP_F" -> emitJmpF(op1, op2);
            case "JMP" -> emitJmp(op1);
            case "LABEL" -> emitLabel(op1);
            default -> throw new AssemblerException(
                    "TripletAssemblerDriver: instruccion no soportada: " + instr);
        }
    }

    private void emitRead(String varName) {
        gen.emitirRead(varName);
        textLines.add("    READ_NUM " + varName);
    }

    private void emitWrite(String operand) {
        gen.emitirWrite(operand);
        loadOperandToXmm("xmm0", operand);
        textLines.add("    PRINT_NUM");
    }

    private void emitJmp(String label) {
        int pc = gen.getPC();
        gen.emitirJmp(0);
        addPending(label, pc);
        textLines.add("    jmp " + label);
    }

    private void emitLabel(String label) {
        int labelAddr = gen.getPC();
        List<Integer> patches = pending.remove(label);
        if (patches != null) {
            for (int jpc : patches) gen.parchear(jpc, labelAddr);
        }
        textLines.add(label + ":");
    }

    private void emitJmpF(String condStr, String label) {
        String[] parts = condStr.split(" ", 3);
        if (parts.length != 3) {
            throw new AssemblerException("JMP_F: condicion invalida: \"" + condStr + "\"");
        }

        String left = parts[0];
        String op = parts[1];
        String right = parts[2];

        String cmpTarget = right;
        if (isNumeric(right)) {
            int val = parseIntVal(right);
            gen.emitirMov(val);
            gen.emitirStore("__rhs");
            cmpTarget = "__rhs";
        }

        if (isNumeric(left)) {
            gen.emitirMov(parseIntVal(left));
        } else {
            gen.emitirLoad(left);
        }
        gen.emitirCmp(cmpTarget);

        loadOperandToXmm("xmm0", left);
        loadOperandToXmm("xmm1", right);
        textLines.add("    ucomisd xmm0, xmm1");

        String jmpMnem = negatedJump(op);
        int pc = gen.getPC();
        emitConditionalJump(jmpMnem, 0);
        addPending(label, pc);
        textLines.add("    " + nasmJump(jmpMnem) + " " + label);
    }

    private String negatedJump(String operator) {
        return switch (operator) {
            case ">" -> "JLE";
            case "<" -> "JGE";
            case "==" -> "JNE";
            case "<>" -> "JEQ";
            default -> throw new AssemblerException("Operador relacional no soportado: " + operator);
        };
    }

    private void emitConditionalJump(String mnemonic, int dest) {
        switch (mnemonic) {
            case "JLE" -> gen.emitirJle(dest);
            case "JGE" -> gen.emitirJge(dest);
            case "JNE" -> gen.emitirJne(dest);
            case "JEQ" -> gen.emitirJeq(dest);
            default -> throw new AssemblerException("Salto condicional desconocido: " + mnemonic);
        }
    }

    private void loadOp(String operand) {
        if (isNumeric(operand)) {
            gen.emitirMov(parseIntVal(operand));
        } else {
            gen.emitirLoad(operand);
        }
        loadOperandToXmm("xmm0", operand);
    }

    private void loadVar(String name) {
        gen.emitirLoad(name);
        textLines.add("    movsd xmm0, [rel " + name + "]");
    }

    private void storeVar(String name) {
        gen.emitirStore(name);
        textLines.add("    movsd [rel " + name + "], xmm0");
    }

    private void emitArith(String op) {
        switch (op) {
            case "+" -> {
                gen.emitirAdd();
                textLines.add("    addsd xmm0, xmm1");
            }
            case "-" -> {
                gen.emitirSub();
                textLines.add("    subsd xmm0, xmm1");
            }
            case "*" -> {
                gen.emitirMul();
                textLines.add("    mulsd xmm0, xmm1");
            }
            case "/" -> {
                gen.emitirDiv();
                textLines.add("    divsd xmm0, xmm1");
            }
            default -> throw new AssemblerException("Operador no soportado: " + op);
        }
    }

    private void emitProgramExit() {
        textLines.add("    xor eax, eax");
        textLines.add("    add rsp, 32");
        textLines.add("    pop rbp");
        textLines.add("    ret");
    }

    private void addPending(String label, int pc) {
        pending.computeIfAbsent(label, k -> new ArrayList<>()).add(pc);
    }

    public String getTextoEnsamblador() {
        StringBuilder sb = new StringBuilder();
        sb.append("; ============================================================\n");
        sb.append("; PROGRAMA : ").append(programName).append("\n");
        sb.append("; TARGET   : NASM x86-64 real, enlazado con libc\n");
        sb.append(";\n");
        sb.append("; Linux:\n");
        sb.append(";   nasm -f elf64 ").append(programName).append(".asm -o ").append(programName).append(".o\n");
        sb.append(";   gcc -no-pie ").append(programName).append(".o -o ").append(programName).append("\n");
        sb.append(";   ./").append(programName).append("\n");
        sb.append(";\n");
        sb.append("; Windows x64 (MinGW-w64):\n");
        sb.append(";   nasm -f win64 ").append(programName).append(".asm -o ").append(programName).append(".obj\n");
        sb.append(";   gcc ").append(programName).append(".obj -o ").append(programName).append(".exe\n");
        sb.append(";   .\\").append(programName).append(".exe\n");
        sb.append("; ============================================================\n\n");

        sb.append("default rel\n");
        sb.append("global main\n");
        sb.append("extern printf\n");
        sb.append("extern scanf\n\n");

        sb.append("%macro READ_NUM 1\n");
        sb.append("%ifidn __OUTPUT_FORMAT__, win64\n");
        sb.append("    lea rdx, [rel %1]\n");
        sb.append("    lea rcx, [rel fmt_read_num]\n");
        sb.append("%else\n");
        sb.append("    lea rsi, [rel %1]\n");
        sb.append("    lea rdi, [rel fmt_read_num]\n");
        sb.append("%endif\n");
        sb.append("    xor eax, eax\n");
        sb.append("    call scanf\n");
        sb.append("%endmacro\n\n");

        sb.append("%macro PRINT_NUM 0\n");
        sb.append("%ifidn __OUTPUT_FORMAT__, win64\n");
        sb.append("    lea rcx, [rel fmt_write_num]\n");
        sb.append("    movapd xmm1, xmm0\n");
        sb.append("    movq rdx, xmm0\n");
        sb.append("%else\n");
        sb.append("    lea rdi, [rel fmt_write_num]\n");
        sb.append("%endif\n");
        sb.append("    mov eax, 1\n");
        sb.append("    call printf\n");
        sb.append("%endmacro\n\n");

        sb.append("section .data\n");
        sb.append("    fmt_read_num     db \"%lf\", 0\n");
        sb.append("    fmt_write_num    db \"%.6g\", 10, 0\n");
        for (Map.Entry<String, String> entry : numericConstants.entrySet()) {
            sb.append(String.format("    %-16s dq %s%n", entry.getValue(), asNasmFloat(entry.getKey())));
        }
        sb.append("\n");

        sb.append("section .bss\n");
        for (String line : bssLines) sb.append(line).append("\n");
        sb.append("\n");

        sb.append("section .text\n");
        sb.append("main:\n");
        sb.append("    push rbp\n");
        sb.append("    mov rbp, rsp\n");
        sb.append("    sub rsp, 32\n");
        for (String line : textLines) sb.append(line).append("\n");

        return sb.toString();
    }

    public void escribirArchivo(String path) throws IOException {
        Files.writeString(Path.of(path), getTextoEnsamblador());
    }

    public Assembler getAssembler() {
        return asm;
    }

    private boolean isLabel(String s) {
        return s != null && s.matches("L\\d+");
    }

    private boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignore) {
        }
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    private int parseIntVal(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return (int) Float.parseFloat(s);
        }
    }

    private void emitLegacyLoadOp(String operand) {
        if (isNumeric(operand)) {
            gen.emitirMov(parseIntVal(operand));
        } else {
            gen.emitirLoad(operand);
        }
    }

    private void loadOperandToXmm(String register, String operand) {
        if (isNumeric(operand)) {
            textLines.add("    movsd " + register + ", [rel " + constantLabel(operand) + "]");
        } else {
            textLines.add("    movsd " + register + ", [rel " + operand + "]");
        }
    }

    private String constantLabel(String value) {
        return numericConstants.computeIfAbsent(value, ignored -> "__const_" + numericConstants.size());
    }

    private String asNasmFloat(String value) {
        return value.contains(".") ? value : value + ".0";
    }

    private String nasmJump(String mnemonic) {
        return switch (mnemonic) {
            case "JLE" -> "jbe";
            case "JGE" -> "jae";
            case "JNE" -> "jne";
            case "JEQ" -> "je";
            default -> throw new AssemblerException("Salto condicional desconocido: " + mnemonic);
        };
    }
}
