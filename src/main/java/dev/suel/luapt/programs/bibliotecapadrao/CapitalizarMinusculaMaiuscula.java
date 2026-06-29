package dev.suel.luapt.programs.bibliotecapadrao;

import dev.suel.luapt.programs.Program;

public class CapitalizarMinusculaMaiuscula extends Program {
    public CapitalizarMinusculaMaiuscula() {
        var codigoFonte = """
                
                local banana = "banana"
                local limao = "LIMÃO"
                local nome = "pedro"
                
                escreva('maiuscula("banana") -> ')
                escrevaLn(maiuscula(banana))
                
                escreva('minuscula("LIMÃO") -> ')
                escrevaLn(minuscula(limao))
                
                escreva('capitalizar("pedro") -> ')
                escrevaLn(capitalizar(nome))                
                """;
        super(codigoFonte);

    }
}
