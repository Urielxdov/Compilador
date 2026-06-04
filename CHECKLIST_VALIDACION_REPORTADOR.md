# Checklist de Validación - ReportadorConsola

**Versión**: 1.0  
**Fecha**: 2026-05-31

---

## ✅ Verificación de Instalación

### Paso 1: Archivo Creado

- [ ] `src/io/ReportadorConsola.java` existe
- [ ] Contiene ~365 líneas
- [ ] Tiene 4 métodos públicos estáticos
- [ ] Compilable sin errores

**Comando de compilación**:

```bash
cd src
javac io/ReportadorConsola.java
```

---

### Paso 2: Importes en Main.java

- [ ] `import io.ReportadorConsola;` está presente
- [ ] No hay conflictos de nombres

**Verificación**:

```bash
grep "ReportadorConsola" src/Main.java
```

---

### Paso 3: Llamadas a ReportadorConsola

- [ ] `Main.java` llama `ReportadorConsola.imprimirCompilacion(...)`
- [ ] Llamada está en `runPipeline()` después de code generation
- [ ] Llamada a `imprimirResumenCompilacion()` está presente

**Verificación**:

```java
// En Main.java, dentro de runPipeline(), después de driver.generate():
ReportadorConsola.imprimirCompilacion(path, ic, icGlobal, driver.getAssembler());
ReportadorConsola.imprimirResumenCompilacion(ic, icGlobal, driver.getAssembler());
```

---

## ✅ Pruebas Funcionales

### Test 1: Compilación Simple

**Archivo**: `programa4.txt`

```
programa principal
variable entero a, b, diferencia;
leer a, b;
diferencia = a - b;
escribir diferencia;
fin.
```

**Esperado en Output**:

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
...
```

**Criterios de Éxito**:

- [x] Se imprime el fuente con números de línea
- [x] Se imprimen los tercetos con índices entre paréntesis
- [x] Separadores de `=` están presentes
- [x] Encabezados de columna están alineados

---

### Test 2: Optimización Visible

**Esperado**:

```
======================================================================
OPTIMIZACION (Comparativo)
======================================================================

--- ANTES (Original) ---
  (0)  ...
  (1)  ...
  ...

--- DESPUES (Optimizado) ---
  (0)  ...
  ...
