# LuaPT

LuaPT é um projeto de estudo voltado ao desenvolvimento de uma linguagem de programação inspirada em Lua, mas com palavras-chave e estruturas em português. O objetivo principal é demonstrar, na prática, como construir um pipeline simples de compilação/execução com Java, cobrindo análise léxica, parsing e interpretação.

## Visão geral do projeto

O repositório implementa um mini compilador/interpretador para a linguagem LuaPT. A execução atual ocorre por interpretação, mas a estrutura do projeto segue o fluxo clássico de um compilador:

1. Lexer: transforma o código-fonte em tokens.
2. Parser: converte os tokens em uma árvore sintática (AST).
3. Interpretador: executa a AST e produz resultados.

A implementação está organizada principalmente em:

- [src/main/java/dev/suel/luapt/compiler](src/main/java/dev/suel/luapt/compiler): componentes centrais do compilador.
- [src/main/java/dev/suel/luapt/programs](src/main/java/dev/suel/luapt/programs): exemplos e programas de demonstração.
- [src/main/java/dev/suel/luapt/App.java](src/main/java/dev/suel/luapt/App.java): ponto de entrada para execução dos exemplos.

## Características da linguagem

A linguagem LuaPT atualmente suporta:

- Variáveis locais com `local`
- Atribuições e expressões aritméticas
- Operadores comparativos e lógicos
- Condicionais com `se ... então ... senão ... fim`
- Laços com `enquanto ... faça ... fim`
- Laços numéricos com `para ... faça ... fim`
- Funções com `função ... fim`
- Retorno com `retorne`
- Saída com `escreva(...)`
- Concatenação de texto com `..`
- Tipos básicos como números, texto, booleanos e `nulo`

## Estrutura do projeto

- [build.gradle.kts](build.gradle.kts): configuração do Gradle.
- [src/main/java/dev/suel/luapt/compiler](src/main/java/dev/suel/luapt/compiler): lexer, parser, interpretador e ambiente de execução.
- [src/test/java/dev/suel/luapt/compiler](src/test/java/dev/suel/luapt/compiler): testes automatizados para lexer e parser.

## Como executar

### Pré-requisitos

- Java 17 ou superior
- Gradle (o projeto já inclui o wrapper)

### Windows

```powershell
./gradlew.bat test
./gradlew.bat classes
java -cp build\classes\java\main App
```

### Linux/macOS

```bash
./gradlew test
./gradlew classes
java -cp build/classes/java/main App
```

O comando acima executa o exemplo principal definido em [src/main/java/dev/suel/luapt/App.java](src/main/java/dev/suel/luapt/App.java).

## Sintaxe da linguagem

### 1. Variáveis e expressões

```lua
local nome = "Ana"
local idade = 20
local soma = 2 + 3
local texto = "Olá" .. " mundo"
```

### 2. Condicionais

```lua
se idade >= 18 então
    escreva("Maior de idade")
senão
    escreva("Menor de idade")
fim
```

### 3. Laços

```lua
para i = 1, 5 faça
    escreva(i)
fim
```

```lua
enquanto idade < 30 faça
    idade = idade + 1
fim
```

### 4. Funções

```lua
função somar(a, b)
    retorne a + b
fim

local resultado = somar(2, 3)
escreva(resultado)
```

### 5. Comentários

```lua
-- Isso é um comentário
```

## Exemplos

### Exemplo 1: mensagem simples

```lua
local mensagem = "Olá, LuaPT!"
escreva(mensagem)
```

### Exemplo 2: função fatorial

```lua
função fatorial(n)
    se n <= 1 então
        retorne 1
    fim
    retorne n * fatorial(n - 1)
fim

local numero = 5
local resultado = fatorial(numero)
escreva(resultado)
```

### Exemplo 3: estrutura condicional

```lua
local nota = 7

se nota >= 7 então
    escreva("Aprovado")
senão
    escreva("Reprovado")
fim
```

## Referências


- Manual do Lua 5.5 [https://www.lua.org/manual/5.5/]
- Livro: Entendendo Algoritmos. Aditya Y. Bhargava, 1ª Edição, Abril/2017
- Livro: Compiladores, Princípios, Técnicas e Ferramentas. Alfred V. Aho, Ravi Sethi, Leffrey D. Ullman
