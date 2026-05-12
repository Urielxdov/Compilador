# Documentación Técnica — Compilador/Ensamblador

**Lenguaje de implementación:** Java (JDK 24)  
**IDE:** IntelliJ IDEA  
**Paradigma:** Compilador de una pasada con generación de código ensamblador

---

## 1. Visión General

El proyecto implementa un compilador completo para un lenguaje de alto nivel simple (estilo pseudocódigo en español). El pipeline de compilación tiene cinco fases:

```
Código fuente
     │
     ▼
┌─────────┐
│  Lexer  │  → tokens
└────┬────┘
     │
     ▼
┌──────────┐
│  Parser  │  → árbol de análisis (LL(1))
└────┬─────┘
     │
     ▼
┌───────────────────┐
│ Análisis Semántico│  → AST + tabla de símbolos
└────────┬──────────┘
         │
         ▼
┌────────────────┐
│  Ensamblador   │  → código máquina (absoluto / reubicable / reutilizable)
└────────────────┘
```

---

## 2. Estructura de Directorios

```
Compilador/
├── src/
│   ├── Main.java                        Punto de entrada
│   ├── data_structures/                 Estructuras de datos propias
│   │   ├── Lista.java                   Lista simplemente enlazada genérica
│   │   ├── Map.java                     Mapa clave-valor (lista enlazada)
│   │   ├── Pila.java                    Pila LIFO genérica
│   │   ├── Set.java                     Conjunto sin duplicados
│   │   └── Conjunto.java                Conjunto alternativo
│   ├── io/                              Entrada/salida de archivos
│   │   ├── FileReaderManager.java
│   │   ├── RutaArchivos.java
│   │   └── archivos/
│   │       ├── gramatica.txt            Gramática BNF del lenguaje
│   │       └── programa.txt             Programa de prueba
│   ├── lexer/                           Analizador léxico
│   │   ├── Lexer.java
│   │   ├── Token.java
│   │   ├── Context.java
│   │   ├── constants/
│   │   │   ├── TiposTokens.java         Categorías léxicas (enum)
│   │   │   ├── TablaPalabrasReservadas.java
│   │   │   └── TablaCaracteresSimples.java
│   │   ├── handlers/                    Manejadores de estados del lexer
│   │   └── validators/                 Validadores de límites y punteros
│   ├── parser/                          Analizador sintáctico
│   │   ├── GrammarParser.java
│   │   ├── grammar/                     Modelo de gramática
│   │   │   ├── Grammar.java
│   │   │   ├── Production.java
│   │   │   ├── Symbol.java
│   │   │   ├── Terminal.java
│   │   │   ├── NoTerminal.java
│   │   │   └── Epsilon.java
│   │   ├── ll1/                         Parser LL(1)
│   │   │   ├── LL1Parser.java
│   │   │   ├── LL1ParsingTable.java
│   │   │   ├── Comparator.java
│   │   │   └── TerminalFactory.java
│   │   └── reader/
│   │       └── GrammarReader.java
│   ├── semantic/                        Análisis semántico
│   │   ├── SemanticAnalyzer.java
│   │   ├── SemanticException.java
│   │   ├── SymbolTable.java             Tabla de símbolos básica (lexer→parser)
│   │   ├── ast/                         Árbol de sintaxis abstracta
│   │   │   ├── ASTNode.java
│   │   │   ├── ASTBuilder.java
│   │   │   ├── BinaryOpNode.java
│   │   │   ├── ExpressionNode.java
│   │   │   ├── IdentifierNode.java
│   │   │   ├── LiteralNode.java
│   │   │   └── StatementNode.java
│   │   └── operations/                  Conversión a notación postfija
│   │       ├── PostfixConverter.java
│   │       ├── OperationToken.java
│   │       ├── OperatorToken.java
│   │       └── NumberToken.java
│   └── assembler/                       Ensamblador
│       ├── Assembler.java               Fachada principal
│       ├── AssemblerException.java
│       ├── AssemblerSymbolTable.java    Tabla de símbolos con direcciones
│       ├── AddressTable.java            Tabla de direcciones nombre→dirección
│       ├── DataType.java                Tipos de datos del ensamblador
│       ├── MemoryMatrix.java            Matriz absoluta de memoria (16 KB)
│       ├── MemoryCell.java              Celda de memoria: dirección + contenido
│       ├── SymbolRecord.java            Registro completo de símbolo
│       ├── registers/
│       │   ├── Register.java            Registro base abstracto
│       │   ├── GeneralRegister.java     AX/BX/CX/DX con partes alta/baja
│       │   ├── FlagRegister.java        FLAGS: ZF, SF, CF, OF
│       │   ├── PointerRegister.java     IP, SP, BP
│       │   └── RegisterFile.java        Archivo de registros completo
│       ├── codegen/
│       │   ├── Opcode.java              Juego de instrucciones (enum)
│       │   ├── Instruction.java         Instrucción de 4 bytes
│       │   ├── MachineCodeGenerator.java     Código absoluto
│       │   ├── RelocatableCodeGenerator.java Código reubicable
│       │   └── ReusableCodeGenerator.java    Código reutilizable
│       └── structures/
│           ├── StructureType.java       PROGRAMA, FUNCION, METODO
│           ├── StructureRecord.java     Registro de estructura
│           └── StructureTable.java      Tabla de estructuras del lenguaje
└── docs/
    └── documentacion_tecnica.md        Este documento
```

