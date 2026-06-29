package dev.suel.luapt.compiler;

import java.util.ArrayList;
import java.util.List;
import dev.suel.luapt.compiler.ast.No;
import dev.suel.luapt.compiler.ast.declaracoes.*;
import dev.suel.luapt.compiler.ast.expressoes.*;

public class Parser {

    private final List<Token> tokens;
    private int atual = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<No> parsear() {
        List<No> declaracoes = new ArrayList<>();
        while (!chegouAoFim()) {
            declaracoes.add(declaracao());
        }
        return declaracoes;
    }


private No declaracao() {
    if (verificar(TokenType.LOCAL))    return varLocal();
    if (verificar(TokenType.SE))       return seEntao();
    if (verificar(TokenType.ENQUANTO)) return enquanto();
    if (verificar(TokenType.PARA))     return para();
    if (verificar(TokenType.FUNCAO))   return declaracaoFuncao();
    if (verificar(TokenType.RETORNE))  return retorne();
    if (verificar(TokenType.ESCREVA))  return escreva();
    return expressaoDeclaracao();
}


    private No varLocal() {
        consumir(TokenType.LOCAL, "Esperado 'local'");
        Token nome = consumir(TokenType.IDENTIFICADOR, "Esperado nome da variável");
        consumir(TokenType.IGUAL, "Esperado '=' após nome da variável");
        No valor = expressao();
        return new VarLocal(nome, valor);
    }

    private No seEntao() {
        consumir(TokenType.SE, "Esperado 'se'");
        No condicao = expressao();
        consumir(TokenType.ENTAO, "Esperado 'então' após condição");

        List<No> entao = new ArrayList<>();
        List<No> senao = new ArrayList<>();

        while (!verificar(TokenType.SENAO) && !verificar(TokenType.FIM) && !chegouAoFim()) {
            entao.add(declaracao());
        }

        if (conferir(TokenType.SENAO)) {
            while (!verificar(TokenType.FIM) && !chegouAoFim()) {
                senao.add(declaracao());
            }
        }

        consumir(TokenType.FIM, "Esperado 'fim' para fechar 'se'");
        return new Se(condicao, entao, senao);
    }

    private No enquanto() {
        consumir(TokenType.ENQUANTO, "Esperado 'enquanto'");
        No condicao = expressao();
        consumir(TokenType.FACA, "Esperado 'faça' após condição");

        List<No> corpo = new ArrayList<>();
        while (!verificar(TokenType.FIM) && !chegouAoFim()) {
            corpo.add(declaracao());
        }

        consumir(TokenType.FIM, "Esperado 'fim' para fechar 'enquanto'");
        return new Enquanto(condicao, corpo);
    }

    private No para() {
        consumir(TokenType.PARA, "Esperado 'para'");
        Token variavel = consumir(TokenType.IDENTIFICADOR, "Esperado variável do 'para'");
        consumir(TokenType.IGUAL, "Esperado '='");
        No inicio = expressao();
        consumir(TokenType.VIRGULA, "Esperado ',' após valor inicial");
        No fim = expressao();
        consumir(TokenType.FACA, "Esperado 'faça'");

        List<No> corpo = new ArrayList<>();
        while (!verificar(TokenType.FIM) && !chegouAoFim()) {
            corpo.add(declaracao());
        }

        consumir(TokenType.FIM, "Esperado 'fim' para fechar 'para'");
        return new Para(variavel, inicio, fim, corpo);
    }

    private No declaracaoFuncao() {
        consumir(TokenType.FUNCAO, "Esperado 'função'");
        Token nome = consumir(TokenType.IDENTIFICADOR, "Esperado nome da função");
        consumir(TokenType.PAREN_ESQ, "Esperado '('");

        List<Token> parametros = new ArrayList<>();
        if (!verificar(TokenType.PAREN_DIR)) {
            do {
                parametros.add(consumir(TokenType.IDENTIFICADOR, "Esperado nome do parâmetro"));
            } while (conferir(TokenType.VIRGULA));
        }
        consumir(TokenType.PAREN_DIR, "Esperado ')'");

        List<No> corpo = new ArrayList<>();
        while (!verificar(TokenType.FIM) && !chegouAoFim()) {
            corpo.add(declaracao());
        }

        consumir(TokenType.FIM, "Esperado 'fim' para fechar função");
        return new DeclaracaoFuncao(nome, parametros, corpo);
    }

    private No retorne() {
        consumir(TokenType.RETORNE, "Esperado 'retorne'");
        No valor = expressao();
        return new Retorne(valor);
    }

    private No escreva() {
        consumir(TokenType.ESCREVA, "Esperado 'escreva'");
        consumir(TokenType.PAREN_ESQ, "Esperado '('");
        No valor = expressao();
        consumir(TokenType.PAREN_DIR, "Esperado ')'");
        return new Escreva(valor);
    }

