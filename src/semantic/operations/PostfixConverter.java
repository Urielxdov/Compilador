package semantic.operations;

import data_structures.Pila;

import java.util.ArrayList;
import java.util.List;

public class PostfixConverter {

    public List<OperationToken> convert(List<OperationToken> tokens) {
        List<OperationToken> output = new ArrayList<>();
        Pila<OperationToken> operators = new Pila<>();

        for (OperationToken token : tokens) {
            if (token instanceof NumberToken) {
                output.add(token);
            } else if (token instanceof OperationToken op) {
                if (isLeftParenthesis((OperatorToken) op)) {
                    while (!isLeftParenthesis((OperatorToken) operators.peek())) {
                        output.add(operators.pop());
                    }
                    operators.pop(); // Quitamos el (
                }
            } else {

                while (!operators.esVacia() &&
                        op.getPrecedence() <= operators.peek().getPrecedence()) {
                    output.add(operators.pop());
                }

                operators.push(op);
            }
        }

        while (!operators.esVacia()) {
            output.add(operators.pop());
        }

        return output;
    }

    private boolean isLeftParenthesis(OperatorToken op) {
        return
    }
}
