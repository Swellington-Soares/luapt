package dev.suel.luapt.programs.math;

import dev.suel.luapt.programs.Program;

public class Divisao extends Program {
    public Divisao(double a, double b) {
        super("""
                        local x = %s
                        local y = %s
                        escreva(x / y)
                """.formatted(a, b));
    }
}