```

**Criterios de Éxito**:

- [x] Sección ANTES se imprime
- [x] Sección DESPUES se imprime
- [x] Se muestra "Líneas eliminadas: X"
- [x] Se muestra "Reducción: X.X%"

---

### Test 3: Código Máquina Completo

**Esperado**:

```
======================================================================
CODIGO MAQUINA (Instrucciones Ejecutables)
======================================================================
PC     INSTRUCCION        MODO             BYTES
----------------------------------------------------------------------
0000   READ @0100         Directo (@)      0E 01 00 01
0004   ...                ...
...
000X   WRITE @0102        Directo (@)      0F 01 02 01
...
0040   HALT               Inmediato (#)    FF 00 00 00
----------------------------------------------------------------------
```

**Criterios de Éxito**:

- [x] Dirección PC en hexadecimal (0000, 0004, etc.)
- [x] Instrucción con opcode correcto
- [x] MODO con descripción (Directo, Inmediato, Registro)
- [x] BYTES en formato hexadecimal (2 dígitos cada uno)
- [x] WRITE (0x0F) está incluida
- [x] READ (0x0E) está incluida
- [x] HALT (0xFF) está al final

---

### Test 4: Dump de Memoria

**Esperado**:

```
======================================================================
DUMP DE MEMORIA
======================================================================
0000: 0E 01 00 01 | 0E 01 01 01 | 01 01 01 01 | 01 01 00 01
0010: 05 00 00 00 | ...
```

**Criterios de Éxito**:

- [x] Memoria organizada en 16 bytes por fila
- [x] Direcciones en hexadecimal
- [x] Bytes separados por espacios
- [x] Bytes agrupados de 4 en 4 (instrucciones)

---

### Test 5: Resumen de Compilación

**Esperado**:

```
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

**Criterios de Éxito**:

- [x] Se muestran conteos de tercetos
- [x] Se calcula correctamente la tasa de compresión
- [x] Número de instrucciones es correcto
- [x] Memoria en bytes y hexadecimal

---

## ✅ Validación de Datos

### Validación 1: Tercetos Crudos vs Optimizados

- [ ] Tercetos crudos contienen más líneas que optimizados
- [ ] Cada terceto optimizado tiene sentido (no hay basura)
- [ ] Las instrucciones de control (JMP, LABEL) se preservan

---

### Validación 2: Código Máquina

- [ ] Cada instrucción tiene 4 bytes
- [ ] PC incrementa en 4 cada línea
- [ ] Cantidad total de instrucciones = PC_final / 4
- [ ] WRITE y READ están presentes (no omitidos)

---

### Validación 3: Mapeo de Memoria

- [ ] Bytes en código máquina coinciden con dump de memoria
- [ ] Direcciones coinciden exactamente

**Verificación Manual**:

```
Si CODIGO MAQUINA muestra:
0000   MOV #0007          Inmediato (#)    03 00 07 00

Entonces DUMP debe tener:
0000: 03 00 07 00 | ...
```

---

### Validación 4: Sin Errores de I/O

- [ ] Archivos se leen sin excepción
- [ ] Si hay error, se imprime mensaje descriptivo
- [ ] El programa continúa (no crash)

---

## ✅ Validación de Forma

### Alineamiento Visual

- [ ] Todos los índices están alineados a la izquierda
- [ ] Todas las instrucciones están alineadas
- [ ] Todos los operandos están alineados
- [ ] Todos los bytes están alineados
- [ ] No hay tabuladores, solo espacios

**Verificación**:

```bash
# Debe haber solo espacios, no tabs
grep -P '\t' src/io/ReportadorConsola.java
# Si retorna nada, está bien
```

---

### Separadores

- [ ] Líneas de `=` tienen exactamente 70 caracteres
- [ ] Líneas de `-` tienen exactamente 70 caracteres
- [ ] Todos los títulos están entre separadores

---

### Formato Hexadecimal

- [ ] Todas las direcciones PC son MAYUSCULAS (0x0000, no 0x0000)
- [ ] Todos los bytes son 2 dígitos (0x0E, no 0xE)
- [ ] Espacios correctos entre bytes

---

## ✅ Prueba de Stress

### Test con Programa Grande

**Si tienes un programa con 20+ instrucciones**:

- [ ] No hay performance issues
- [ ] Output es legible completo
- [ ] Memoria no crece descontroladamente
- [ ] Tiempo de compilación < 5 segundos

---

## ✅ Compatibilidad

### Verificación de Versiones

```java
// Confirmar que estas clases tienen los métodos esperados:
// Triplet:
getTriplets()      ✓
getInstruccion()   ✓
getOp1()           ✓
getOp2()           ✓

// IntermediateCode:
getTriplets()      ✓

// Instruction:
getOpcode()        ✓
getModo()          ✓
getOperando()      ✓
getBytes()         ✓

// MachineCodeGenerator:
getInstrucciones() ✓
getPC()            ✓
getDumpMemoria()   ✓
```

---

## ✅ Casos Edge

### Edge Case 1: Código Vacío

**Input**: Programa sin instrucciones  
**Esperado**: "[VACÍO]" en secciones correspondientes  
**Verificado**: [ ]

---

### Edge Case 2: Archivo No Existe

**Input**: Ruta inválida en parámetro  
**Esperado**: Mensaje de error en stderr, ejecución continúa  
**Verificado**: [ ]

---

### Edge Case 3: Sin Optimización

**Input**: Tercetos crudos == optimizados  
**Esperado**: "[SIN CAMBIOS]" en OPTIMIZACION  
**Verificado**: [ ]

---

### Edge Case 4: Instrucciones Sin Operandos

**Input**: Instrucción solo con opcode (ej. MOV sin argumentos)  
**Esperado**: Celda vacía en Op2, sin basura  
**Verificado**: [ ]

---

## ✅ Documentación

- [ ] `GUIA_REPORTADOR_CONSOLA.md` está en raíz del proyecto
- [ ] `EJEMPLO_OUTPUT_REPORTADOR.md` contiene 2 ejemplos claros
- [ ] `TECNICA_REPORTADOR_CONSOLA.md` documenta arquitectura
- [ ] Todos los archivos .md son legibles en cualquier editor

---

## 🚀 Casos de Uso Verificados

### Caso 1: Debugging de Tercetos

```java
ReportadorConsola.imprimirSoloTercetos(path, codigoIntermedioCrudo);
```

**Verificado**: [ ]

---

### Caso 2: Debugging de Código Máquina

```java
ReportadorConsola.imprimirSoloCodigoMaquina(generador);
```

**Verificado**: [ ]

---

### Caso 3: Pipeline Completo

```java
ReportadorConsola.imprimirCompilacion(path, ic, icGlobal, generador);
ReportadorConsola.imprimirResumenCompilacion(ic, icGlobal, generador);
```

**Verificado**: [ ]

---

## 📋 Resumen Final

**Total de Puntos a Verificar**: 50+  
**Puntos Verificados**: \_**\_ / \_\_**  
**Porcentaje**: \_\_\_\_ %

### Firma de Validación

- **Revisor**: ************\_\_\_************
- **Fecha**: ************\_\_\_************
- **Resultado**: ☐ APROBADO ☐ NO APROBADO

### Notas

```
_________________________________________________________________

_________________________________________________________________

_________________________________________________________________
```

---

## 🔧 Si Algo No Funciona

1. **Verificar que ReportadorConsola.java compila**:

   ```bash
   javac src/io/ReportadorConsola.java
   ```

2. **Verificar que Main.java tiene el import**:

   ```bash
   grep "import io.ReportadorConsola" src/Main.java
   ```

3. **Verificar que se llama correctamente**:

   ```bash
   grep "ReportadorConsola.imprimirCompilacion" src/Main.java
   ```

4. **Si hay exception, ver el stack trace completo**:

   - Asegúrese que `ic.getTriplets()` no es null
   - Asegúrese que `driver.getAssembler()` no es null
   - Asegúrese que el archivo existe en la ruta especificada

5. **Si no se imprime nada**:
   - Verificar que `System.out` no está redirigido
   - Verificar que el programa llega a `runPipeline()`
   - Verificar que no hay exception silenciosa

---

## 📚 Referencias

- [GUIA_REPORTADOR_CONSOLA.md](../GUIA_REPORTADOR_CONSOLA.md)
- [EJEMPLO_OUTPUT_REPORTADOR.md](../EJEMPLO_OUTPUT_REPORTADOR.md)
- [TECNICA_REPORTADOR_CONSOLA.md](../TECNICA_REPORTADOR_CONSOLA.md)
