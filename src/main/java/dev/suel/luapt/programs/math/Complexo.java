package dev.suel.luapt.programs.math;

import dev.suel.luapt.programs.Program;

public class Complexo extends Program {
    public Complexo() {
        var codigoFonte =
                """
                    local x = (-2 + 5 + 10 + 13) / 2 + 5 * 8
                    escreva(x)
                """;
        super(codigoFonte);
    }
}
