package assembler;

import data_structures.Map;

public class AssemblerSymbolTable {
    public static final int DATA_BASE = 0x1000;

    private Map<String, SymbolRecord> tabla = new Map<>();
    private AddressTable tablaDirecciones = new AddressTable();
    private int contadorDirecciones = DATA_BASE;

    public void declarar(String nombre, DataType tipo) {
        if (tabla.existKey(nombre))
            throw new AssemblerException("Variable ya declarada: " + nombre);
        SymbolRecord registro = new SymbolRecord(nombre, tipo, contadorDirecciones);
        tabla.put(nombre, registro);
        tablaDirecciones.registrar(nombre, contadorDirecciones);
        contadorDirecciones += tipo.getTamano() > 0 ? tipo.getTamano() : 1;
    }

    public void declararCadena(String nombre, int longitud) {
        if (tabla.existKey(nombre))
            throw new AssemblerException("Variable ya declarada: " + nombre);
        SymbolRecord registro = new SymbolRecord(nombre, DataType.CADENA, contadorDirecciones);
        registro.setTamano(longitud);
        tabla.put(nombre, registro);
        tablaDirecciones.registrar(nombre, contadorDirecciones);
        contadorDirecciones += longitud;
    }

    public SymbolRecord obtener(String nombre) {
        SymbolRecord r = tabla.get(nombre);
        if (r == null) throw new AssemblerException("Simbolo no encontrado: " + nombre);
        return r;
    }

    public boolean existe(String nombre) {
        return tabla.existKey(nombre);
    }

    public void asignar(String nombre, String valor) {
        SymbolRecord r = obtener(nombre);
        r.setContenido(valor);
        r.setInicializado(true);
    }

    public void recalcularDireccion(String nombre, int nuevaDireccion) {
        SymbolRecord r = obtener(nombre);
        r.setDireccion(nuevaDireccion);
        tablaDirecciones.recalcular(nombre, nuevaDireccion);
    }

    public AddressTable getAddressTable() { return tablaDirecciones; }
    public int getContadorDirecciones() { return contadorDirecciones; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tablaDirecciones.toString()).append("\n");
        sb.append("=== TABLA DE SIMBOLOS ===\n");
        sb.append(String.format("  %-15s %-8s %-8s %-6s %s\n", "NOMBRE", "TIPO", "DIR", "TAM", "VALOR"));
        sb.append("  ").append("-".repeat(55)).append("\n");
        for (String nombre : tabla) {
            sb.append("  ").append(tabla.get(nombre)).append("\n");
        }
        return sb.toString();
    }
}
