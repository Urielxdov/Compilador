# Generación de Código Ensamblador

## 1. Introducción

La etapa de generación de código ensamblador constituye la fase final del compilador. Su objetivo es traducir el código intermedio optimizado (representado como tripletas) a instrucciones ejecutables en hardware real.

La herramienta de ensamblador empleada es **NASM** (*Netwide Assembler*) en su variante para arquitectura **x86-64**. NASM es un ensamblador de dos pasadas, multiplataforma y de sintaxis Intel, que permite generar objetos en formatos ELF64 (Linux) y WIN64 (Windows). El código producido por el compilador puede ensamblarse y enlazarse directamente con:

```bash
# Linux
nasm -f elf64 programa.asm -o programa.o
gcc -no-pie programa.o -o programa
./programa

# Windows (MinGW-w64)
nasm -f win64 programa.asm -o programa.obj
gcc programa.obj -o programa.exe
```

El ensamblador generado utiliza registros SSE2 (`xmm0`, `xmm1`) para todas las operaciones aritméticas en punto flotante de doble precisión, y delega la entrada/salida en las funciones estándar de C `scanf` y `printf` a través de macros NASM parametrizadas.

Adicionalmente, el módulo incorpora un **ensamblador educativo interno** que simula la arquitectura de un procesador de pila simple, generando código máquina absoluto, reubicable y reutilizable con propósitos didácticos. Esta capa interna coexiste con la generación NASM sin interferir en la salida ejecutable.

---

## 2. Desarrollo

### 2.1 Algoritmo General

El proceso de generación de código ensamblador sigue cuatro pasos principales:

```
IntermediateCode (tripletas optimizadas)
        │
        ▼
1. Declaración anticipada de variables
        │  Recorre todas las tripletas una primera vez.
        │  Registra cada variable en la tabla de símbolos
        │  y reserva espacio en la sección .bss del archivo NASM.
        ▼
2. Inicio del programa
        │  Abre las estructuras de control en la tabla de estructuras.
        │  Emite el prólogo del bloque main (push rbp, mov rbp rsp, sub rsp 32).
        ▼
3. Traducción de tripletas (una a una)
        │  Cada tripleta se mapea a una o más instrucciones NASM x86-64
        │  y simultáneamente a instrucciones del código máquina educativo.
        │  Los saltos pendientes se resuelven por backpatching cuando
        │  se encuentra la tripleta LABEL correspondiente.
        ▼
4. Epílogo y escritura del archivo
           Emite el retorno limpio (xor eax eax, add rsp 32, pop rbp, ret).
           Ensambla las secciones .data, .bss y .text en un único string
           y lo escribe al archivo .asm.
```

### 2.2 Mapeo de Tripletas a Instrucciones NASM

| Tripleta | Instrucciones NASM emitidas |
|----------|-----------------------------|
| `Int x` / `Real x` | Reserva `x resq 1` en `.bss` |
| `= dest, src` | `movsd xmm0, [rel src]` → `movsd [rel dest], xmm0` |
| `+ dest, src` | `movsd xmm0,[rel dest]` / `movsd xmm1,[rel src]` / `addsd xmm0,xmm1` / `movsd [rel dest],xmm0` |
| `- dest, src` | igual con `subsd` |
| `* dest, src` | igual con `mulsd` |
| `/ dest, src` | igual con `divsd` |
| `Leer x` | macro `READ_NUM x` (llama `scanf`) |
| `Mostrar x` | carga `xmm0` + macro `PRINT_NUM` (llama `printf`) |
| `JMP_F "a op b", Ln` | `ucomisd xmm0,xmm1` + salto condicional negado + backpatch pendiente |
| `JMP Ln` | `jmp Ln` + backpatch pendiente |
| `LABEL Ln` | emite `Ln:` + resuelve todos los saltos pendientes hacia `Ln` |

Los literales numéricos que aparecen como operandos se almacenan en la sección `.data` con etiquetas auto-generadas (`__const_0`, `__const_1`, …) y se referencian mediante `movsd xmm0, [rel __const_N]`.

