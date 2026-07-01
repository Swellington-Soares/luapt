package dev.suel.luapt.compiler.ast.expressoes;

import dev.suel.luapt.compiler.ast.No;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Texto extends No {
    public final String valor;

    private static Map<String, Character> SIMBOLO_CONTROL = Map.of(
           "\\n", '\n',
           "\\r", '\r',
           "\\t", '\t',
           "\\0", '\0'
    );

    public String getValor() {
        var texto = valor;
        for (Map.Entry<String, Character> entry : SIMBOLO_CONTROL.entrySet()) {
            texto = texto.replace(entry.getKey(), entry.getValue().toString());
        }
        return texto;
    }
}
