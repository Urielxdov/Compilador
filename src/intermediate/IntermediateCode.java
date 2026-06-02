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
     * REQ-03: print source file with each line's optimized triplets shown aligned.
     * Muestra OBLIGATORIAMENTE todas las líneas del archivo fuente (con o sin tercetos).
     */
    public void imprimirConFuente(String sourcePath) {
        List<String> sourceLines = new ArrayList<>();
        try {
            sourceLines = Files.readAllLines(Path.of(sourcePath));
        } catch (Exception e) {
            System.out.println("  (no se pudo leer fuente: " + e.getMessage() + ")");
            return;
        }

        System.out.println("\n========================================================================================================");
        System.out.println("===                        TERCETOS OPTIMIZADOS | CODIGO FUENTE COMPLETO                        ===");
        System.out.println("========================================================================================================");
        System.out.printf("%-7s %-50s %s%n", "LÍNEA", "CÓDIGO FUENTE ORIGINAL", "TERCETOS ASOCIADOS");
        System.out.println("-".repeat(104));

        for (int i = 0; i < sourceLines.size(); i++) {
            int lineNo = i + 1;
            String sourceCode = sourceLines.get(i).trim();
            boolean firstTriplet = true;

            // Buscamos todos los tercetos que pertenezcan a la línea actual
            for (int j = 0; j < triplets.size(); j++) {
                Triplet t = triplets.get(j);

                if (t.getSourceLineNumber() == lineNo) {
                    // Formateamos el terceto de manera explícita mostrando su índice y sus operadores completos
                    String tripletStr = String.format("(%d) Inst: %-8s | Op1: %-10s | Op2: %-10s",
                            j,
                            t.getInstruccion(),
                            t.getOp1() != null ? t.getOp1() : "null",
                            t.getOp2() != null ? t.getOp2() : "null");

                    if (firstTriplet) {
                        System.out.printf("%-7d %-50s -> %s%n", lineNo, sourceCode, tripletStr);
                        firstTriplet = false;
                    } else {
                        // Si la línea generó múltiples tercetos, se alinean abajo limpiamente
                        System.out.printf("%-7s %-50s -> %s%n", "", "", tripletStr);
                    }
                }
            }

            // Si la línea del código fuente no generó tercetos (como Inicio, Fin, comentarios), se imprime limpia
            if (firstTriplet) {
                System.out.printf("%-7d %-50s |%n", lineNo, sourceCode);
            }
        }
        System.out.println("-".repeat(104));
    }
}