### 2.3 Clases del Módulo Ensamblador

---

#### `TripletAssemblerDriver`
**Rol:** Punto de entrada de la fase 7. Orquesta toda la generación a partir de un `IntermediateCode`.

**Atributos principales:**
- `textLines` — líneas de la sección `.text` (instrucciones NASM).
- `bssLines` — líneas de la sección `.bss` (reservas de variables).
- `numericConstants` — mapa de valor literal → etiqueta `__const_N` para la sección `.data`.
- `pending` — mapa de etiqueta → lista de PCs que apuntan a ella (backpatching).
- `declaredNames` — conjunto de nombres ya declarados (evita duplicados).

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `generate(IntermediateCode)` | Llama `declareAll`, luego itera tripletas con `emitTrip`, y finaliza con `emitProgramExit`. |
| `declareAll(triplets)` | Dos pasadas: primero declara `Int`/`Real` explícitos, luego auto-declara temporales implícitos y `__rhs`. |
| `declareVar(name, tipo)` | Registra en `AssemblerSymbolTable` y agrega línea `resq 1` a `bssLines`. |
| `autoDecl(name, instr, isOp2)` | Declara variables implícitas (temporales del optimizador) que no tienen tripleta `Int`/`Real` propia. |
| `emitTrip(Triplet)` | Despacha cada tripleta a su método `emit*` correspondiente mediante un `switch`. |
| `loadOperandToXmm(reg, operand)` | Emite `movsd reg, [rel nombre]` o `movsd reg, [rel __const_N]` según si el operando es variable o literal. |
| `emitArith(op)` | Emite `addsd`/`subsd`/`mulsd`/`divsd` tanto en `textLines` como en el generador de código máquina educativo. |
| `emitJmpF(condStr, label)` | Parsea la condición `"left op right"`, carga ambos operandos en `xmm0`/`xmm1`, emite `ucomisd` y el salto condicional negado. |
| `emitLabel(label)` | Emite `label:` y resuelve todas las entradas pendientes en `pending` (backpatching). |
| `negatedJump(op)` | Convierte el operador relacional al salto negado equivalente (`>` → `JLE`, `<` → `JGE`, etc.). |
| `nasmJump(mnemonic)` | Convierte mnemónico interno al mnemónico NASM real (`JLE` → `jbe`, `JGE` → `jae`). |
| `constantLabel(value)` | Crea o reutiliza la etiqueta `__const_N` para un literal numérico. |
| `getTextoEnsamblador()` | Ensambla el string final con cabecera, macros, secciones `.data`/`.bss`/`.text` y epilogo. |
| `escribirArchivo(path)` | Escribe el texto NASM al archivo `.asm` en disco. |

---

#### `Assembler`
**Rol:** Fachada educativa que integra todos los subsistemas del ensamblador simulado. Expone una API de alto nivel para declarar variables y emitir operaciones, delegando en los tres generadores de código.

