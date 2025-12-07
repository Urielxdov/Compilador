package io;


import data_structures.Lista;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderManager {
    private FileReader lector;
    private Lista<String> programa;
    private final String RUTA_PROGRAMA = "src/io/archivos/programa.txt";

    public FileReaderManager(){
        this.programa = new Lista<>();
    }

    public Lista<String> leerArchivo () {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_PROGRAMA))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                programa.agregar(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al momento de leer el archivo");
            return null;
        }
        return programa;
    }
}
