package assembler;

import assembler.codegen.Instruction;
import assembler.codegen.MachineCodeGenerator;
import assembler.codegen.Opcode;
import assembler.codegen.RelocatableCodeGenerator;
import assembler.codegen.ReusableCodeGenerator;
import assembler.registers.RegisterFile;
import assembler.structures.StructureRecord;
import assembler.structures.StructureTable;
import assembler.structures.StructureType;

/**
 * Ensamblador principal.
 *
 * Integra:
 *   - Tabla de símbolos con direcciones
 *   - Matriz de memoria absoluta
 *   - Archivo de registros (generales, banderas, apuntadores)
 *   - Tabla de estructuras (programa, funciones, métodos)
 *   - Tres modos de generación de código: absoluto, reubicable, reutilizable
 */
public class Assembler {
    private final AssemblerSymbolTable tablaSimbolos;
    private final MemoryMatrix memoria;
    private final RegisterFile registros;
    private final StructureTable tablaEstructuras;
    private final MachineCodeGenerator generadorAbsoluto;
    private final RelocatableCodeGenerator generadorReubicable;
    private final ReusableCodeGenerator generadorReutilizable;

    public Assembler() {
        tablaSimbolos        = new AssemblerSymbolTable();
        memoria              = new MemoryMatrix();
        registros            = new RegisterFile();
        tablaEstructuras     = new StructureTable();
        generadorAbsoluto    = new MachineCodeGenerator(tablaSimbolos, memoria);
        generadorReubicable  = new RelocatableCodeGenerator(tablaSimbolos, new MemoryMatrix());
        generadorReutilizable= new ReusableCodeGenerator(tablaSimbolos, new MemoryMatrix());
    }

    // ---------------------------------------------------------------
    // Gestión de estructuras
    // ---------------------------------------------------------------

    public void iniciarPrograma(String nombre) {
        tablaEstructuras.registrar(nombre, StructureType.PROGRAMA, generadorAbsoluto.getPC());
        generadorReutilizable.iniciarSegmento(nombre);
    }

    public void terminarPrograma(String nombre) {
        generadorAbsoluto.emitirHalt();
        generadorReutilizable.emitirHalt();
        tablaEstructuras.cerrar(nombre, generadorAbsoluto.getPC());
        generadorReutilizable.terminarSegmento();
    }

    public void iniciarFuncion(String nombre, String tipoRetorno) {
        tablaEstructuras.registrar(nombre, StructureType.FUNCION, generadorAbsoluto.getPC());
        tablaEstructuras.obtener(nombre).setTipoRetorno(tipoRetorno);
        generadorReutilizable.iniciarSegmento(nombre);
    }

    public void terminarFuncion(String nombre) {
        generadorAbsoluto.emitirRet();
        generadorReutilizable.emitirRet();
        tablaEstructuras.cerrar(nombre, generadorAbsoluto.getPC());
        generadorReutilizable.terminarSegmento();
    }

    public void iniciarMetodo(String clase, String nombre) {
        String clave = clase + "." + nombre;
        tablaEstructuras.registrar(clave, StructureType.METODO, generadorAbsoluto.getPC());
        generadorReutilizable.iniciarSegmento(clave);
    }

    public void terminarMetodo(String clase, String nombre) {
        String clave = clase + "." + nombre;
        generadorAbsoluto.emitirRet();
        generadorReutilizable.emitirRet();
        tablaEstructuras.cerrar(clave, generadorAbsoluto.getPC());
        generadorReutilizable.terminarSegmento();
    }

    // ---------------------------------------------------------------
    // Declaración de variables y tipos
    // ---------------------------------------------------------------

    public void declararEntero(String nombre) {
        tablaSimbolos.declarar(nombre, DataType.ENTERO);
    }

    public void declararReal(String nombre) {
        tablaSimbolos.declarar(nombre, DataType.REAL);
    }

    public void declararChar(String nombre) {
        tablaSimbolos.declarar(nombre, DataType.CHAR);
    }

    public void declararCadena(String nombre, int longitud) {
        tablaSimbolos.declararCadena(nombre, longitud);
    }

    // ---------------------------------------------------------------
    // Operaciones de código (delegadas a los generadores)
    // ---------------------------------------------------------------

    public void cargar(String nombreVar) {
        generadorAbsoluto.emitirLoad(nombreVar);
        generadorReubicable.emitirLoad(nombreVar);
        generadorReutilizable.emitirLoad(nombreVar);
        registros.getAX().setValor(tablaSimbolos.obtener(nombreVar).getDireccion());
    }

