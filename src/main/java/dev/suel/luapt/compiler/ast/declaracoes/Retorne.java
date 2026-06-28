package dev.suel.luapt.compiler.ast.declaracoes;

import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class Retorne extends No {
    public final No valor;
}
