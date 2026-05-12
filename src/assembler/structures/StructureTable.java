package assembler.structures;

import assembler.AssemblerException;
import data_structures.Map;

public class StructureTable {
    private final Map<String, StructureRecord> tabla = new Map<>();

    public void registrar(String nombre, StructureType tipo, int direccionInicio) {
        if (tabla.existKey(nombre))
            throw new AssemblerException("Estructura ya registrada: " + nombre);
        tabla.put(nombre, new StructureRecord(nombre, tipo, direccionInicio));
    }

    public StructureRecord obtener(String nombre) {
        StructureRecord r = tabla.get(nombre);
        if (r == null) throw new AssemblerException("Estructura no encontrada: " + nombre);
        return r;
    }

    public boolean existe(String nombre) {
        return tabla.existKey(nombre);
    }

    public void cerrar(String nombre, int direccionFin) {
        obtener(nombre).setDireccionFin(direccionFin);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== TABLA DE ESTRUCTURAS ===\n");
        sb.append(String.format("  %-10s %-20s %-8s %-8s %s\n",
                "TIPO", "NOMBRE", "INICIO", "FIN", "RETORNO"));
        sb.append("  ").append("-".repeat(60)).append("\n");
        for (String nombre : tabla) {
            sb.append("  ").append(tabla.get(nombre)).append("\n");
        }
        return sb.toString();
    }
}
