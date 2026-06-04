package io;

import assembler.Assembler;
import assembler.codegen.Instruction;
import assembler.codegen.MachineCodeGenerator;
import assembler.codegen.Opcode;
import data_structures.Lista;
import intermediate.IntermediateCode;
import intermediate.Triplet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportadorConsola
 *
 * Clase dedicada exclusivamente a formatear e imprimir el output del compilador
 * en consola con estructura profesional y completa.
 *
 * Responsabilidades:
 * 1. Imprimir código FUENTE original
 * 2. Imprimir TERCETOS (Código Intermedio) con indexación correcta
 * 3. Imprimir TERCETOS OPTIMIZADOS con cambios algebraicos/folding
 * 4. Imprimir CÓDIGO MÁQUINA con instrucciones E/S completas
 *
 * Uso:
 *   ReportadorConsola.imprimirCompilacion(
 *       "ruta/programa.txt",
 *       codigoIntermedioCrudo,
 *       codigoIntermediaOptimizado,
 *       generadorCodigoMaquina
 *   );
 */
public class ReportadorConsola {

    private static final String SEPARADOR_MAYOR = "=".repeat(70);
    private static final String SEPARADOR_MENOR = "-".repeat(70);
    private static final int ANCHO_INDICE = 5;
    private static final int ANCHO_INSTRUCCION = 18;
    private static final int ANCHO_OP1 = 15;
    private static final int ANCHO_OP2 = 20;

    // ==================== API PUBLICA ====================

    /**
     * Imprime la compilación completa en consola de forma estructurada.
     *
     * @param rutaPrograma          Ruta del archivo fuente
     * @param codigoIntermedioCrudo Código intermedio antes de optimizaciones
     * @param codigoOptimizado      Código intermedio después de optimizaciones
     * @param assembler             Ensamblador con instrucciones generadas
     */
    public static void imprimirCompilacion(
            String rutaPrograma,
            IntermediateCode codigoIntermedioCrudo,
            IntermediateCode codigoOptimizado,
            Assembler assembler) {

        imprimirFuente(rutaPrograma);
        imprimirTercetos("TERCETOS (CODIGO INTERMEDIO CRUDO)", codigoIntermedioCrudo);
        imprimirComparativoOptimizacion(codigoIntermedioCrudo, codigoOptimizado);
        imprimirCodigoMaquina(assembler.getMachineCodeGenerator());
    }

    /**
     * Versión simplificada para casos donde solo se necesita imprimir tercetos.
     */
    public static void imprimirSoloTercetos(
            String rutaPrograma,
            IntermediateCode codigo) {
        imprimirFuente(rutaPrograma);
        imprimirTercetos("TERCETOS (CODIGO INTERMEDIO)", codigo);
    }

    /**
     * Versión simplificada para imprimir solo código máquina.
     */
    public static void imprimirSoloCodigoMaquina(Assembler assembler) {
        imprimirCodigoMaquina(assembler.getMachineCodeGenerator());
    }

    // ==================== METODOS PRIVADOS ====================

