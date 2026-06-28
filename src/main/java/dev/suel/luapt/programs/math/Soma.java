package dev.suel.luapt.programs.math;

import dev.suel.luapt.programs.Program;

public class Soma extends Program {
    public Soma(double a, double b) {

        super("""
                        local x = %s
                        local y = %s
                        escreva(x + y)
                """.formatted(a, b));

    }
}
