package intermediate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntermediateCode {
    private final List<Triplet> triplets;

    public IntermediateCode(List<Triplet> triplets) {
        this.triplets = Collections.unmodifiableList(new ArrayList<>(triplets));
    }

    public List<Triplet> getTriplets() { return triplets; }

    public void imprimir() {
        System.out.printf("%-12s %-10s %s%n", "Instruccion", "Op1", "Op2");
        System.out.println("-".repeat(36));
        for (Triplet t : triplets) System.out.println(t);
    }
}