---

## 3. Gramática del Lenguaje

El lenguaje soporta el siguiente subconjunto BNF (leído desde `gramatica.txt`):

```
programa         → Programa id Inicio lista_sent Fin
lista_sent       → sentencia sent_final
sent_final       → sentencia sent_final | ε
sentencia        → tipo lista_id ;
                 | id = expresion ;
                 | Leer ( lista_id ) ;
                 | Escribir ( lista_expr ) ;
                 | Si expr_bool Entonces sentencia Sino sentencia
lista_id         → id id_final
id_final         → , id id_final | ε
lista_expr       → expresion lista_exprfinal
lista_exprfinal  → , expresion lista_exprfinal | ε
expr_bool        → expr_arit operel expr_arit
expresion        → expr_arit expr_final
expr_final       → operador expr_arit expr_final | ε
expr_arit        → ( expresion ) | id | literalentera | literalreal
tipo             → Entero | Real
operador         → + | - | *
operel           → > | < | <> | ==
inicio           → programa
```

### Palabras Reservadas

| Lexema    | Código | Descripción               |
|-----------|--------|---------------------------|
| Programa  | 400    | Inicio de programa        |
| Real      | 401    | Tipo flotante             |
| Entero    | 402    | Tipo entero               |
| Leer      | 403    | Entrada estándar          |
| Escribir  | 404    | Salida estándar           |
| Si        | 405    | Condicional               |
| Entonces  | 406    | Rama verdadera            |
| Sino      | 407    | Rama falsa                |
| Inicio    | 408    | Inicio de bloque          |
| Fin       | 409    | Fin de bloque             |
| Char      | 410    | Tipo carácter             |
| Cadena    | 411    | Tipo cadena de caracteres |

---

## 4. Módulo Léxico (`lexer/`)

### Token

Unidad léxica con tres campos:

| Campo     | Tipo         | Descripción                        |
|-----------|--------------|------------------------------------|
| `lexema`  | `String`     | Texto original del token           |
| `atributo`| `int`        | Código numérico (ej. 400 = Programa)|
| `tipo`    | `TiposTokens`| Categoría léxica                   |

### Categorías Léxicas (`TiposTokens`)

