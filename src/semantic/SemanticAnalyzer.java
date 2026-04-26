package semantic;

import lexer.Token;
import lexer.constants.TiposTokens;
import lexer.tokens.NodoLineaToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {

    public SemanticReport analyze(List<NodoLineaToken> tokensLinea) {
        Map<Integer, List<Token>> tokensPorLinea = agruparPorLinea(tokensLinea);
        Map<String, TipoDato> tiposDeclarados = new LinkedHashMap<>();
        List<String> errores = new ArrayList<>();
        List<VerificationTrace> verificaciones = new ArrayList<>();

        for (Map.Entry<Integer, List<Token>> entry : tokensPorLinea.entrySet()) {
            int linea = entry.getKey();
            for (List<Token> sentencia : dividirSentencias(entry.getValue())) {
                if (sentencia.isEmpty()) {
                    continue;
                }

                Token primero = sentencia.get(0);
                if (esDeclaracion(primero)) {
                    procesarDeclaracion(sentencia, tiposDeclarados, errores, linea);
                    continue;
                }

                String lexemaInicial = primero.getLexema();
                if ("Leer".equals(lexemaInicial) || "Mostrar".equals(lexemaInicial)) {
                    validarReferencias(sentencia, tiposDeclarados, errores, linea);
                    continue;
                }

                if (esAsignacion(sentencia)) {
                    verificaciones.add(procesarAsignacion(sentencia, tiposDeclarados, errores, linea));
                }
            }
        }

        return new SemanticReport(tokensLinea, tiposDeclarados, errores, verificaciones);
    }

    private Map<Integer, List<Token>> agruparPorLinea(List<NodoLineaToken> tokensLinea) {
        Map<Integer, List<Token>> agrupados = new LinkedHashMap<>();
        for (NodoLineaToken nodo : tokensLinea) {
            agrupados.computeIfAbsent(nodo.getLinea(), ignored -> new ArrayList<>()).add(nodo.getToken());
        }
        return agrupados;
    }

    private List<List<Token>> dividirSentencias(List<Token> tokens) {
        List<List<Token>> sentencias = new ArrayList<>();
        List<Token> actual = new ArrayList<>();

        for (Token token : tokens) {
            actual.add(token);
            if (";".equals(token.getLexema())) {
                sentencias.add(actual);
                actual = new ArrayList<>();
            }
        }

        if (!actual.isEmpty()) {
            sentencias.add(actual);
        }

        return sentencias;
    }

    private boolean esDeclaracion(Token token) {
        return token.getTipo() == TiposTokens.PALABRA_RESERVADA
                && TipoDato.desdeDeclaracion(token.getLexema()) != TipoDato.DESCONOCIDO;
    }

    private void procesarDeclaracion(List<Token> tokens, Map<String, TipoDato> tiposDeclarados,
                                     List<String> errores, int linea) {
        TipoDato tipo = TipoDato.desdeDeclaracion(tokens.get(0).getLexema());
        for (int i = 1; i < tokens.size(); i++) {
            Token actual = tokens.get(i);
            if (actual.getTipo() != TiposTokens.IDENTIFICADOR) {
                continue;
            }

            String variable = actual.getLexema();
            if (tiposDeclarados.containsKey(variable)) {
                errores.add("Linea " + linea + ": la variable '" + variable + "' ya fue declarada.");
            } else {
                tiposDeclarados.put(variable, tipo);
            }
        }
    }

    private void validarReferencias(List<Token> tokens, Map<String, TipoDato> tiposDeclarados,
                                    List<String> errores, int linea) {
        for (Token token : tokens) {
            if (token.getTipo() == TiposTokens.IDENTIFICADOR && !tiposDeclarados.containsKey(token.getLexema())) {
                errores.add("Linea " + linea + ": la variable '" + token.getLexema() + "' no fue declarada.");
            }
        }
    }

    private boolean esAsignacion(List<Token> tokens) {
        return tokens.size() >= 3
                && tokens.get(0).getTipo() == TiposTokens.IDENTIFICADOR
                && "=".equals(tokens.get(1).getLexema());
    }

    private VerificationTrace procesarAsignacion(List<Token> tokens, Map<String, TipoDato> tiposDeclarados,
                                                 List<String> errores, int linea) {
        String variable = tokens.get(0).getLexema();
        TipoDato tipoVariable = tiposDeclarados.get(variable);
        String expresionCompleta = reconstruir(tokens);

        if (tipoVariable == null) {
            String mensaje = "Linea " + linea + ": la variable '" + variable + "' no fue declarada.";
            errores.add(mensaje);
            return new VerificationTrace(linea, expresionCompleta, TipoDato.ERROR,
                    new ArrayList<>(), TipoDato.ERROR, mensaje);
        }

        List<Token> expresion = extraerExpresion(tokens);
        List<String> tiposOperandos = new ArrayList<>();
        TipoDato tipoExpresion = evaluarExpresion(expresion, tiposDeclarados, tiposOperandos, errores, linea);

        String errorAsignacion = null;
        if (tipoExpresion == TipoDato.ERROR || tipoVariable != tipoExpresion) {
            errorAsignacion = "Linea " + linea + ": no se puede asignar '" + tipoExpresion
                    + "' a la variable '" + variable + "' de tipo '" + tipoVariable + "'.";
            errores.add(errorAsignacion);
        }

        return new VerificationTrace(linea, expresionCompleta, tipoVariable, tiposOperandos, tipoExpresion, errorAsignacion);
    }

    private List<Token> extraerExpresion(List<Token> tokens) {
        List<Token> expresion = new ArrayList<>();
        for (int i = 2; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (";".equals(token.getLexema())) {
                break;
            }
            expresion.add(token);
        }
        return expresion;
    }

    private TipoDato evaluarExpresion(List<Token> expresion, Map<String, TipoDato> tiposDeclarados,
                                      List<String> tiposOperandos, List<String> errores, int linea) {
        TipoDato acumulado = null;

        for (Token token : expresion) {
            String lexema = token.getLexema();
            if (esOperador(lexema) || "(".equals(lexema) || ")".equals(lexema)) {
                continue;
            }

            TipoDato tipoActual = resolverTipo(token, tiposDeclarados, errores, linea);
            tiposOperandos.add(lexema + " : " + tipoActual);

            if (tipoActual == TipoDato.ERROR) {
                return TipoDato.ERROR;
            }

            if (acumulado == null) {
                acumulado = tipoActual;
            } else {
                acumulado = combinar(acumulado, tipoActual);
            }
        }

        return acumulado == null ? TipoDato.DESCONOCIDO : acumulado;
    }

    private TipoDato resolverTipo(Token token, Map<String, TipoDato> tiposDeclarados,
                                  List<String> errores, int linea) {
        if (token.getTipo() == TiposTokens.IDENTIFICADOR) {
            TipoDato tipo = tiposDeclarados.get(token.getLexema());
            if (tipo == null) {
                errores.add("Linea " + linea + ": la variable '" + token.getLexema() + "' no fue declarada.");
                return TipoDato.ERROR;
            }
            return tipo;
        }

        TipoDato tipoLiteral = TipoDato.desdeToken(token);
        return tipoLiteral == TipoDato.DESCONOCIDO ? TipoDato.ERROR : tipoLiteral;
    }

    private TipoDato combinar(TipoDato izquierdo, TipoDato derecho) {
        if (!izquierdo.esNumerico() || !derecho.esNumerico()) {
            return TipoDato.ERROR;
        }
        if (izquierdo == TipoDato.REAL || derecho == TipoDato.REAL) {
            return TipoDato.REAL;
        }
        return TipoDato.INT;
    }

    private boolean esOperador(String lexema) {
        return "+".equals(lexema) || "-".equals(lexema) || "*".equals(lexema) || "/".equals(lexema);
    }

    private String reconstruir(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(tokens.get(i).getLexema());
        }
        return sb.toString();
    }
}
