package semantic;

import java.util.List;

public class VerificationTrace {
    private final int linea;
    private final String expresion;
    private final TipoDato tipoVariable;
    private final List<String> tiposOperandos;
    private final TipoDato tipoResultado;
    private final String error;

    public VerificationTrace(int linea, String expresion, TipoDato tipoVariable,
                             List<String> tiposOperandos, TipoDato tipoResultado, String error) {
        this.linea = linea;
        this.expresion = expresion;
        this.tipoVariable = tipoVariable;
        this.tiposOperandos = tiposOperandos;
        this.tipoResultado = tipoResultado;
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Linea ").append(linea).append(": ").append(expresion).append("\n");
        sb.append("Tipo variable: ").append(tipoVariable).append("\n");
        sb.append("Operandos: ");
        sb.append(tiposOperandos.isEmpty() ? "-" : String.join(" | ", tiposOperandos));
        sb.append("\n");
        sb.append("Resultado expresion: ").append(tipoResultado).append("\n");
        sb.append("Estado: ").append(error == null ? "correcto" : "error").append("\n");
        if (error != null) {
            sb.append("Detalle: ").append(error).append("\n");
        }
        return sb.toString();
    }
}
