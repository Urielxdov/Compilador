# Documentación Técnica - ReportadorConsola

**Autor**: GitHub Copilot  
**Fecha**: 2026-05-31  
**Versión**: 1.0  
**Ubicación**: `src/io/ReportadorConsola.java`

---

## Arquitectura

### Responsabilidades

```
ReportadorConsola
├── Lectura del Código Fuente
│   └── imprimirFuente()
│       └── BufferedReader → numeración de líneas
│
├── Formateo de Tercetos
│   ├── imprimirTercetos()
│   ├── imprimirTercetosSimple()
│   └── imprimirEncabezadoTercetos()
│
├── Comparativo de Optimización
│   └── imprimirComparativoOptimizacion()
│       ├── Detección de cambios
│       └── Cálculo de estadísticas
│
├── Código Máquina
│   └── imprimirCodigoMaquina()
│       └── imprimirDumpMemoria()
│
└── Utilidades
    ├── formatearInstruccion()
    ├── formatearOperando()
    ├── formatearBytes()
    ├── formatearModo()
    └── sonIdenticos()
```

---

## Métodos Públicos

### 1. `imprimirCompilacion(...)`

**Propósito**: Impresión completa integrada  
**Entrada**:

- `String rutaPrograma`: Ruta del archivo fuente
- `IntermediateCode codigoIntermedioCrudo`: Tercetos sin optimizar
- `IntermediateCode codigoOptimizado`: Tercetos optimizados
- `MachineCodeGenerator generador`: Generador con instrucciones

**Flujo**:

```
1. imprimirFuente()
2. imprimirTercetos("CRUDO", ic)
3. imprimirComparativoOptimizacion(ic, icOptim)
4. imprimirCodigoMaquina(gen)
```

**Output**: 4 secciones separadas por líneas de `=`

---

### 2. `imprimirSoloTercetos(...)`

**Propósito**: Imprime fuente + tercetos únicamente  
**Útil para**: Debugging de ICG sin optimizaciones

---

### 3. `imprimirSoloCodigoMaquina(...)`

**Propósito**: Imprime código máquina + dump  
**Útil para**: Verificar salida sin intermediarios

---

### 4. `imprimirResumenCompilacion(...)`

**Propósito**: Estadísticas finales  
**Calcula**:

- Triplets crudos vs optimizados
- Tasa de compresión
- Instrucciones totales
- Memoria consumida

---

## Métodos Privados

### Lectura de Fuente

```java
private static void imprimirFuente(String rutaPrograma)
```

**Algoritmo**:

1. Abre `BufferedReader` con `FileReader`
2. Lee línea por línea
3. Numera comenzando en 1
4. Captura excepciones de I/O
5. Formato: `NNN | texto`

**Características**:

- Try-with-resources (seguro)
- Manejo robusto de excepciones
- Salida alineada

---

### Formateo de Tercetos

```java
private static void imprimirTercetos(String titulo, IntermediateCode codigo)
```

**Pasos**:

1. Extrae lista de `Triplet` via `getTriplets()`
2. Verifica si está vacía
3. Imprime encabezado: `Idx | Instruccion | Op1 | Op2`
4. Itera con índice `i` de 0 a N
5. Para cada triplet:
   - Formato índice: `(i)`
   - Llama formatead funciones de estilo
   - Imprime con `printf` alineado

**Anchuras predefinidas**:

```java
ANCHO_INDICE = 5
ANCHO_INSTRUCCION = 18
ANCHO_OP1 = 15
ANCHO_OP2 = 20
```

---

### Comparativo de Optimización

```java
private static void imprimirComparativoOptimizacion(
    IntermediateCode crudo,
    IntermediateCode optimizado)
```

**Lógica**:

1. Extrae listas de ambos: `antes` y `despues`
2. Verifica si `sonIdenticos(antes, despues)`
   - Si SÍ: imprime "[SIN CAMBIOS]"
   - Si NO: continúa
3. Imprime sección ANTES
4. Imprime sección DESPUES
5. Calcula y reporta:
   - Líneas eliminadas
   - % de reducción

