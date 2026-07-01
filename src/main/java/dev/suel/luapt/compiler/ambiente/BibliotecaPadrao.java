package dev.suel.luapt.compiler.ambiente;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BibliotecaPadrao {

    private final Ambiente ambiente;

    public static void Inject(Ambiente ambiente) {
        new BibliotecaPadrao(ambiente).registrar();
    }


    private void registrar(){
        ambiente.definir("tam", (FuncaoNativa) this::tam);
        ambiente.definir("escrevaLn", (FuncaoNativa) this::escrevaLn);
        ambiente.definir("numero", (FuncaoNativa) this::numero);
        ambiente.definir("maiuscula", (FuncaoNativa) this::maiuscula);
        ambiente.definir("minuscula", (FuncaoNativa) this::minuscula);
        ambiente.definir("capitalizar", (FuncaoNativa) this::capitalizar);
        registarTabelas();
    }

    private Object capitalizar(List<Object> args) {
        if (args.size() != 1)
            throw new IllegalArgumentException("capitalizar() espera 1 argumento.");
        if (!(args.getFirst() instanceof String s))
            throw new IllegalArgumentException("capitalizar() espera uma string como argumento.");
        return s.toUpperCase(Locale.ROOT).charAt(0) + s.substring(1);
    }


    private Object maiuscula(List<Object> args){
        if (args.size() != 1)
            throw new IllegalArgumentException("maiuscula() espera 1 argumento.");
        if (!(args.getFirst() instanceof String s))
            throw new IllegalArgumentException("maiuscula() espera uma string como argumento.");
        return s.toUpperCase(Locale.ROOT);
    }

    private Object minuscula(List<Object> args){
        if (args.size() != 1)
            throw new IllegalArgumentException("minuscula() espera 1 argumento.");
        if (!(args.getFirst() instanceof String s))
            throw new IllegalArgumentException("minuscula() espera uma string como argumento.");
        return s.toLowerCase(Locale.ROOT);
    }

    private Object tam(List<Object> args) {
        if (args.size() != 1) throw new RuntimeException("tam() espera 1 argumento");
        if (args.getFirst() instanceof String s)  return (double) s.length();
        if (args.getFirst() instanceof Map<?,?> m) return (double) m.size();
        throw new RuntimeException("tam() espera string, array ou tabela");
    }

    private Object escrevaLn(List<Object> args) {
        if (args == null || args.isEmpty()) {
            System.out.println();
        } else {
            args.forEach(System.out::println);
        }
        return null;
    }

    private Object numero(List<Object> args) {
        if (args.size() != 1)
            throw new RuntimeException("numero() espera 1 argumento");
        if (args.get(0) instanceof Double d) return d;
        if (args.get(0) instanceof String s) {
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) {
                throw new RuntimeException("Não foi possível converter '" + s + "' para número");
            }
        }
        throw new RuntimeException("numero() espera string ou número");
    }

    private void registarTabelas() {
        // chaves(tabela) → imprime as chaves
        ambiente.definir("chaves", (FuncaoNativa) args -> {
            if (!(args.getFirst() instanceof Map<?,?> m))
                throw new RuntimeException("chaves() espera uma tabela");
            LinkedHashMap<Object, Object> resultado = new LinkedHashMap<>();
            int i = 1;
            for (Object k : m.keySet()) resultado.put((double) i++, k);
            return resultado;
        });

        // temChave(tabela, chave)
        ambiente.definir("temChave", (FuncaoNativa) args -> {
            if (!(args.get(0) instanceof Map<?,?> m))
                throw new RuntimeException("temChave() espera uma tabela");
            return m.containsKey(args.get(1));
        });
    }

}
