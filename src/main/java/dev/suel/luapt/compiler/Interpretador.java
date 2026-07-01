package dev.suel.luapt.compiler;

import dev.suel.luapt.compiler.ambiente.*;
import dev.suel.luapt.compiler.ast.No;
import dev.suel.luapt.compiler.ast.declaracoes.*;
import dev.suel.luapt.compiler.ast.expressoes.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Interpretador {

    private Ambiente ambiente = new Ambiente();

    public Interpretador() {
        BibliotecaPadrao.Inject(ambiente);
    }

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
            System.out.print(formatar(valor));

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
        } else if (no instanceof Tabela.AtribuicaoIndice n) {
            Object alvo = ambiente.obter(n.nome.lexema);
            Object indice = avaliar(n.indice);
            Object valor  = avaliar(n.valor);

            if (alvo instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> tabela = (Map<Object, Object>) alvo;
                tabela.put(indice, valor);
            } else {
                throw new RuntimeException("'" + n.nome.lexema + "' não suporta indexação");
            }

        } else if (no instanceof Tabela.AtribuicaoCampo n) {
            Object alvo = ambiente.obter(n.nome.lexema);
            if (!(alvo instanceof Map))
                throw new RuntimeException("'" + n.nome.lexema + "' não é uma tabela");

            @SuppressWarnings("unchecked")
            Map<Object, Object> tabela = (Map<Object, Object>) alvo;
            tabela.put(n.campo.lexema, avaliar(n.valor));
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
            Object alvo  = avaliar(n.getAlvo());
            Object indice = avaliar(n.getIndice());

            if (alvo instanceof String s) {
                int pos = ((Double) indice).intValue();
                if (pos < 1 || pos > s.length())
                    throw new RuntimeException("Índice " + pos + " fora do intervalo");
                return String.valueOf(s.charAt(pos - 1));
            }

            if (alvo instanceof Map<?,?> tabela) {
                Object resultado = tabela.get(indice);
                if (resultado == null && !tabela.containsKey(indice))
                    throw new RuntimeException("Chave '" + indice + "' não existe na tabela");
                return resultado;
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

            if (alvo instanceof FuncaoNativa fn) {
                var args = new ArrayList<>();
                for (No arg: n.getArgumentos()) {
                    args.add(avaliar(arg));
                }
                return fn.chamar(args);
            }

            if (!(alvo instanceof FuncaoLuaPT funcao)) {
                throw new RuntimeException("'" + n.nome.lexema + "' não é uma função");
            }

            if (n.getArgumentos().size() != funcao.parametros().size()) {
                throw new RuntimeException("Função '" + n.nome.lexema + "' esperava "
                        + funcao.parametros().size() + " argumento(s), recebeu "
                        + n.getArgumentos().size());
            }

            // Cria escopo da função com os argumentos
            Ambiente escopoFuncao = new Ambiente(funcao.fechamento());
            for (int i = 0; i < funcao.parametros().size(); i++) {
                String param = funcao.parametros().get(i).lexema;
                Object valor = avaliar(n.getArgumentos().get(i));
                escopoFuncao.definir(param, valor);
            }

            try {
                executarBloco(funcao.corpo(), escopoFuncao);
                return null; // função sem retorne
            } catch (Retorno r) {
                return r.valor;
            }
        }

        if (no instanceof Tabela n) {
            LinkedHashMap<Object, Object> tabela = new LinkedHashMap<>();
            for (int i = 0; i < n.getIndices().size(); i++) {
                Object chave = avaliar(n.getIndices().get(i));
                Object valor = avaliar(n.getValores().get(i));
                tabela.put(chave, valor);
            }
            return tabela;
        }

        if (no instanceof Tabela.AcessoCampo n) {
            Object alvo = avaliar(n.alvo);
            if (!(alvo instanceof Map<?,?> tabela))
                throw new RuntimeException("Tipo não suporta acesso por campo");
            return tabela.get(n.campo.lexema);
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
        if (valor == null)               return "nulo";
        if (valor instanceof Boolean b)  return b ? "verdadeiro" : "falso";
        if (valor instanceof Double d) {
            if (d == Math.floor(d) && !Double.isInfinite(d))
                return String.valueOf(d.longValue());
            return String.valueOf(d);
        }
        if (valor instanceof Map<?,?> tabela) {
            StringBuilder sb = new StringBuilder("{");
            tabela.forEach((k, v) -> {
                if (k instanceof Double d && d == Math.floor(d))
                    sb.append(formatar(v));  // posicional — mostra só o valor
                else
                    sb.append(k).append(" = ").append(formatar(v));
                sb.append(", ");
            });
            if (sb.length() > 1) sb.setLength(sb.length() - 2);
            sb.append("}");
            return sb.toString();
        }
        return String.valueOf(valor);
    }
}


