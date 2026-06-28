package dev.suel.luapt.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import dev.suel.luapt.compiler.ast.No;
import dev.suel.luapt.compiler.ast.expressoes.*;
import dev.suel.luapt.compiler.ast.declaracoes.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private List<No> parsear(String codigo) {
        Lexer lexer = new Lexer(codigo);
        List<Token> tokens = lexer.tokenizar();
        Parser parser = new Parser(tokens);
        return parser.parsear();
    }

    private No parseUm(String codigo) {
        List<No> nos = parsear(codigo);
        assertEquals(1, nos.size(), "Esperado exatamente 1 nó");
        return nos.get(0);
    }

    // ---- Variáveis locais ----

    @Test
    @DisplayName("local x = 10 gera VarLocal com número")
    void testVarLocalNumero() {
        No no = parseUm("local x = 10");

        assertInstanceOf(VarLocal.class, no);
        VarLocal var = (VarLocal) no;

        assertEquals("x", var.nome.lexema);
        assertInstanceOf(Numero.class, var.valor);
        assertEquals(10.0, ((Numero) var.valor).getValor());
    }

    @Test
    @DisplayName("local nome = \"João\" gera VarLocal com string")
    void testVarLocalString() {
        No no = parseUm("local nome = \"João\"");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        Texto texto = assertInstanceOf(Texto.class, var.valor);
        assertEquals("João", texto.valor);
    }

    @Test
    @DisplayName("local ativo = verdadeiro gera VarLocal com booleano")
    void testVarLocalVerdadeiro() {
        No no = parseUm("local ativo = verdadeiro");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        Booleano bool = assertInstanceOf(Booleano.class, var.valor);
        assertTrue(bool.isValor());
    }

    @Test
    @DisplayName("local x = falso gera VarLocal com booleano falso")
    void testVarLocalFalso() {
        No no = parseUm("local x = falso");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        Booleano bool = assertInstanceOf(Booleano.class, var.valor);
        assertFalse(bool.isValor());
    }

    @Test
    @DisplayName("local x = nulo gera VarLocal com Nulo")
    void testVarLocalNulo() {
        No no = parseUm("local x = nulo");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        assertInstanceOf(Nulo.class, var.valor);
    }

    // ---- Expressões aritméticas ----

    @Test
    @DisplayName("local x = 2 + 3 gera BinOp com MAIS")
    void testSoma() {
        No no = parseUm("local x = 2 + 3");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        BinOp op = assertInstanceOf(BinOp.class, var.valor);

        assertEquals(TokenType.MAIS, op.getOperador().tipo);
        assertEquals(2.0, ((Numero) op.getEsquerda()).valor);
        assertEquals(3.0, ((Numero) op.getDireita()).valor);
    }

    @Test
    @DisplayName("local x = 10 - 4 gera BinOp com MENOS")
    void testSubtracao() {
        No no = parseUm("local x = 10 - 4");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        BinOp op = assertInstanceOf(BinOp.class, var.valor);

        assertEquals(TokenType.MENOS, op.getOperador().tipo);
    }

    @Test
    @DisplayName("local x = 3 * 4 gera BinOp com ESTRELA")
    void testMultiplicacao() {
        No no = parseUm("local x = 3 * 4");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        BinOp op = assertInstanceOf(BinOp.class, var.valor);

        assertEquals(TokenType.ESTRELA, op.getOperador().tipo);
    }

    @Test
    @DisplayName("precedência: 2 + 3 * 4 agrupa multiplicação primeiro")
    void testPrecedencia() {
        No no = parseUm("local x = 2 + 3 * 4");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        BinOp soma = assertInstanceOf(BinOp.class, var.valor);

        assertEquals(TokenType.MAIS, soma.getOperador().tipo);
        // lado direito deve ser a multiplicação
        BinOp mult = assertInstanceOf(BinOp.class, soma.getDireita());
        assertEquals(TokenType.ESTRELA, mult.getOperador().tipo);
    }

    @Test
    @DisplayName("concatenação: \"olá\" .. \" mundo\" gera BinOp PONTO_PONTO")
    void testConcatenacao() {
        No no = parseUm("local s = \"olá\" .. \" mundo\"");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        BinOp op = assertInstanceOf(BinOp.class, var.valor);

        assertEquals(TokenType.PONTO_PONTO, op.getOperador().tipo);
    }

    // ---- Condicionais ----

    @Test
    @DisplayName("se simples sem senão")
    void testSeSemSenao() {
        No no = parseUm("""
            se x > 5 então
                escreva("ok")
            fim
            """);

        Se se = assertInstanceOf(Se.class, no);
        assertFalse(se.entao.isEmpty());
        assertTrue(se.senao.isEmpty());
    }

    @Test
    @DisplayName("se com senão gera ambos os blocos")
    void testSeComSenao() {
        No no = parseUm("""
            se x > 5 então
                escreva("maior")
            senão
                escreva("menor")
            fim
            """);

        Se se = assertInstanceOf(Se.class, no);
        assertFalse(se.entao.isEmpty());
        assertFalse(se.senao.isEmpty());
    }

    @Test
    @DisplayName("condição do se é uma comparação BinOp")
    void testSeCondicaoBinOp() {
        No no = parseUm("""
            se x == 10 então
                escreva("dez")
            fim
            """);

        Se se = assertInstanceOf(Se.class, no);
        BinOp cond = assertInstanceOf(BinOp.class, se.condicao);
        assertEquals(TokenType.IGUAL_IGUAL, cond.getOperador().tipo);
    }

    // ---- Laços ----

    @Test
    @DisplayName("enquanto gera nó Enquanto com corpo")
    void testEnquanto() {
        No no = parseUm("""
            enquanto x > 0 faça
                escreva(x)
            fim
            """);

        Enquanto enquanto = assertInstanceOf(Enquanto.class, no);
        assertInstanceOf(BinOp.class, enquanto.condicao);
        assertFalse(enquanto.corpo.isEmpty());
    }

    @Test
    @DisplayName("para gera nó Para com inicio e fim")
    void testPara() {
        No no = parseUm("""
            para i = 1, 10 faça
                escreva(i)
            fim
            """);

        Para para = assertInstanceOf(Para.class, no);
        assertEquals("i", para.getVariavel().getLexema());
        assertEquals(1.0, ((Numero) para.getInicio()).getValor());
        assertEquals(10.0, ((Numero) para.getFim()).getValor());
        assertFalse(para.corpo.isEmpty());
    }

    // ---- Funções ----

    @Test
    @DisplayName("declaração de função com parâmetros")
    void testDeclaracaoFuncao() {
        No no = parseUm("""
            função somar(a, b)
                retorne a + b
            fim
            """);

        DeclaracaoFuncao fn = assertInstanceOf(DeclaracaoFuncao.class, no);
        assertEquals("somar", fn.nome.lexema);
        assertEquals(2, fn.parametros.size());
        assertEquals("a", fn.parametros.get(0).lexema);
        assertEquals("b", fn.parametros.get(1).lexema);
        assertFalse(fn.corpo.isEmpty());
    }

    @Test
    @DisplayName("função sem parâmetros")
    void testFuncaoSemParametros() {
        No no = parseUm("""
            função saudar()
                escreva("olá")
            fim
            """);

        DeclaracaoFuncao fn = assertInstanceOf(DeclaracaoFuncao.class, no);
        assertTrue(fn.parametros.isEmpty());
    }

    @Test
    @DisplayName("retorne gera nó Retorne com expressão")
    void testRetorne() {
        No no = parseUm("""
            função dobro(x)
                retorne x * 2
            fim
            """);

        DeclaracaoFuncao fn = assertInstanceOf(DeclaracaoFuncao.class, no);
        Retorne ret = assertInstanceOf(Retorne.class, fn.corpo.get(0));
        assertInstanceOf(BinOp.class, ret.valor);
    }

    // ---- Chamadas de função ----

    @Test
    @DisplayName("chamada de função com argumentos")
    void testChamadaFuncao() {
        No no = parseUm("local r = somar(3, 4)");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        ChamadaFuncao chamada = assertInstanceOf(ChamadaFuncao.class, var.getValor());
        assertEquals("somar", chamada.getNome().getLexema());
        assertEquals(2, chamada.getArgumentos().size());
    }

    @Test
    @DisplayName("chamada de função sem argumentos")
    void testChamadaFuncaoSemArgs() {
        No no = parseUm("local r = valor()");

        VarLocal var = assertInstanceOf(VarLocal.class, no);
        ChamadaFuncao chamada = assertInstanceOf(ChamadaFuncao.class, var.valor);
        assertTrue(chamada.getArgumentos().isEmpty());
    }

    // ---- Erros ----

    @Test
    @DisplayName("se sem fim lança exceção")
    void testSeSemFim() {
        assertThrows(RuntimeException.class, () -> parsear("""
            se x > 0 então
                escreva("ok")
            """));
    }

    @Test
    @DisplayName("função sem fim lança exceção")
    void testFuncaoSemFim() {
        assertThrows(RuntimeException.class, () -> parsear("""
            função teste()
                escreva("ops")
            """));
    }

    @Test
    @DisplayName("atribuição inválida lança exceção")
    void testAtribuicaoInvalida() {
        assertThrows(RuntimeException.class, () -> parsear("10 = x"));
    }

    // ---- Programa completo ----

    @Test
    @DisplayName("programa completo gera múltiplos nós")
    void testProgramaCompleto() {
        List<No> nos = parsear("""
            local x = 10
            local y = 20
            se x > 5 então
                escreva("maior")
            fim
            para i = 1, 5 faça
                escreva(i)
            fim
            """);

        assertEquals(4, nos.size());
        assertInstanceOf(VarLocal.class, nos.get(0));
        assertInstanceOf(VarLocal.class, nos.get(1));
        assertInstanceOf(Se.class, nos.get(2));
        assertInstanceOf(Para.class, nos.get(3));
    }
}