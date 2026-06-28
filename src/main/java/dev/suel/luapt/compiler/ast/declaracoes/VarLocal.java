package dev.suel.luapt.compiler.ast.declaracoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class VarLocal extends No {
    public final Token nome;
    public final No valor;
}
