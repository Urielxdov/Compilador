package codegen;

public enum OpKind {
    // Declaraciones
    VARI, VARR,
    // Asignacion y aritmetica
    ASSIGN, ADDR, REST, MULT, DIV, NEG,
    // E/S
    READ, WRITE,
    // Control de flujo
    IF_FALSE, GOTO, LABEL, HALT,
    // Relacionales
    MAYOR, MENOR, DIST, IGUAL
}
