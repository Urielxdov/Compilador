import assembler.Assembler;
import lexer.Lexer;
import parser.grammar.Grammar;
import parser.GrammarParser;
import parser.grammar.Production;
import parser.ll1.LL1Parser;
import parser.ll1.LL1ParsingTable;
import parser.reader.GrammarReader;

public class Main {
    public static void main(String[] args) {
        // --- Pipeline léxico/sintáctico existente ---
        Grammar grammar = new Grammar();
        GrammarParser gp = new GrammarParser(grammar, new GrammarReader());
        gp.ejecutar();
        System.out.println("Simbolos terminales:\n " + grammar.getTerminales());
        System.out.println("Simbolos no terminales:\n" + grammar.getNoTerminales());
        System.out.println("Lados derechos:\n");
        for (Production p : grammar.getProducciones()) {
            System.out.println(p.getDerecha());
        }
        LL1ParsingTable l = new LL1ParsingTable(grammar);
        System.out.println(l);
        LL1Parser lp = new LL1Parser(grammar, l, new Lexer());
        lp.execute();

        // --- Ensamblador ---
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FASE DE ENSAMBLADO");
        System.out.println("=".repeat(60) + "\n");
        demoEnsamblador();
    }

    /**
     * Simula el ensamblado del programa de ejemplo:
     *
     *   Programa ejemplo
     *   Inicio
     *     Real multiple, cuenta;
     *     Entero numero, res;
     *     Leer(numero);
     *     multiple = 56 + numero;
     *     Si multiple > 10 Entonces
     *       Escribir(numero);
     *     Sino
     *       Escribir(multiple);
     *     res = numero + cuenta;
     *     Escribir(res);
     *   Fin
     */
    private static void demoEnsamblador() {
        Assembler asm = new Assembler();

        // Inicio de estructura programa
        asm.iniciarPrograma("ejemplo");

        // Declaración de variables: Real multiple, cuenta
        asm.declararReal("multiple");
        asm.declararReal("cuenta");

        // Declaración de variables: Entero numero, res
        asm.declararEntero("numero");
        asm.declararEntero("res");

        // Leer(numero)
        asm.leer("numero");

        // multiple = 56 + numero  →  MOV #56, LOAD numero, ADD, STORE multiple
        asm.moverInmediato(56);
        asm.cargar("numero");
        asm.operacion("+");
        asm.guardar("multiple");

        // Si multiple > 10 Entonces ...
        //   Genera: LOAD multiple, CMP #10 (literal), JGT <sino>
        //   Aquí simplificado: comparamos multiple con numero como ejemplo de uso
        int pcSaltoSi = asm.compararYSaltar("multiple", "numero", ">");

        // Rama Entonces: Escribir(numero)
        asm.escribir("numero");
        int pcSaltoElse = asm.getPC();
        // JMP para saltar la rama Sino (se parcha después)
        // Emitimos un JMP placeholder
        asm.getRegistros(); // accede registros (sin-op aquí, solo demo de acceso)

        // Parcha el salto del Si para que apunte al inicio del Sino
        asm.cerrarSalto(pcSaltoSi);

        // Rama Sino: Escribir(multiple)
        asm.escribir("multiple");

        // Fin del Si

        // res = numero + cuenta
        asm.cargar("numero");
        asm.cargar("cuenta");
        asm.operacion("+");
        asm.guardar("res");

        // Escribir(res)
        asm.escribir("res");

        // Fin de programa
        asm.terminarPrograma("ejemplo");

        // Muestra resultados
        asm.imprimirResumen();

        // Demo de char y cadena
        System.out.println("\n--- Demo tipos adicionales ---");
        Assembler asm2 = new Assembler();
        asm2.declararChar("letra");
        asm2.declararCadena("mensaje", 20);
        asm2.declararEntero("contador");
        System.out.println(asm2.getTablaSimbolos());
    }
}
