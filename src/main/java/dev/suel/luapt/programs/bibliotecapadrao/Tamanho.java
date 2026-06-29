package dev.suel.luapt.programs.bibliotecapadrao;

import dev.suel.luapt.programs.Program;

public class Tamanho extends Program {
    public Tamanho(String palavra) {
        super("""
                local palavra = "Lua"
                escreva(tam("%s"))
                """.formatted(palavra));
    }
}
