package semantic.operations;

import data_structures.Lista;
import data_structures.Map;
import data_structures.Pila;
import semantic.operations.operands.NumberOperation;
import semantic.operations.operators.OperatorSymbol;

import java.util.ArrayList;
import java.util.List;

public class ResolutionOperations {
    private String operacion;

    private ResolutionOperations(String operacion) {
        this.operacion = operacion;
    }

    public void execute() {
        int i = 0;
        List operacionParseada = new ArrayList();

        while (i < operacion.length()) {
            char c  = operacion.charAt(i);

            // Si es un espacio en blanco ignoramos
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Es numero
            if (Character.isDigit(c)) {
                StringBuilder numero = new StringBuilder();
                Boolean esDecimal = false;

                while (i < operacion.length() && (
                        Character.isDigit(operacion.charAt(i))
                        || operacion.charAt(i) == '.'
                )) {
                    if(operacion.charAt(i) == '.') {
                        if (esDecimal) break; // Error segundo punto decimal
                        esDecimal = true;
                    }
                    numero.append(operacion.charAt(i));
                    i++;
                }
                Number aux = parsingNumber(numero.toString());
                if (aux instanceof Integer) {
                    operacionParseada.add(new NumberOperation<Integer>(aux));
                } else {
                    operacionParseada.add(new NumberOperation<Double>(aux));
                }
            }

            if ("+*-/=()".indexOf(c) != -1) {
                i++;
                operacionParseada.add(new OperatorSymbol(Character.toString(c)));
                continue;
            }

            System.out.println("Token invalido: " + c);
            i++;
        }

        Pila resultado = new Pila();
        Pila operadores = new Pila();
        i = 0;

        Map<String, Integer> precedencia = new Map<>();

        precedencia.put("+", 1);
        precedencia.put("-", 1);
        precedencia.put("*", 2);
        precedencia.put("/", 2);
        precedencia.put("(", 0);
        precedencia.put(")", 0);

        while (i < operacionParseada.size()) {
            if (operacionParseada.get(i) instanceof NumberOperation<?>) {
                resultado.push(operacionParseada.get(i));
            } else if (operacionParseada.get(i).equals("(")) {
                operadores.push("(");
            } else if (operacionParseada.get(i).equals(")")) {
                while (!operadores.peek().equals("(")) {
                    resultado.push(operadores.pop());
                }
            } else {
                while (!operadores.esVacia() &&
                        precedencia.get(operacionParseada.get(i).toString()) <=
                        precedencia.get(operadores.peek().toString())) {
                    resultado.push(operadores.pop());
                }
                operadores.push(operacionParseada.get(i));
            }
        }

        while (!operadores.esVacia()) {
            resultado.push(operadores.pop());
        }

    }

    public Number parsingNumber(String valor) {
        try {
            int numero = Integer.parseInt(valor);
            System.out.println("Numero entero");
            return numero;
        } catch (NumberFormatException e1) {
            try {
                double numero = Double.parseDouble(valor);
                System.out.println("Numero flotante");
                return numero;
            } catch (NumberFormatException e2) {
                System.out.println("No es un numero valido");
            }
        }
        return null;
    }
}
