package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ChamadaFuncao extends No {
    public final Token nome;
    private final List<No> argumentos;
}
