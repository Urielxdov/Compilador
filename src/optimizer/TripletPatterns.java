package optimizer;

import intermediate.Triplet;
import java.util.List;

public class TripletPatterns {

    private TripletPatterns() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isTemp(String s) {
        return s != null && s.matches("t\\d+");
    }

    public static boolean isMovToTemp(Triplet t) {
        return "mov".equals(t.getInstruccion()) && isTemp(t.getOp1());
    }

    public static boolean isBinaryOpOnTemp(Triplet t, String temp) {
        return temp.equals(t.getOp1()) && t.getOp2() != null
               && !"mov".equals(t.getInstruccion());
    }

    public static boolean isCommutativeOp(String op) {
        return "+".equals(op) || "*".equals(op);
    }

    public static boolean isCommutativeChain(List<String[]> ops) {
        return ops.stream().allMatch(o -> isCommutativeOp(o[0]));
    }

    public static boolean isLiteral(String val) {
        try { Integer.parseInt(val);  return true; } catch (NumberFormatException ignored) {}
        try { Float.parseFloat(val);  return true; } catch (NumberFormatException ignored) {}
        return false;
    }
}
