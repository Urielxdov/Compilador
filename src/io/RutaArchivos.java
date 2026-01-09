package io;

/**
 * RutaArchivos
 *
 * Enum que centraliza las rutas de archivos utilizados
 * por el sistema, evitando el suso de hardcode
 */
public enum RutaArchivos {
    PROGRAMA("src/io/archivos/programa.txt"),
    GRAMATICA("src/io/archivos/gramatica.txt");

    /**Ruta fisica del archivo en el sistema*/
    public final String ruta;

    RutaArchivos(String ruta) {
        this.ruta = ruta;
    }
}
