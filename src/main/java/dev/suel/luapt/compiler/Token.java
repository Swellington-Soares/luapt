package dev.suel.luapt.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Token {
    public final TokenType tipo;
    public final String lexema;
    public final Object valor;
    public final int linha;

    @Override
    public String toString() {
        return tipo + " '" + lexema + "'";
    }
}
