package parser.reader;

import data_structures.Lista;
import io.FileReaderManager;
import io.RutaArchivos;

/**
 * Respinsable de leer la gramatica desde un archivo de entrada
 *
 * La gramatica se obtiene esde la ruta definida en {@link RutaArchivos#GRAMATICA}
 * y se devuelve como una lista de lineas sin procesamiento adicional
 *
 * Esta clase deacopla la lectura de archivos de la logica
 * de analisis gramtical y sintactico
 */
public class GrammarReader {

    private FileReaderManager lector = new FileReaderManager(); // Manejador de lectura

    /**
     * Leer el archivo de gramatica y dvuelve su contenido linea por linea
     *
     * @return lista de cadenas que representan la gramatica
     */
    public Lista<String> leerGramatica() {
        Lista<String> gramatica = lector.leerArchivo(RutaArchivos.GRAMATICA);
        return gramatica;
    }
}
