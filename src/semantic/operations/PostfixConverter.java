package semantic.operations;

import data_structures.Pila;

import java.util.ArrayList;
import java.util.List;

public class PostfixConverter {
    public static void main(String[] args) {
        // List<OperationToken> tokens = List.of(
        //         new NumberToken("3"),
        //         new OperatorToken("+"),
        //         new NumberToken("5"),
        //         new OperatorToken("*"),
        //         new OperatorToken("("),
        //         new NumberToken("2"),
        //         new OperatorToken("-"),
        //         new NumberToken("8"),
        //         new OperatorToken(")")
        // );
        // List p = convert(tokens);
        // System.out.println(p.toString());
    }

    public static List<OperationToken> convert(List<OperationToken> tokens) {
        List<OperationToken> output = new ArrayList<>();
        Pila<OperatorToken> operators = new Pila<>();

        for (OperationToken token : tokens) {

            if (token.isNumber()) {
                output.add(token);
            }

            else if (token.isOperator()) {

                OperatorToken op = (OperatorToken) token;

                if (op.isLeftParenthesis()) {
                    operators.push(op);
                }

                else if (op.isRightParenthesis()) {
                    while (!operators.peek().isLeftParenthesis()) {
                        output.add(operators.pop());
                    }
                    operators.pop();
                }

                else {
                    while (!operators.esVacia() &&
                            op.getPrecedence() <=  operators.peek().getPrecedence()) {
                        output.add(operators.pop());
                    }
                    operators.push(op);
                }
            }
        }


        while (!operators.esVacia()) {
            output.add(operators.pop());
        }

        return output;
    }

}
