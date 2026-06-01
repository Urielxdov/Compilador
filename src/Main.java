import assembler.AssemblerDriver;
import intermediate.IntermediateCode;
import intermediate.IntermediateCodeGenerator;
import intermediate.PostfixPrinter;
import optimizer.LocalOptimizer;
import optimizer.GlobalOptimizer;
import lexer.Lexer;
import parser.GrammarParser;
import parser.grammar.Grammar;
import parser.ll1.LL1Parser;
import parser.ll1.LL1ParsingTable;
import parser.reader.GrammarReader;
import semantic.SemanticAnalyzer;
import semantic.SemanticResult;
import semantic.ast.ASTBuilder;
import io.RutaArchivos;

public class Main {

    public static void main(String[] args) {
        // Build grammar and LL1 table once (shared across all programs)
        Grammar grammar = new Grammar();
        GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
        gp.ejecutar();
        LL1ParsingTable table = new LL1ParsingTable(grammar);

        // Run all 10 programs through the gated pipeline
        RutaArchivos[] programs = {
            RutaArchivos.PROGRAMA1, RutaArchivos.PROGRAMA2, RutaArchivos.PROGRAMA3
//            RutaArchivos.PROGRAMA4, RutaArchivos.PROGRAMA5, RutaArchivos.PROGRAMA6,
//            RutaArchivos.PROGRAMA7, RutaArchivos.PROGRAMA8, RutaArchivos.PROGRAMA9,
//            RutaArchivos.PROGRAMA10
        };

        for (RutaArchivos prog : programs) {
            runPipeline(grammar, table, prog.ruta);
        }
    }

    private static void runPipeline(Grammar grammar, LL1ParsingTable table, String path) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Procesando: " + path);
        System.out.println("=".repeat(60));

        // Phase 1: Lexical + Syntactic + AST
        ASTBuilder astBuilder = new ASTBuilder();
        Lexer lexer = new Lexer(path);
        LL1Parser parser = new LL1Parser(grammar, table, lexer, astBuilder);
        parser.execute();

        if (!astBuilder.isParseOk()) {
            System.out.println("[ERROR SINTACTICO] Analisis detenido.");
            return;
        }
        System.out.println("[OK] Analisis sintactico exitoso.");

        // Phase 2: Semantic analysis
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        SemanticResult result = analyzer.analyze(astBuilder.getProgram());

        if (!result.isSuccess()) {
            System.out.println("[ERROR SEMANTICO]");
            for (String err : result.getErrors()) System.out.println("  " + err);
            return;
        }
        System.out.println("[OK] Analisis semantico exitoso.");

        // Phase 3: Intermediate code
        IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
        IntermediateCode ic = icg.generate(result.getProgram());
        System.out.println("\n=== CODIGO INTERMEDIO (CRUDO) ===");
        ic.imprimir();

        // Phase 4: Postfix (RPN) representation
        new PostfixPrinter().print(result.getProgram());

        // Phase 5: Local optimizations (in-place, 4 passes)
        new LocalOptimizer().optimize(ic);

        // Phase 6: Global optimization (in-place, same instance)
        new GlobalOptimizer().optimize(ic, path);
        System.out.println("\n[OK] Optimizaciones completadas.");

        // REQ-02: validate jump targets are intact after optimization
        ic.validateJumps();

        // REQ-03: show optimized triplets alongside original source
        ic.imprimirConFuente(path);

        // Phase 7: Code generation (uses original AST — AssemblerDriver unchanged)
        AssemblerDriver driver = new AssemblerDriver();
        driver.generate(result.getProgram());
        System.out.println("[OK] Codigo generado.");
        driver.getAssembler().imprimirResumen();
    }
}
