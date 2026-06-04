import assembler.TripletAssemblerDriver;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        // Build grammar and LL1 table once (shared across all programs)
        Grammar grammar = new Grammar();
        GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
        gp.ejecutar();
        LL1ParsingTable table = new LL1ParsingTable(grammar);

        // Run all 10 programs through the gated pipeline
        RutaArchivos[] programs = {
            //RutaArchivos.PROGRAMA1,
                //RutaArchivos.PROGRAMA2,
                RutaArchivos.PROGRAMA3
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
        
        // Imprimir contenido del archivo
        try {
            Files.lines(Paths.get(path)).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }
        
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

        // Phase 7: Code generation from optimized intermediate code
        String fileName = Path.of(path).getFileName().toString();
        String progName = fileName.endsWith(".txt")
                ? fileName.substring(0, fileName.length() - 4)
                : fileName;
        TripletAssemblerDriver driver = new TripletAssemblerDriver(progName);
        driver.generate(ic);
        System.out.println("[OK] Ensamblador NASM x86-64 generado.");
        driver.getAssembler().imprimirResumen();

        String asmPath = path.replace(".txt", ".asm");
        try {
            driver.escribirArchivo(asmPath);
            System.out.println("[OK] Ensamblador escrito en: " + asmPath);
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo escribir .asm: " + e.getMessage());
        }
    }
}
