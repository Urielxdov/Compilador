package assembler;

import assembler.codegen.MachineCodeGenerator;
import intermediate.IntermediateCode;
import intermediate.Triplet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Traduce IntermediateCode (tripletas optimizadas) a código máquina absoluto
 * y genera un archivo de texto .asm con los mnemónicos.
 *
 * Instrucciones de tripleta soportadas:
 *   Int / Real              → declaración de variable
 *   =  / mov                → asignación  (dest = src)
 *   + / - / * /             → aritmética  (dest = dest op right)
 *   Leer                    → READ var
 *   Mostrar                 → WRITE var
 *   JMP_F "left op right" L → salto condicional negado
 *   JMP   L                 → salto incondicional
 *   LABEL L                 → definición de etiqueta + backpatch
 */
public class TripletAssemblerDriver {

    private final Assembler asm;
    private final AssemblerSymbolTable tabla;
    private final MachineCodeGenerator gen;
    private final String programName;

    private final List<String> textLines = new ArrayList<>();
    private final List<String> dataLines = new ArrayList<>();

    // label → lista de PC de instrucciones que deben ser parcheadas
    private final Map<String, List<Integer>> pending = new LinkedHashMap<>();

    public TripletAssemblerDriver(String programName) {
        this.asm         = new Assembler();
        this.tabla       = asm.getTablaSimbolos();
        this.gen         = asm.getMachineCodeGenerator();
        this.programName = programName;
    }

    // ---------------------------------------------------------------
    // Punto de entrada
    // ---------------------------------------------------------------

    public void generate(IntermediateCode ic) {
        List<Triplet> triplets = ic.getTriplets();

        declareAll(triplets);

        asm.iniciarPrograma(programName);

        for (Triplet t : triplets) {
            emitTrip(t);
        }

        textLines.add("        HALT");
        asm.terminarPrograma(programName);
    }

    // ---------------------------------------------------------------
    // Pase de declaraciones
    // ---------------------------------------------------------------

    private void declareAll(List<Triplet> triplets) {
        Set<String> declared = new LinkedHashSet<>();

        // Declaraciones explícitas
        for (Triplet t : triplets) {
            switch (t.getInstruccion()) {
                case "Int" -> declareVar(t.getOp1(), DataType.ENTERO, "INT", declared);
                case "Real" -> declareVar(t.getOp1(), DataType.REAL,  "REAL", declared);
            }
        }

        // Auto-declarar temps y cualquier identificador no declarado
        for (Triplet t : triplets) {
            String instr = t.getInstruccion();
            if ("Int".equals(instr) || "Real".equals(instr)) continue;

            autoDecl(t.getOp1(), instr, declared, false);
            autoDecl(t.getOp2(), instr, declared, true);
        }

        // Variable auxiliar para comparaciones con inmediatos
        if (!tabla.existe("__rhs")) {
            tabla.declarar("__rhs", DataType.REAL);
            dataLines.add("    __rhs    REAL    ; aux comparacion");
        }
    }

    private void declareVar(String name, DataType tipo, String textTipo, Set<String> declared) {
        if (name == null || declared.contains(name)) return;
        tabla.declarar(name, tipo);
        declared.add(name);
        dataLines.add(String.format("    %-12s %s", name, textTipo));
    }

    private void autoDecl(String name, String instr, Set<String> declared, boolean isOp2) {
        if (name == null) return;
        if (isLabel(name) || isNumeric(name) || name.contains(" ")) return;
        // op1 de JMP / op2 de JMP_F son etiquetas aunque no comiencen con L
        if ("JMP".equals(instr)  && !isOp2) return;
        if ("LABEL".equals(instr)) return;
        if (declared.contains(name)) return;

        tabla.declarar(name, DataType.REAL);
        declared.add(name);
        dataLines.add(String.format("    %-12s REAL    ; auto", name));
    }

    // ---------------------------------------------------------------
    // Emisión de instrucciones
    // ---------------------------------------------------------------

    private void emitTrip(Triplet t) {
        String instr = t.getInstruccion();
        String op1   = t.getOp1();
        String op2   = t.getOp2();

        switch (instr) {
            case "Int", "Real" -> { /* ya declarado */ }

            case "=", "mov" -> {
                loadOp(op2);
                storeVar(op1);
            }

            case "+", "-", "*", "/" -> {
                loadVar(op1);
                loadOp(op2);
                emitArith(instr);
                storeVar(op1);
            }

            case "Leer" -> {
                gen.emitirRead(op1);
                textLines.add("        READ    " + op1);
            }

            case "Mostrar" -> {
                gen.emitirWrite(op1);
                textLines.add("        WRITE   " + op1);
            }

            case "JMP_F" -> emitJmpF(op1, op2);

            case "JMP" -> {
                int pc = gen.getPC();
                gen.emitirJmp(0);
                addPending(op1, pc);
                textLines.add("        JMP     " + op1);
            }

            case "LABEL" -> {
                int labelAddr = gen.getPC();
                List<Integer> patches = pending.remove(op1);
                if (patches != null) {
                    for (int jpc : patches) gen.parchear(jpc, labelAddr);
                }
                textLines.add(op1 + ":");
            }

            default -> throw new AssemblerException(
                "TripletAssemblerDriver: instruccion no soportada: " + instr);
        }
    }

    // ---------------------------------------------------------------
    // Saltos condicionales (JMP_F = saltar si condición es FALSA)
    // ---------------------------------------------------------------

