package lexer;

import data_structures.Lista;
import data_structures.Set;
import io.FileReaderManager;
import io.RutaArchivos;
import lexer.handlers.errors.LexicalError;
import lexer.validators.boundaries.FinalLexema;
import lexer.validators.boundaries.IdentificadorLimites;

public class Context {
    private IdentificadorLimites limitador;
    private Lista<Token> tokens; // Simbolos que encontremos
    private  Lista<LexicalError> errores; // Para la tabla de errores
    private Lista<Token> simbolos; // Simbolos encontrados
    private Lista<String> palabrasReservadas; // Palabras reservadas
    private Set<String> caracteresSimples; // Caracteres sumples

    private Lista<String> programa;
    private int numeroLinea;
    private String lineaActual;
    private int punteroInicial;
    private int punteroFinal;

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

    public void setPunteroInicial(int nuevaPosicion) {
        if (nuevaPosicion > punteroFinal) throw new IllegalArgumentException("El puntero inicial no puede ser mayor que el puntero final");
        this.punteroInicial = nuevaPosicion;
    }

    public void setPunteroFinal(int nuevaPosicion) {
        if (nuevaPosicion > this.lineaActual.length()) throw new IllegalArgumentException("El puntero final no puede ser mas alto que la longitud de la cadena actual");
        this.punteroFinal = nuevaPosicion;
    }

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

    public boolean limitador() {
        if (punteroFinal >= lineaActual.length()) {
            return true;
        }

        char c = lineaActual.charAt(punteroFinal);
        return limitador.verificar(c) || caracteresSimples.contains(String.valueOf(c));
    }


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

    public Lista<Token> getSimbolos() {
        return simbolos;
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
