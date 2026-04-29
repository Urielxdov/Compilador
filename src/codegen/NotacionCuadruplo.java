package codegen;

// Notacion estandar: (OP, result, arg1, arg2)
public class NotacionCuadruplo implements INotacion {
    @Override
    public String representar(Cuadruplo c) {
        return c.toString(); // ya usa formato (OP, result, arg1, arg2)
    }
}
