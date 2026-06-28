package dev.suel.luapt.compiler.ambiente;

public class Retorno extends RuntimeException {

    public final Object valor;

    public Retorno(Object valor) {
        super(null, null, true, false);
        this.valor = valor;
    }
}