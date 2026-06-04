# ReportadorConsola - Guía de Uso

## Descripción General

`ReportadorConsola` es una clase dedicada exclusivamente a formatear e imprimir el output de tu compilador en consola con estructura profesional y completa.

**Ubicación:** `src/io/ReportadorConsola.java`

---

## Características Principales

### 1. **FUENTE** - Código Original

- Imprime el código fuente línea por línea
- Muestra números de línea para referencia
- Facilita el seguimiento del compilador

### 2. **TERCETOS** - Código Intermedio

- Formato: `(índice) INSTRUCCION, op1, op2`
- Sin temporales explícitos innecesarios
- Índices numéricos para referencias claras
- Soporta instrucciones de control (JMP_F, LABEL)

### 3. **OPTIMIZACION** - Comparativo Antes/Después

- Muestra el estado crudo vs optimizado
- Incluye estadísticas de reducción
- Refuerza visualización de algebraica y constant folding
- Calcula porcentaje de compresión

### 4. **CODIGO MAQUINA** - Instrucciones Ejecutables

- Muestra todas las instrucciones emitidas (LOAD, STORE, ADD, SUB, MOV, WRITE, READ, JMP, etc.)
- Formatea en hexadecimal con direcciones PC
- Incluye bytes de cada instrucción
- Dump de memoria completo

---

## API Pública

### Métodos Disponibles

#### 1. Impresión Completa

```java
ReportadorConsola.imprimirCompilacion(
    String rutaPrograma,
    IntermediateCode codigoIntermedioCrudo,
    IntermediateCode codigoOptimizado,
    MachineCodeGenerator generador
);
```

**Qué imprime:**

- FUENTE
- TERCETOS (crudo)
- OPTIMIZACION (comparativo)
- CODIGO MAQUINA
- (Todo en orden)

---

#### 2. Solo Tercetos

```java
ReportadorConsola.imprimirSoloTercetos(
    String rutaPrograma,
    IntermediateCode codigo
);
```

**Qué imprime:**

- FUENTE
- TERCETOS

---

#### 3. Solo Código Máquina

```java
ReportadorConsola.imprimirSoloCodigoMaquina(
    MachineCodeGenerator generador
);
```

**Qué imprime:**

- CODIGO MAQUINA
- DUMP DE MEMORIA

---

#### 4. Resumen Estadístico

```java
ReportadorConsola.imprimirResumenCompilacion(
    IntermediateCode codigoIntermedio,
    IntermediateCode codigoOptimizado,
    MachineCodeGenerator generador
);
```

**Qué imprime:**

- Conteos de tercetos
- Tasa de compresión
- Número de instrucciones
- Memoria utilizada

---

## Ejemplo de Uso Completo

### En tu `Main.java` (ya configurado):

```java
// Phase 3: Intermediate code
IntermediateCodeGenerator icg = new IntermediateCodeGenerator();
IntermediateCode ic = icg.generate(result.getProgram());

// Phase 5 & 6: Optimizations
IntermediateCode icLocal = new LocalOptimizer().optimize(ic);
IntermediateCode icGlobal = new GlobalOptimizer().optimize(icLocal, path);

// Phase 7: Code generation
AssemblerDriver driver = new AssemblerDriver();
driver.generate(result.getProgram());

// Phase 8: NUEVA IMPRESION PROFESIONAL
ReportadorConsola.imprimirCompilacion(path, ic, icGlobal, driver.getAssembler());
ReportadorConsola.imprimirResumenCompilacion(ic, icGlobal, driver.getAssembler());
```

---

## Formato de Output Esperado

### FUENTE

```
  1 | programa principal
  2 | variable entero a, b, diferencia;
  3 | leer a, b;
  4 | diferencia = a - b;
  5 | escribir diferencia;
  6 | fin.
```

### TERCETOS (CODIGO INTERMEDIO CRUDO)

```
Idx  Instruccion        Op1            Op2
----------------------------------------------------------------------
(0)  MOV                A
(1)  -                  t0             B
(2)  =                  DIFERENCIA     t0
(3)  MOSTRAR            DIFERENCIA
```

### OPTIMIZACION

