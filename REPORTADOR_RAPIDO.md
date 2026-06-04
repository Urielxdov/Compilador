# ReportadorConsola - Resumen Rápido

**Creado**: 2026-05-31  
**Estado**: ✅ IMPLEMENTADO Y DOCUMENTADO

---

## ¿Qué es?

`ReportadorConsola` es una clase Java profesional que formatea e imprime el output de tu compilador en consola con estructura completa y clara.

**Ubicación**: `src/io/ReportadorConsola.java`

---

## ¿Qué Imprime?

### 1. **FUENTE**

Código original numerado por línea

```
  1 | programa principal
  2 | variable entero a, b;
  3 | leer a, b;
  ...
```

### 2. **TERCETOS**

Código intermedio con índices (sin temporales innecesarios)

```
Idx  Instruccion        Op1            Op2
------
(0)  LEER               A
(1)  -                  DIFERENCIA     B
```

### 3. **OPTIMIZACION**

Comparativo antes/después de optimizaciones

```
--- ANTES (Original) ---
(0)  MOV                B
(1)  +                  t0             C
(2)  =                  A              t0

--- DESPUES (Optimizado) ---
(0)  +                  A              B

Líneas eliminadas: 2
Reducción: 66.7%
```

### 4. **CODIGO MAQUINA**

Instrucciones ejecutables con bytes en hexadecimal (incluyendo READ/WRITE)

```
PC     INSTRUCCION        MODO             BYTES
------
0000   READ @0100         Directo (@)      0E 01 00 01
0004   WRITE @0101        Directo (@)      0F 01 01 01
0008   HALT               Inmediato (#)    FF 00 00 00
```

### 5. **DUMP MEMORIA**

Contenido de memoria en hexadecimal

```
0000: 0E 01 00 01 | 0F 01 01 01 | FF 00 00 00
```

### 6. **RESUMEN**

Estadísticas de compilación

```
Tercetos generados (crudo):     6
Tercetos después optimización:  4
Tercetos eliminados:            2
Tasa de compresión:             33.3%

Instrucciones generadas:        9
Memoria usada:                  36 bytes (0x0024)
```

---

## Cómo Usar

### Opción 1: Impresión Completa (Recomendado)

```java
ReportadorConsola.imprimirCompilacion(
    rutaPrograma,
    codigoIntermedioCrudo,
    codigoOptimizado,
    generadorCodigoMaquina
);

ReportadorConsola.imprimirResumenCompilacion(
    codigoIntermedioCrudo,
    codigoOptimizado,
    generadorCodigoMaquina
);
```

### Opción 2: Solo Tercetos

```java
ReportadorConsola.imprimirSoloTercetos(rutaPrograma, codigo);
```

### Opción 3: Solo Código Máquina

```java
ReportadorConsola.imprimirSoloCodigoMaquina(generador);
```

---

## Archivos Documentación

| Archivo                              | Contenido                     |
| ------------------------------------ | ----------------------------- |
| `GUIA_REPORTADOR_CONSOLA.md`         | Manual de uso completo        |
| `EJEMPLO_OUTPUT_REPORTADOR.md`       | 2 ejemplos con output visual  |
| `TECNICA_REPORTADOR_CONSOLA.md`      | Arquitectura e implementación |
| `CHECKLIST_VALIDACION_REPORTADOR.md` | Validar que todo funciona     |
| `EXTENSIONES_REPORTADOR_CONSOLA.md`  | Cómo extender la clase        |

---

## Verificación Rápida

✅ **¿Está instalado?**

```bash
ls src/io/ReportadorConsola.java
```

✅ **¿Compila?**

```bash
javac src/io/ReportadorConsola.java
```

✅ **¿Main.java lo llama?**

```bash
grep "ReportadorConsola" src/Main.java
```

---

## Características Principales

- ✅ Imprime código FUENTE original
- ✅ Tercetos con índices (sin temporales explícitos)
- ✅ Comparativo ANTES/DESPUES de optimización
- ✅ Código máquina COMPLETO (incluyendo READ/WRITE)
- ✅ Estadísticas automáticas de optimización
- ✅ Dump de memoria en hexadecimal
- ✅ Manejo robusto de excepciones
- ✅ Alineamiento automático de columnas
- ✅ 100% documentado y extensible

---

## Ejemplo de Ejecución

```
============================================================
Procesando: programa4.txt
============================================================
[OK] Analisis sintactico exitoso.
[OK] Analisis semantico exitoso.
[OK] Optimizaciones completadas.
[OK] Codigo generado.

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
------
(0)  LEER               A
(1)  LEER               B
(2)  MOV                B
(3)  -                  t0             B
(4)  =                  DIFERENCIA     t0
(5)  MOSTRAR            DIFERENCIA
------
Total de instrucciones: 6

[... resto del output ...]

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

## Integración en Pipeline

Tu compilador ahora tiene este flujo mejorado:

```
1. LEXICAL ANALYSIS
2. SYNTAX ANALYSIS → BUILD AST
3. SEMANTIC ANALYSIS
4. INTERMEDIATE CODE GENERATION
5. LOCAL OPTIMIZATIONS (4 pasadas)
6. GLOBAL OPTIMIZATION
7. CODE GENERATION
8. ✨ REPORTADOR CONSOLA (NUEVO) ← ¡AQUI!
   ├── Imprime FUENTE
   ├── Imprime TERCETOS
   ├── Imprime OPTIMIZACION
   ├── Imprime CODIGO MAQUINA
   └── Imprime RESUMEN
```

---

## Próximas Mejoras (Opcionales)

- 🎨 Colorización ANSI
- 📄 Exportación a HTML
- 📊 Timeline de optimizaciones
- 🔍 Validación de integridad
- 📈 Comparativa múltiple ejecuciones

**Ver**: `EXTENSIONES_REPORTADOR_CONSOLA.md` para implementar cualquiera de estas.

---

## ¿Preguntas?

1. **¿Cómo cambio el formato?**
   → Edita las constantes en `ReportadorConsola` (líneas 20-30)

2. **¿Cómo agrego más información?**
   → Crea un método privado nuevo y llámalo desde uno público

3. **¿Cómo exporto a archivo?**
   → Ver `EXTENSIONES_REPORTADOR_CONSOLA.md` (Extensión 2)

4. **¿Por qué no se imprime nada?**
   → Ver `CHECKLIST_VALIDACION_REPORTADOR.md` sección "Troubleshooting"

---

## Resumen en Una Línea

**ReportadorConsola** es tu herramienta profesional para visualizar todo lo que hace tu compilador, desde el código fuente hasta el código máquina ejecutable, con estadísticas de optimización en tiempo real.

---

**Estado**: LISTO PARA USAR ✅
