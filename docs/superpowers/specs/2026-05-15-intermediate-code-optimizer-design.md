# Diseño: Código Intermedio, Postfija y Optimizaciones

**Fecha:** 2026-05-15  
**Proyecto:** Compilador — pipeline extendido

---

## 1. Contexto y objetivo

Pipeline actual: `Lexer → LL1Parser+ASTBuilder → SemanticAnalyzer → AssemblerDriver`

Se añaden tres fases entre el análisis semántico y la generación de código objeto:
1. Generación de código intermedio (triplets/cuádruplos)
2. Representación en notación postfija del programa completo
3. Optimizaciones (4 locales + 1 global en 3 fases)

El pipeline resultante es:
```
Lexer + LL1Parser + ASTBuilder
        ↓ (si parse OK)
SemanticAnalyzer
        ↓ (si semántica OK)
IntermediateCodeGenerator  → imprime CI crudo
PostfixPrinter             → imprime programa en RPN
LocalOptimizer             → imprime antes/después (4 pases)
GlobalOptimizer            → imprime antes/después (norm + bloques + constantes)
AssemblerDriver            → genera código objeto (sin cambio)
```

Restricción: `AssemblerDriver` y todo el código existente permanecen intactos. Solo `Main.java` añade las nuevas fases.

---

## 2. Estructura de paquetes nuevos

```
src/
├── intermediate/
│   ├── Triplet.java
│   ├── IntermediateCode.java
│   ├── IntermediateCodeGenerator.java
│   └── PostfixPrinter.java
│
└── optimizer/
    ├── LocalOptimizer.java
    ├── BasicBlock.java
    ├── FlowGraph.java
    └── GlobalOptimizer.java
```

---

## 3. Código intermedio (triplets)

### 3.1 `Triplet`

```
Triplet(String instruccion, String op1, String op2)
```

`op1` y `op2` pueden ser null. Representación tabular al imprimir:

```
Instrucción  Op1   Op2
─────────────────────
Int          a     null
mov          t1    c
*            t1    2
mov          t2    b
+            t2    t1
=            a     t2
Mostrar      a     null
```

### 3.2 `IntermediateCode`

Wrapper sobre `List<Triplet>` con:
- `imprimir()` — salida tabular a consola
- `getTriplets()` — acceso inmutable

### 3.3 `IntermediateCodeGenerator`

Recorre `ProgramNode` y emite triplets por tipo de nodo:

| Nodo AST | Triplets emitidos |
|----------|------------------|
| `DeclarationNode` | `Int/Real id` por cada identificador |
| `AssignmentNode` | triplets de expresión + `= target Tn` |
| `ReadNode` | `Leer var` por cada variable |
| `WriteNode` | triplets de expresión + `Mostrar Tn` (o `Mostrar var` si simple) |
| `IfNode` | `JMP_FALSE condTmp label_else`, rama else, `JMP label_end`, etiqueta then, rama then, etiqueta end |

**Algoritmo para expresiones** (postfix stack → triplets):

Para cada token en la secuencia postfija:
- Identificador o literal → push al stack
- Operador binario → pop `right`, pop `left`:
  - Crear nuevo temp `Tn`
  - Emitir `mov Tn left`
  - Emitir `op Tn right`
  - Push `Tn`

El stack al finalizar tiene el temp resultado. Para asignación: emitir `= target Tn`.

Ejemplo `a = b + c * 2`:
```
mov  t1  c
*    t1  2
mov  t2  b
+    t2  t1
=    a   t2
```

---

## 4. Notación postfija del programa completo

`PostfixPrinter` recorre `ProgramNode` y emite cada sentencia en RPN, una por línea.

| Sentencia fuente | Salida RPN |
|-----------------|-----------|
| `Int a, b;` | `a Int b Int` |
| `a = 0;` | `0 a =` |
| `Leer a;` | `a Leer` |
| `a = b + c * 2;` | `b c 2 * + a =` |
| `Mostrar a;` | `a Mostrar` |
| `Si a > b Entonces x=1 Sino x=0` | `a b > x 1 = Entonces x 0 = Sino FinSi` |

---

## 5. Optimizaciones locales

`LocalOptimizer.optimize(IntermediateCode)` aplica 4 pases secuenciales. Cada pase recibe y retorna `List<Triplet>` (nueva lista — inmutabilidad garantiza que el "antes" persiste para impresión). Se imprime antes/después de cada pase.

### Pase 1 — Subexpresiones redundantes

Detecta grupos de instrucciones que computan el mismo valor a distintos temps.

Algoritmo:
1. Escanear bloques de la forma `(mov Ti X, op Ti C)`
2. Si aparece un bloque idéntico posterior `(mov Tj X, op Tj C)`:
   - Registrar `Tj → Ti`
   - Eliminar el bloque duplicado
   - Reemplazar todas las referencias a `Tj` con `Ti` en el resto

```
ANTES:  mov t1 c, * t1 2, ..., mov t3 c, * t3 2, mov t4 a, - t4 t3
DESPUÉS: mov t1 c, * t1 2, ..., mov t4 a, - t4 t1
```

### Pase 2 — Código muerto (dead code elimination)

Detecta temps definidos pero nunca usados como operando en instrucciones posteriores.

Algoritmo:
1. Recolectar todos los temps definidos (lados izquierdos de `mov` y resultados de operaciones)
2. Recolectar todos los temps usados (op1, op2 de instrucciones que no son `mov` o `=`)
3. Temps definidos pero no usados → eliminar su definición y la instrucción `op` siguiente si también es sobre ese temp

```
ANTES:  = T3 T2, + T3 3   (T3 nunca referenciado después)
DESPUÉS: (eliminado)
```

