# Documentación Técnica — Motor de Optimización C3D

**Paquete:** `optimizer`  
**Nomenclatura formal:** Código de Tres Direcciones (C3D) basado en Cuádruplos  
*(se usan temporales explícitos t0, t1, … por lo que aplica la definición de cuádruplo)*

---

## Índice

1. [Arquitectura General](#1-arquitectura-general)
2. [LocalOptimizer](#2-localoptimizer)
   - [Pase 1 — Subexpresiones Redundantes](#pase-1--subexpresiones-redundantes)
   - [Pase 2 — Reutilización CSE](#pase-2--reutilización-cse)
   - [Pase 3 — Reducciones Algebraicas](#pase-3--reducciones-algebraicas)
   - [Pase 4 — Eliminación de Código Muerto](#pase-4--eliminación-de-código-muerto)
3. [GlobalOptimizer](#3-globaloptimizer)
   - [Fase 1 — Normalización de Fuente](#fase-1--normalización-de-fuente)
   - [Fase 2 — Análisis de Flujo de Control](#fase-2--análisis-de-flujo-de-control)
   - [Fase 3 — Propagación de Constantes](#fase-3--propagación-de-constantes)
4. [Estructuras de Soporte](#4-estructuras-de-soporte)
   - [BasicBlock](#basicblock)
   - [FlowGraph](#flowgraph)
   - [TripletPatterns](#tripletpatterns)
5. [Restricciones de Diseño (REQ-01/02/03)](#5-restricciones-de-diseño)

---

## 1. Arquitectura General

El pipeline de optimización opera **in-place** sobre una única instancia de `IntermediateCode`. No se crean instancias secundarias; las transformaciones mutan directamente la lista interna de triplets mediante `replaceTriplets(List<Triplet>)`.

```
IntermediateCode ic  (única instancia)
        │
        ▼
LocalOptimizer.optimize(ic)        ← 4 pases locales, convergencia por punto fijo
        │
        ▼
GlobalOptimizer.optimize(ic, path) ← normalización + CFG + propagación de constantes
        │
        ▼
ic.validateJumps()                 ← REQ-02: integridad de saltos
        │
        ▼
ic.imprimirConFuente(path)         ← REQ-03: mapeo semántico línea fuente ↔ triplets
```

Cada pase recibe y devuelve `List<Triplet>` internamente. Al terminar, llama `ic.replaceTriplets(resultado)` para persistir los cambios sobre el mismo objeto.

---

## 2. LocalOptimizer

**Clase:** `optimizer.LocalOptimizer`  
**Método principal:** `void optimize(IntermediateCode ic)`

Ejecuta hasta 3 iteraciones de los 4 pases en orden. Si en una iteración el tamaño de la lista no cambia (punto fijo), detiene el ciclo anticipadamente.

```
iteración 1..3:
  Pase 1 → Pase 2 → Pase 3 → Pase 4
  si |lista| no cambió → break (punto fijo)
```

---

### Pase 1 — Subexpresiones Redundantes

**Método:** `pase1Redundantes(List<Triplet>)`

**Objetivo:** eliminar pares `(mov Ti X, op Ti Y)` duplicados cuando la misma expresión ya fue calculada anteriormente en el mismo temporal.

**Algoritmo:**

1. Recorre la lista buscando pares consecutivos `(mov Ti X, op Ti Y)`.
2. Construye una clave canónica `"X op Y"`.
3. Si la clave ya existe en el mapa (calculada antes con otro temporal `Tj`):
   - Marca los dos triplets del par actual para eliminar.
   - Registra `Ti → Tj` como reemplazo.
4. Aplica las eliminaciones y sustituye todas las referencias a `Ti` por `Tj`.

**Ejemplo:**

```
ANTES:
  mov  t0  a
  +    t0  b        ← "a + b" guardado como t0
  mov  t1  a
  +    t1  b        ← "a + b" duplicado → eliminar, reemplazar t1 por t0

DESPUÉS:
  mov  t0  a
  +    t0  b
```

**Precondición:** los pares deben ser consecutivos (`isMovToTemp` + `isBinaryOpOnTemp`).

---

### Pase 2 — Reutilización CSE

**Método:** `pase2CSE(List<Triplet>)`

**Objetivo:** Common Subexpression Elimination. Si la cadena de operaciones de `Ti` es sufijo de la cadena de `Tj`, y el operando que precede ese sufijo en `Tj` es la base de `Ti`, reemplaza el sufijo completo por una sola operación `op Tj Ti`.

**Algoritmo:**

1. Construye para cada temporal un perfil: `{ base: String, ops: [(instruccion, op2)] }`.
2. Para cada par `(Ti, Tj)` donde `Ti ≠ Tj`:
   - Verifica que `ops(Ti)` sea sufijo de `ops(Tj)`.
   - Verifica que todas las ops de `Ti` sean conmutativas (`+`, `*`).
   - Verifica que el elemento de `Tj` justo antes del sufijo referencie la base de `Ti`.
3. Si hay match: elimina el feeder y todos los ops del sufijo excepto el último; reemplaza el último por `op Tj Ti`.

**Ejemplo:**

```
ANTES:
  mov  t0  X
  +    t0  10        ← Ti: base=X, ops=[(+,10)]

  mov  t1  Y
  +    t1  X         ← feeder (op2==base de Ti)
  +    t1  10        ← sufijo idéntico a ops(Ti)

DESPUÉS:
  mov  t0  X
  +    t0  10

  mov  t1  Y
  +    t1  t0        ← sufijo colapsado: t1 = Y + t0
```

**Restricción:** solo aplica a cadenas de operaciones conmutativas.

---

### Pase 3 — Reducciones Algebraicas

**Método:** `pase3Algebraicas(List<Triplet>)`

**Objetivo:** eliminar operaciones identidad o absorción algebraica.

| Patrón | Acción |
|--------|--------|
| `Ti + 0` | Eliminar triplet |
| `Ti - 0` | Eliminar triplet |
| `Ti * 1` | Eliminar triplet |
| `Ti / 1` | Eliminar triplet |
| `Ti * 0` | Reemplazar por `mov Ti 0` |

**Ejemplo:**

```
ANTES:
  +    t0  0    ← identidad aditiva

DESPUÉS:
  (triplet eliminado)
```

El reemplazo por `mov Ti 0` preserva el `sourceLineNumber` del triplet original.

---

### Pase 4 — Eliminación de Código Muerto

**Método:** `pase4CodigoMuerto(List<Triplet>)`

**Objetivo:** eliminar pares `(mov Ti X, op Ti Y)` cuyo temporal `Ti` nunca se usa después de la operación.

**Algoritmo:**

1. Para cada par `(mov Ti X, op Ti Y)`:
2. Busca en el resto de la lista si `Ti` aparece como `op2` de cualquier instrucción, o como `op1` de un `Mostrar`.
3. Si `Ti` **no** es referenciado después → marca ambos triplets para eliminar.

**Ejemplo:**

```
ANTES:
  mov  t2  a
  +    t2  b        ← t2 nunca más se usa

DESPUÉS:
  (par eliminado)
```

**Nota:** se ejecuta después de los pases 1-3 para que las sustituciones previas no dejen referencias fantasma.

---

## 3. GlobalOptimizer

**Clase:** `optimizer.GlobalOptimizer`  
**Método principal:** `void optimize(IntermediateCode ic, String sourcePath)`

Coordina tres sub-fases secuenciales sobre el mismo `ic`.

---

### Fase 1 — Normalización de Fuente

**Clase:** `optimizer.SourceNormalizer`  
**Método:** `void display(String sourcePath)`

Lee el archivo fuente original y muestra tres vistas para auditoría:

| Vista | Descripción |
|-------|-------------|
| `ANTES` | Líneas tal como existen en el archivo (con sangría y blancos) |
| `DESPUES (sin blancos/sangrias)` | Líneas filtradas y recortadas |
| `DESPUES (compacto)` | Concatenación sin espacios de todas las líneas |

No modifica `ic`. Es un paso de diagnóstico/trazabilidad.

---

### Fase 2 — Análisis de Flujo de Control

**Clase:** `optimizer.ControlFlowAnalyzer`  
**Método:** `List<BasicBlock> buildBlocks(List<Triplet>)`

Delega en `FlowGraph.build()`. Ver [FlowGraph](#flowgraph) para el algoritmo completo.

Imprime el grafo de flujo en consola para diagnóstico. El resultado (lista de bloques) se retorna pero no se usa para modificar `ic` en el pase actual — sirve de base para optimizaciones globales futuras (e.g., eliminación de bloques no alcanzables).

---

### Fase 3 — Propagación de Constantes

**Clase:** `optimizer.ConstantPropagator`  
**Método:** `List<Triplet> propagate(List<Triplet>)`

**Objetivo:** sustituir referencias a variables cuyo valor es una constante literal conocida en el punto de uso.

**Algoritmo:** recorrido lineal con un mapa `constants: variable → literal`.

| Instrucción encontrada | Acción sobre `constants` |
|------------------------|--------------------------|
| `= var literal` | Agrega/actualiza `constants[var] = literal` |
| `= var expr` (no literal) | Elimina `var` de `constants` (asignación invalida la constante) |
| `mov Ti var` donde `var ∈ constants` | Reemplaza por `mov Ti literal` |
| `LABEL` / `JMP` / `JMP_F` | Limpia todo `constants` (punto de unión de flujo) |

El vaciado ante saltos y etiquetas es conservador: garantiza que no se propague un valor que podría diferir según la rama tomada.

**Ejemplo (Programa 2 — multiplicador):**

```
ANTES:
  =    x    5          → constants[x] = 5
  =    y    3          → constants[y] = 3
  mov  t0   x          → x en constants → reemplazar

DESPUÉS:
  =    x    5
  =    y    3
  mov  t0   5          ← propagado
  *    t0   y
```

El resultado se persiste vía `ic.replaceTriplets()`.

---

## 4. Estructuras de Soporte

### BasicBlock

**Clase:** `optimizer.BasicBlock`

Unidad atómica de flujo de control: secuencia de triplets sin saltos internos (excepto el último).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `label` | `String` | Identificador del bloque (B0, B1, …) |
| `triplets` | `List<Triplet>` | Instrucciones del bloque (inmutable) |
| `successors` | `List<String>` | Labels de bloques sucesores (inmutable externamente) |

---

### FlowGraph

**Clase:** `optimizer.FlowGraph`  
**Método principal:** `static FlowGraph build(List<Triplet>)`

**Algoritmo de identificación de líderes** (tres reglas):

| Regla | Condición |
|-------|-----------|
| R1 | El primer triplet siempre es líder |
| R2 | El triplet inmediatamente después de `JMP` o `JMP_F` es líder |
| R3 | Todo triplet `LABEL` es líder (destino de salto) |

**Construcción de aristas:**

| Último triplet del bloque | Sucesor(es) |
|--------------------------|-------------|
| `JMP label` | Bloque que contiene `LABEL label` |
| `JMP_F cond label` | Siguiente bloque (fall-through) + bloque con `LABEL label` |
| Cualquier otra instrucción | Siguiente bloque (fall-through) |
| `LABEL` | Sin sucesor automático |

**Ejemplo — Programa 3:**

```
B0: Int a, Int b, Leer a, Leer b, JMP_F "a>b" L0
    → sucesores: B1 (fall-through), B2 (LABEL L0)

B1: Mostrar a, JMP L1
    → sucesor: B3 (LABEL L1)

B2: LABEL L0, Mostrar b
    → sucesor: B3

B3: LABEL L1
    → sin sucesores
```

---

### TripletPatterns

**Clase:** `optimizer.TripletPatterns` (utilidad estática)

| Método | Descripción |
|--------|-------------|
| `isTemp(String s)` | Retorna `true` si `s` coincide con `t\d+` |
| `isMovToTemp(Triplet t)` | `instruccion == "mov"` y `op1` es temporal |
| `isBinaryOpOnTemp(Triplet t, String temp)` | `op1 == temp`, `op2 != null`, no es `mov` |
| `isCommutativeOp(String op)` | `op ∈ {"+", "*"}` |
| `isCommutativeChain(List<String[]> ops)` | Todos los ops de la cadena son conmutativos |
| `isLiteral(String val)` | Parseable como `int` o `float` |

---

## 5. Restricciones de Diseño

### REQ-01 — Instancia Única / In-Place

- Un solo objeto `IntermediateCode` (`ic`) fluye por todo el pipeline.
- `LocalOptimizer.optimize(ic)` y `GlobalOptimizer.optimize(ic, path)` son `void` — modifican `ic` mediante `ic.replaceTriplets(...)`.
- Prohibido: `new IntermediateCode(...)` dentro de los optimizadores.

### REQ-02 — Validación de Integridad de Saltos

`ic.validateJumps()` verifica que cada destino referenciado por `JMP` u `JMP_F` tenga su `LABEL` correspondiente dentro de la misma lista de triplets. Imprime `[WARN]` por cada referencia rota y `[OK]` si todo es válido.

### REQ-03 — Trazabilidad Semántica

Cada `Triplet` lleva `sourceLineNumber` (1-based) derivado del `Lexer` durante el parsing:

```
Lexer.next()
  → token.lineNumber = ctx.getNumeroLinea() + 1

onProductionApplied(p, lookahead)
  → currentStmtLine = lookahead.getLineNumber()

processStatement(stmt)
  → currentSrcLine = stmt.getLineNumber()
  → emit(...) → new Triplet(..., currentSrcLine)
```

`ic.imprimirConFuente(path)` agrupa triplets por `sourceLineNumber` y los imprime debajo de su línea fuente correspondiente. Una línea fuente puede originar múltiples triplets (e.g., `suma = a + b` genera `mov`, `+`, `=`).
