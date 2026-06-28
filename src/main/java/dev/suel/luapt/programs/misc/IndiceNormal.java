package dev.suel.luapt.programs.misc;

import dev.suel.luapt.programs.Program;

public class IndiceNormal extends Program {
    public IndiceNormal() {
        super("""
                local x = "abc"
                escreva(x[1])
                """);
    }
}
