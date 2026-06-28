package dev.suel.luapt.compiler;

import dev.suel.luapt.compiler.exception.InvalidTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private final String codigoFonte =
            """
                local x = 10
                se x > 5 então
                    escreva("Maior")
                fim
            """;

    private Lexer lexer;

    @BeforeEach
    void setUp() {
        lexer = new Lexer(codigoFonte);
    }

    @Test
    void testSeNaoDeuNenhumErroNaTokenizacao() {
        Assertions.assertDoesNotThrow(() -> lexer.tokenizar());
    }

    @Test
    void testSeQuantidadeDeTokenCorrespondeAoCodigoFonte() {
        var tokens = lexer.tokenizar();
        Assertions.assertEquals(15, tokens.size());
    }

    @Test
    void testSeVaiDarErroSeExistirUmTokenDesconhecido() {
        lexer = new Lexer("""
                    local x = !x
                """);
        Assertions.assertThrows(InvalidTokenException.class, () -> lexer.tokenizar());
    }

}