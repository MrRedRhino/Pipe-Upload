package org.pipeman.pipe_dl.util.misc;

import java.util.function.Consumer;

public class ChainString<P> {
    private String value;
    private final P parent;
    private final Consumer<String> howToSet;

    public ChainString(P parent, String value, Consumer<String> howToSet) {
        this.value = value;
        this.parent = parent;
        this.howToSet = howToSet;
    }

    public P append(String toAppend) {
        value += toAppend;
        howToSet.accept(toAppend);
        return parent;
    }

    public String getValue() {
        return value;
    }
}
