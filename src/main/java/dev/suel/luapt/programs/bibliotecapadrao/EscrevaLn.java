package dev.suel.luapt.programs.bibliotecapadrao;

import dev.suel.luapt.programs.Program;

import java.util.List;

public class EscrevaLn extends Program {
    public EscrevaLn(String arg) {
        super("""
                escrevaLn("%s", "Hello World")
                """.formatted(arg));
    }
}
