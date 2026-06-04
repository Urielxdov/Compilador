# Guía de Extensión - ReportadorConsola

**Propósito**: Ejemplos de cómo extender `ReportadorConsola` con nuevas funcionalidades

---

## Extensión 1: Colorización ANSI

Agrega colores al output para mayor legibilidad.

```java
// Agregár estas constantes a la clase
private static final String ANSI_RESET = "\u001B[0m";
private static final String ANSI_BOLD = "\u001B[1m";
private static final String ANSI_RED = "\u001B[31m";
private static final String ANSI_GREEN = "\u001B[32m";
private static final String ANSI_YELLOW = "\u001B[33m";
private static final String ANSI_BLUE = "\u001B[34m";
private static final String ANSI_CYAN = "\u001B[36m";

// Modificar imprimirFuente para agregar color:
private static void imprimirFuente(String rutaPrograma) {
    System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + SEPARADOR_MAYOR + ANSI_RESET);
    System.out.println(ANSI_BOLD + ANSI_CYAN + "FUENTE (Código Original)" + ANSI_RESET);
    System.out.println(ANSI_BOLD + ANSI_CYAN + SEPARADOR_MAYOR + ANSI_RESET);

    // ... resto del método igual ...

    while ((linea = br.readLine()) != null) {
        System.out.printf(ANSI_CYAN + "%3d" + ANSI_RESET + " | %s%n", numLinea, linea);
        numLinea++;
    }
}

// Colorizar tercetos optimizados:
private static void imprimirComparativoOptimizacion(...) {
    // ... previo código ...

    System.out.println("\n" + ANSI_GREEN + "--- ANTES (Original) ---" + ANSI_RESET);
    // ...
    System.out.println("\n" + ANSI_GREEN + "--- DESPUES (Optimizado) ---" + ANSI_RESET);
    // ...
    System.out.println("\n" + ANSI_BOLD + ANSI_GREEN + "Líneas eliminadas: " + ANSI_RESET + count);
}
```

**Resultado**: Output con colores diferenciados por sección

---

## Extensión 2: Exportación a Archivo HTML

Genera un reporte HTML visualizable en navegador.