### Pase 3 — Reutilización de sub-expresiones (CSE)

Busca sub-expresiones que ya fueron calculadas en un temp anterior.

Algoritmo:
1. Mantener un mapa `(op, arg) → temp_que_lo_calculó`
2. Por cada instrucción `op Ti arg`:
   - Si `(op, arg)` ya existe en el mapa con temp `Tj`, y `Ti` fue inicializado con `mov Ti X` donde `X` = el primer operando del par anterior:
     - Reemplazar `Ti` con `Tj` en referencias posteriores
     - Eliminar `mov Ti X` + `op Ti arg`

```
ANTES:  mov t1 X, + t1 10, ..., mov t2 Y, + t2 X, + t2 10
DESPUÉS: mov t1 X, + t1 10, ..., mov t2 Y, + t2 t1
```

### Pase 4 — Reducciones algebraicas

Reglas aplicadas instrucción por instrucción:

| Patrón | Acción |
|--------|--------|
| `+ Ti 0` | Eliminar instrucción |
| `- Ti 0` | Eliminar instrucción |
| `* Ti 1` | Eliminar instrucción |
| `* Ti 0` | Reemplazar con `mov Ti 0` |
| `/ Ti 1` | Eliminar instrucción |

```
ANTES:  mov t1 X, + t1 0, mov t2 Y, + t2 t1, + t2 10
DESPUÉS: mov t1 X, mov t2 Y, + t2 t1, + t2 10
```

---

## 6. Optimización global

`GlobalOptimizer.optimize(IntermediateCode, String sourcePath)` aplica 3 fases.

### Fase 1 — Normalización del código fuente

Lee el archivo fuente `.txt`:
- Elimina líneas en blanco
- Elimina sangrías (espacios/tabs iniciales)
- Elimina espacios al final de línea
- Versión compacta: elimina todos los espacios entre tokens

Imprime antes/después en consola.

### Fase 2 — Bloques básicos y grafo de flujo

Particiona los triplets en `BasicBlock` según:
1. Primera instrucción → nuevo bloque
2. Instrucción con etiqueta (destino de salto) → nuevo bloque
3. Instrucción siguiente a un `JMP` o `JMP_FALSE` → nuevo bloque

`FlowGraph` conecta bloques con aristas:
- Bloque sin salto → arista al siguiente
- `JMP label` → arista al bloque de `label`
- `JMP_FALSE label` → arista al siguiente (fall-through) y arista al bloque de `label`

Salida en consola:
```
BLOQUE B1 [0..5]:
  Int a   Int b   Int c
  ...
BLOQUE B2 [6..9]:
  ...
Grafo: B1 → B2, B1 → B3, B2 → B3
```

### Fase 3 — Propagación de constantes

Dentro de cada bloque: si una variable recibe una constante literal (`= var C`) y no es reasignada antes del uso, reemplazar el uso con la constante.

```
ANTES:  = a 0, ..., mov t1 a
DESPUÉS: = a 0, ..., mov t1 0
```

---

## 7. Salida en consola (formato)

```
============================================================
Procesando: PROGRAMA1.txt
============================================================
[OK] Analisis sintactico exitoso.
[OK] Analisis semantico exitoso.

=== CODIGO INTERMEDIO (CRUDO) ===
Instrucción  Op1   Op2
...

=== NOTACION POSTFIJA ===
a Int b Int c Int
0 a = 0 b = 0 c =
...

=== OPTIMIZACION LOCAL: Pase 1 - Subexpresiones Redundantes ===
ANTES:
  ...
DESPUES:
  ...

=== OPTIMIZACION LOCAL: Pase 2 - Codigo Muerto ===
...

=== OPTIMIZACION LOCAL: Pase 3 - Reutilizacion CSE ===
...

=== OPTIMIZACION LOCAL: Pase 4 - Reducciones Algebraicas ===
...

=== OPTIMIZACION GLOBAL ===
--- Fase 1: Normalizacion fuente ---
ANTES:
  ...
DESPUES:
  ...
--- Fase 2: Bloques basicos + grafo de flujo ---
...
--- Fase 3: Propagacion de constantes ---
ANTES:
  ...
DESPUES:
  ...

[OK] Codigo generado.
...
```

---

## 8. Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `src/Main.java` | Añadir fases 3-6 al método `runPipeline` |
| `src/intermediate/Triplet.java` | Nuevo |
| `src/intermediate/IntermediateCode.java` | Nuevo |
| `src/intermediate/IntermediateCodeGenerator.java` | Nuevo |
| `src/intermediate/PostfixPrinter.java` | Nuevo |
| `src/optimizer/LocalOptimizer.java` | Nuevo |
| `src/optimizer/BasicBlock.java` | Nuevo |
| `src/optimizer/FlowGraph.java` | Nuevo |
| `src/optimizer/GlobalOptimizer.java` | Nuevo |

Todo lo demás: sin cambios.

---

## 9. Decisiones de diseño

- **Optimizadores inmutables**: cada pase retorna nueva `List<Triplet>`, no modifica la entrada. Permite capturar "antes" y "después" sin copia manual.
- **AssemblerDriver intacto**: opera desde el `ProgramNode` AST original. El código objeto no se ve afectado por las optimizaciones (el objetivo es educativo — mostrar las optimizaciones, no aplicarlas al código máquina final).
- **PostfixConverter reutilizado**: `semantic.operations.PostfixConverter` ya existe y es correcto. `IntermediateCodeGenerator` lo usa directamente.
- **`FlowGraph` solo texto**: sin librería de grafos externa. Se imprime como lista de adyacencia en consola.