    public void guardar(String nombreVar) {
        generadorAbsoluto.emitirStore(nombreVar);
        generadorReubicable.emitirStore(nombreVar);
        generadorReutilizable.emitirStore(nombreVar);
        tablaSimbolos.asignar(nombreVar, String.valueOf(registros.getAX().getValor()));
    }

    public void moverInmediato(int valor) {
        generadorAbsoluto.emitirMov(valor);
        generadorReubicable.emitirMov(valor);
        generadorReutilizable.emitirMov(valor);
        registros.getAX().setValor(valor);
    }

    public void operacion(String op) {
        switch (op) {
            case "+" -> { generadorAbsoluto.emitirAdd(); generadorReubicable.emitirAdd(); generadorReutilizable.emitirAdd(); }
            case "-" -> { generadorAbsoluto.emitirSub(); generadorReubicable.emitirSub(); generadorReutilizable.emitirSub(); }
            case "*" -> { generadorAbsoluto.emitirMul(); generadorReubicable.emitirMul(); generadorReutilizable.emitirMul(); }
            case "/" -> { generadorAbsoluto.emitirDiv(); generadorReubicable.emitirDiv(); generadorReutilizable.emitirDiv(); }
            default  -> throw new AssemblerException("Operador no soportado: " + op);
        }
    }

    public void leer(String nombre) {
        generadorAbsoluto.emitirRead(nombre);
        generadorReubicable.emitirRead(nombre);
        generadorReutilizable.emitirRead(nombre);
    }

    public void escribir(String nombre) {
        generadorAbsoluto.emitirWrite(nombre);
        generadorReubicable.emitirWrite(nombre);
        generadorReutilizable.emitirWrite(nombre);
    }

    /**
     * Emite CMP + salto condicional.
     * @param operador ">", "<", "==", "<>"
     * @return PC del salto en el generador absoluto (para parchear después)
     */
    public int compararYSaltar(String var1, String var2, String operador) {
        generadorAbsoluto.emitirLoad(var1);
        generadorAbsoluto.emitirCmp(var2);
        int pcSalto = generadorAbsoluto.getPC();
        Opcode op = switch (operador) {
            case ">"  -> Opcode.JGT;
            case "<"  -> Opcode.JLT;
            case "==" -> Opcode.JEQ;
            case "<>" -> Opcode.JNE;
            default   -> throw new AssemblerException("Operador relacional no soportado: " + operador);
        };
        generadorAbsoluto.emitir(new Instruction(op, Instruction.MODO_DIRECTO, 0));
        return pcSalto;
    }

    /** Parcha el destino de un salto emitido en pcSalto con la dirección actual del PC */
    public void cerrarSalto(int pcSalto) {
        generadorAbsoluto.parchear(pcSalto, generadorAbsoluto.getPC());
    }

    /** Recalcula la dirección de un símbolo existente */
    public void recalcularDireccion(String nombre, int nuevaDireccion) {
        tablaSimbolos.recalcularDireccion(nombre, nuevaDireccion);
    }

    // ---------------------------------------------------------------
    // Generación de código final
    // ---------------------------------------------------------------

    public String generarCodigoAbsoluto() {
        return generadorAbsoluto.getMachineCode();
    }

    public String generarCodigoReubicable() {
        generadorReubicable.setBaseOffset(0x2000);
        return generadorReubicable.getMachineCode();
    }

    public String generarCodigoReutilizable() {
        return generadorReutilizable.getMachineCode();
    }

    public String dumpMemoria() {
        return generadorAbsoluto.getDumpMemoria();
    }

    // ---------------------------------------------------------------
    // Acceso a subsistemas
    // ---------------------------------------------------------------

    public RegisterFile       getRegistros()       { return registros; }
    public AssemblerSymbolTable getTablaSimbolos() { return tablaSimbolos; }
    public MemoryMatrix       getMemoria()         { return memoria; }
    public StructureTable     getTablaEstructuras(){ return tablaEstructuras; }
    public int                getPC()              { return generadorAbsoluto.getPC(); }

    public void imprimirResumen() {
        System.out.println("=== RESUMEN DEL ENSAMBLADOR ===\n");
        System.out.println(tablaSimbolos);
        System.out.println(tablaEstructuras);
        System.out.println(registros);
        System.out.println(generarCodigoAbsoluto());
        System.out.println(generarCodigoReubicable());
        System.out.println(generarCodigoReutilizable());
        System.out.println("=== DUMP MEMORIA (segmento de codigo) ===");
        System.out.println(dumpMemoria());
    }
}
