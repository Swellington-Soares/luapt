package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Indice extends No {
    private final No alvo;
    private final No indice;
}
