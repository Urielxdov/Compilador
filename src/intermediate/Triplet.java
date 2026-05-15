package intermediate;

import java.util.Objects;

public class Triplet {
    private final String instruccion;
    private final String op1;
    private final String op2;

    public Triplet(String instruccion, String op1, String op2) {
        this.instruccion = instruccion;
        this.op1 = op1;
        this.op2 = op2;
    }

    public Triplet(String instruccion, String op1) { this(instruccion, op1, null); }
    public Triplet(String instruccion) { this(instruccion, null, null); }

    public String getInstruccion() { return instruccion; }
    public String getOp1()        { return op1; }
    public String getOp2()        { return op2; }

    public Triplet withOp1(String newOp1) { return new Triplet(instruccion, newOp1, op2); }
    public Triplet withOp2(String newOp2) { return new Triplet(instruccion, op1, newOp2); }

    @Override
    public String toString() {
        String c1 = String.format("%-12s", instruccion);
        String c2 = op1 != null ? String.format("%-10s", op1) : "          ";
        String c3 = op2 != null ? op2 : "";
        return c1 + c2 + c3;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triplet t)) return false;
        return instruccion.equals(t.instruccion)
            && Objects.equals(op1, t.op1)
            && Objects.equals(op2, t.op2);
    }

    @Override
    public int hashCode() { return Objects.hash(instruccion, op1, op2); }
}