```java
/**
 * Exporta la compilación a un archivo HTML
 */
public static void exportarHTML(
        String rutaPrograma,
        IntermediateCode codigoIntermedioCrudo,
        IntermediateCode codigoOptimizado,
        MachineCodeGenerator generador,
        String archivoSalida) {

    try (java.io.FileWriter fw = new java.io.FileWriter(archivoSalida)) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='es'>\n");
        html.append("<head>\n");
        html.append("  <meta charset='UTF-8'>\n");
        html.append("  <title>Reporte de Compilación</title>\n");
        html.append("  <style>\n");
        html.append("    body { font-family: monospace; margin: 20px; }\n");
        html.append("    .seccion { border: 1px solid #ccc; margin: 20px 0; padding: 10px; }\n");
        html.append("    .titulo { background: #333; color: white; padding: 10px; font-weight: bold; }\n");
        html.append("    table { border-collapse: collapse; width: 100%; }\n");
        html.append("    th, td { border: 1px solid #999; padding: 5px; text-align: left; }\n");
        html.append("    th { background: #ddd; }\n");
        html.append("    .antes { background: #ffe6e6; }\n");
        html.append("    .despues { background: #e6ffe6; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("<h1>Reporte de Compilación</h1>\n");

        // Sección FUENTE
        html.append("<div class='seccion'>\n");
        html.append("  <div class='titulo'>FUENTE</div>\n");
        html.append("  <pre>\n");
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.FileReader(rutaPrograma))) {
            String linea;
            int numLinea = 1;
            while ((linea = br.readLine()) != null) {
                html.append(String.format("%3d | %s\n", numLinea++,
                    escapeHTML(linea)));
            }
        }
        html.append("  </pre>\n");
        html.append("</div>\n");

        // Sección TERCETOS CRUDOS
        html.append("<div class='seccion'>\n");
        html.append("  <div class='titulo'>TERCETOS (CRUDO)</div>\n");
        html.append("  <table>\n");
        html.append("    <tr><th>Idx</th><th>Instrucción</th><th>Op1</th><th>Op2</th></tr>\n");
        for (int i = 0; i < codigoIntermedioCrudo.getTriplets().size(); i++) {
            Triplet t = codigoIntermedioCrudo.getTriplets().get(i);
            html.append(String.format(
                "    <tr><td>(%d)</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                i, t.getInstruccion(),
                t.getOp1() != null ? t.getOp1() : "",
                t.getOp2() != null ? t.getOp2() : ""));
        }
        html.append("  </table>\n");
        html.append("</div>\n");

        // Sección COMPARATIVO
        html.append("<div class='seccion'>\n");
        html.append("  <div class='titulo'>OPTIMIZACION (Comparativo)</div>\n");
        html.append("  <h3>ANTES</h3>\n");
        html.append("  <table class='antes'>\n");
        html.append("    <tr><th>Idx</th><th>Instrucción</th><th>Op1</th><th>Op2</th></tr>\n");
        for (int i = 0; i < codigoIntermedioCrudo.getTriplets().size(); i++) {
            Triplet t = codigoIntermedioCrudo.getTriplets().get(i);
            html.append(String.format(
                "    <tr><td>(%d)</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                i, t.getInstruccion(),
                t.getOp1() != null ? t.getOp1() : "",
                t.getOp2() != null ? t.getOp2() : ""));
        }
        html.append("  </table>\n");

        html.append("  <h3>DESPUES</h3>\n");
        html.append("  <table class='despues'>\n");
        html.append("    <tr><th>Idx</th><th>Instrucción</th><th>Op1</th><th>Op2</th></tr>\n");
        for (int i = 0; i < codigoOptimizado.getTriplets().size(); i++) {
            Triplet t = codigoOptimizado.getTriplets().get(i);
            html.append(String.format(
                "    <tr><td>(%d)</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                i, t.getInstruccion(),
                t.getOp1() != null ? t.getOp1() : "",
                t.getOp2() != null ? t.getOp2() : ""));
        }
        html.append("  </table>\n");

        int eliminadas = Math.max(0,
            codigoIntermedioCrudo.getTriplets().size() -
            codigoOptimizado.getTriplets().size());
        double tasa = 100.0 * eliminadas / codigoIntermedioCrudo.getTriplets().size();
        html.append(String.format("  <p>Líneas eliminadas: %d (%.1f%%)</p>\n",
            eliminadas, tasa));
        html.append("</div>\n");

        html.append("</body>\n");
        html.append("</html>\n");

        fw.write(html.toString());
        System.out.println("\n[OK] Reporte HTML exportado a: " + archivoSalida);

    } catch (java.io.IOException e) {
        System.err.println("ERROR: No se pudo escribir HTML: " + e.getMessage());
    }
}

private static String escapeHTML(String text) {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
}
```

**Uso**:

```java
ReportadorConsola.exportarHTML(path, ic, icGlobal, generador, "reporte.html");
// Luego abrir reporte.html en navegador
```

---

## Extensión 3: Análisis de Patrones de Optimización

Detalla qué tipo de optimización se aplicó en cada línea.

