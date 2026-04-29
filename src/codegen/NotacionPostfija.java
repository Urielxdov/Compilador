package codegen;

// Notacion postfija: arg1 arg2 OP -> result
public class NotacionPostfija implements INotacion {
    private String n(String s) { return s != null ? s : ""; }

    @Override
    public String representar(Cuadruplo c) {
        OpKind op = c.getOp();
        String result = n(c.getResult());
        String arg1 = n(c.getArg1());
        String arg2 = n(c.getArg2());

        switch (op) {
            // Aritmeticos y relacionales: arg1 arg2 OP -> result
            case ADDR: case REST: case MULT: case DIV:
            case MAYOR: case MENOR: case DIST: case IGUAL:
                return String.format("%02d %s %s %s -> %s",
                        c.getIndice(), arg1, arg2, op, result);

            // Unario: arg1 OP -> result
            case NEG:
                return String.format("%02d %s %s -> %s",
                        c.getIndice(), arg1, op, result);

            // Asignacion: arg1 -> result
            case ASSIGN:
                return String.format("%02d %s -> %s",
                        c.getIndice(), arg1, result);

            // Declaraciones: result VARI/VARR
            case VARI: case VARR:
                return String.format("%02d %s %s", c.getIndice(), result, op);

            // E/S: result READ/WRITE
            case READ: case WRITE:
                return String.format("%02d %s %s", c.getIndice(), result, op);

            // Control de flujo
            case IF_FALSE:
                return String.format("%02d %s IF_FALSE -> %s", c.getIndice(), result, arg1);
            case GOTO:
                return String.format("%02d GOTO %s", c.getIndice(), arg1);
            case LABEL:
                return String.format("%02d LABEL %s:", c.getIndice(), result);
            case HALT:
                return String.format("%02d HALT", c.getIndice());

            default:
                return c.toString();
        }
    }
}
