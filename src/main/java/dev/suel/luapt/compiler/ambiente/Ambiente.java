package dev.suel.luapt.compiler.ambiente;

import java.util.HashMap;
import java.util.Map;

public class Ambiente {

    private final Map<String, Object> variaveis = new HashMap<>();
    private final Ambiente pai;

    public Ambiente() {
        this.pai = null;
    }

    public Ambiente(Ambiente pai) {
        this.pai = pai;
    }

    public void definir(String nome, Object valor) {
        variaveis.put(nome, valor);
    }

    public Object obter(String nome) {
        if (variaveis.containsKey(nome)) return variaveis.get(nome);
        if (pai != null) return pai.obter(nome);
        throw new RuntimeException("Variável indefinida: '" + nome + "'");
    }

    public void atribuir(String nome, Object valor) {
        if (variaveis.containsKey(nome)) {
            variaveis.put(nome, valor);
            return;
        }
        if (pai != null) {
            pai.atribuir(nome, valor);
            return;
        }
        throw new RuntimeException("Variável indefinida: '" + nome + "'");
    }
}
