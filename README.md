# Compilador

Proyecto académico que implementa las fases clásicas de un compilador: análisis léxico, sintáctico (LL1), semántico, construcción de AST y generación de código intermedio en formato de cuádruplos.

## Objetivo

Comprender e implementar los conceptos fundamentales de compiladores:

- Representación formal de gramáticas libres de contexto
- Análisis léxico mediante tokens
- Parser predictivo LL(1) con tabla de análisis
- Árbol de Sintaxis Abstracta (AST)
- Análisis semántico con tabla de símbolos
- Generación de código intermedio de 3 direcciones

Proyecto de uso académico, no productivo.

## Tecnologías

- Java
- POO / patrones de diseño (Strategy)

---

## Estructura del proyecto

```
src/
├── Main.java                        # Punto de entrada; ejecuta el pipeline completo
├── lexer/                           # Análisis léxico
│   ├── Lexer.java
│   ├── Token.java
│   ├── handlers/                    # Manejadores por tipo de token
│   └── constants/TiposTokens.java
├── parser/                          # Análisis sintáctico
│   ├── GrammarParser.java
│   ├── grammar/                     # Representación de gramática (Grammar, Production, Symbol…)
│   ├── ll1/                         # Tabla LL(1) y parser predictivo
│   └── reader/GrammarReader.java
├── semantic/                        # Análisis semántico
│   ├── SymbolTable.java             # Tabla de símbolos (declare / get)
│   ├── SemanticException.java
│   └── ast/
│       ├── NodeKind.java            # Enum de tipos de nodo AST
│       ├── ASTNode.java             # Nodo AST (n-ario, con NodeKind y Token)
│       └── ASTBuilder.java          # Parser descendente recursivo → AST
└── codegen/                         # Generación de código intermedio
    ├── OpKind.java                  # Enum de operaciones del código intermedio
    ├── Cuadruplo.java               # Cuádruplo (indice, op, result, arg1, arg2)
    ├── INotacion.java               # Interfaz de notación (Strategy)
    ├── NotacionCuadruplo.java       # (OP, result, arg1, arg2)
    ├── NotacionPostfija.java        # arg1 arg2 OP -> result
    ├── NotacionPrefija.java         # OP result arg1 arg2
    └── GeneradorCodigo.java         # Recorre AST y emite cuádruplos
```

---

## Pipeline de ejecución (`Main.java`)

```
1. Análisis sintáctico LL(1)
    └─ GrammarParser → Grammar → LL1ParsingTable → LL1Parser.execute()

2. Segunda pasada léxica
   └─ Lexer.all() → List<Token>

3. Análisis semántico + construcción de AST
   └─ SymbolTable + ASTBuilder.construirAST(tokens) → ASTNode raiz

4. Generación de código intermedio
   └─ GeneradorCodigo(notacion).generar(raiz) → List<Cuadruplo>
      └─ gen.imprimir(codigo)
```

La notación de salida se elige con la constante `NOTACION` en `Main.java`:

```java
private static final String NOTACION = "CUADRUPLO"; // CUADRUPLO | POSTFIJA | PREFIJA
```

---

## Gramática soportada (programa.txt)

```
Iniciar
    Int  x, y ;          # declaración entera
    Real z ;             # declaración real
    Leer x ;             # lectura
    x = y + 1 ;          # asignación
    Mostrar x + y ;      # escritura
    Si x > 0 Entonces
        y = x ;
    Sino
        y = 0 ;
Finalizar
```

| Construcción | Sintaxis |
|---|---|
| Declaración entera | `Int id (, id)* ;` |
| Declaración real | `Real id (, id)* ;` |
| Asignación | `id = expresion ;` |
| Lectura | `Leer id ;` |
| Escritura | `Mostrar expresion ;` |
| Condicional | `Si expr_bool Entonces sent Sino sent` |
| Expr. aritmética | `+  -  *  /` con paréntesis y negación unaria |
| Expr. relacional | `>  <  <>  ==` |

---

## Código intermedio — OpKind

| Categoría | Opcodes |
|---|---|
| Declaración | `VARI` (entero), `VARR` (real) |
| Asignación | `ASSIGN` |
| Aritmética | `ADDR` `REST` `MULT` `DIV` `NEG` |
| E/S | `READ` `WRITE` |
| Control de flujo | `IF_FALSE` `GOTO` `LABEL` `HALT` |
| Relacionales | `MAYOR` `MENOR` `DIST` `IGUAL` |

### Ejemplo — notación cuádruplo

```
00 (VARI, x, NULL, NULL)
01 (VARI, y, NULL, NULL)
02 (READ, x, NULL, NULL)
03 (ADDR, t0, y, 1)
04 (ASSIGN, x, t0, NULL)
05 (WRITE, x, NULL, NULL)
06 (MAYOR, t1, x, 0)
07 (IF_FALSE, t1, L0, NULL)
08 (ASSIGN, y, x, NULL)
09 (GOTO, NULL, L1, NULL)
10 (LABEL, L0, NULL, NULL)
11 (ASSIGN, y, 0, NULL)
12 (LABEL, L1, NULL, NULL)
13 (HALT, NULL, NULL, NULL)
```

### Ejemplo — notación postfija

```
03 y 1 ADDR -> t0
04 t0 -> x
```

### Ejemplo — notación prefija

```
03 ADDR t0 y 1
04 ASSIGN x t0
```

---

## Análisis semántico — SymbolTable

`ASTBuilder` invoca `SymbolTable` durante la construcción del AST:

- **`declare(id, tipo)`** — llamado en cada declaración; lanza `SemanticException` si el identificador ya fue declarado.
- **`get(id)`** — llamado en asignaciones, lecturas y expresiones; lanza `SemanticException` si el identificador no fue declarado.

---

## Uso

1. Clonar: `https://github.com/Urielxdov/Compilador.git`
2. Cambiar a la rama: `refactor`
3. Abrir en IntelliJ IDEA (o cualquier IDE Java)
4. Colocar el programa fuente en `programa.txt` (raíz del proyecto o ruta configurada en `RutaArchivos`)
5. Ejecutar `Main.java`

---

## Estado del proyecto

### Implementado
- Análisis léxico completo
- Representación de gramática libre de contexto
- Cálculo de conjunto FIRST
- Tabla LL(1) y parser predictivo
- AST con parser descendente recursivo
- Tabla de símbolos con validación semántica
- Generación de código intermedio (cuádruplos, 3 direcciones)
- Notaciones: cuádruplo estándar, postfija, prefija (patrón Strategy)
- Pipeline completo conectado en `Main.java`

### Pendiente / en desarrollo
- Cálculo de conjunto FOLLOW
- Sincronización entre `gramatica.txt` y `programa.txt` (keywords distintos)
- Manejo avanzado de errores sintácticos con recuperación
- Generación de código objeto / ensamblador
