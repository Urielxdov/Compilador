package optimizer;

import intermediate.IntermediateCode;
import intermediate.Triplet;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class LocalOptimizer {

    public IntermediateCode optimize(IntermediateCode ic) {
        Objects.requireNonNull(ic, "IntermediateCode no puede ser null");
        List<Triplet> current = new ArrayList<>(ic.getTriplets());
        if (current.isEmpty()) return ic;

        int maxIterations = 3;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            int beforeSize = current.size();

            current = runPass("Pase 1 - Subexpresiones Redundantes", current, this::pase1Redundantes);
            current = runPass("Pase 2 - Reutilizacion CSE",          current, this::pase2CSE);
            current = runPass("Pase 3 - Reducciones Algebraicas",    current, this::pase3Algebraicas);
            current = runPass("Pase 4 - Codigo Muerto",              current, this::pase4CodigoMuerto);

            if (current.size() == beforeSize) {
                System.out.println("[LOCAL] Punto fijo alcanzado en iteracion " + (iteration + 1));
                break;
            }
        }

        return new IntermediateCode(current);
    }

    private List<Triplet> runPass(String nombre, List<Triplet> antes,
                                  UnaryOperator<List<Triplet>> pass) {
        System.out.println("\n=== OPTIMIZACION LOCAL: " + nombre + " ===");
        System.out.println("ANTES:");
        antes.forEach(t -> System.out.println("  " + t));
        List<Triplet> despues = pass.apply(antes);
        System.out.println("DESPUES:");
        despues.forEach(t -> System.out.println("  " + t));
        return despues;
    }

    // -----------------------------------------------------------------------
    // Pase 1: Find exact duplicate (mov Ti A, op Ti B) pairs.
    //   Second occurrence is redundant — remove it and replace Ti refs with first Ti.
    // -----------------------------------------------------------------------
    private List<Triplet> pase1Redundantes(List<Triplet> in) {
        Map<String, String> exprToTemp = new LinkedHashMap<>(); // "A op B" -> first temp
        Map<String, String> replacement = new HashMap<>();       // redundant temp -> first temp
        Set<Integer> toRemove = new HashSet<>();

        for (int i = 0; i + 1 < in.size(); i++) {
            Triplet t1 = in.get(i);
            Triplet t2 = in.get(i + 1);
            if (!TripletPatterns.isMovToTemp(t1) || !TripletPatterns.isBinaryOpOnTemp(t2, t1.getOp1())) continue;

            String key = t1.getOp2() + " " + t2.getInstruccion() + " " + t2.getOp2();
            String temp = t1.getOp1();

            if (exprToTemp.containsKey(key)) {
                replacement.put(temp, exprToTemp.get(key));
                toRemove.add(i);
                toRemove.add(i + 1);
            } else {
                exprToTemp.put(key, temp);
            }
        }

        return applyReplacements(filterOut(in, toRemove), replacement);
    }

    // -----------------------------------------------------------------------
    // Pase 4: Remove (mov Ti X, op Ti Y) pairs where Ti is never used after.
    //   "Used" means Ti appears in op2, or as op1 of Mostrar.
    // -----------------------------------------------------------------------
    private List<Triplet> pase4CodigoMuerto(List<Triplet> in) {
        Set<Integer> toRemove = new HashSet<>();

        for (int i = 0; i + 1 < in.size(); i++) {
            Triplet t1 = in.get(i);
            Triplet t2 = in.get(i + 1);
            if (!TripletPatterns.isMovToTemp(t1) || !TripletPatterns.isBinaryOpOnTemp(t2, t1.getOp1())) continue;

            String temp = t1.getOp1();
            boolean usedAfter = false;
            for (int j = i + 2; j < in.size(); j++) {
                Triplet t = in.get(j);
                if (temp.equals(t.getOp2())) { usedAfter = true; break; }
                if ("Mostrar".equals(t.getInstruccion()) && temp.equals(t.getOp1())) {
                    usedAfter = true; break;
                }
            }
            if (!usedAfter) {
                toRemove.add(i);
                toRemove.add(i + 1);
            }
        }

        return filterOut(in, toRemove);
    }

    // -----------------------------------------------------------------------
    // Pase 2: CSE — if Ti's ops-chain is a suffix of Tj's ops-chain (commutative ops only),
    //   AND the op just before the suffix in Tj uses Ti's base as op2,
    //   replace the feeder op + suffix ops with a single: opsI[0].op Tj Ti.
    //
    // Example:
    //   Ti: mov t1 X, + t1 10  → base=X, ops=[(+,10)]
    //   Tj: mov t2 Y, + t2 X, + t2 10 → base=Y, ops=[(+,X),(+,10)]
    //   Match: ops_j ends with ops_i AND ops_j[suffixStart-1].op2 == X (Ti's base)
    //   Replace: remove "+ t2 X" and "+ t2 10", add "+ t2 t1"
    //   Result: mov t2 Y, + t2 t1
    // -----------------------------------------------------------------------
    private List<Triplet> pase2CSE(List<Triplet> in) {
        // Build per-temp: movIndex, base value, ops chain (indices + content)
        Map<String, String>        tempBase    = new LinkedHashMap<>();
        Map<String, List<Integer>> tempOpIdxs  = new LinkedHashMap<>();
        Map<String, List<String[]>> tempOps    = new LinkedHashMap<>();

        for (int i = 0; i < in.size(); i++) {
            Triplet t = in.get(i);
            if (TripletPatterns.isMovToTemp(t)) {
                String ti = t.getOp1();
                tempBase.put(ti, t.getOp2());
                tempOpIdxs.put(ti, new ArrayList<>());
                tempOps.put(ti, new ArrayList<>());
            } else if (TripletPatterns.isTemp(t.getOp1()) && tempOps.containsKey(t.getOp1())) {
                String ti = t.getOp1();
                tempOpIdxs.get(ti).add(i);
                tempOps.get(ti).add(new String[]{t.getInstruccion(), t.getOp2()});
            }
        }

        List<Triplet> result = new ArrayList<>(in);
        Set<Integer>  toRemove = new HashSet<>();

        outer:
        for (String ti : tempOps.keySet()) {
            List<String[]> opsI = tempOps.get(ti);
            if (opsI.isEmpty() || !TripletPatterns.isCommutativeChain(opsI)) continue;
            String baseI = tempBase.get(ti);

            for (String tj : tempOps.keySet()) {
                if (ti.equals(tj)) continue;
                List<String[]>  opsJ = tempOps.get(tj);
                List<Integer>   idxJ = tempOpIdxs.get(tj);
                if (opsJ.size() < opsI.size() + 1) continue;

                int suffixStart = opsJ.size() - opsI.size();

                // Check suffix match
                boolean match = true;
                for (int k = 0; k < opsI.size(); k++) {
                    if (!opsJ.get(suffixStart + k)[0].equals(opsI.get(k)[0]) ||
                        !opsJ.get(suffixStart + k)[1].equals(opsI.get(k)[1])) {
                        match = false; break;
                    }
                }
                if (!match) continue;

                // Check feeder: element just before suffix uses Ti's base
                if (!opsJ.get(suffixStart - 1)[1].equals(baseI)) continue;

                // Apply CSE: remove feeder + all-but-last suffix ops, replace last with + tj ti
                for (int k = suffixStart - 1; k < opsJ.size() - 1; k++) {
                    toRemove.add(idxJ.get(k));
                }
                int lastIdx = idxJ.get(opsJ.size() - 1);
                result.set(lastIdx, new Triplet(opsI.get(0)[0], tj, ti));
                break outer;
            }
        }

        return filterOut(result, toRemove);
    }

    // -----------------------------------------------------------------------
    // Pase 3: Remove or simplify identity operations.
    // -----------------------------------------------------------------------
    private List<Triplet> pase3Algebraicas(List<Triplet> in) {
        List<Triplet> result = new ArrayList<>();
        for (Triplet t : in) {
            String op  = t.getInstruccion();
            String op2 = t.getOp2();
            if (("+".equals(op) || "-".equals(op)) && "0".equals(op2)) continue; // X +/- 0 -> remove
            if (("*".equals(op) || "/".equals(op)) && "1".equals(op2)) continue; // X */÷ 1 -> remove
            if ("*".equals(op) && "0".equals(op2)) {                              // X * 0 -> mov Ti 0
                result.add(new Triplet("mov", t.getOp1(), "0"));
                continue;
            }
            result.add(t);
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private List<Triplet> filterOut(List<Triplet> list, Set<Integer> indices) {
        List<Triplet> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) if (!indices.contains(i)) out.add(list.get(i));
        return out;
    }

    private List<Triplet> applyReplacements(List<Triplet> list, Map<String, String> map) {
        if (map.isEmpty()) return list;
        return list.stream()
            .map(t -> new Triplet(t.getInstruccion(),
                                  map.getOrDefault(t.getOp1(), t.getOp1()),
                                  t.getOp2() != null ? map.getOrDefault(t.getOp2(), t.getOp2()) : null))
            .collect(Collectors.toList());
    }
}