**Fórmula de reducción**:

```
reduccion = (antes.size() - despues.size()) / antes.size() * 100.0
```

---

### Código Máquina

```java
private static void imprimirCodigoMaquina(MachineCodeGenerator generador)
```

**Pasos**:

1. Obtiene `Lista<Instruction>` via `getInstrucciones()`
2. Verifica cantidad (>0)
3. Imprime encabezado: `PC | INSTRUCCION | MODO | BYTES`
4. Itera instrucciones:
   - PC = índice \* 4 (cada instrucción = 4 bytes)
   - Obtiene bytes con `getBytes()`
   - Formatea bytes en hex
   - Obtiene modo con `getModo()`
   - Convierte modo a descriptivo

**PC Calculation**:

```
PC = baseIndex * 4
PC inicial = 0x0000
PC incrementa en 4 cada iteración
```

---

## Funciones de Utilidad

### `formatearInstruccion(String instr)`

```java
// Input: "MOV" → Output: "MOV               "
// Input: "VeryLongInstructionName" → Output: "VeryLongInstruc..."
```

**Reglas**:

- Convierte a MAYUSCULAS
- Si > ANCHO_INSTRUCCION: trunca + "..."
- Rellena con espacios (padding)

---

### `formatearOperando(String operando)`

```java
// Input: null → Output: ""
// Input: "" → Output: ""
// Input: "variable" → Output: "variable"
```

**Reglas**:

- Devuelve vacío si es null o ""
- De lo contrario, devuelve tal cual

---

### `formatearBytes(byte[] bytes)`

```java
// Input: [0x03, 0x00, 0x07, 0x00]
// Output: "03 00 07 00"
```

**Algoritmo**:

1. Verifica si bytes es null o vacío
2. Para cada byte: formatea como hex de 2 dígitos
3. Separados por espacios
4. Elimina espacios finales

---

### `formatearModo(int modo)`

```java
// Input: MODO_INMEDIATO (0) → Output: "Inmediato (#)"
// Input: MODO_DIRECTO (1)   → Output: "Directo (@)"
// Input: MODO_REGISTRO (2)  → Output: "Registro (R)"
```

**Usa Switch Statement con Pattern Matching**.

---

### `sonIdenticos(List<Triplet> l1, List<Triplet> l2)`

```java
// Compara elemento por elemento
// Usa equals() de Triplet
```

**Algoritmo**:

1. Si tamaños distintos → false
2. Itera ambas listas
3. Si algún elemento difiere → false
4. Si todos coinciden → true

---

## Manejo de Errores

### I/O Exceptions

```java
catch (IOException e) {
    System.err.println("ERROR: No se pudo leer el archivo: " + rutaPrograma);
    System.err.println("  " + e.getMessage());
}
```

**Comportamiento**: Continúa execution, pero notifica al usuario.

---

### Null Safety

```java
if (triplets.isEmpty()) { ... }
if (instrucciones.getCantidad() == 0) { ... }
if (bytes == null || bytes.length == 0) { ... }
```

**Política**: Verificar antes de acceder.

---

## Constantes

```java
// Separadores
SEPARADOR_MAYOR = "=".repeat(70)  // Título principal
SEPARADOR_MENOR = "-".repeat(70)  // Subtítulos

// Anchuras de columna (caracteres)
ANCHO_INDICE = 5
ANCHO_INSTRUCCION = 18
ANCHO_OP1 = 15
ANCHO_OP2 = 20
```

**Notas**:

- Estos valores se pueden ajustar según preferencia
- El cambio afecta el alineamiento visual
- Se recomienda mantener proporcionales

---

## Dependencias Externas

```
java.io.*              → BufferedReader, FileReader, IOException
java.util.*            → List
assembler.codegen.*    → Instruction, Opcode
intermediate.*         → IntermediateCode, Triplet
data_structures.*      → Lista (estructura custom)
```

---

## Integración con Pipeline

**Llamada en Main.java** (Phase 8):

