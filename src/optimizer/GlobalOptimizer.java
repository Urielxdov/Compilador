package optimizer;

import intermediate.IntermediateCode;
import intermediate.Triplet;
import java.nio.file.*;
import java.util.*;

public class GlobalOptimizer {

    public IntermediateCode optimize(IntermediateCode ic, String sourcePath) {
        System.out.println("\n=== OPTIMIZACION GLOBAL ===");

        fase1Normalizacion(sourcePath);
        List<BasicBlock> blocks = fase2BloqueBasicos(ic);
        List<Triplet> optimized = fase3PropagacionConstantes(ic.getTriplets(), blocks);

        return new IntermediateCode(optimized);
    }

    // -----------------------------------------------------------------------
    // Fase 1: Show source file before/after removing blank lines and indentation.
    // -----------------------------------------------------------------------
    private void fase1Normalizacion(String sourcePath) {
        System.out.println("\n--- Fase 1: Normalizacion fuente ---");
        try {
            List<String> lines = Files.readAllLines(Path.of(sourcePath));

            System.out.println("ANTES:");
            lines.forEach(l -> System.out.println("  |" + l + "|"));

            List<String> sinBlancos = lines.stream()
                .filter(l -> !l.isBlank())
                .map(String::trim)
                .toList();

            System.out.println("DESPUES (sin blancos/sangrias):");
            sinBlancos.forEach(l -> System.out.println("  |" + l + "|"));

            String compacto = String.join("", sinBlancos.stream()
                .map(l -> l.replace(" ", "")).toArray(String[]::new));
            System.out.println("DESPUES (compacto):");
            System.out.println("  " + compacto);

        } catch (Exception e) {
            System.out.println("  (no se pudo leer: " + e.getMessage() + ")");
        }
    }

    // -----------------------------------------------------------------------
    // Fase 2: Build and print basic-block graph.
    // -----------------------------------------------------------------------
    private List<BasicBlock> fase2BloqueBasicos(IntermediateCode ic) {
        FlowGraph graph = FlowGraph.build(ic.getTriplets());
        graph.imprimir();
        return graph.getBlocks();
    }

    // -----------------------------------------------------------------------
    // Fase 3: Intra-block constant propagation.
    //   If "= var C" appears (literal assignment) and var is not reassigned,
    //   replace subsequent "mov Ti var" with "mov Ti C".
    //   Clear known constants at control-flow boundaries (LABEL, JMP, JMP_F).
    // -----------------------------------------------------------------------
    private List<Triplet> fase3PropagacionConstantes(List<Triplet> in,
                                                      List<BasicBlock> blocks) {
        System.out.println("\n--- Fase 3: Propagacion de Constantes ---");
        System.out.println("ANTES:");
        in.forEach(t -> System.out.println("  " + t));

        List<Triplet> result = new ArrayList<>(in);
        Map<String, String> constants = new HashMap<>();

        for (int i = 0; i < result.size(); i++) {
            Triplet t     = result.get(i);
            String  instr = t.getInstruccion();

            if ("=".equals(instr) && t.getOp2() != null && isLiteral(t.getOp2())) {
                // Variable assigned a literal — record it
                constants.put(t.getOp1(), t.getOp2());

            } else if ("LABEL".equals(instr) || "JMP".equals(instr) || "JMP_F".equals(instr)) {
                // Control-flow boundary — conservative reset
                constants.clear();

            } else if ("mov".equals(instr) && t.getOp2() != null
                       && constants.containsKey(t.getOp2())) {
                // Replace variable reference with known constant
                result.set(i, new Triplet("mov", t.getOp1(), constants.get(t.getOp2())));

            } else if ("=".equals(instr)) {
                // Variable reassigned to non-literal — invalidate
                constants.remove(t.getOp1());
            }
        }

        System.out.println("DESPUES:");
        result.forEach(t -> System.out.println("  " + t));

        return result;
    }

    private boolean isLiteral(String val) {
        try { Integer.parseInt(val);   return true; } catch (NumberFormatException ignored) {}
        try { Float.parseFloat(val);   return true; } catch (NumberFormatException ignored) {}
        return false;
    }
}
