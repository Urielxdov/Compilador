package io;


import data_structures.Lista;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * FileReaderManager
 *
 * Responsable de carga de un archivo de texto y almacenar
 * su contanido linea por linea en una estructura de datos
 *
 * Funcion principal:
 * - Abstraer el acceso al sistema de archivos
 * - Proveer al programa fuente al resto del sistema
 *
 * Uso:
 * - Lectura de codigo fuente para analisis lexico
 * - Lexutra de gramatica para analisis sintactico
 *
 * Nota:
 * Esta clase no interpreta el contenido del archivo,
 * unicamente lo carga en memoria
 */
public class FileReaderManager {
    /**
     * Lector de archivos (no expone directamente)
     */
    private FileReader lector;
    /**Contenido del archivo leido, linea por linea*/
    private Lista<String> programa;

    public FileReaderManager(){
        this.programa = new Lista<>();
    }

    /**
     * Lee el archivo de texto y almacena su contenido
     * linea por linea
     *
     * @param ruta ubicacion del archivo
     * @return lista de lineas leidas o null si ocurre un error
     */
    public Lista<String> leerArchivo (RutaArchivos ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta.ruta))) {
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
