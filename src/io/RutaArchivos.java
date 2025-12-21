package io;

public enum RutaArchivos {
    PROGRAMA("src/io/archivos/programa.txt"),
    GRAMATICA("src/io/archivos/gramatica.txt");

    public final String ruta;

    RutaArchivos(String ruta) {
        this.ruta = ruta;
    }
}
