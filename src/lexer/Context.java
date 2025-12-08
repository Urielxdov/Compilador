package lexer;

import data_structures.Lista;
import data_structures.Set;
import lexer.handlers.errors.LexicalError;
import lexer.validators.boundaries.FinalLexema;
import lexer.validators.boundaries.IdentificadorLimites;

public class Context {
    private final IdentificadorLimites limitador;
    private Lista<Token> tokens; // Simbolos que encontremos
    private  Lista<LexicalError> errores; // Para la tabla de errores
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
        this.lineaActual = "<>";
        this.punteroFinal = 0;
        this.punteroInicial = 0;
        this.limitador = new FinalLexema();
    }

    public boolean hasMoreChars() {
        return punteroFinal < lineaActual.length();
    }

    public String getLineaActual() {
        return lineaActual;
    }

    public int getPunteroInicial() {
        return this.punteroInicial;
    }

    public int getPunteroFinal() {
        return this.punteroFinal;
    }

    public void incrementarPunteroFinal() {
        this.punteroFinal++;
    }

    public void incrementarPunteroInicial() {
        this.punteroInicial++;
    }

    public void setPunteroInicial(int nuevaPosicion) {
        if (nuevaPosicion > punteroFinal) throw new IllegalArgumentException("El puntero inicial no puede ser mayor que el puntero final");
        this.punteroInicial = nuevaPosicion;
    }

    public void setPunteroFinal(int nuevaPosicion) {
        if (nuevaPosicion > this.lineaActual.length()) throw new IllegalArgumentException("El puntero final no puede ser mas alto que la longitud de la cadena actual");
        this.punteroFinal = nuevaPosicion;
    }

    public boolean finArchivo() {
        return punteroFinal == lineaActual.length();
    }

    public boolean limitador() {
        return punteroFinal >= lineaActual.length() || limitador.verificar(lineaActual.charAt(punteroFinal));
    }

    public String consumirLexema() {
        int inicio = punteroInicial;

        while (punteroFinal < lineaActual.length() && !limitador.verificar(lineaActual.charAt(punteroFinal))) {
            punteroFinal++;
        }

        String lexema = lineaActual.substring(inicio, punteroFinal);

        punteroInicial = punteroFinal;

        return lexema;
    }


    public void agregarToken(Token token) {
        this.tokens.agregar(token);
    }

    public Lista<Token> getTokens() {
        return tokens;
    }

    public void agregarError(LexicalError error) {
        this.errores.agregar(error);
    }

    public Lista<LexicalError> getErrores() {
        return errores;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }
}
