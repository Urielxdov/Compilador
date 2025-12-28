package parser.grammar;

public class Epsilon extends Terminal{

    private static final Epsilon INSTANCE = new Epsilon();

    public Epsilon () {
        super("ε");
    }


    public static Epsilon getInstance() {
        return INSTANCE;
    }
}
