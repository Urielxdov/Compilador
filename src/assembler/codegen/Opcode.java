package assembler.codegen;

public enum Opcode {
    NOP  (0x00),
    LOAD (0x01),  // carga de memoria a registro
    STORE(0x02),  // guarda de registro a memoria
    MOV  (0x03),  // mueve valor inmediato a registro
    ADD  (0x04),
    SUB  (0x05),
    MUL  (0x06),
    DIV  (0x07),
    CMP  (0x08),  // compara dos operandos y actualiza FLAGS
    JMP  (0x09),  // salto incondicional
    JGT  (0x0A),  // salta si ZF=0 y SF=0  (mayor)
    JLT  (0x0B),  // salta si SF=1         (menor)
    JEQ  (0x0C),  // salta si ZF=1         (igual)
    JNE  (0x0D),  // salta si ZF=0         (distinto)
    READ (0x0E),  // Leer
    WRITE(0x0F),  // Escribir
    CALL (0x10),  // llama subrutina
    RET  (0x11),  // retorna de subrutina
    HALT (0xFF);  // fin de programa

    private final int codigo;

    Opcode(int codigo) { this.codigo = codigo; }

    public int getCodigo() { return codigo; }
}
