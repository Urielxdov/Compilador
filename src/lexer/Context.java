package lexer;

import data_structures.Lista;
import data_structures.Set;
import io.FileReaderManager;
import io.RutaArchivos;
import lexer.handlers.errors.LexicalError;
import lexer.validators.boundaries.FinalLexema;
import lexer.validators.boundaries.IdentificadorLimites;

/**
 * Contexto central del analisis lexico
 *
 * Mantiene el estado actual del proceso de tokenizacion
 * - Linea actual
 * - Punteros de inicio y fin de lexema
 * - Tokens reconocidos
 * - Errores lexicos
 * - Tabla de simbolos
 *
 * Es compartido por los distintos TokenHandler y validadores
 * durante el analisis del programa fuente
 */
public class Context {
    private IdentificadorLimites limitador;
    private Lista<Token> tokens; // Simbolos que encontremos
    private Lista<LexicalError> errores; // Para la tabla de errores
    private Lista<Token> simbolos; // Simbolos encontrados
    private Lista<String> palabrasReservadas; // Palabras reservadas
    private Set<String> caracteresSimples; // Caracteres sumples

    private Lista<String> programa;
    private int numeroLinea;
    private String lineaActual;
    private int punteroInicial;
    private int punteroFinal;

    private Token tokenActual;

    public Context() {
        iniciarVariables();
    }

    private void iniciarVariables () {
        this.tokens = new Lista<>();
        this.errores = new Lista<>();
        this.simbolos = new Lista<>();
        this.palabrasReservadas = new Lista<>();
        this.caracteresSimples = new Set<>();
        this.numeroLinea = 0;
        this.punteroFinal = 0;
        this.punteroInicial = 0;
        this.limitador = new FinalLexema();

        this.caracteresSimples.add(";");
        this.caracteresSimples.add("=");
        this.caracteresSimples.add("+");
        this.caracteresSimples.add("-");
        this.caracteresSimples.add("*");
        this.caracteresSimples.add("(");
        this.caracteresSimples.add(")");
        this.caracteresSimples.add(",");
        this.caracteresSimples.add("<");
        this.caracteresSimples.add(">");
        this.caracteresSimples.add("==");
        this.caracteresSimples.add("<>");

        this.palabrasReservadas.agregar("Programa");
        this.palabrasReservadas.agregar("Real");
        this.palabrasReservadas.agregar("Entero");
        this.palabrasReservadas.agregar("Leer");
        this.palabrasReservadas.agregar("Escribir");
        this.palabrasReservadas.agregar("Si");
        this.palabrasReservadas.agregar("Entonces");
        this.palabrasReservadas.agregar("Sino");
        this.palabrasReservadas.agregar("Inicio");
        this.palabrasReservadas.agregar("Fin");

        this.programa = new FileReaderManager().leerArchivo(RutaArchivos.PROGRAMA);
        lineaActual = programa.obtener(numeroLinea);
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

    /**
     * Establece el puntero inicial del lexema
     *
     * @throws IllegalArgumentException si supera al puntero final
     */
    public void setPunteroInicial(int nuevaPosicion) {
        if (nuevaPosicion > punteroFinal) throw new IllegalArgumentException("El puntero inicial no puede ser mayor que el puntero final");
        this.punteroInicial = nuevaPosicion;
    }

    public void setPunteroFinal(int nuevaPosicion) {
        if (nuevaPosicion > this.lineaActual.length()) throw new IllegalArgumentException("El puntero final no puede ser mas alto que la longitud de la cadena actual");
        this.punteroFinal = nuevaPosicion;
    }

    /**
     * Avanza a la siguiente linea del programa si el puntero
     * alcanzo el final de la linea actual
     *
     * @return true si se alcanzo el final del archivo, false en caso contrario
     */
    public boolean finArchivo() {
        if (punteroFinal >= lineaActual.length()) {
            if (numeroLinea >= programa.nodosExistentes()) {
                return true;
            } else {
                numeroLinea++;
                if(numeroLinea >= programa.nodosExistentes()) return true;
                lineaActual = programa.obtener(numeroLinea);
                while ((lineaActual == null || lineaActual.isEmpty()) && numeroLinea <= programa.nodosExistentes()) {
                    lineaActual = programa.obtener(numeroLinea);
                    numeroLinea++;
                }
                punteroFinal = 0;
                punteroInicial = 0;
                return false;
            }
        } else return false;
    }

    /**
     * Verificar si el caracter actual marca el fin valido de un lexema
     *
     * @return true si el puntero esta en un limite valido, false en caso contrario
     */
    public boolean limitador() {
        if (punteroFinal >= lineaActual.length()) {
            return true;
        }

        char c = lineaActual.charAt(punteroFinal);
        return limitador.verificar(c) || caracteresSimples.contains(String.valueOf(c));
    }


    /**
     * Consume caracteres desde el puntero inicial hasta encontrar
     * un delimitador valido y devuelve el lexema resultante
     *
     * @return lexema consumido
     */
    public String consumirLexema() {
        int inicio = punteroInicial;

        while (punteroFinal < lineaActual.length() && !limitador.verificar(lineaActual.charAt(punteroFinal)) && !caracteresSimples.contains(String.valueOf(lineaActual.charAt(punteroFinal)))) {
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

    public boolean isReservedWord (String lexema) {
        return palabrasReservadas.existe(lexema);
    }

    public boolean isSimpleCharacter (String c) {
        return caracteresSimples.contains(c);
    }

    public void agregarSimbolo (Token simbolo) {
        if (simbolos.nodosExistentes() == 0 || !simbolos.existe(simbolo)) simbolos.agregar(simbolo);
    }

    public void setTokenActual(Token t) {
        this.tokenActual = t;
    }


    public Token getTokenActual() {
        return tokenActual;
    }

    @Override
    public String toString() {
        String string = "";
        string += "Palabras reservadas: \n";
        string += palabrasReservadas.toString();
        string += "\nSimbolos: \n";
        string += simbolos.toString();
        string += "\nTokens:\n";
        string += tokens.toString();
        string += "\nErrores:\n";
        string += errores.toString();
        return string;
    }
}
