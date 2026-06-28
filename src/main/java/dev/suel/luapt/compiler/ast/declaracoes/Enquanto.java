package dev.suel.luapt.compiler.ast.declaracoes;

import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@Getter
public class Enquanto extends No {
    public final No condicao;
    public final List<No> corpo;
}
