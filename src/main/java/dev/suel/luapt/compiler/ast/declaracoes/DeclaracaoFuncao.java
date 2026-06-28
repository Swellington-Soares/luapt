package dev.suel.luapt.compiler.ast.declaracoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class DeclaracaoFuncao extends No {
    public final Token nome;
    public final List<Token> parametros;
    public final List<No> corpo;
}
