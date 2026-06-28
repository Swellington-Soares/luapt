package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Texto extends No {
    public final String valor;
}
