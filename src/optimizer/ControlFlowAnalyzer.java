package optimizer;

import intermediate.Triplet;
import java.util.List;

public class ControlFlowAnalyzer {

    public List<BasicBlock> buildBlocks(List<Triplet> triplets) {
        FlowGraph graph = FlowGraph.build(triplets);
        graph.imprimir();
        return graph.getBlocks();
    }
}
