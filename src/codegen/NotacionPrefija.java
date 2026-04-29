package codegen;

// Notacion prefija: OP result arg1 arg2
public class NotacionPrefija implements INotacion {
    private String n(String s) { return s != null ? s : ""; }

    @Override
    public String representar(Cuadruplo c) {
        OpKind op = c.getOp();
        String result = n(c.getResult());
        String arg1 = n(c.getArg1());
        String arg2 = n(c.getArg2());

        switch (op) {
            // Aritmeticos y relacionales: OP result arg1 arg2
            case ADDR: case REST: case MULT: case DIV:
            case MAYOR: case MENOR: case DIST: case IGUAL:
                return String.format("%02d %s %s %s %s",
                        c.getIndice(), op, result, arg1, arg2);

            // Unario: OP result arg1
            case NEG:
                return String.format("%02d %s %s %s",
                        c.getIndice(), op, result, arg1);

            // Asignacion: ASSIGN result arg1
            case ASSIGN:
                return String.format("%02d ASSIGN %s %s",
                        c.getIndice(), result, arg1);

            // Declaraciones
            case VARI: case VARR:
                return String.format("%02d %s %s", c.getIndice(), op, result);

            // E/S
            case READ: case WRITE:
                return String.format("%02d %s %s", c.getIndice(), op, result);

            // Control de flujo
            case IF_FALSE:
                return String.format("%02d IF_FALSE %s %s", c.getIndice(), result, arg1);
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
