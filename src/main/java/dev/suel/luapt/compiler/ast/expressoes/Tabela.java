package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Tabela extends No {
    private final List<No> indices;
    private final List<No> valores;


    @Getter
    @RequiredArgsConstructor
    public static class AcessoCampo extends No {
        public final No alvo;
        public final Token campo;
    }

    @Getter
    @RequiredArgsConstructor
    public static class AtribuicaoCampo extends No {
        public final Token nome;
        public final Token campo;
        public final No valor;
    }


    @Getter
    @RequiredArgsConstructor
    public static class AtribuicaoIndice extends No {
        public final Token nome;
        public final No indice;
        public final No valor;

    }
}