**Subsistemas que integra:**
- `AssemblerSymbolTable` — tabla de símbolos con direcciones de memoria.
- `MemoryMatrix` — memoria simulada de 16 KB.
- `RegisterFile` — archivo de registros (AX, BX, CX, DX, IP, SP, BP, FLAGS).
- `StructureTable` — tabla de estructuras (programas, funciones, métodos).
- `MachineCodeGenerator` — generador de código absoluto.
- `RelocatableCodeGenerator` — generador de código reubicable.
- `ReusableCodeGenerator` — generador de código reutilizable por segmentos.

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `iniciarPrograma(nombre)` | Registra el programa en `StructureTable` e inicia su segmento en el generador reutilizable. |
| `terminarPrograma(nombre)` | Emite `HALT`, cierra el registro en `StructureTable` y termina el segmento. |
| `declararEntero(nombre)` / `declararReal(nombre)` | Registra la variable en `AssemblerSymbolTable` con el tipo correspondiente. |
| `cargar(nombreVar)` | Emite `LOAD` en los tres generadores y actualiza el registro AX con la dirección del símbolo. |
| `guardar(nombreVar)` | Emite `STORE` en los tres generadores y marca el símbolo como inicializado. |
| `moverInmediato(valor)` | Emite `MOV #valor` en los tres generadores y actualiza AX. |
| `operacion(op)` | Emite `ADD`/`SUB`/`MUL`/`DIV` en los tres generadores según el operador. |
| `leer(nombre)` / `escribir(nombre)` | Emite `READ`/`WRITE` en los tres generadores. |
| `compararYSaltar(var1, var2, op)` | Emite `LOAD` + `CMP` + salto condicional. Retorna el PC del salto para backpatching posterior. |
| `saltoIncondicional()` | Emite `JMP 0` con destino temporal. Retorna PC para backpatching. |
| `cerrarSalto(pcSalto)` | Parcha el operando del salto en `pcSalto` con el PC actual (backpatching). |
| `imprimirResumen()` | Imprime tabla de símbolos, tabla de estructuras, registros, los tres códigos máquina y dump de memoria. |

---

#### `AssemblerSymbolTable`
**Rol:** Administra los símbolos (variables) del programa con sus tipos, direcciones y valores asignados.

La base del segmento de datos es `0x1000`. Cada variable ocupa el número de bytes definido por su `DataType` (`ENTERO`=2, `REAL`=4, `CHAR`=1).

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `declarar(nombre, tipo)` | Crea un `SymbolRecord`, asigna dirección secuencial y registra en `AddressTable`. |
| `declararCadena(nombre, longitud)` | Igual que `declarar` pero con tamaño variable para cadenas. |
| `obtener(nombre)` | Retorna el `SymbolRecord`; lanza excepción si no existe. |
| `asignar(nombre, valor)` | Actualiza el contenido y marca el símbolo como inicializado. |
| `recalcularDireccion(nombre, dir)` | Permite reubicar un símbolo (usado en código reubicable). |

---

#### `SymbolRecord`
**Rol:** Registro individual de un símbolo. Almacena nombre, tipo, dirección de memoria, tamaño, valor actual y estado de inicialización.

---

#### `AddressTable`
**Rol:** Índice auxiliar nombre → dirección. Permite consultas rápidas de dirección sin recorrer la tabla de símbolos completa.

**Métodos:** `registrar`, `getDireccion`, `recalcular`, `existe`.

---

#### `DataType`
**Rol:** Enumeración de tipos de datos soportados con su tamaño en bytes.

| Valor | Tamaño |
|-------|--------|
| `ENTERO` | 2 bytes |
| `REAL` | 4 bytes |
| `CHAR` | 1 byte |
| `CADENA` | variable |

---

#### `MemoryMatrix`
**Rol:** Simula una memoria de 16 KB (`0x0000`–`0x3FFF`) como un arreglo de enteros de 8 bits. Soporta acceso por byte y por palabra de 16 bits.

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `escribir(dir, valor)` | Escribe un byte validando rango. |
| `leer(dir)` | Lee un byte. |
| `escribirPalabra(dir, valor)` | Escribe 2 bytes en little-endian. |
| `leerPalabra(dir)` | Lee 2 bytes en little-endian. |
| `dump(desde, hasta)` | Genera volcado hexadecimal con visualización ASCII para depuración. |

---

#### `MemoryCell`
**Rol:** Encapsula una celda de memoria con su dirección y valor. Usado como tipo de retorno por `MemoryMatrix.getCell()`.

---

#### `MachineCodeGenerator`
**Rol:** Generador de código máquina absoluto. Mantiene una lista de instrucciones de 4 bytes cada una y las escribe en `MemoryMatrix`. Base de código: `0x0000`.

Cada instrucción ocupa exactamente 4 bytes con el formato:

```
[ opcode (1 byte) | modo (1 byte) | operando_lo (1 byte) | operando_hi (1 byte) ]
```

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `emitir(Instruction)` | Agrega instrucción a la lista, escribe sus bytes en memoria y avanza el PC. |
| `emitirLoad(nombreVar)` | Consulta dirección en tabla de símbolos y emite `LOAD @dir`. |
| `emitirStore(nombreVar)` | Emite `STORE @dir`. |
| `emitirMov(valor)` | Emite `MOV #valor` (modo inmediato). |
| `emitirAdd/Sub/Mul/Div()` | Emiten la operación aritmética sin operando. |
| `emitirCmp(nombreVar)` | Emite `CMP @dir`. |
| `emitirJmp/Jgt/Jlt/Jeq/Jne/Jle/Jge(destino)` | Emiten saltos con destino provisional. |
| `emitirRead/Write(nombreVar)` | Emiten `READ`/`WRITE` con dirección del símbolo. |
| `parchear(pcInstruccion, nuevoDest)` | Actualiza el operando de una instrucción ya emitida (backpatching). Recalcula índice como `(pc - CODE_BASE) / 4`. |
| `getMachineCode()` | Genera tabla formateada con PC, mnemónico y bytes hexadecimales. |
| `getDumpMemoria()` | Retorna el volcado de la zona de código. |

---

#### `RelocatableCodeGenerator`
**Rol:** Extiende `MachineCodeGenerator` para generar código reubicable. Las referencias a símbolos se registran en una tabla de reubicación y sus direcciones se resuelven en función de un `baseOffset` configurable, permitiendo cargar el código en cualquier dirección.

**Métodos adicionales:**

| Método | Descripción |
|--------|-------------|
| `setBaseOffset(offset)` | Establece la base de reubicación y reinicia el contador PC. |
| `resolver()` | Recorre la tabla de reubicación y aplica `baseOffset` a cada referencia de símbolo. |
| `getMachineCode()` | Llama `resolver()` antes de generar el listado; incluye la tabla de reubicación al final. |

---

#### `ReusableCodeGenerator`
**Rol:** Extiende `MachineCodeGenerator` para organizar el código en segmentos nombrados (programas, funciones, métodos). Permite localizar y llamar segmentos por nombre mediante `CALL`.

**Métodos adicionales:**

| Método | Descripción |
|--------|-------------|
| `iniciarSegmento(nombre)` | Registra el índice inicial del segmento actual. |
| `terminarSegmento()` | Cierra el segmento activo. |
| `getDireccionSegmento(nombre)` | Calcula la dirección absoluta del inicio de un segmento. |
| `emitirCallSegmento(nombre)` | Emite un `CALL` apuntando al segmento indicado. |

---

#### `Instruction`
**Rol:** Representa una instrucción de máquina de tamaño fijo (4 bytes). Soporta tres modos de direccionamiento: inmediato (`#`), directo (`@`) y registro (`R`).

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `getBytes()` | Serializa la instrucción a 4 bytes: `[opcode, modo, operando_lo, operando_hi]`. |
| `setOperando(valor)` | Permite modificar el operando después de emitida (usado en backpatching). |
| `toString()` | Produce representación legible como `"LOAD  @1002"` para listados. |

---

#### `Opcode`
**Rol:** Enumeración de todos los códigos de operación soportados con su valor hexadecimal.

| Opcode | Código | Descripción |
|--------|--------|-------------|
| `NOP` | `0x00` | Sin operación |
| `LOAD` | `0x01` | Carga memoria → registro |
| `STORE` | `0x02` | Guarda registro → memoria |
| `MOV` | `0x03` | Mueve valor inmediato a registro |
| `ADD` | `0x04` | Suma |
| `SUB` | `0x05` | Resta |
| `MUL` | `0x06` | Multiplicación |
| `DIV` | `0x07` | División |
| `CMP` | `0x08` | Compara y actualiza FLAGS |
| `JMP` | `0x09` | Salto incondicional |
| `JGT` | `0x0A` | Salta si mayor (ZF=0, SF=0) |
| `JLT` | `0x0B` | Salta si menor (SF=1) |
| `JEQ` | `0x0C` | Salta si igual (ZF=1) |
| `JNE` | `0x0D` | Salta si distinto (ZF=0) |
| `READ` | `0x0E` | Lee valor de entrada |
| `WRITE` | `0x0F` | Escribe valor de salida |
| `JLE` | `0x10` | Salta si menor o igual |
| `JGE` | `0x11` | Salta si mayor o igual |
| `CALL` | `0x12` | Llama subrutina |
| `RET` | `0x13` | Retorna de subrutina |
| `HALT` | `0xFF` | Fin de programa |