```java
// Después de Phase 7 (Code Generation)
ReportadorConsola.imprimirCompilacion(
    path,                          // Ruta del archivo fuente
    ic,                            // Código crudo
    icGlobal,                      // Código optimizado
    driver.getAssembler()          // Generador con instrucciones
);

ReportadorConsola.imprimirResumenCompilacion(
    ic,                            // Para estadísticas
    icGlobal,
    driver.getAssembler()
);
```

**Timing**: Después de que AssemblerDriver ha completado `generate()`.

---

## Mejoras Futuras

### 1. Colorización ANSI

```java
public static final String COLOR_RESET = "\u001B[0m";
public static final String COLOR_BOLD = "\u001B[1m";
public static final String COLOR_RED = "\u001B[31m";
public static final String COLOR_GREEN = "\u001B[32m";

// Uso: System.out.println(COLOR_BOLD + "Texto" + COLOR_RESET);
```

### 2. Exportación a Archivos

```java
public static void exportarHTML(String outputPath, ...) { }
public static void exportarJSON(String outputPath, ...) { }
public static void exportarCSV(String outputPath, ...) { }
```

### 3. Análisis Comparativo Profundo

```java
private static void analizarPatronesOptimizacion(
    List<Triplet> antes,
    List<Triplet> despues) { }
```

### 4. Timeline de Optimizaciones

Mostrar resultados de cada pasada del optimizer:

```
Pase 1: Redundantes    → 12 → 10 instr.
Pase 2: CSE            → 10 → 9 instr.
Pase 3: Algebraica     → 9 → 7 instr.
Pase 4: Código Muerto  → 7 → 7 instr. [sin cambios]
```

### 5. Validación de Correctitud

```java
private static void validarIntegridad(
    IntermediateCode ic,
    MachineCodeGenerator gen) { }
```

---

## Testing

### Caso de Prueba 1: Sin Optimización

**Input**: `codigoIntermedioCrudo == codigoOptimizado`  
**Esperado**: "[SIN CAMBIOS]"

### Caso de Prueba 2: Con Optimización

**Input**: Diferentes tamaños de lista  
**Esperado**: "Reducción: X.X%"

### Caso de Prueba 3: Archivo No Encontrado

**Input**: Ruta inválida  
**Esperado**: Mensaje de error en stderr, continúa execution

### Caso de Prueba 4: Listas Vacías

**Input**: `ic.getTriplets().isEmpty()`  
**Esperado**: "[VACÍO]"

---

## Performance

**Complejidad Temporal**:

- Lectura de fuente: **O(n)** donde n = número de líneas
- Impresión de tercetos: **O(m)** donde m = número de triplets
- Comparativo: **O(m)** para verificar identidad
- Código máquina: **O(k)** donde k = número de instrucciones
- **Total**: O(n + m + k) - Linear

**Complejidad Espacial**:

- O(1) - No almacena estructuras adicionales significativas
- Lee línea por línea sin buffering completo

**Optimización**: Eficiente para compiladores pequeños y medianos (<100K LOC).

---

## Estándares de Codificación

✅ **Seguidos**:

- JavaDoc comments en métodos públicos
- Nombre de variables descriptivos
- Constantes en UPPERCASE
- Try-with-resources para I/O
- No campos mutable (todos static)

✅ **Convenciones**:

- Métodos privados con `private static`
- Utilidades sin estado (sin constructor)
- Validaciones al inicio de métodos

---

## Notas para Mantenimiento

1. **Modificar anchuras**: Todos en una sección de constantes
2. **Agregar nueva instrucción de salida**: Asegurar que `Opcode` incluya
3. **Cambiar formato**: Centralizado en métodos de formateo
4. **Nuevos tipos de tercetos**: Extender `formatearInstruccion()`
5. **Nuevas estadísticas**: Agregar método privado + llamar en `imprimirResumenCompilacion()`

---

## Referencias

- **Triplet.java**: Estructura de tercetos
- **IntermediateCode.java**: Contenedor de triplets
- **Instruction.java**: Estructura de instrucción máquina
- **Opcode.java**: Enumeración de códigos de operación
- **MachineCodeGenerator.java**: Generador de código máquina
