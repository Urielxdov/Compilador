# Ejemplo de Output - ReportadorConsola

Este archivo muestra el output EXACTO que debería ver en consola al compilar un programa.

---

## Ejemplo 1: Programa Simple

**Archivo fuente: `programa4.txt`**

```
programa principal
variable entero a, b, diferencia;
leer a, b;
diferencia = a - b;
escribir diferencia;
fin.
```

---

## OUTPUT EN CONSOLA

```
======================================================================
FUENTE (Código Original)
======================================================================
  1 | programa principal
  2 | variable entero a, b, diferencia;
  3 | leer a, b;
  4 | diferencia = a - b;
  5 | escribir diferencia;
  6 | fin.

======================================================================
TERCETOS (CODIGO INTERMEDIO CRUDO)
======================================================================
Idx  Instruccion        Op1            Op2
----------------------------------------------------------------------
(0)  LEER               A
(1)  LEER               B
(2)  MOV                B
(3)  -                  t0             C
(4)  =                  DIFERENCIA     t0
(5)  MOSTRAR            DIFERENCIA
----------------------------------------------------------------------
Total de instrucciones: 6

======================================================================
OPTIMIZACION (Comparativo)
======================================================================

--- ANTES (Original) ---
  (0)  LEER               A
  (1)  LEER               B
  (2)  MOV                B
  (3)  -                  t0             C
  (4)  =                  DIFERENCIA     t0
  (5)  MOSTRAR            DIFERENCIA

--- DESPUES (Optimizado) ---
  (0)  LEER               A
  (1)  LEER               B
  (2)  -                  DIFERENCIA     B
  (3)  MOSTRAR            DIFERENCIA

----------------------------------------------------------------------
Líneas eliminadas: 2
Reducción: 33.3%

======================================================================
CODIGO MAQUINA (Instrucciones Ejecutables)
======================================================================
PC     INSTRUCCION        MODO             BYTES
----------------------------------------------------------------------
0000   READ @0100         Directo (@)      0E 01 00 01
0004   READ @0101         Directo (@)      0E 01 01 01
0008   LOAD @0101         Directo (@)      01 01 01 01
000C   LOAD @0100         Directo (@)      01 01 00 01
0010   SUB                Inmediato (#)    05 00 00 00
0014   STORE @0102        Directo (@)      02 01 02 01
0018   LOAD @0102         Directo (@)      01 01 02 01
001C   WRITE @0102        Directo (@)      0F 01 02 01
0020   HALT               Inmediato (#)    FF 00 00 00
----------------------------------------------------------------------
Total de instrucciones: 9
Memoria usada: 36 bytes

======================================================================
DUMP DE MEMORIA
======================================================================
0000: 0E 01 00 01 | 0E 01 01 01 | 01 01 01 01 | 01 01 00 01
0010: 05 00 00 00 | 02 01 02 01 | 01 01 02 01 | 0F 01 02 01
0020: FF 00 00 00

======================================================================
RESUMEN DE COMPILACION
======================================================================
Tercetos generados (crudo):     6
Tercetos después optimización:  4
Tercetos eliminados:            2
Tasa de compresión:             33.3%

Instrucciones generadas:        9
Memoria usada:                  36 bytes (0x0024)

```

---

## Ejemplo 2: Programa con Condicional

**Archivo fuente: `programa5.txt`**

```
programa principal
variable entero x, resultado;
leer x;
si (x > 10) entonces
  resultado = x + 5;
sino
  resultado = x - 5;
fin si
escribir resultado;
fin.
```

---

## OUTPUT ESPERADO

