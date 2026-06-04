; ============================================================
; PROGRAMA : programa3
; TARGET   : NASM x86-64 real, enlazado con libc
;
; Linux:
;   nasm -f elf64 programa3.asm -o programa3.o
;   gcc -no-pie programa3.o -o programa3
;   ./programa3
;
; Windows x64 (MinGW-w64):
;   nasm -f win64 programa3.asm -o programa3.obj
;   gcc programa3.obj -o programa3.exe
;   .\programa3.exe
; ============================================================

default rel
global main
extern printf
extern scanf

%macro READ_NUM 1
%ifidn __OUTPUT_FORMAT__, win64
    lea rdx, [rel %1]
    lea rcx, [rel fmt_read_num]
%else
    lea rsi, [rel %1]
    lea rdi, [rel fmt_read_num]
%endif
    xor eax, eax
    call scanf
%endmacro

%macro PRINT_NUM 0
%ifidn __OUTPUT_FORMAT__, win64
    lea rcx, [rel fmt_write_num]
    movapd xmm1, xmm0
    movq rdx, xmm0
%else
    lea rdi, [rel fmt_write_num]
%endif
    mov eax, 1
    call printf
%endmacro

section .data
    fmt_read_num     db "%lf", 0
    fmt_write_num    db "%.6g", 10, 0

section .bss
    a                resq 1
    b                resq 1
    __rhs            resq 1

section .text
main:
    push rbp
    mov rbp, rsp
    sub rsp, 32
    READ_NUM a
    READ_NUM b
    movsd xmm0, [rel a]
    movsd xmm1, [rel b]
    ucomisd xmm0, xmm1
    jbe L0
    movsd xmm0, [rel a]
    PRINT_NUM
    jmp L1
L0:
    movsd xmm0, [rel b]
    PRINT_NUM
L1:
    xor eax, eax
    add rsp, 32
    pop rbp
    ret
