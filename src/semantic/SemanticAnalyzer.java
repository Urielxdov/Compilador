package semantic;

import lexer.constants.TiposTokens;
import semantic.ast.ASTNode;
import semantic.ast.BinaryOpNode;

public class SemanticAnalyzer {
    public void analyze(ASTNode node) {
        if (node instanceof BinaryOpNode bin) {
            //TiposTokens left = analyze();
        }
    }
}