```
======================================================================
FUENTE (Código Original)
======================================================================
  1 | programa principal
  2 | variable entero x, resultado;
  3 | leer x;
  4 | si (x > 10) entonces
  5 |   resultado = x + 5;
  6 | sino
  7 |   resultado = x - 5;
  8 | fin si
  9 | escribir resultado;
 10 | fin.

======================================================================
TERCETOS (CODIGO INTERMEDIO CRUDO)
======================================================================
Idx  Instruccion        Op1            Op2
----------------------------------------------------------------------
(0)  LEER               X
(1)  JMP_F              X > 10         L0
(2)  MOV                5
(3)  +                  t0             X
(4)  =                  RESULTADO      t0
(5)  JMP                L1
(6)  LABEL              L0
(7)  MOV                5
(8)  -                  t1             X
(9)  =                  RESULTADO      t1
(10) LABEL              L1
(11) MOSTRAR            RESULTADO
----------------------------------------------------------------------
Total de instrucciones: 12

======================================================================
OPTIMIZACION (Comparativo)
======================================================================

--- ANTES (Original) ---
  (0)  LEER               X
  (1)  JMP_F              X > 10         L0
  (2)  MOV                5
  (3)  +                  t0             X
  (4)  =                  RESULTADO      t0
  (5)  JMP                L1
  (6)  LABEL              L0
  (7)  MOV                5
  (8)  -                  t1             X
  (9)  =                  RESULTADO      t1
  (10) LABEL              L1
  (11) MOSTRAR            RESULTADO

--- DESPUES (Optimizado) ---
  (0)  LEER               X
  (1)  JMP_F              X > 10         L0
  (2)  +                  RESULTADO      X 5
  (3)  JMP                L1
  (4)  LABEL              L0
  (5)  -                  RESULTADO      X 5
  (6)  LABEL              L1
  (7)  MOSTRAR            RESULTADO

----------------------------------------------------------------------
Líneas eliminadas: 5
Reducción: 41.7%

======================================================================
CODIGO MAQUINA (Instrucciones Ejecutables)
======================================================================
PC     INSTRUCCION        MODO             BYTES
----------------------------------------------------------------------
0000   READ @0100         Directo (@)      0E 01 00 01
0004   LOAD @0100         Directo (@)      01 01 00 01
0008   MOV #000A          Inmediato (#)    03 00 0A 00
000C   CMP                Inmediato (#)    08 00 00 00
0010   JLE @0018          Directo (@)      0B 01 18 00
0014   JMP @0020          Directo (@)      09 01 20 00
0018   LOAD @0100         Directo (@)      01 01 00 01
001C   MOV #0005          Inmediato (#)    03 00 05 00
0020   ADD                Inmediato (#)    04 00 00 00
0024   STORE @0101        Directo (@)      02 01 01 01
0028   JMP @0030          Directo (@)      09 01 30 00
002C   LOAD @0100         Directo (@)      01 01 00 01
0030   MOV #0005          Inmediato (#)    03 00 05 00
0034   SUB                Inmediato (#)    05 00 00 00
0038   STORE @0101        Directo (@)      02 01 01 01
003C   LOAD @0101         Directo (@)      01 01 01 01
0040   WRITE @0101        Directo (@)      0F 01 01 01
0044   HALT               Inmediato (#)    FF 00 00 00
----------------------------------------------------------------------
Total de instrucciones: 18
Memoria usada: 72 bytes

======================================================================
DUMP DE MEMORIA
======================================================================
0000: 0E 01 00 01 | 01 01 00 01 | 03 00 0A 00 | 08 00 00 00
0010: 0B 01 18 00 | 09 01 20 00 | 01 01 00 01 | 03 00 05 00
0020: 04 00 00 00 | 02 01 01 01 | 09 01 30 00 | 01 01 00 01
0030: 03 00 05 00 | 05 00 00 00 | 02 01 01 01 | 01 01 01 01
0040: 0F 01 01 01 | FF 00 00 00

======================================================================
RESUMEN DE COMPILACION
======================================================================
Tercetos generados (crudo):     12
Tercetos después optimización:  7
Tercetos eliminados:            5
Tasa de compresión:             41.7%

Instrucciones generadas:        18
Memoria usada:                  72 bytes (0x0048)

```

---

## Análisis del Output

### Observaciones Clave:

1. **FUENTE**: Numerada por línea, exactamente como está en el archivo

2. **TERCETOS CRUDOS**:

   - Contiene temporales `t0, t1` para asignaciones intermedias
   - Incluye instrucciones de salto con etiquetas (`L0, L1`)
   - Mostrará operaciones sin optimizar

3. **OPTIMIZACION**:

   - Muestra claramente qué se eliminó
   - Registra el % de compresión
   - Las instrucciones de salto se mantienen (son críticas)

4. **CODIGO MAQUINA**:

   - Cada instrucción en hexadecimal (4 bytes)
   - Incluye READ (0x0E) y WRITE (0x0F)
   - Direcciones en formato PC de 4 en 4

5. **DUMP DE MEMORIA**:
   - Cada fila = 16 bytes (4 instrucciones)
   - Facilita verificación manual

---

## Mappeo de Códigos de Operación

```
NOP   = 0x00  (sin operación)
LOAD  = 0x01  (cargar de memoria)
STORE = 0x02  (guardar a memoria)
MOV   = 0x03  (mover inmediato)
ADD   = 0x04  (suma)
SUB   = 0x05  (resta)
MUL   = 0x06  (multiplicación)
DIV   = 0x07  (división)
CMP   = 0x08  (comparar)
JMP   = 0x09  (salto incondicional)
JGT   = 0x0A  (salta si mayor)
JLT   = 0x0B  (salta si menor)
JEQ   = 0x0C  (salta si igual)
JNE   = 0x0D  (salta si distinto)
READ  = 0x0E  (leer entrada)
WRITE = 0x0F  (escribir salida)
CALL  = 0x10  (llamada a función)
RET   = 0x11  (retorno)
HALT  = 0xFF  (fin de programa)
```

---

## Verificación de Correctitud

✅ El programa:

- Lee variables con READ
- Realiza operaciones aritméticas con ADD/SUB
- Maneja condicionales con JMP/JLE/JLT
- Escribe resultados con WRITE
- Termina con HALT

✅ El reportador:

- Muestra fuente numerada
- Lista tercetos en orden
- Compara antes/después de optimización
- Dumpa memoria en hexadecimal
- Reporta estadísticas finales
