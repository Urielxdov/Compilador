# Cambios Realizados

## Objetivo general

Se incorporó una etapa de verificación de tipos al compilador con el fin de validar la coherencia semántica de declaraciones, asignaciones y expresiones aritméticas dentro del programa fuente.

## Cambios principales

### 1. Integración de verificación de tipos

Se agregó un análisis semántico que permite:

- identificar variables declaradas con tipo `Int` y `Real`
- asociar cada identificador con su tipo de dato
- validar el uso de variables en instrucciones como `Leer` y `Mostrar`
- comprobar que una asignación sea compatible con el tipo de la variable destino
- determinar el tipo resultante de una expresión aritmética
- reportar errores cuando una expresión no coincide con el tipo esperado

## 2. Nuevas clases agregadas

Para organizar la fase semántica se añadieron las siguientes clases:

- `src/semantic/TipoDato.java`
- `src/semantic/SemanticAnalyzer.java`
- `src/semantic/SemanticReport.java`
- `src/semantic/VerificationTrace.java`

Estas clases permiten modelar los tipos de dato, realizar el análisis y generar la salida de verificación.

## 3. Ajustes al análisis léxico

Se realizaron mejoras al lexer para apoyar correctamente la verificación semántica:

- se corrigió el reconocimiento del número `0` como entero válido
- se mantuvo la detección correcta de números reales como `0.0` o `3.14`
- se añadió el operador `/` al conjunto de caracteres simples
- se ajustó el mapeo de operadores para reflejar la división
- se corrigió el registro de líneas para que la numeración mostrada comience en 1

## 4. Mejoras en la tabla de símbolos

Se adaptó la representación de la tabla de símbolos para mostrar de forma más cercana al formato esperado:

- lexema
- tipo de dato
- identificador léxico
- valor
- número de repeticiones
- líneas donde aparece cada símbolo

Además, ahora se conservan repeticiones del mismo lexema dentro de una misma línea, lo cual permite reflejar con mayor precisión su uso real en el programa.

## 5. Cambios en la salida principal

Se modificó `src/Main.java` para mostrar:

- el archivo de prueba leído
- la secuencia de tokens detectados
- la tabla de símbolos semántica
- la verificación de tipos de cada asignación

## 6. Soporte para múltiples sentencias en una línea

Se agregó soporte para procesar varias instrucciones dentro de una misma línea, por ejemplo:

```txt
a = 0; b = 0; c = 0;
```

Esto permite analizar correctamente programas escritos con varias asignaciones en una sola línea, como en los ejemplos de referencia.

## Resultado

Con estos cambios, el compilador no solo reconoce los componentes léxicos del programa, sino que también puede evaluar la consistencia semántica de las operaciones, detectando errores de tipos y reforzando el proceso de validación del lenguaje fuente.
