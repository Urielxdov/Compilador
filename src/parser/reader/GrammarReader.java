package parser.reader;

import data_structures.Lista;
import io.FileReaderManager;
import io.RutaArchivos;

public class GrammarReader {

    private FileReaderManager lector = new FileReaderManager();

    public Lista<String> leerGramatica() {
        Lista<String> gramatica = lector.leerArchivo(RutaArchivos.GRAMATICA);
        return gramatica;
    }
}
