# Compilador

Implementación académica de un compilador completo, desde análisis léxico hasta generación de código objeto, con código intermedio (triplets), notación postfija y optimizaciones locales y globales.

## Objetivo

Comprender e implementar las etapas clásicas de un compilador:

- Análisis léxico mediante tokenización
- Representación formal de gramáticas libres de contexto
- Construcción de parser LL(1)
- Análisis semántico con tabla de símbolos y verificación de tipos
- Generación de código intermedio en formato triplets/cuádruplos
- Representación del programa en notación postfija (RPN)
- Optimizaciones locales y globales antes de generar código objeto
- Generación de código objeto (máquina virtual de registros)

Proyecto de uso académico y experimentación.

## Tecnologías

- Java 21
- IntelliJ IDEA (sin Maven/Gradle — fuente en `src/`)

## Estructura del proyecto

```
src/
├── Main.java                          # Pipeline principal (7 fases)
├── lexer/                             # Análisis léxico
│   ├── Lexer.java
│   ├── Token.java
│   ├── handlers/                      # Manejadores por tipo de token
│   ├── constants/                     # Tokens, palabras reservadas
│   └── validators/                    # Límites, punteros
├── parser/                            # Análisis sintáctico LL(1)
│   ├── GrammarParser.java
│   ├── LL1Parser.java
│   ├── LL1ParsingTable.java
│   ├── grammar/                       # Símbolos, producciones, gramática
│   └── reader/                        # Lector de gramática desde archivo
├── semantic/                          # Análisis semántico + AST
│   ├── SemanticAnalyzer.java
│   ├── SymbolTable.java
│   ├── ast/                           # Nodos del AST (ProgramNode, IfNode, ...)
│   └── operations/                    # Tokens de expresión + PostfixConverter
├── intermediate/                      # Código intermedio
│   ├── Triplet.java                   # (instrucción, op1, op2)
│   ├── IntermediateCode.java          # Lista inmutable de triplets
│   ├── IntermediateCodeGenerator.java # AST → triplets
│   └── PostfixPrinter.java            # Programa completo en RPN
├── optimizer/                         # Optimizaciones
│   ├── LocalOptimizer.java            # 4 pases locales
│   ├── BasicBlock.java                # Bloque básico
│   ├── FlowGraph.java                 # Grafo de flujo con aristas
│   └── GlobalOptimizer.java          # Normalización + flujo + constantes
├── assembler/                         # Generación de código objeto
│   ├── Assembler.java
│   ├── AssemblerDriver.java           # AST → instrucciones máquina
│   ├── codegen/                       # Generadores de código
│   └── registers/                     # Registros (general, puntero, bandera)
├── data_structures/                   # Estructuras propias (Pila, Lista, ...)
└── io/                                # E/S y rutas de archivos de programa
```

## Pipeline de compilación

Cada programa fuente pasa por 7 fases en secuencia. Una fase fallida detiene el proceso e imprime el error.

```
Fase 1: Léxico + Sintáctico + Construcción de AST
        ↓ (si parse OK)
Fase 2: Análisis Semántico
        ↓ (si semántica OK)
Fase 3: Generación de Código Intermedio  →  imprime triplets crudos
Fase 4: Notación Postfija (RPN)          →  imprime programa en RPN
Fase 5: Optimización Local (4 pases)     →  imprime antes/después por pase
Fase 6: Optimización Global (3 fases)    →  imprime antes/después por fase
Fase 7: Generación de Código Objeto      →  AssemblerDriver (usa AST original)
```

## Funcionalidades

### Análisis léxico
- Tokenización de identificadores, números enteros y reales, operadores, palabras reservadas

### Análisis sintáctico
- Gramática libre de contexto cargada desde `src/io/archivos/gramatica.txt`
- Cálculo de conjuntos FIRST
- Tabla de análisis LL(1)
- Parser LL(1) con reporte de errores sintácticos

### Análisis semántico
- Tabla de símbolos con tipos (`Entero`, `Real`)
- Verificación de variables declaradas antes de uso
- Inferencia y verificación de tipos en expresiones y asignaciones

### Código intermedio (triplets)
Formato `(instrucción, op1, op2)`. Ejemplo para `a = b + c * 2`:
```
Int          a
Int          b
mov          t0         b
mov          t1         c
*            t1         2
+            t0         t1
=            a          t0
```

### Notación postfija (RPN)
Representación de cada sentencia del programa en notación polaca inversa:
```
a Entero b Entero
a Leer b Leer
b c 2 * + a =
a Mostrar
```

### Optimizaciones locales (4 pases)
Cada pase muestra el estado antes y después:

| Pase | Descripción |
|------|-------------|
| 1 | Subexpresiones redundantes — elimina pares `(mov Ti A, op Ti B)` duplicados |
| 2 | Reutilización CSE — detecta sub-expresiones en cadenas de operaciones |
| 3 | Reducciones algebraicas — `X±0`, `X*/÷1`, `X*0` |
| 4 | Código muerto — elimina temporales definidos pero nunca usados |

### Optimización global (3 fases)
| Fase | Descripción |
|------|-------------|
| 1 | Normalización del fuente — elimina líneas en blanco, sangrías, muestra forma compacta |
| 2 | Bloques básicos + grafo de flujo — partición por reglas de líderes, aristas condicionales/incondicionales |
| 3 | Propagación de constantes — reemplaza referencias a variables con valor literal conocido |

### Generación de código objeto
Máquina virtual de registros generada desde el AST. Instrucciones: LOAD, STORE, ADD, SUB, MUL, CMP, JGT, JMP, READ, WRITE.

## Uso

1. Clonar: `git clone https://github.com/Urielxdov/Compilador.git`
2. Abrir en IntelliJ IDEA (JDK 21)
3. Ejecutar `Main.java`

Los programas de prueba están en `src/io/archivos/programa1.txt` – `programa3.txt` (activos) y `programa4.txt` – `programa10.txt` (disponibles, comentados en `Main.java`).

## Gramática soportada

```
Programa <id>
Inicio
  [Entero | Real] <id> {, <id>} ;
  <id> = <expr> ;
  Leer(<id> {, <id>}) ;
  Escribir(<expr> {, <expr>}) ;
  Si <expr> <relop> <expr> Entonces <sentencia> Sino <sentencia>
Fin
```

Operadores aritméticos: `+  -  *`  
Operadores relacionales: `>  <  <>  ==`

## Estado

### Implementado
- Lexer completo
- Gramática LL(1) + tabla de análisis
- Parser LL(1) con reporte de errores
- Análisis semántico (tipos, declaraciones)
- AST completo
- Código intermedio en triplets
- Notación postfija del programa completo
- Optimizaciones locales (4 pases)
- Optimización global (normalización + bloques básicos + propagación de constantes)
- Generación de código objeto

### Limitaciones
- Gramática LL(1) únicamente (sin recursión izquierda, sin ambigüedad)
- Operador `*` soportado; `/` reconocido léxicamente pero no en gramática actual
- Optimizaciones globales conservadoras (reset de constantes en todo salto)
- Sin soporte para funciones o procedimientos
