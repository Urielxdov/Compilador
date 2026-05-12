package assembler;

import data_structures.Map;

public class AddressTable {
    private Map<String, Integer> tabla = new Map<>();

    public void registrar(String nombre, int direccion) {
        tabla.put(nombre, direccion);
    }

    public int getDireccion(String nombre) {
        Integer dir = tabla.get(nombre);
        if (dir == null) throw new AssemblerException("Simbolo no registrado en tabla de direcciones: " + nombre);
        return dir;
    }

    public boolean existe(String nombre) {
        return tabla.existKey(nombre);
    }

    public void recalcular(String nombre, int nuevaDireccion) {
        if (!tabla.existKey(nombre))
            throw new AssemblerException("Simbolo no existe para recalcular: " + nombre);
        tabla.put(nombre, nuevaDireccion);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== TABLA DE DIRECCIONES ===\n");
        sb.append(String.format("  %-15s %s\n", "NOMBRE", "DIRECCION"));
        sb.append("  ").append("-".repeat(25)).append("\n");
        for (String nombre : tabla) {
            sb.append(String.format("  %-15s %04X\n", nombre, tabla.get(nombre)));
        }
        return sb.toString();
    }
}
