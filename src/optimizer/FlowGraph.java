package optimizer;

import intermediate.Triplet;
import java.util.*;

public class FlowGraph {
    private final List<BasicBlock> blocks;

    private FlowGraph(List<BasicBlock> blocks) { this.blocks = blocks; }

    public List<BasicBlock> getBlocks() { return blocks; }

    /**
     * Partition triplets into basic blocks using three leader rules:
     * 1. First instruction is a leader.
     * 2. Any instruction immediately after a JMP or JMP_F is a leader.
     * 3. Any LABEL triplet is a leader (it is the target of a jump).
     */
    public static FlowGraph build(List<Triplet> triplets) {
        if (triplets.isEmpty()) return new FlowGraph(List.of());

        Set<Integer> leaderSet = new TreeSet<>();
        leaderSet.add(0);
        for (int i = 0; i < triplets.size(); i++) {
            String instr = triplets.get(i).getInstruccion();
            if ("JMP".equals(instr) || "JMP_F".equals(instr)) {
                if (i + 1 < triplets.size()) leaderSet.add(i + 1);
            }
            if ("LABEL".equals(instr)) leaderSet.add(i);
        }

        List<Integer> leaders = new ArrayList<>(leaderSet);
        List<BasicBlock> blocks = new ArrayList<>();

        for (int b = 0; b < leaders.size(); b++) {
            int start = leaders.get(b);
            int end   = (b + 1 < leaders.size()) ? leaders.get(b + 1) : triplets.size();
            blocks.add(new BasicBlock("B" + b, new ArrayList<>(triplets.subList(start, end))));
        }

        // Build edges
        for (int b = 0; b < blocks.size(); b++) {
            BasicBlock block = blocks.get(b);
            List<Triplet> bt = block.getTriplets();
            if (bt.isEmpty()) continue;

            Triplet last  = bt.get(bt.size() - 1);
            String  instr = last.getInstruccion();

            if ("JMP".equals(instr)) {
                // Unconditional: find block that contains LABEL <op1>
                findBlockWithLabel(blocks, last.getOp1())
                    .ifPresent(tgt -> block.addSuccessor(tgt.getLabel()));

            } else if ("JMP_F".equals(instr)) {
                // Conditional: fall-through to next block AND jump to target
                if (b + 1 < blocks.size()) block.addSuccessor(blocks.get(b + 1).getLabel());
                findBlockWithLabel(blocks, last.getOp2())
                    .ifPresent(tgt -> block.addSuccessor(tgt.getLabel()));

            } else if (!"LABEL".equals(instr)) {
                // Normal fall-through (LABEL blocks don't fall-through automatically)
                if (b + 1 < blocks.size()) block.addSuccessor(blocks.get(b + 1).getLabel());
            }
        }

        return new FlowGraph(blocks);
    }

    private static Optional<BasicBlock> findBlockWithLabel(List<BasicBlock> blocks,
                                                             String labelName) {
        return blocks.stream()
            .filter(bl -> bl.getTriplets().stream()
                .anyMatch(t -> "LABEL".equals(t.getInstruccion())
                            && labelName.equals(t.getOp1())))
            .findFirst();
    }

    public void imprimir() {
        System.out.println("\n--- Fase 2: Bloques basicos + grafo de flujo ---");
        for (BasicBlock b : blocks) {
            System.out.println("BLOQUE " + b.getLabel() + ":");
            b.getTriplets().forEach(t -> System.out.println("  " + t));
        }
        System.out.print("Grafo: ");
        List<String> edges = new ArrayList<>();
        for (BasicBlock b : blocks)
            b.getSuccessors().forEach(s -> edges.add(b.getLabel() + " -> " + s));
        System.out.println(edges.isEmpty() ? "(sin saltos)" : String.join(", ", edges));
    }
}
