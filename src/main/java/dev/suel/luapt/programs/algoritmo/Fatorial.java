package dev.suel.luapt.programs.algoritmo;

import dev.suel.luapt.programs.Program;

public class Fatorial extends Program {
    public Fatorial(int numero) {
        super("""
                função fatorial(n)
                    se n <= 1 então
                        retorne 1
                    fim
                    retorne n * fatorial(n - 1)
                fim
                
                local numero = %d
                
                se numero > 10 então
                    escreva("O limite é 10")                   
                senão
                    para i = 1, numero faça
                        local resultado = fatorial(i)
                        escreva(i .. "! = " .. resultado)
                    fim
                fim
                """.formatted(numero));
    }
}
