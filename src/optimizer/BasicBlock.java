package optimizer;

import intermediate.Triplet;
import java.util.*;

public class BasicBlock {
    private final String label;
    private final List<Triplet> triplets;
    private final List<String> successors = new ArrayList<>();

    public BasicBlock(String label, List<Triplet> triplets) {
        this.label    = label;
        this.triplets = Collections.unmodifiableList(new ArrayList<>(triplets));
    }

    public String        getLabel()      { return label; }
    public List<Triplet> getTriplets()   { return triplets; }
    public List<String>  getSuccessors() { return successors; }
    public void addSuccessor(String s)   { if (!successors.contains(s)) successors.add(s); }
}
