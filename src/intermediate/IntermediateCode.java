package intermediate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IntermediateCode {
    private final List<Triplet> triplets;

    public IntermediateCode(List<Triplet> triplets) {
        this.triplets = new ArrayList<>(triplets);
    }

    public List<Triplet> getTriplets() { return triplets; }

    /** In-place bulk replacement — satisfies REQ-01 (same instance, no new object). */
    public void replaceTriplets(List<Triplet> newList) {
        triplets.clear();
        triplets.addAll(newList);
    }

    /**
     * REQ-02: validate that every JMP/JMP_F target label has a matching LABEL triplet.
     * Returns true when all targets resolve correctly.
     */
    public boolean validateJumps() {
        Set<String> defined = triplets.stream()
            .filter(t -> "LABEL".equals(t.getInstruccion()))
            .map(Triplet::getOp1)
            .collect(Collectors.toSet());

        boolean ok = true;
        for (Triplet t : triplets) {
            String target = null;
            if ("JMP".equals(t.getInstruccion()))   target = t.getOp1();
            if ("JMP_F".equals(t.getInstruccion())) target = t.getOp2();
            if (target != null && !defined.contains(target)) {
                System.out.println("[WARN] Salto roto: " + t.getInstruccion() + " -> " + target);
                ok = false;
            }
        }
        if (ok) System.out.println("[OK] Validacion de saltos: todos los destinos existen.");
        return ok;
    }

    public void imprimir() {
        System.out.printf("%-12s %-10s %s%n", "Instruccion", "Op1", "Op2");
        System.out.println("-".repeat(36));
        for (Triplet t : triplets) System.out.println(t);
    }

    /**
     * REQ-03: print source file with each line's optimized triplets shown directly below it.
     * Grouping uses sourceLineNumber (semantic), not array position (positional).
     */
    public void imprimirConFuente(String sourcePath) {
        List<String> sourceLines = new ArrayList<>();
        try {
            sourceLines = Files.readAllLines(Path.of(sourcePath));
        } catch (Exception e) {
            System.out.println("  (no se pudo leer fuente: " + e.getMessage() + ")");
        }

        // Build sourceLineNumber -> list of formatted triplet strings
        Map<Integer, List<String>> byLine = new LinkedHashMap<>();
        for (int i = 0; i < triplets.size(); i++) {
            Triplet t = triplets.get(i);
            byLine.computeIfAbsent(t.getSourceLineNumber(), k -> new ArrayList<>())
                  .add(String.format("    -> (%d) %s", i, t));
        }

        System.out.println("\n=== TERCETOS OPTIMIZADOS | CODIGO FUENTE ORIGINAL ===");
        System.out.println("-".repeat(60));
        for (int i = 0; i < sourceLines.size(); i++) {
            int lineNo = i + 1;
            System.out.printf("(%d) %s%n", lineNo, sourceLines.get(i));
            if (byLine.containsKey(lineNo)) {
                byLine.get(lineNo).forEach(System.out::println);
            }
        }
    }
}