```java
/**
 * Analiza qué tipo de optimización se aplicó a cada triplet
 */
private static void analizarPatronesOptimizacion(
        List<Triplet> antes,
        List<Triplet> despues) {

    System.out.println("\n" + SEPARADOR_MAYOR);
    System.out.println("ANALISIS DE PATRONES - QUE FUE OPTIMIZADO");
    System.out.println(SEPARADOR_MAYOR);

    Map<String, Integer> patrones = new HashMap<>();
    patrones.put("Eliminadas", 0);
    patrones.put("ConstantFolding", 0);
    patrones.put("CommonSubexpression", 0);
    patrones.put("DeadCode", 0);
    patrones.put("Algebraica", 0);

    for (Triplet t : antes) {
        if (!despues.contains(t)) {
            // Detectar tipo de optimización
            if (esConstantFolding(t)) {
                patrones.put("ConstantFolding",
                    patrones.get("ConstantFolding") + 1);
            } else if (esDeadCode(t)) {
                patrones.put("DeadCode",
                    patrones.get("DeadCode") + 1);
            } else if (esAlgebraica(t)) {
                patrones.put("Algebraica",
                    patrones.get("Algebraica") + 1);
            } else {
                patrones.put("Eliminadas",
                    patrones.get("Eliminadas") + 1);
            }
        }
    }

    System.out.println("\nConteo por tipo de optimización:");
    System.out.println(SEPARADOR_MENOR);
    patrones.forEach((tipo, count) -> {
        if (count > 0) {
            System.out.printf("%-25s: %3d líneas%n", tipo, count);
        }
    });
}

private static boolean esConstantFolding(Triplet t) {
    String op1 = t.getOp1();
    String op2 = t.getOp2();
    return (op1 != null && esNumero(op1)) || (op2 != null && esNumero(op2));
}

private static boolean esDeadCode(Triplet t) {
    return t.getInstruccion().equals("mov") && !usadoLuego(t);
}

private static boolean esAlgebraica(Triplet t) {
    String instr = t.getInstruccion();
    return instr.equals("+") || instr.equals("-") ||
           instr.equals("*") || instr.equals("/");
}

private static boolean esNumero(String s) {
    try {
        Double.parseDouble(s);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}

private static boolean usadoLuego(Triplet t) {
    // Implementar verificación de uso posterior
    return true;
}
```

---

## Extensión 4: Timeline de Optimizaciones

Muestra resultado de cada pasada del optimizer.

```java
/**
 * Imprime timeline de cada pasada de optimización
 */
public static void imprimirTimelineOptimizaciones(
        IntermediateCode[] estados,
        String[] nombresPases) {

    System.out.println("\n" + SEPARADOR_MAYOR);
    System.out.println("TIMELINE DE OPTIMIZACIONES");
    System.out.println(SEPARADOR_MAYOR);

    System.out.printf("%-30s %-10s %s%n", "PASE", "TRIPLETS", "CAMBIOS");
    System.out.println(SEPARADOR_MENOR);

    int prevCount = 0;
    for (int i = 0; i < estados.length; i++) {
        int currCount = estados[i].getTriplets().size();
        int cambios = i == 0 ? 0 : (prevCount - currCount);
        String arrow = cambios > 0 ? "↓" : (cambios < 0 ? "↑" : "=");

        System.out.printf("%-30s %-10d %s %d%n",
            nombresPases[i], currCount, arrow, Math.abs(cambios));

        prevCount = currCount;
    }

    int total = prevCount;
    int original = estados[0].getTriplets().size();
    double percent = 100.0 * (original - total) / original;

    System.out.println(SEPARADOR_MENOR);
    System.out.printf("REDUCCION TOTAL: %d → %d (-%.1f%%)%n",
        original, total, percent);
}
```

**Uso**:

```java
IntermediateCode[] estados = {
    codigoOriginal,
    despuesPase1,
    despuesPase2,
    despuesPase3,
    codigoFinal
};
String[] nombres = {
    "Original",
    "Pase 1: Redundantes",
    "Pase 2: CSE",
    "Pase 3: Algebraica",
    "Pase 4: Dead Code"
};
ReportadorConsola.imprimirTimelineOptimizaciones(estados, nombres);
```

---

## Extensión 5: Validación de Integridad

Verifica que el código compilado sea correcto.