```
NUMERO_NATURAL    enteros sin signo
NUMERO_FLOTANTE   números con punto decimal
IDENTIFICADOR     nombres de variables / programa
PALABRA_RESERVADA palabras del lenguaje
CARACTER_SIMPLE   operadores y delimitadores (+ - * = ; , ( ))
INVALIDO          secuencia no reconocida
```

### Handlers

Cada tipo de token tiene su propio handler que implementa la interfaz `TokenHandler`:

- `NumeroNaturalesHandler` — reconoce dígitos
- `NumeroFloatHandler` — reconoce dígitos con punto
- `IdentificadoresHandler` — letras seguidas de alfanuméricos
- `PalabrasReservadasHandler` — consulta `TablaPalabrasReservadas`
- `CaracterSimpleHandler` — consulta `TablaCaracteresSimples`

---

## 5. Módulo Sintáctico (`parser/`)

### Gramática

`Grammar` contiene tres colecciones:
- `terminales: Lista<Terminal>`
- `noTerminales: Lista<NoTerminal>`
- `producciones: Lista<Production>`

### Tabla LL(1)

`LL1ParsingTable` construye la tabla de análisis predictivo a partir de los conjuntos **First** y **Follow** calculados por `GrammarAnalysis`.

Estructura interna: `Map<NoTerminal, Map<Terminal, Integer>>` donde el valor es el número de producción a aplicar.

### Parser LL(1)

`LL1Parser` implementa el algoritmo de análisis descendente:

```
Pila ← { simbolo_inicial }
token_actual ← lexer.next()

mientras pila no vacía:
    x ← pila.peek()
    si x es NoTerminal:
        p ← tabla[x][token_actual]
        pila.pop()
        apilar simbolos de p.derecha en orden inverso
    si x es Terminal:
        si x == token_actual: pila.pop(), token_actual = lexer.next()
        si no: error sintáctico
    si x es Epsilon:
        pila.pop()
```

---

## 6. Módulo Semántico (`semantic/`)

### Tabla de Símbolos Básica

`SymbolTable` mapea `String → TiposTokens`. Detecta declaraciones duplicadas y referencias a variables no declaradas.

### AST

Jerarquía de nodos:

```
ASTNode (abstracto)
├── ExpressionNode
├── StatementNode
├── BinaryOpNode   (operaciones aritméticas / relacionales)
├── IdentifierNode (referencias a variables)
└── LiteralNode    (literales enteras y reales)
```

### Conversión Postfija

`PostfixConverter.convert(List<OperationToken>)` implementa el algoritmo de shunting-yard:

- Prioridades: `*` > `+` = `-`
- Soporta paréntesis

---

## 7. Módulo Ensamblador (`assembler/`)

### 7.1 Mapa de Memoria

| Segmento       | Rango              | Contenido                    |
|----------------|--------------------|------------------------------|
| Código         | `0x0000 – 0x0FFF`  | Instrucciones generadas      |
| Datos          | `0x1000 – 0x3FFF`  | Variables declaradas         |
| (Stack, futuro)| `0x4000 – ...`     | No implementado aún          |

Capacidad total de `MemoryMatrix`: **16 KB** (0x4000 bytes).

### 7.2 Tipos de Datos

| Tipo     | Bytes | Descripción                  |
|----------|-------|------------------------------|
| `ENTERO` | 2     | Entero con signo 16-bit      |
| `REAL`   | 4     | Flotante 32-bit              |
| `CHAR`   | 1     | Carácter ASCII               |
| `CADENA` | n     | Cadena; longitud especificada|

### 7.3 Registro de Símbolo (`SymbolRecord`)

Cada variable declarada genera un `SymbolRecord`:

| Campo         | Tipo      | Descripción                         |
|---------------|-----------|-------------------------------------|
| `nombre`      | `String`  | Identificador                       |
| `tipo`        | `DataType`| Tipo de dato                        |
| `direccion`   | `int`     | Dirección en segmento de datos      |
| `tamano`      | `int`     | Bytes ocupados                      |
| `contenido`   | `String`  | Último valor asignado               |
| `inicializado`| `boolean` | `true` tras primera asignación      |

