package optimizer;

import intermediate.Triplet;
import java.util.*;

public class ConstantPropagator {

    public List<Triplet> propagate(List<Triplet> triplets) {
        System.out.println("\n--- Fase 3: Propagacion de Constantes ---");
        System.out.println("ANTES:");
        triplets.forEach(t -> System.out.println("  " + t));

        List<Triplet> result = new ArrayList<>(triplets);
        Map<String, String> constants = new HashMap<>();

        for (int i = 0; i < result.size(); i++) {
            Triplet t     = result.get(i);
            String  instr = t.getInstruccion();

            if ("=".equals(instr) && t.getOp2() != null && TripletPatterns.isLiteral(t.getOp2())) {
                constants.put(t.getOp1(), t.getOp2());

            } else if ("LABEL".equals(instr) || "JMP".equals(instr) || "JMP_F".equals(instr)) {
                constants.clear();

            } else if ("mov".equals(instr) && t.getOp2() != null
                       && constants.containsKey(t.getOp2())) {
                result.set(i, new Triplet("mov", t.getOp1(), constants.get(t.getOp2())));

            } else if ("=".equals(instr)) {
                constants.remove(t.getOp1());
            }
        }

        System.out.println("DESPUES:");
        result.forEach(t -> System.out.println("  " + t));

        return result;
    }

}