    private void emitJmpF(String condStr, String label) {
        String[] parts = condStr.split(" ", 3);
        if (parts.length != 3)
            throw new AssemblerException("JMP_F: condicion invalida: \"" + condStr + "\"");

        String left  = parts[0];
        String op    = parts[1];
        String right = parts[2];

        // Si right es inmediato, lo almacena en __rhs antes de la comparación
        String cmpTarget;
        if (isNumeric(right)) {
            int val = parseIntVal(right);
            gen.emitirMov(val);
            gen.emitirStore("__rhs");
            cmpTarget = "__rhs";
            textLines.add("        MOV     #" + val);
            textLines.add("        STORE   __rhs");
        } else {
            cmpTarget = right;
        }

        // Cargar left en AX
        if (isNumeric(left)) {
            gen.emitirMov(parseIntVal(left));
            textLines.add("        MOV     #" + parseIntVal(left));
        } else {
            gen.emitirLoad(left);
            textLines.add("        LOAD    " + left);
        }

        // CMP: compara AX con mem[cmpTarget]
        gen.emitirCmp(cmpTarget);
        textLines.add("        CMP     " + cmpTarget);

        // Salto negado (si condición es FALSE → salta a label)
        String jmpMnem = negatedJump(op);
        int pc = gen.getPC();
        emitConditionalJump(jmpMnem, 0);
        addPending(label, pc);
        textLines.add("        " + jmpMnem + "     " + label);
    }

    private String negatedJump(String operator) {
        return switch (operator) {
            case ">"  -> "JLE";
            case "<"  -> "JGE";
            case "==" -> "JNE";
            case "<>" -> "JEQ";
            default   -> throw new AssemblerException("Operador relacional no soportado: " + operator);
        };
    }

    private void emitConditionalJump(String mnemonic, int dest) {
        switch (mnemonic) {
            case "JLE" -> gen.emitirJle(dest);
            case "JGE" -> gen.emitirJge(dest);
            case "JNE" -> gen.emitirJne(dest);
            case "JEQ" -> gen.emitirJeq(dest);
            default    -> throw new AssemblerException("Salto condicional desconocido: " + mnemonic);
        }
    }

    // ---------------------------------------------------------------
    // Helpers de emisión
    // ---------------------------------------------------------------

    private void loadOp(String operand) {
        if (isNumeric(operand)) {
            int v = parseIntVal(operand);
            gen.emitirMov(v);
            textLines.add("        MOV     #" + v);
        } else {
            gen.emitirLoad(operand);
            textLines.add("        LOAD    " + operand);
        }
    }

    private void loadVar(String name) {
        gen.emitirLoad(name);
        textLines.add("        LOAD    " + name);
    }

    private void storeVar(String name) {
        gen.emitirStore(name);
        textLines.add("        STORE   " + name);
    }

    private void emitArith(String op) {
        String mnem = switch (op) {
            case "+" -> { gen.emitirAdd(); yield "ADD"; }
            case "-" -> { gen.emitirSub(); yield "SUB"; }
            case "*" -> { gen.emitirMul(); yield "MUL"; }
            case "/" -> { gen.emitirDiv(); yield "DIV"; }
            default  -> throw new AssemblerException("Operador no soportado: " + op);
        };
        textLines.add("        " + mnem);
    }

    private void addPending(String label, int pc) {
        pending.computeIfAbsent(label, k -> new ArrayList<>()).add(pc);
    }

    // ---------------------------------------------------------------
    // Salida de texto (.asm)
    // ---------------------------------------------------------------

    public String getTextoEnsamblador() {
        StringBuilder sb = new StringBuilder();
        sb.append("; ============================================================\n");
        sb.append("; PROGRAMA : ").append(programName).append("\n");
        sb.append("; ISA      : Custom Educacional v1.0\n");
        sb.append("; Opcodes  : LOAD=01 STORE=02 MOV=03 ADD=04 SUB=05 MUL=06\n");
        sb.append(";            DIV=07  CMP=08  JMP=09  JGT=0A JLT=0B JEQ=0C\n");
        sb.append(";            JNE=0D  READ=0E WRITE=0F JLE=10 JGE=11\n");
        sb.append(";            CALL=12 RET=13  HALT=FF\n");
        sb.append("; Modos    : 00=#inmediato  01=@directo  02=Rn\n");
        sb.append("; Data base: 0x").append(String.format("%04X", AssemblerSymbolTable.DATA_BASE)).append("\n");
        sb.append("; ============================================================\n\n");

        sb.append("section .data\n");
        for (String line : dataLines) sb.append(line).append("\n");
        sb.append("\n");

        sb.append("section .code\n");
        for (String line : textLines) sb.append(line).append("\n");

        sb.append("\n").append(asm.generarCodigoAbsoluto());
        return sb.toString();
    }

    public void escribirArchivo(String path) throws IOException {
        Files.writeString(Path.of(path), getTextoEnsamblador());
    }

    public Assembler getAssembler() { return asm; }

    // ---------------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------------

    private boolean isLabel(String s) {
        return s != null && s.matches("L\\d+");
    }

    private boolean isNumeric(String s) {
        if (s == null) return false;
        try { Integer.parseInt(s); return true; } catch (NumberFormatException ignore) {}
        try { Float.parseFloat(s); return true;  } catch (NumberFormatException ignore) {}
        return false;
    }

    private int parseIntVal(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return (int) Float.parseFloat(s); }
    }
}
