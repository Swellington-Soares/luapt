package dev.suel.luapt.programs.bibliotecapadrao;

import dev.suel.luapt.programs.Program;

public class TabelaTest extends Program {
    public TabelaTest() {
        var codigoFonte = """
                -- array posicional
                local nums = {10, 20, 30}
                escreva(nums[1])        -- 10
                escreva(tam(nums))      -- 3
                
                -- dicionário
                local pessoa = {nome = "João", idade = 25}
                escreva(pessoa.nome)    -- João
                escreva(pessoa["idade"]) -- 25
                
                -- atribuição
                pessoa.cidade = "Recife"
                escreva(pessoa.cidade)  -- Recife
                
                -- misto
                local aluno = {nome = "Ana", notas = {8, 9, 7}}
                escreva(aluno.nome)         -- Ana
                escreva(aluno.notas[2])     -- 9
                
                -- verifica chave
                escreva(temChave(pessoa, "nome"))   -- verdadeiro
                escreva(temChave(pessoa, "cpf"))    -- falso
                
                -- itera dicionário
                local cores = {vermelho = "#FF0000", verde = "#00FF00", azul = "#0000FF"}
                escreva(cores.verde)    -- #00FF00
                """;

        super(codigoFonte);
    }
}
