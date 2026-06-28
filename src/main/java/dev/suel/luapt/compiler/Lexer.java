package dev.suel.luapt.compiler;

import dev.suel.luapt.compiler.exception.InvalidTokenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private static final Map<String, TokenType> PALAVRAS_CHAVE = new HashMap<>();

    static {
        PALAVRAS_CHAVE.put("se", TokenType.SE);
        PALAVRAS_CHAVE.put("então", TokenType.ENTAO);
        PALAVRAS_CHAVE.put("senão", TokenType.SENAO);
        PALAVRAS_CHAVE.put("fim", TokenType.FIM);
        PALAVRAS_CHAVE.put("enquanto", TokenType.ENQUANTO);
        PALAVRAS_CHAVE.put("faça", TokenType.FACA);
        PALAVRAS_CHAVE.put("para", TokenType.PARA);
        PALAVRAS_CHAVE.put("função", TokenType.FUNCAO);
        PALAVRAS_CHAVE.put("retorne", TokenType.RETORNE);
        PALAVRAS_CHAVE.put("local", TokenType.LOCAL);
        PALAVRAS_CHAVE.put("e", TokenType.E);
        PALAVRAS_CHAVE.put("ou", TokenType.OU);
        PALAVRAS_CHAVE.put("não", TokenType.NAO);
        PALAVRAS_CHAVE.put("verdadeiro", TokenType.VERDADEIRO);
        PALAVRAS_CHAVE.put("falso", TokenType.FALSO);
        PALAVRAS_CHAVE.put("nulo", TokenType.NULO);
        PALAVRAS_CHAVE.put("escreva", TokenType.ESCREVA);
        PALAVRAS_CHAVE.put("[", TokenType.COLCHETE_ESQ);
        PALAVRAS_CHAVE.put("]", TokenType.COLCHETE_DIR);
    }

    private final String fonte;
    private final List<Token> tokens = new ArrayList<>();
    private int inicio = 0;
    private int atual = 0;
    private int linha = 1;

    public Lexer(String fonte) {
        this.fonte = fonte;
    }



    public List<Token> tokenizar() {
        while (!chegouAoFim()) {
            inicio = atual;
            lerToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, linha));
        return tokens;
    }

    private void lerToken() {
        char c = avancar();

        switch (c) {
            // Símbolos simples
            case '(':
                adicionarToken(TokenType.PAREN_ESQ);
                break;
            case ')':
                adicionarToken(TokenType.PAREN_DIR);
                break;
            case '[':
                adicionarToken(TokenType.COLCHETE_ESQ);
                break;
            case ']':
                adicionarToken(TokenType.COLCHETE_DIR);
                break;

            case ',':
                adicionarToken(TokenType.VIRGULA);
                break;
            case ';':
                adicionarToken(TokenType.PONTO_VIRGULA);
                break;
            case '+':
                adicionarToken(TokenType.MAIS);
                break;
            case '-':
                // Verifica se é comentário (--)
                if (conferir('-')) {
                    while (espiar() != '\n' && !chegouAoFim()) avancar();
                } else {
                    adicionarToken(TokenType.MENOS);
                }
                break;
            case '*':
                adicionarToken(TokenType.ESTRELA);
                break;
            case '/':
                adicionarToken(TokenType.BARRA);
                break;
            case '%':
                adicionarToken(TokenType.MODULO);
                break;

            // Operadores de um ou dois caracteres
            case '=':
                adicionarToken(conferir('=') ? TokenType.IGUAL_IGUAL : TokenType.IGUAL);
                break;
            case '<':
                adicionarToken(conferir('=') ? TokenType.MENOR_IGUAL : TokenType.MENOR);
                break;
            case '>':
                adicionarToken(conferir('=') ? TokenType.MAIOR_IGUAL : TokenType.MAIOR);
                break;
            case '~':
                if (conferir('=')) adicionarToken(TokenType.DIFERENTE);
                break;

            // Concatenação (..)
            case '.':
                if (conferir('.')) {
                    adicionarToken(TokenType.PONTO_PONTO);
                }
                break;

            // Ignorar espaços e quebras de linha
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                linha++;
                break;

            // Strings
            case '"':
                lerString('"');
                break;
            case '\'':
                lerString('\'');
                break;


            default:
                if (ehDigito(c)) {
                    lerNumero();
                } else if (ehLetra(c)) {
                    lerPalavra();
                } else {
                    //System.err.println("Erro na linha " + linha + ": caractere inesperado '" + c + "'");
                    throw new InvalidTokenException(linha, atual, c);
                }
                break;
        }

    }

    private void lerString(char delimitador) {
        while (espiar() != delimitador && !chegouAoFim()) {
            if (espiar() == '\n') linha++;
            avancar();
        }

        if (chegouAoFim()) {
            System.err.println("Erro na linha " + linha + ": string não fechada!");
            return;
        }

        avancar(); // fecha o delimitador

        // Pega o valor sem as aspas
        String valor = fonte.substring(inicio + 1, atual - 1);
        adicionarToken(TokenType.STRING, valor);
    }

    private void lerNumero() {
        while (ehDigito(espiar())) avancar();

        // Verifica parte decimal
        if (espiar() == '.' && ehDigito(espiarProximo())) {
            avancar(); // consome o '.'
            while (ehDigito(espiar())) avancar();
        }

        double valor = Double.parseDouble(fonte.substring(inicio, atual));
        adicionarToken(TokenType.NUMERO, valor);
    }

    private void lerPalavra() {
        while (ehLetraOuDigito(espiar())) avancar();

        String texto = fonte.substring(inicio, atual);
        TokenType tipo = PALAVRAS_CHAVE.getOrDefault(texto, TokenType.IDENTIFICADOR);
        adicionarToken(tipo);
    }

    // ---- Métodos auxiliares ----

    private char avancar() {
        return fonte.charAt(atual++);
    }

    private boolean conferir(char esperado) {
        if (chegouAoFim() || fonte.charAt(atual) != esperado) return false;
        atual++;
        return true;
    }

    private char espiar() {
        if (chegouAoFim()) return '\0';
        return fonte.charAt(atual);
    }

    private char espiarProximo() {
        if (atual + 1 >= fonte.length()) return '\0';
        return fonte.charAt(atual + 1);
    }

    private boolean ehDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean ehLetra(char c) {
        // Suporta letras portuguesas (ã, ç, é, etc.)
        return Character.isLetter(c) || c == '_';
    }

    private boolean ehLetraOuDigito(char c) {
        return ehLetra(c) || ehDigito(c);
    }

    private boolean chegouAoFim() {
        return atual >= fonte.length();
    }

    private void adicionarToken(TokenType tipo) {
        adicionarToken(tipo, null);
    }

    private void adicionarToken(TokenType tipo, Object valor) {
        String texto = fonte.substring(inicio, atual);
        tokens.add(new Token(tipo, texto, valor, linha));
    }
}
