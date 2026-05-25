package optimizer;

import java.nio.file.*;
import java.util.*;

public class SourceNormalizer {

    public void display(String sourcePath) {
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
}
