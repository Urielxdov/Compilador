package codegen;

public class Cuadruplo {
    private int indice;
    private OpKind op;
    private String result;
    private String arg1;
    private String arg2;

    public Cuadruplo(int indice, OpKind op, String result, String arg1, String arg2) {
        this.indice = indice;
        this.op = op;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public int getIndice() { return indice; }
    public OpKind getOp() { return op; }
    public String getResult() { return result; }
    public String getArg1() { return arg1; }
    public String getArg2() { return arg2; }

    public void setResult(String result) { this.result = result; }
    public void setArg1(String arg1) { this.arg1 = arg1; }
    public void setArg2(String arg2) { this.arg2 = arg2; }

    private String n(String s) { return s != null ? s : "NULL"; }

    @Override
    public String toString() {
        return String.format("%02d (%s, %s, %s, %s)", indice, op, n(result), n(arg1), n(arg2));
    }
}
