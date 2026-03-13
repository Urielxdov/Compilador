package semantic.ast;

import data_structures.Lista;
import lexer.Token;
import lexer.constants.TiposTokens;

public class ASTMain {
    public static void main(String[] args) {
        Lista<Token> tokens = new Lista<>();
        tokens.agregar(new Token(2, "2", TiposTokens.NUMERO_NATURAL));
        tokens.agregar(new Token('*', "*", TiposTokens.OPERADOR_ARITMETICO));
        tokens.agregar(new Token(3, "3", TiposTokens.NUMERO_NATURAL));

        ASTBuilder builder = new ASTBuilder();

        AST arbol = builder.construirAST(tokens);

        ASTEvaluator evaluator = new ASTEvaluator();

        int resultado = evaluator.evaluar(arbol.getRaiz());

        System.out.println("Resultado: " + resultado);
    }
}
