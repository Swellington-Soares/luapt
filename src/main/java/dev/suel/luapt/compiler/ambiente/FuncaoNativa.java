package dev.suel.luapt.compiler.ambiente;

import java.util.List;

@FunctionalInterface
public interface FuncaoNativa {
    Object chamar(List<Object> argumentos);
}