**Cálculo de direcciones:** el `AssemblerSymbolTable` mantiene un contador `contadorDirecciones` que inicia en `DATA_BASE = 0x1000` y avanza `tamano` bytes por cada declaración.

### 7.4 Tabla de Direcciones (`AddressTable`)

Mapa `nombre → dirección`. Operaciones:

- `registrar(nombre, dir)` — registra al declarar la variable
- `getDireccion(nombre)` — consulta
- `recalcular(nombre, nuevaDir)` — actualiza cuando una dirección es reutilizada

### 7.5 Registros

#### Registros Generales

| Registro | Bits | Parte Alta (8-bit) | Parte Baja (8-bit) |
|----------|------|--------------------|--------------------|
| AX       | 16   | AH                 | AL                 |
| BX       | 16   | BH                 | BL                 |
| CX       | 16   | CH                 | CL                 |
| DX       | 16   | DH                 | DL                 |

#### Registro de Banderas (`FlagRegister`)

| Bandera | Significado                              |
|---------|------------------------------------------|
| ZF      | Zero Flag — resultado == 0               |
| SF      | Sign Flag — resultado < 0                |
| CF      | Carry Flag — desbordamiento sin signo    |
| OF      | Overflow Flag — desbordamiento con signo |

#### Registros Apuntadores (`PointerRegister`)

| Registro | Función                  |
|----------|--------------------------|
| IP       | Instruction Pointer      |
| SP       | Stack Pointer            |
| BP       | Base Pointer             |

**`RegisterFile`** expone `registrosEnUso()` (cuenta registros generales activos), `totalRegistros()` = 8, y `registroLibre()` (retorna primer registro general libre).

### 7.6 Juego de Instrucciones

Cada instrucción ocupa **4 bytes**:

```
┌──────────┬──────────┬───────────────────────┐
│ opcode   │  modo    │      operando         │
│  1 byte  │  1 byte  │       2 bytes         │
└──────────┴──────────┴───────────────────────┘
```

**Modos de direccionamiento:**

| Modo | Código | Símbolo | Descripción                   |
|------|--------|---------|-------------------------------|
| Inmediato | `0x00` | `#` | Valor literal en la instrucción |
| Directo   | `0x01` | `@` | Dirección de memoria            |
| Registro  | `0x02` | `R` | Número de registro              |

**Tabla de opcodes:**

| Mnemónico | Código | Descripción                          |
|-----------|--------|--------------------------------------|
| `NOP`     | `0x00` | Sin operación                        |
| `LOAD`    | `0x01` | Carga memoria → registro             |
| `STORE`   | `0x02` | Guarda registro → memoria            |
| `MOV`     | `0x03` | Mueve inmediato → registro           |
| `ADD`     | `0x04` | Suma                                 |
| `SUB`     | `0x05` | Resta                                |
| `MUL`     | `0x06` | Multiplicación                       |
| `DIV`     | `0x07` | División                             |
| `CMP`     | `0x08` | Compara y actualiza FLAGS            |
| `JMP`     | `0x09` | Salto incondicional                  |
| `JGT`     | `0x0A` | Salta si ZF=0 y SF=0 (mayor)        |
| `JLT`     | `0x0B` | Salta si SF=1 (menor)               |
| `JEQ`     | `0x0C` | Salta si ZF=1 (igual)               |
| `JNE`     | `0x0D` | Salta si ZF=0 (distinto)            |
| `READ`    | `0x0E` | Leer de entrada estándar             |
| `WRITE`   | `0x0F` | Escribir a salida estándar           |
| `CALL`    | `0x10` | Llamada a subrutina                  |
| `RET`     | `0x11` | Retorno de subrutina                 |
| `HALT`    | `0xFF` | Fin de programa                      |

### 7.7 Generadores de Código