```java
/**
 * Valida la integridad del código generado
 */
public static boolean validarIntegridad(
        IntermediateCode codigoIntermedio,
        MachineCodeGenerator generador) {

    System.out.println("\n" + SEPARADOR_MAYOR);
    System.out.println("VALIDACION DE INTEGRIDAD");
    System.out.println(SEPARADOR_MAYOR);

    boolean ok = true;

    // Validación 1: Tercetos válidos
    if (codigoIntermedio.getTriplets().isEmpty()) {
        System.out.println("[ADVERTENCIA] No hay tercetos generados");
        ok = false;
    }

    // Validación 2: Instrucciones generadas
    Lista<Instruction> instrucciones = generador.getInstrucciones();
    if (instrucciones.getCantidad() == 0) {
        System.out.println("[ADVERTENCIA] No hay instrucciones generadas");
        ok = false;
    }

    // Validación 3: HALT al final
    if (instrucciones.getCantidad() > 0) {
        Instruction ultima = instrucciones.obtener(instrucciones.getCantidad() - 1);
        if (ultima.getOpcode() != Opcode.HALT) {
            System.out.println("[ERROR] Instrucción final no es HALT");
            ok = false;
        }
    }

    // Validación 4: READ y WRITE presentes
    boolean tieneLectura = false;
    boolean tieneEscritura = false;
    for (int i = 0; i < instrucciones.getCantidad(); i++) {
        Instruction inst = instrucciones.obtener(i);
        if (inst.getOpcode() == Opcode.READ) tieneLectura = true;
        if (inst.getOpcode() == Opcode.WRITE) tieneEscritura = true;
    }

    System.out.println("[" + (tieneLectura ? "✓" : "✗") + "] Lectura (READ) presente");
    System.out.println("[" + (tieneEscritura ? "✓" : "✗") + "] Escritura (WRITE) presente");

    // Validación 5: Memoria suficiente
    int memoriaUsada = generador.getPC();
    if (memoriaUsada < 0) {
        System.out.println("[ERROR] Contador PC negativo");
        ok = false;
    } else {
        System.out.printf("[✓] Memoria usada: %d bytes (0x%04X)%n", memoriaUsada, memoriaUsada);
    }

    System.out.println(SEPARADOR_MENOR);
    System.out.println(ok ? "[OK] Validación completada sin errores"
                        : "[ERROR] Validación encontró problemas");

    return ok;
}
```

**Uso**:

```java
boolean valido = ReportadorConsola.validarIntegridad(ic, generador);
if (!valido) {
    System.err.println("El código generado tiene problemas");
    // Tomar acciones correctivas
}
```

---

## Extensión 6: Comparativa de Múltiples Ejecuciones

Compara resultados entre compilaciones diferentes.

```java
/**
 * Guarda estadísticas de una compilación
 */
public static class EstadisticasCompilacion {
    public String nombrePrograma;
    public int tripletsCrudos;
    public int tripletsOptimizados;
    public int instrucciones;
    public int memoria;
    public long tiempoMs;

    public EstadisticasCompilacion(
            String nombre,
            IntermediateCode ic,
            IntermediateCode icOpt,
            MachineCodeGenerator gen) {
        nombrePrograma = nombre;
        tripletsCrudos = ic.getTriplets().size();
        tripletsOptimizados = icOpt.getTriplets().size();
        instrucciones = gen.getInstrucciones().getCantidad();
        memoria = gen.getPC();
        tiempoMs = System.currentTimeMillis();
    }
}

/**
 * Compara múltiples compilaciones
 */
public static void reportarComparativa(EstadisticasCompilacion... stats) {
    System.out.println("\n" + SEPARADOR_MAYOR);
    System.out.println("COMPARATIVA DE COMPILACIONES");
    System.out.println(SEPARADOR_MAYOR);

    System.out.printf("%-20s %-12s %-12s %-12s %-10s%n",
        "PROGRAMA", "CRUDOS", "OPTIMIZADOS", "INSTRUCCIONES", "MEMORIA");
    System.out.println(SEPARADOR_MENOR);

    for (EstadisticasCompilacion s : stats) {
        System.out.printf("%-20s %-12d %-12d %-12d %-10d%n",
            s.nombrePrograma,
            s.tripletsCrudos,
            s.tripletsOptimizados,
            s.instrucciones,
            s.memoria);
    }
}
```

---

## Integración Ejemplo

```java
// En Main.java, agregar:
import io.ReportadorConsola.EstadisticasCompilacion;

// En runPipeline():
long inicio = System.currentTimeMillis();

// ... compilación ...

EstadisticasCompilacion stats = new EstadisticasCompilacion(
    new File(path).getName(), ic, icGlobal, driver.getAssembler());
stats.tiempoMs = System.currentTimeMillis() - inicio;

// Agregar a lista global y reportar al final:
estadisticasGlobales.add(stats);
```

---

## Notas Importantes

- ✅ Todas las extensiones son **opcionales**
- ✅ Cada una se puede agregar **independientemente**
- ✅ No modifican la funcionalidad **existente**
- ✅ Se pueden **combinar** varias extensiones
- ✅ Documentar cualquier extensión que agregues