    /**
     * Imprime el código fuente original del archivo.
     */
    private static void imprimirFuente(String rutaPrograma) {
        System.out.println("\n" + SEPARADOR_MAYOR);
        System.out.println("FUENTE (Código Original)");
        System.out.println(SEPARADOR_MAYOR);

        try (BufferedReader br = new BufferedReader(new FileReader(rutaPrograma))) {
            String linea;
            int numLinea = 1;
            while ((linea = br.readLine()) != null) {
                System.out.printf("%3d | %s%n", numLinea, linea);
                numLinea++;
            }
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo leer el archivo: " + rutaPrograma);
            System.err.println("  " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Imprime tercetos con índice y valores sin usar temporales explícitos.
     * Usa referencias a instrucciones previas cuando es posible.
     */
    private static void imprimirTercetos(String titulo, IntermediateCode codigo) {
        System.out.println(SEPARADOR_MAYOR);
        System.out.println(titulo);
        System.out.println(SEPARADOR_MAYOR);

        List<Triplet> triplets = codigo.getTriplets();
        if (triplets.isEmpty()) {
            System.out.println("[VACÍO]");
            System.out.println();
            return;
        }

        imprimirEncabezadoTercetos();

        for (int i = 0; i < triplets.size(); i++) {
            Triplet t = triplets.get(i);
            String indiceStr = String.format("(%d)", i);
            String instrStr = formatearInstruccion(t.getInstruccion());
            String op1Str = formatearOperando(t.getOp1());
            String op2Str = formatearOperando(t.getOp2());

            System.out.printf("%-5s %-18s %-15s %s%n",
                    indiceStr, instrStr, op1Str, op2Str);
        }

        System.out.println(SEPARADOR_MENOR);
        System.out.println("Total de instrucciones: " + triplets.size());
        System.out.println();
    }

    /**
     * Imprime un comparativo ANTES/DESPUÉS de las optimizaciones.
     */
    private static void imprimirComparativoOptimizacion(
            IntermediateCode crudo,
            IntermediateCode optimizado) {

        List<Triplet> antes = crudo.getTriplets();
        List<Triplet> despues = optimizado.getTriplets();

        // Si son idénticos, no hay nada que reportar
        if (sonIdenticos(antes, despues)) {
            System.out.println(SEPARADOR_MAYOR);
            System.out.println("OPTIMIZACION");
            System.out.println(SEPARADOR_MAYOR);
            System.out.println("[SIN CAMBIOS] El código no fue optimizado.");
            System.out.println();
            return;
        }

        System.out.println(SEPARADOR_MAYOR);
        System.out.println("OPTIMIZACION (Comparativo)");
        System.out.println(SEPARADOR_MAYOR);

        System.out.println("\n--- ANTES (Original) ---");
        imprimirTercetosSimple(antes);

        System.out.println("\n--- DESPUES (Optimizado) ---");
        imprimirTercetosSimple(despues);

        // Estadísticas
        System.out.println("\n" + SEPARADOR_MENOR);
        System.out.printf("Líneas eliminadas: %d%n", Math.max(0, antes.size() - despues.size()));
        System.out.printf("Reducción: %.1f%%%n",
                100.0 * (antes.size() - despues.size()) / antes.size());
        System.out.println();
    }

    /**
     * Imprime tercetos sin encabezado (para uso interno en comparativos).
     */
    private static void imprimirTercetosSimple(List<Triplet> triplets) {
        for (int i = 0; i < triplets.size(); i++) {
            Triplet t = triplets.get(i);
            String indiceStr = String.format("(%d)", i);
            String instrStr = formatearInstruccion(t.getInstruccion());
            String op1Str = formatearOperando(t.getOp1());
            String op2Str = formatearOperando(t.getOp2());

            System.out.printf("  %-5s %-18s %-15s %s%n",
                    indiceStr, instrStr, op1Str, op2Str);
        }
    }

    /**
     * Imprime el código máquina con mapeo a instrucciones y datos completos.
     */
    private static void imprimirCodigoMaquina(MachineCodeGenerator generador) {
        System.out.println(SEPARADOR_MAYOR);
        System.out.println("CODIGO MAQUINA (Instrucciones Ejecutables)");
        System.out.println(SEPARADOR_MAYOR);

        Lista<Instruction> instrucciones = generador.getInstrucciones();
        if (instrucciones.nodosExistentes() == 0) {
            System.out.println("[VACÍO] No hay instrucciones generadas.");
            System.out.println();
            return;
        }

        // Encabezado
        System.out.printf("%-6s %-18s %-20s %s%n",
                "PC", "INSTRUCCION", "MODO", "BYTES");
        System.out.println(SEPARADOR_MENOR);

        int pc = 0x0000;
        for (int i = 0; i < instrucciones.nodosExistentes(); i++) {
            Instruction inst = instrucciones.obtener(i);
            if (inst == null) continue;

            byte[] bytes = inst.getBytes();
            String bytesStr = formatearBytes(bytes);
            String modoStr = formatearModo(inst.getModo());
            String instrStr = inst.toString();

            System.out.printf("%04X   %-18s %-20s %s%n",
                    pc, instrStr, modoStr, bytesStr);

            pc += 4; // Cada instrucción ocupa 4 bytes
        }

        System.out.println(SEPARADOR_MENOR);
        System.out.printf("Total de instrucciones: %d%n", instrucciones.nodosExistentes());
        System.out.printf("Memoria usada: %d bytes%n", pc);
        System.out.println();

        // Dump de memoria
        imprimirDumpMemoria(generador);
    }

    /**
     * Imprime un dump hexadecimal de la memoria usada.
     */
    private static void imprimirDumpMemoria(MachineCodeGenerator generador) {
        System.out.println(SEPARADOR_MAYOR);
        System.out.println("DUMP DE MEMORIA");
        System.out.println(SEPARADOR_MAYOR);

        String dump = generador.getDumpMemoria();
        if (dump != null && !dump.isEmpty()) {
            System.out.print(dump);
        } else {
            System.out.println("[SIN DATOS] Memoria vacía.");
        }
        System.out.println();
    }

    // ==================== UTILIDADES DE FORMATEO ====================

    /**
     * Encabezado para la tabla de tercetos.
     */
    private static void imprimirEncabezadoTercetos() {
        System.out.printf("%-5s %-18s %-15s %s%n",
                "Idx", "Instruccion", "Op1", "Op2");
        System.out.println(SEPARADOR_MENOR);
    }

    /**
     * Formatea una instrucción para presentación (mayúsculas, truncado si es largo).
     */
    private static String formatearInstruccion(String instr) {
        if (instr == null) return "?";
        String upper = instr.toUpperCase();
        if (upper.length() > ANCHO_INSTRUCCION - 1) {
            return upper.substring(0, ANCHO_INSTRUCCION - 4) + "...";
        }
        return String.format("%-" + ANCHO_INSTRUCCION + "s", upper);
    }

    /**
     * Formatea un operando para presentación.
     */
    private static String formatearOperando(String operando) {
        if (operando == null || operando.isEmpty()) {
            return "";
        }
        return operando;
    }

    /**
     * Formatea el arreglo de bytes en notación hexadecimal.
     */
    private static String formatearBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString().trim();
    }

    /**
     * Convierte el código de modo a descripción legible.
     */
    private static String formatearModo(int modo) {
        return switch (modo) {
            case Instruction.MODO_INMEDIATO -> "Inmediato (#)";
            case Instruction.MODO_DIRECTO -> "Directo (@)";
            case Instruction.MODO_REGISTRO -> "Registro (R)";
            default -> "Desconocido (?)";
        };
    }

    /**
     * Verifica si dos listas de triplets son idénticas.
     */
    private static boolean sonIdenticos(List<Triplet> lista1, List<Triplet> lista2) {
        if (lista1.size() != lista2.size()) return false;
        for (int i = 0; i < lista1.size(); i++) {
            if (!lista1.get(i).equals(lista2.get(i))) return false;
        }
        return true;
    }

    /**
     * Genera un resumen estadístico del compilador.
     */
    public static void imprimirResumenCompilacion(
            IntermediateCode codigoIntermedio,
            IntermediateCode codigoOptimizado,
            Assembler assembler) {

        MachineCodeGenerator generador = assembler.getMachineCodeGenerator();

        System.out.println("\n" + SEPARADOR_MAYOR);
        System.out.println("RESUMEN DE COMPILACION");
        System.out.println(SEPARADOR_MAYOR);

        int tripletsCrudos = codigoIntermedio.getTriplets().size();
        int tripletsOptimizados = codigoOptimizado.getTriplets().size();
        int instrucciones = generador.getInstrucciones().nodosExistentes();
        int memoria = generador.getPC();

        System.out.printf("Tercetos generados (crudo):     %d%n", tripletsCrudos);
        System.out.printf("Tercetos después optimización:  %d%n", tripletsOptimizados);
        System.out.printf("Tercetos eliminados:            %d%n",
                Math.max(0, tripletsCrudos - tripletsOptimizados));
        System.out.printf("Tasa de compresión:             %.1f%%%n",
                100.0 * (tripletsCrudos - tripletsOptimizados) / tripletsCrudos);
        System.out.println();
        System.out.printf("Instrucciones generadas:        %d%n", instrucciones);
        System.out.printf("Memoria usada:                  %d bytes (0x%04X)%n", memoria, memoria);
        System.out.println();
    }
}