#### Código Absoluto (`MachineCodeGenerator`)

- Todas las direcciones se resuelven en tiempo de ensamblado.
- El programa puede ejecutarse directamente desde `CODE_BASE = 0x0000`.
- Soporte para **backpatching**: `parchear(pcInstruccion, nuevoDest)` actualiza el operando de un salto ya emitido (necesario para `Si/Sino`).

**Ejemplo de salida:**
```
PC     INSTRUCCION     BYTES
------------------------------------------
0000   READ  @1000     0E 01 00 10
0004   MOV   #0038     03 00 38 00
0008   LOAD  @1000     01 01 00 10
000C   ADD              04 00 00 00
0010   STORE @1004     02 01 04 10
```

#### Código Reubicable (`RelocatableCodeGenerator`)

- Las referencias a variables se marcan como **pendientes** al emitir (`etiqueta` en `Instruction`).
- `resolver()` aplica `baseOffset` a todas las referencias pendientes.
- Genera **tabla de reubicación**: índice de instrucción → nombre del símbolo.
- Permite cargar el programa en cualquier dirección base sin re-ensamblar.

**Ejemplo de salida:**
```
=== TABLA DE REUBICACION ===
  IDX    SIMBOLO      DIR_RESUELTA
  0      numero       3000
  2      multiple     3004
```

#### Código Reutilizable (`ReusableCodeGenerator`)

- Organiza el código en **segmentos nombrados** (subrutinas, funciones, métodos).
- Cada segmento tiene un punto de entrada registrado en `Map<String, Integer>`.
- `emitirCallSegmento(nombre)` genera `CALL @<dir_segmento>`.
- Permite invocar el mismo bloque de código desde múltiples puntos.

### 7.8 Tabla de Estructuras (`StructureTable`)

Detecta e inicializa tres tipos de estructura:

| Tipo       | Descripción                                    |
|------------|------------------------------------------------|
| `PROGRAMA` | Bloque principal (`Programa ... Inicio...Fin`) |
| `FUNCION`  | Subrutina con tipo de retorno                  |
| `METODO`   | Subrutina asociada a una clase (`clase.nombre`)|

Cada `StructureRecord` almacena: nombre, tipo, dirección de inicio, dirección de fin, tipo de retorno, lista de parámetros.

### 7.9 Clase Fachada `Assembler`

Punto de entrada único para el ensamblador. Coordina todos los subsistemas:

```java
Assembler asm = new Assembler();

// Estructuras
asm.iniciarPrograma("ejemplo");
asm.iniciarFuncion("calcular", "Entero");
asm.terminarFuncion("calcular");
asm.iniciarMetodo("MiClase", "imprimir");
asm.terminarMetodo("MiClase", "imprimir");

// Variables
asm.declararEntero("x");
asm.declararReal("y");
asm.declararChar("c");
asm.declararCadena("s", 20);

// Operaciones
asm.leer("x");
asm.moverInmediato(42);
asm.cargar("x");
asm.operacion("+");
asm.guardar("y");
asm.escribir("y");

// Condicional con backpatching
int pcSalto = asm.compararYSaltar("x", "y", ">");
// ... rama Entonces ...
asm.cerrarSalto(pcSalto);
// ... rama Sino ...

// Recalcular dirección de símbolo existente
asm.recalcularDireccion("x", 0x1100);

asm.terminarPrograma("ejemplo");

// Generación de código final
String absoluto    = asm.generarCodigoAbsoluto();
String reubicable  = asm.generarCodigoReubicable();
String reutilizable= asm.generarCodigoReutilizable();
String dump        = asm.dumpMemoria();

asm.imprimirResumen(); // imprime todo
```

---

## 8. Estructuras de Datos Propias

Todas las estructuras están en `data_structures/` y evitan el uso de `java.util.*`.

