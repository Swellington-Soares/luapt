package dev.suel.luapt.compiler.ambiente;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FuncaoLuaPT {

    public final List<Token> parametros;
    public final List<No> corpo;
    public final Ambiente fechamento;

    @Override
    public String toString() {
        return "<função>";
    }
}