```
--- ANTES (Original) ---
  (0)  MOV                A
  (1)  -                  t0             B
  (2)  =                  DIFERENCIA     t0
  (3)  MOSTRAR            DIFERENCIA

--- DESPUES (Optimizado) ---
  (0)  MOV                7
  (1)  =                  DIFERENCIA     7
  (2)  MOSTRAR            DIFERENCIA

Líneas eliminadas: 1
Reducción: 25.0%
```

### CODIGO MAQUINA

```
PC     INSTRUCCION        MODO             BYTES
----------------------------------------------------------------------
0000   MOV #0007          Inmediato (#)    03 00 07 00
0004   STORE @0100        Directo (@)      02 01 00 01
0008   WRITE @0100        Directo (@)      0F 01 00 01
000C   HALT                                FF 00 00 00
```

---

## Características Especiales

### ✅ Manejo de Instrucciones E/S

- **WRITE (0x0F)**: Escritura a salida estándar
- **READ (0x0E)**: Lectura desde entrada estándar
- Ambas incluidas en el dump de memoria completo

### ✅ Mapeo de Saltos

- Saltos condicionales (JGT, JLT, JEQ, JNE)
- Saltos incondicionales (JMP)
- Direcciones de destino en hexadecimal

### ✅ Estadísticas Automáticas

- Porcentaje de optimización
- Bytes de memoria utilizados
- Líneas del código fuente
- Comparativa antes/después

---

## Notas Importantes

1. **Tercetos sin temporales explícitos**: Si una asignación como `a = b + c` se convierte en:

   ```
   (0)  MOV                B
   (1)  +                  t0             C
   (2)  =                  A              t0
   ```

   El sistema entiende que `t0` en (2) es el resultado de (1), NO un temporal independiente.

2. **Optimización real reflejada**: Las optimizaciones de constant folding (si `a=10` y `b=3`, entonces `a-b` se calcula como `7`) se muestran claramente en el bloque DESPUES.

3. **Inclusión de WRITE**: Todas las instrucciones de salida (Escribir/WRITE) se incluyen en el código máquina final con sus opcodes correspondientes.

4. **Líneas de código**: La clase maneja automáticamente la lectura del archivo fuente y su formateo.

---

## Configuración Actual (Ya Hecha)

Tu `Main.java` está configurado para usar:

```java
ReportadorConsola.imprimirCompilacion(path, ic, icGlobal, driver.getAssembler());
ReportadorConsola.imprimirResumenCompilacion(ic, icGlobal, driver.getAssembler());
```

Esto genera el output completo y ordenado en cada compilación.

---

## Personalización

Si necesitas cambiar:

- **Ancho de columnas**: Modifica las constantes en `ReportadorConsola`:

  ```java
  private static final int ANCHO_INDICE = 5;
  private static final int ANCHO_INSTRUCCION = 18;
  private static final int ANCHO_OP1 = 15;
  private static final int ANCHO_OP2 = 20;
  ```

- **Separadores**: Cambia:

  ```java
  private static final String SEPARADOR_MAYOR = "=".repeat(70);
  private static final String SEPARADOR_MENOR = "-".repeat(70);
  ```

- **Información adicional**: Todos los métodos son `private`, así que puedes extender con nuevos métodos públicos.

---

## Troubleshooting

**Si los tercetos no se muestran correctamente:**

- Verifica que `IntermediateCode` tenga el método `getTriplets()`
- Asegúrate que `Triplet` tenga getters para `getInstruccion()`, `getOp1()`, `getOp2()`

**Si el código máquina está vacío:**

- Verifica que `MachineCodeGenerator` tenga `getInstrucciones()` que retorne `Lista<Instruction>`
- Confirma que `Instruction` tenga el método `getBytes()`

**Si la lectura del fuente falla:**

- Verifica que la ruta sea absoluta o relativa válida
- Comprueba permisos de lectura del archivo

---

## Mejoras Futuras (Opcionales)

1. Colorear output con ANSI codes
2. Generar HTML con la salida
3. Exportar a JSON/XML para herramientas externas
4. Visualización gráfica del AST
5. Timeline de optimizaciones paso a paso