| Clase   | Tipo                  | Complejidad inserción | Complejidad búsqueda |
|---------|-----------------------|-----------------------|----------------------|
| `Lista` | Lista enlazada simple | O(1) al final         | O(n)                 |
| `Pila`  | Pila LIFO enlazada    | O(1) push/pop         | O(1) peek            |
| `Map`   | Mapa por lista        | O(n) busca duplicados | O(n)                 |
| `Set`   | Conjunto por lista    | O(n)                  | O(n)                 |

---

## 9. Flujo Completo de Compilación

```
1. Main.java
   │
   ├── GrammarParser.ejecutar()
   │     Lee gramatica.txt → puebla Grammar (terminales, no-terminales, producciones)
   │
   ├── LL1ParsingTable(grammar)
   │     Calcula First/Follow → construye tabla predictiva
   │
   ├── LL1Parser.execute()
   │     Lee programa.txt vía Lexer
   │     Aplica tabla LL(1) token por token
   │     Detecta errores sintácticos
   │
   └── demoEnsamblador()
         Instancia Assembler
         Declara variables → AssemblerSymbolTable asigna direcciones
         Emite instrucciones → MachineCodeGenerator escribe en MemoryMatrix
         Genera código absoluto, reubicable, reutilizable
         Imprime tablas, registros y dump de memoria
```

---

## 10. Ejemplo de Compilación

**Entrada (`programa.txt`):**
```
Programa ejemplo
Inicio
  Real multiple, cuenta;
  Entero numero, res;
  Leer (numero);
  multiple = 56 + numero;
  Si multiple > 10 Entonces
    Escribir(numero);
  Sino
    Escribir(multiple);
  res = numero + cuenta;
  Escribir(res);
Fin
```

**Tabla de símbolos generada (segmento datos desde 0x1000):**
```
NOMBRE          TIPO     DIR    TAM    VALOR
multiple        REAL     1000   4      56+numero
cuenta          REAL     1004   4      ---
numero          ENTERO   1008   2      (leer)
res             ENTERO   100A   2      numero+cuenta
```

**Instrucciones generadas (fragmento):**
```
PC     INSTRUCCION     BYTES
0000   READ  @1008     0E 01 08 10    ; Leer(numero)
0004   MOV   #0038     03 00 38 00    ; literal 56
0008   LOAD  @1008     01 01 08 10    ; cargar numero
000C   ADD              04 00 00 00    ; 56 + numero
0010   STORE @1000     02 01 00 10    ; multiple = resultado
0014   LOAD  @1000     01 01 00 10    ; cargar multiple para CMP
0018   CMP   @1008     08 01 08 10    ; multiple > numero
001C   JGT   @????     0A 01 ?? ??    ; salto (parchado después)
0020   WRITE @1008     0F 01 08 10    ; Escribir(numero)
0024   WRITE @1000     0F 01 00 10    ; Sino: Escribir(multiple)
...
00??   HALT             FF 00 00 00
```

---

## 11. Extensión del Sistema

### Agregar nuevo tipo de dato
1. Añadir entrada en `DataType.java` con su tamaño en bytes.
2. Agregar palabra reservada en `TablaPalabrasReservadas.java` con código único.
3. Agregar método `declarar<Tipo>()` en `Assembler.java`.

### Agregar nueva instrucción
1. Agregar entrada en `Opcode.java` con su código de byte.
2. Agregar método `emitir<Instruccion>()` en `MachineCodeGenerator.java`.
3. Agregar caso en `Assembler.java` según semántica.

### Agregar nueva estructura del lenguaje
1. Añadir valor en `StructureType.java`.
2. Añadir métodos `iniciar<Estructura>` / `terminar<Estructura>` en `Assembler.java`.

### Agregar nueva producción gramatical
1. Editar `gramatica.txt` con la nueva producción.
2. Si introduce nuevo terminal: agregar a `TablaPalabrasReservadas` o `TablaCaracteresSimples`.
3. La tabla LL(1) se recalcula automáticamente al ejecutar.
