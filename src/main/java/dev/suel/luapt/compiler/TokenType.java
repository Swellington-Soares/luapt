package dev.suel.luapt.compiler;

public enum TokenType {

    // Literais
    NUMERO,
    STRING,
    VERDADEIRO,
    FALSO,
    NULO,

    // Identificadores e palavras-chave
    IDENTIFICADOR,
    SE,
    SENAO,
    SENAOSE,
    ENTAO,
    FIM,
    ENQUANTO, FACA, PARA,
    FUNCAO,
    RETORNE,
    LOCAL,
    E, OU, NAO,
    ESCREVA,

    // Operadores
    MAIS,
    MENOS,
    ESTRELA,
    BARRA,
    IGUAL_IGUAL,
    DIFERENTE,
    MAIOR,
    MENOR,
    MAIOR_IGUAL,
    MENOR_IGUAL,
    IGUAL,
    PONTO_PONTO,  // = e ..
    MODULO,

    // Delimitadores
    PAREN_ESQ,
    PAREN_DIR,
    VIRGULA,
    PONTO_VIRGULA,

    COLCHETE_ESQ,
    COLCHETE_DIR,
    EOF
}
