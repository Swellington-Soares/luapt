package dev.suel.luapt.compiler.ast.declaracoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@Getter
public class Para extends No {
    public final Token variavel;
    public final No inicio;
    public final No fim;
    public final List<No> corpo;
}
