package lexer;

import data_structures.Lista;
import data_structures.Set;

public class Context {
    private Lista<String> tokens; // Simbolos que encontremos
    private  Lista<String> errores; // Para la tabla de errores
    private Set<String> simbolos; // Simbolos encontrados
    private Set<String> palabrasReservadas; // Palabras reservadas
    private Set<String> caracteresSimples; // Caracteres sumples
    private int numeroLinea;
    private String lineaActual;
    private int punteroInicial;
    private int punteroFinal;

    public Context() {
        this.tokens = new Lista<>();
        this.errores = new Lista<>();
        this.simbolos = new Set<>();
        this.palabrasReservadas = new Set<>();
        this.caracteresSimples = new Set<>();
        this.numeroLinea = 0;
        this.lineaActual = null;
        this.punteroFinal = 0;
        this.punteroInicial = 0;
    }

    public boolean hasMoreChars() {
        return punteroFinal < lineaActual.length();
    }


}
