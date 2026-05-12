package assembler.structures;

import data_structures.Lista;

public class StructureRecord {
    private final String nombre;
    private final StructureType tipo;
    private final int direccionInicio;
    private int direccionFin;
    private String tipoRetorno;
    private final Lista<String> parametros = new Lista<>();

    public StructureRecord(String nombre, StructureType tipo, int direccionInicio) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccionInicio = direccionInicio;
        this.direccionFin = -1;
    }

    public String getNombre()         { return nombre; }
    public StructureType getTipo()    { return tipo; }
    public int getDireccionInicio()   { return direccionInicio; }
    public int getDireccionFin()      { return direccionFin; }
    public String getTipoRetorno()    { return tipoRetorno; }
    public Lista<String> getParametros() { return parametros; }

    public void setDireccionFin(int dir)  { this.direccionFin = dir; }
    public void setTipoRetorno(String tr) { this.tipoRetorno = tr; }
    public void agregarParametro(String p){ parametros.agregar(p); }

    public boolean estaCerrada() { return direccionFin >= 0; }

    @Override
    public String toString() {
        String fin = direccionFin >= 0 ? String.format("%04X", direccionFin) : "??";
        return String.format("[%-8s] %-20s inicio=%04X fin=%s retorno=%s",
                tipo, nombre, direccionInicio, fin,
                tipoRetorno != null ? tipoRetorno : "void");
    }
}
