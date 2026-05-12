package assembler;

public class MemoryMatrix {
    public static final int TAMANO = 0x4000; // 16KB

    private final int[] datos = new int[TAMANO];

    public void escribir(int direccion, int valor) {
        validar(direccion);
        datos[direccion] = valor & 0xFF;
    }

    public int leer(int direccion) {
        validar(direccion);
        return datos[direccion];
    }

    public void escribirPalabra(int direccion, int valor) {
        escribir(direccion,     valor & 0xFF);
        escribir(direccion + 1, (valor >> 8) & 0xFF);
    }

    public int leerPalabra(int direccion) {
        return leer(direccion) | (leer(direccion + 1) << 8);
    }

    // Asocia dirección con su contenido como MemoryCell
    public MemoryCell getCell(int direccion) {
        validar(direccion);
        return new MemoryCell(direccion, datos[direccion]);
    }

    public String dump(int desde, int hasta) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s  %-48s  ASCII\n", "ADDR", "HEX"));
        sb.append("-".repeat(60)).append("\n");
        for (int i = desde; i <= hasta; i += 16) {
            sb.append(String.format("%04X:  ", i));
            StringBuilder ascii = new StringBuilder();
            for (int j = 0; j < 16 && (i + j) <= hasta; j++) {
                int b = datos[i + j];
                sb.append(String.format("%02X ", b));
                ascii.append(b >= 32 && b < 127 ? (char) b : '.');
            }
            sb.append("  ").append(ascii).append("\n");
        }
        return sb.toString();
    }

    private void validar(int direccion) {
        if (direccion < 0 || direccion >= TAMANO)
            throw new AssemblerException("Direccion fuera de rango: 0x" + Integer.toHexString(direccion));
    }
}
