package dev.suel.luapt.programs.misc;

import dev.suel.luapt.programs.Program;

public class IndiceComFor extends Program {
    public IndiceComFor() {
        super("""
                local x = "Swellington"
                para i = 1, 11 faça
                    escreva(x[i])
                fim
                """);
    }
}
