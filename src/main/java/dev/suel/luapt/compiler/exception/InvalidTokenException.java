package dev.suel.luapt.compiler.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(int linha, int atual, char c) {
        super("Caractere " + c + " inválido na linha " + linha + " e na coluna " + atual);
    }
}