---

#### `RegisterFile`
**Rol:** Modela el archivo de registros del procesador simulado. Contiene cuatro registros generales (AX, BX, CX, DX), un registro de banderas y tres registros apuntadores (IP, SP, BP).

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `registroLibre()` | Retorna el primer registro general disponible en orden AX→BX→CX→DX. |
| `reiniciarGenerales()` | Libera todos los registros generales. |
| `registrosEnUso()` | Cuenta cuántos registros generales están activos. |

---

#### `StructureTable` y `StructureRecord`
**Rol:** La `StructureTable` lleva registro de las estructuras de código del programa (programas, funciones, métodos) con sus direcciones de inicio y fin. Cada entrada es un `StructureRecord` que además almacena el tipo de retorno para funciones.

**Métodos de `StructureTable`:**

| Método | Descripción |
|--------|-------------|
| `registrar(nombre, tipo, dirInicio)` | Crea una nueva entrada en la tabla. |
| `cerrar(nombre, dirFin)` | Establece la dirección de fin cuando se termina de generar la estructura. |
| `obtener(nombre)` | Retorna el `StructureRecord`; lanza excepción si no existe. |

---

#### `AssemblerDriver` *(legado)*
**Rol:** Driver alternativo que recorre el AST directamente (antes de que existiera la fase de código intermedio). Ya no se invoca desde el pipeline principal (`Main`), pero permanece operativo para referencia. Traduce nodos del AST (`DeclarationNode`, `AssignmentNode`, `ReadNode`, `WriteNode`, `IfNode`) a instrucciones del `Assembler` educativo evaluando expresiones en notación postfija.

---

### 2.4 Mecanismo de Backpatching

Los saltos hacia adelante (cuando la etiqueta destino aún no se ha emitido) se resuelven mediante backpatching en dos estructuras paralelas:

1. **Código máquina educativo:** `MachineCodeGenerator.parchear(pc, destino)` localiza la instrucción por su PC, actualiza el campo `operando` y reescribe los bytes en `MemoryMatrix`.

2. **NASM:** `TripletAssemblerDriver` mantiene el mapa `pending` (etiqueta → lista de PCs). Al encontrar una tripleta `JMP` o `JMP_F`, registra el PC actual en `pending`. Al encontrar la tripleta `LABEL Ln`, retira la entrada de `pending` y llama a `gen.parchear()` para cada PC registrado.

```
Tripleta JMP_F → addPending("L1", pc_actual)
    ...
Tripleta LABEL L1 → textLines.add("L1:"), parchear todos los PCs de "L1"
```

### 2.5 Estructura del Archivo NASM Generado

```
; cabecera con instrucciones de compilación
default rel
global main
extern printf
extern scanf

%macro READ_NUM 1   ; scanf para double
%macro PRINT_NUM 0  ; printf para double (xmm0)

section .data
    fmt_read_num     db "%lf", 0
    fmt_write_num    db "%.6g", 10, 0
    __const_0        dq 5.0        ; literales numéricos del programa

section .bss
    x                resq 1        ; variables declaradas
    __rhs            resq 1        ; temporal para comparaciones con literales

section .text
main:
    push rbp
    mov  rbp, rsp
    sub  rsp, 32
    ; ... instrucciones traducidas de las tripletas ...
    xor  eax, eax
    add  rsp, 32
    pop  rbp
    ret
```
