package dev.suel.luapt.compiler;

import dev.suel.luapt.compiler.ambiente.Ambiente;
import dev.suel.luapt.compiler.ambiente.FuncaoLuaPT;
import dev.suel.luapt.compiler.ambiente.Retorno;
import dev.suel.luapt.compiler.ast.No;
import dev.suel.luapt.compiler.ast.declaracoes.*;
import dev.suel.luapt.compiler.ast.expressoes.*;

import java.util.List;

public class Interpretador {

    private Ambiente ambiente = new Ambiente();

    public void executar(List<No> nos) {
        for (No no : nos) {
            executarNo(no);
        }
    }

    private void executarNo(No no) {
        if (no instanceof VarLocal n) {
            Object valor = avaliar(n.valor);
            ambiente.definir(n.nome.lexema, valor);

        } else if (no instanceof Atribuicao n) {
            Object valor = avaliar(n.valor);
            ambiente.atribuir(n.nome.lexema, valor);

        } else if (no instanceof Escreva n) {
            Object valor = avaliar(n.valor);
            System.out.println(formatar(valor));

        } else if (no instanceof Se n) {
            if (verdadeiro(avaliar(n.condicao))) {
                executarBloco(n.entao, new Ambiente(ambiente));
            } else {
                executarBloco(n.senao, new Ambiente(ambiente));
            }

        } else if (no instanceof Enquanto n) {
            while (verdadeiro(avaliar(n.condicao))) {
                executarBloco(n.corpo, new Ambiente(ambiente));
            }

        } else if (no instanceof Para n) {
            double inicio = (double) avaliar(n.inicio);
            double fim    = (double) avaliar(n.fim);
            String var    = n.variavel.lexema;

            for (double i = inicio; i <= fim; i++) {
                Ambiente escopoLoop = new Ambiente(ambiente);
                escopoLoop.definir(var, i);
                executarBloco(n.corpo, escopoLoop);
            }

        } else if (no instanceof DeclaracaoFuncao n) {
            FuncaoLuaPT funcao = new FuncaoLuaPT(n.parametros, n.corpo, ambiente);
            ambiente.definir(n.nome.lexema, funcao);

        } else if (no instanceof Retorne n) {
            throw new Retorno(avaliar(n.valor));

        } else if (no instanceof ChamadaFuncao n) {
            avaliar(n); // chamada como declaração (ignora retorno)

        } else {
            avaliar(no); // expressões soltas
        }
    }

    private void executarBloco(List<No> nos, Ambiente escopoLocal) {
        Ambiente anterior = this.ambiente;
        try {
            this.ambiente = escopoLocal;
            for (No no : nos) {
                executarNo(no);
            }
        } finally {
            this.ambiente = anterior; // restaura sempre, mesmo com exceção
        }
    }


    private Object avaliar(No no) {
        if (no instanceof Numero n)    return n.getValor();
        if (no instanceof Texto n)     return n.getValor();
        if (no instanceof Booleano n)  return n.isValor();
        if (no instanceof Nulo)        return null;

        if (no instanceof Variavel n) {
            return ambiente.obter(n.getNome().getLexema());
        }

        if (no instanceof Indice n) {
            var alvo = avaliar(n.getAlvo());
            var indice = avaliar(n.getIndice());

            if (!(indice instanceof Double i)) {
                throw new RuntimeException("Índice deve ser um número");
            }

            var pos = i.intValue();

            if (alvo instanceof String s) {
                if (pos < 1 || pos > s.length()) {
                    throw new RuntimeException(
                            "Índice " + pos + " fora do intervalo (1.." + s.length() + ")"
                    );
                }
                return String.valueOf(s.charAt(pos - 1));
            }

            throw new RuntimeException("Tipo não suporta indexação");
        }

        if (no instanceof UnOp n) {
            Object direita = avaliar(n.getDireita());
            return switch (n.getOperador().getTipo()) {
                case MENOS -> -(double) direita;
                case NAO   -> !verdadeiro(direita);
                default    -> throw new RuntimeException("Operador unário desconhecido");
            };
        }

        if (no instanceof BinOp n) {
            // Concatenação avalia os dois lados antes
            if (n.getOperador().tipo == TokenType.PONTO_PONTO) {
                return formatar(avaliar(n.getEsquerda())) + formatar(avaliar(n.getDireita()));
            }

            Object esq = avaliar(n.getEsquerda());
            Object dir = avaliar(n.getDireita());

            return switch (n.getOperador().tipo) {
                case MAIS         -> (double) esq + (double) dir;
                case MENOS        -> (double) esq - (double) dir;
                case ESTRELA      -> (double) esq * (double) dir;
                case BARRA        -> {
                    if ((double) dir == 0) throw new RuntimeException("Divisão por zero!");
                    yield (double) esq / (double) dir;
                }
                case MODULO       -> (double) esq % (double) dir;
                case MAIOR        -> (double) esq >  (double) dir;
                case MENOR        -> (double) esq <  (double) dir;
                case MAIOR_IGUAL  -> (double) esq >= (double) dir;
                case MENOR_IGUAL  -> (double) esq <= (double) dir;
                case IGUAL_IGUAL  -> iguais(esq, dir);
                case DIFERENTE    -> !iguais(esq, dir);
                case E            -> verdadeiro(esq) && verdadeiro(dir);
                case OU           -> verdadeiro(esq) || verdadeiro(dir);
                default           -> throw new RuntimeException("Operador desconhecido: " + n.getOperador().tipo);
            };
        }

        if (no instanceof ChamadaFuncao n) {
            Object alvo = ambiente.obter(n.nome.lexema);

            if (!(alvo instanceof FuncaoLuaPT funcao)) {
                throw new RuntimeException("'" + n.nome.lexema + "' não é uma função");
            }

            if (n.getArgumentos().size() != funcao.parametros.size()) {
                throw new RuntimeException("Função '" + n.nome.lexema + "' esperava "
                        + funcao.parametros.size() + " argumento(s), recebeu "
                        + n.getArgumentos().size());
            }

            // Cria escopo da função com os argumentos
            Ambiente escopoFuncao = new Ambiente(funcao.fechamento);
            for (int i = 0; i < funcao.parametros.size(); i++) {
                String param = funcao.parametros.get(i).lexema;
                Object valor = avaliar(n.getArgumentos().get(i));
                escopoFuncao.definir(param, valor);
            }

            try {
                executarBloco(funcao.corpo, escopoFuncao);
                return null; // função sem retorne
            } catch (Retorno r) {
                return r.valor;
            }
        }

        throw new RuntimeException("Nó desconhecido: " + no.getClass().getSimpleName());
    }


    private boolean verdadeiro(Object valor) {
        if (valor == null)           return false; // nulo é falso
        if (valor instanceof Boolean b) return b;
        return true;
    }

    private boolean iguais(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null)              return false;
        return a.equals(b);
    }

    private String formatar(Object valor) {
        if (valor == null)              return "nulo";
        if (valor instanceof Double d) {
            // Mostra inteiro se não tiver parte decimal
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf(d.longValue());
            }
            return String.valueOf(d);
        }
        if (valor instanceof Boolean b) return b ? "verdadeiro" : "falso";
        return String.valueOf(valor);
    }
}