    private No expressaoDeclaracao() {
        return expressao();
    }

    private No expressao() {
        return atribuicao();
    }

    private No atribuicao() {
        No expr = ou();

        if (verificar(TokenType.IGUAL)) {
            Token igual = avancar();
            No valor = atribuicao();
            if (expr instanceof Variavel) {
                return new Atribuicao(((Variavel) expr).getNome(), valor);
            }
            throw new RuntimeException("Alvo de atribuição inválido na linha " + igual.linha);
        }
        return expr;
    }

    private No ou() {
        No expr = e();
        while (verificar(TokenType.OU)) {
            Token op = avancar();
            expr = new BinOp(expr, op, e());
        }
        return expr;
    }

    private No e() {
        No expr = igualdade();
        while (verificar(TokenType.E)) {
            Token op = avancar();
            expr = new BinOp(expr, op, igualdade());
        }
        return expr;
    }

    private No igualdade() {
        No expr = comparacao();
        while (verificar(TokenType.IGUAL_IGUAL) || verificar(TokenType.DIFERENTE)) {
            Token op = avancar();
            expr = new BinOp(expr, op, comparacao());
        }
        return expr;
    }

    private No comparacao() {
        No expr = adicao();
        while (verificar(TokenType.MAIOR) || verificar(TokenType.MENOR)
                || verificar(TokenType.MAIOR_IGUAL) || verificar(TokenType.MENOR_IGUAL)) {
            Token op = avancar();
            expr = new BinOp(expr, op, adicao());
        }
        return expr;
    }

    private No adicao() {
        No expr = multiplicacao();
        while (verificar(TokenType.MAIS) || verificar(TokenType.MENOS)
                || verificar(TokenType.PONTO_PONTO)) {
            Token op = avancar();
            expr = new BinOp(expr, op, multiplicacao());
        }
        return expr;
    }

    private No multiplicacao() {
        No expr = unario();
        while (verificar(TokenType.ESTRELA) || verificar(TokenType.BARRA)
                || verificar(TokenType.MODULO)) {
            Token op = avancar();
            expr = new BinOp(expr, op, unario());
        }
        return expr;
    }

    private No unario() {
        if (verificar(TokenType.NAO) || verificar(TokenType.MENOS)) {
            Token op = avancar();
            return new UnOp(op, unario());
        }
        return primario();
    }

    private No primario() {
        if (conferir(TokenType.VERDADEIRO)) return new Booleano(true);
        if (conferir(TokenType.FALSO))      return new Booleano(false);
        if (conferir(TokenType.NULO))       return new Nulo();

        if (verificar(TokenType.NUMERO)) {
            Token t = avancar();
            return new Numero((double) t.valor);
        }

        if (verificar(TokenType.STRING)) {
            Token t = avancar();
            return new Texto((String) t.valor);
        }

        if (verificar(TokenType.IDENTIFICADOR)) {
            Token nome = avancar();
            // Chamada de função?
            if (verificar(TokenType.PAREN_ESQ)) {
                avancar();
                List<No> args = new ArrayList<>();
                if (!verificar(TokenType.PAREN_DIR)) {
                    do {
                        args.add(expressao());
                    } while (conferir(TokenType.VIRGULA));
                }
                consumir(TokenType.PAREN_DIR, "Esperado ')' após argumentos");
                return new ChamadaFuncao(nome, args);
            }

            if (verificar(TokenType.COLCHETE_ESQ)){
                avancar();
                No indice = expressao();
                consumir(TokenType.COLCHETE_DIR, "Esperando ']'");
                return new Indice(new Variavel(nome), indice);
            }
            return new Variavel(nome);
        }

        if (conferir(TokenType.PAREN_ESQ)) {
            No expr = expressao();
            consumir(TokenType.PAREN_DIR, "Esperado ')' após expressão");
            return expr;
        }

        throw new RuntimeException("Expressão inesperada na linha " + peek().linha
                + " — token: " + peek());
    }


    private boolean verificar(TokenType tipo) {
        if (chegouAoFim()) return false;
        return peek().tipo == tipo;
    }

    private boolean conferir(TokenType tipo) {
        if (verificar(tipo)) { avancar(); return true; }
        return false;
    }

    private Token avancar() {
        if (!chegouAoFim()) atual++;
        return anterior();
    }

    private Token consumir(TokenType tipo, String mensagem) {
        if (verificar(tipo)) return avancar();
        throw new RuntimeException(mensagem + " — linha " + peek().linha);
    }

    private Token peek() { return tokens.get(atual); }
    private Token anterior() { return tokens.get(atual - 1); }
    private boolean chegouAoFim() { return peek().tipo == TokenType.EOF; }
}