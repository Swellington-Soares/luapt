package dev.suel.luapt.programs;

import dev.suel.luapt.compiler.Interpretador;
import dev.suel.luapt.compiler.Lexer;
import dev.suel.luapt.compiler.Parser;
import dev.suel.luapt.compiler.Token;
import dev.suel.luapt.compiler.ast.No;

import java.util.List;

abstract public class Program {
    private final Interpretador interpretador = new Interpretador();
    private final List<No> arvore;
    protected Program(String codigoFonte){
        Lexer lexer = new Lexer(codigoFonte);
        List<Token> tokens = lexer.tokenizar();
        Parser parser = new Parser(tokens);
        arvore = parser.parsear();
    }


    public void executar(){
        interpretador.executar(arvore);
    }

}
