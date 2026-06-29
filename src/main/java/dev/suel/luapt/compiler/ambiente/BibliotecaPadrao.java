package dev.suel.luapt.compiler.ambiente;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

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
        if (args.size() != 1)
            throw new IllegalArgumentException("tam() espera 1 argumento.");
        if (!(args.getFirst() instanceof String s))
            throw new IllegalArgumentException("tam() espera uma string como argumento.");

        return (double) s.length();
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

}
