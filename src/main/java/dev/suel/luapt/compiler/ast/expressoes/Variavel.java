package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Variavel extends No {
    private final Token nome;
}
