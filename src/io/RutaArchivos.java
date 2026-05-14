package io;

/**
 * RutaArchivos
 *
 * Enum que centraliza las rutas de archivos utilizados
 * por el sistema, evitando el suso de hardcode
 */
public enum RutaArchivos {
    PROGRAMA("src/io/archivos/programa.txt"),
    GRAMATICA("src/io/archivos/gramatica.txt"),
    PROGRAMA1("src/io/archivos/programa1.txt"),
    PROGRAMA2("src/io/archivos/programa2.txt"),
    PROGRAMA3("src/io/archivos/programa3.txt"),
    PROGRAMA4("src/io/archivos/programa4.txt"),
    PROGRAMA5("src/io/archivos/programa5.txt"),
    PROGRAMA6("src/io/archivos/programa6.txt"),
    PROGRAMA7("src/io/archivos/programa7.txt"),
    PROGRAMA8("src/io/archivos/programa8.txt"),
    PROGRAMA9("src/io/archivos/programa9.txt"),
    PROGRAMA10("src/io/archivos/programa10.txt");

    /**Ruta fisica del archivo en el sistema*/
    public final String ruta;

    RutaArchivos(String ruta) {
        this.ruta = ruta;
    }
}
