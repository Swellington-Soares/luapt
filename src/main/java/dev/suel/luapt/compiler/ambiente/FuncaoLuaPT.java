package dev.suel.luapt.compiler.ambiente;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.NonNull;

import java.util.List;

public record FuncaoLuaPT(List<Token> parametros,
                          List<No> corpo,
                          Ambiente fechamento) {

    @Override
    public @NonNull String toString() {
        return "<função>";
    }
}
