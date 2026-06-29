package dev.suel.luapt.programs.algoritmo;

import dev.suel.luapt.programs.Program;

import java.io.UnsupportedEncodingException;

public class ValidarCPF extends Program {

    private final static String algoritmo =
            """                                      
                    se tam(cpf) ~= 11 então
                        escreva("CPF inválido")
                    senão
                        local d1  = numero(cpf[1])
                        local d2  = numero(cpf[2])
                        local d3  = numero(cpf[3])
                        local d4  = numero(cpf[4])
                        local d5  = numero(cpf[5])
                        local d6  = numero(cpf[6])
                        local d7  = numero(cpf[7])
                        local d8  = numero(cpf[8])
                        local d9  = numero(cpf[9])
                        local d10 = numero(cpf[10])
                        local d11 = numero(cpf[11])
                    
                        local soma1 = d1*10 + d2*9 + d3*8 + d4*7 + d5*6 + d6*5 + d7*4 + d8*3 + d9*2
                        local resto1 = (soma1 * 10) % 11
                    
                        se resto1 ~= d10 então
                            escreva("CPF inválido")
                        senão
                            local soma2 = d1*11 + d2*10 + d3*9 + d4*8 + d5*7 + d6*6 + d7*5 + d8*4 + d9*3 + d10*2
                            local resto2 = (soma2 * 10) % 11
                    
                            se resto2 == 10 então
                                resto2 = 0
                            fim
                    
                            se resto2 ~= d11 então
                                escreva("CPF inválido")
                            senão
                                escreva("CPF válido")
                            fim
                        fim
                    fim
                    """;

    public ValidarCPF(String cpf) {
        super("local cpf = \"" + cpf + "\"\n"+algoritmo);
    }
}
