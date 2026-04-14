package lexer.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * TablaPalabras reservadas
 *
 * Tabla lexica que define las palabras reservadas
 * reconocidas por el lenguaje
 *
 * Una palabra reservada es un identificador con
 * significado semantico especial, que no puede
 * ser utilizado como nombre definido por el usuario.
 *
 * Esta tabla es utilizada por el analizador lexico
 * para diferenciar entre identificadores y palabras reservadas
 */
public class TablaPalabrasReservadas {
    /**Conjunto de palabras reservadas dentro del ellenguaje*/
    //static final Set<Elemento> PALABRAS_RESERVADAS = new Set<>();
    static final Map<String, Integer> PALABRAS_RESERVADAS = new HashMap<>();

    static {
        PALABRAS_RESERVADAS.put("Programa", 400);
        PALABRAS_RESERVADAS.put("Real", 401);
        PALABRAS_RESERVADAS.put("Entero", 402);
        PALABRAS_RESERVADAS.put("Leer", 403);
        PALABRAS_RESERVADAS.put("Escribir", 404);
        PALABRAS_RESERVADAS.put("Si", 405);
        PALABRAS_RESERVADAS.put("Entonces", 406);
        PALABRAS_RESERVADAS.put("Sino", 407);
        PALABRAS_RESERVADAS.put("Inicio", 408);
        PALABRAS_RESERVADAS.put("Fin", 409);
        PALABRAS_RESERVADAS.put("Iniciar", 410);
        PALABRAS_RESERVADAS.put("Int", 411);
        PALABRAS_RESERVADAS.put("Mostrar", 412);
        PALABRAS_RESERVADAS.put("Finalizar", 413);
    }

    private TablaPalabrasReservadas() {}

    /**
     * Verifica si un lexema corresponde a una palabra reservada
     *
     * @param value lexema a evaluar
     * @return true si el lexema es una palara reservada
     */
    public static boolean existe (String value) {
        return PALABRAS_RESERVADAS.containsKey(value);
    }


    public static int getValor(String lexema) {
        return PALABRAS_RESERVADAS.getOrDefault(lexema, -1);
    }

}
