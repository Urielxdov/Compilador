package lexer;

import java.util.ArrayList;
import java.util.List;

import data_structures.Lista;
import data_structures.Set;
import io.FileReaderManager;
import io.RutaArchivos;
import lexer.handlers.errors.LexicalError;
import lexer.tokens.NodoLineaToken;
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
    private List<NodoLineaToken> tokens; // Simbolos que encontremos
    private List<LexicalError> errores; // Para la tabla de errores
    private List<Token> simbolos; // Simbolos encontrados
    private List<String> palabrasReservadas; // Palabras reservadas
    private Set<String> caracteresSimples; // Caracteres sumples

    private List<String> programa;
    private int numeroLinea;
    private String lineaActual;
    private int punteroInicial;
    private int punteroFinal;

    private Token tokenActual;

    public Context() {
        iniciarVariables();
    }

    private void iniciarVariables () {
        this.tokens = new ArrayList<>();
        this.errores = new ArrayList<>();
        this.simbolos = new ArrayList<>();
        this.palabrasReservadas = new ArrayList<>();
        this.caracteresSimples = new Set<>();
        this.numeroLinea = 0; // Cuidado, tiene un error logico por alguna razon
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

        this.palabrasReservadas.add("Programa");
        this.palabrasReservadas.add("Real");
        this.palabrasReservadas.add("Entero");
        this.palabrasReservadas.add("Leer");
        this.palabrasReservadas.add("Escribir");
        this.palabrasReservadas.add("Si");
        this.palabrasReservadas.add("Entonces");
        this.palabrasReservadas.add("Sino");
        this.palabrasReservadas.add("Inicio");
        this.palabrasReservadas.add("Fin");

        this.programa = new FileReaderManager().leerArchivo(RutaArchivos.PROGRAMA);
        lineaActual = programa.get(numeroLinea);
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
            if (numeroLinea >= programa.size()) {
                return true;
            } else {
                numeroLinea++;
                if(numeroLinea >= programa.size()) return true;
                lineaActual = programa.get(numeroLinea);
                while ((lineaActual == null || lineaActual.isEmpty()) && numeroLinea <= programa.size()) {
                    lineaActual = programa.get(numeroLinea);
                    numeroLinea++;
                }
                punteroFinal = 0;
                punteroInicial = 0;
                return false;
            }
        } else return false;
    }

    public void validarPosicionPunteros() {
        if (lineaActual != null && punteroInicial >= lineaActual.length()) {
            lineaActual = null;

            while (numeroLinea <= programa.size()) {
                lineaActual = programa.get(numeroLinea);
                numeroLinea++;

                if (lineaActual != null && !lineaActual.trim().isEmpty()) {
                    break;
                }
            }

            punteroInicial = 0;
            punteroFinal = 0;
        }
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
        if (punteroFinal >= lineaActual.length()) return null; // fin de línea

        char actual = lineaActual.charAt(punteroFinal);

        // Si es un carácter simple, consumelo solo
        if (caracteresSimples.contains(String.valueOf(actual))) {
            punteroInicial = punteroFinal;
            punteroFinal++;
            return String.valueOf(actual);
        }

        // Si no, consume hasta encontrar un limitador o carácter simple
        int inicio = punteroFinal;
        while (punteroFinal < lineaActual.length() &&
                !limitador.verificar(lineaActual.charAt(punteroFinal)) &&
                !caracteresSimples.contains(String.valueOf(lineaActual.charAt(punteroFinal)))) {
            punteroFinal++;
        }

        String lexema = lineaActual.substring(inicio, punteroFinal);
        punteroInicial = punteroFinal;
        return lexema;
    }




    public void agregarToken(Token token) {
        this.tokens.add(new NodoLineaToken(numeroLinea, token));
    }

    public Lista<Token> getTokens() {
        Lista<Token> lista = new Lista<>();
        for (NodoLineaToken nlt : tokens) {
            lista.agregar(nlt.getToken());
        }
        return lista;
    }

    public List<NodoLineaToken> getTokensLinea() {
        return tokens;
    }

    public void agregarError(LexicalError error) {
        this.errores.add(error);
    }

    public List<LexicalError> getErrores() {
        return errores;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public boolean isReservedWord (String lexema) {
        return palabrasReservadas.contains(lexema);
    }

    public boolean isSimpleCharacter (String c) {
        return caracteresSimples.contains(c);
    }

    public void agregarSimbolo (Token simbolo) {
        if (simbolos.size() == 0 || !simbolos.contains(simbolo)) simbolos.add(simbolo);
    }

    public void setTokenActual(Token t) {
        this.tokenActual = t;
    }


    public Token getTokenActual() {
        return tokenActual;
    }

    public List<Token> getSimbolos() {
        return simbolos;
    }

    public void addSimbolos(Token t) { this.simbolos.add(t); }

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
