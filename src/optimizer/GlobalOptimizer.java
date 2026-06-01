package optimizer;

import intermediate.IntermediateCode;
import intermediate.Triplet;
import java.util.List;
import java.util.Objects;

public class GlobalOptimizer {

    private final SourceNormalizer    normalizer;
    private final ControlFlowAnalyzer flowAnalyzer;
    private final ConstantPropagator  propagator;

    public GlobalOptimizer() {
        this.normalizer   = new SourceNormalizer();
        this.flowAnalyzer = new ControlFlowAnalyzer();
        this.propagator   = new ConstantPropagator();
    }

    public void optimize(IntermediateCode ic, String sourcePath) {
        Objects.requireNonNull(ic, "IntermediateCode no puede ser null");
        Objects.requireNonNull(sourcePath, "sourcePath no puede ser null");

        System.out.println("\n=== OPTIMIZACION GLOBAL ===");

        normalizer.display(sourcePath);
        flowAnalyzer.buildBlocks(ic.getTriplets());
        List<Triplet> optimized = propagator.propagate(ic.getTriplets());

        ic.replaceTriplets(optimized);
    }
}
