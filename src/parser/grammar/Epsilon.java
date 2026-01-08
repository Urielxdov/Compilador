package parser.grammar;

/**
 * Representa el simbolo especial ε, que denota
 * la cadena vacia dentro de una gramatica libre de contexto
 *
 * Epsilon se modela como un {@link Terminal} especial y se
 * implementa mediante el patron singleton, ya que debe
 * existir una unica instancia de ε en todo el sistema
 *
 * Este simbolo es utilizado principalmente durante el
 * calculo de los conjuntos FIRST y FOLLOW
 */
public class Epsilon extends Terminal{

    private static final Epsilon INSTANCE = new Epsilon();

    public Epsilon () {
        super("ε");
    }


    public static Epsilon getInstance() {
        return INSTANCE;
    }
}
