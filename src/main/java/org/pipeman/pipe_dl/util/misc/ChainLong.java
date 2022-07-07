package org.pipeman.pipe_dl.util.misc;

import java.util.function.Consumer;

public class ChainLong<P> {
    private long value;
    private final P parent;
    private final Consumer<Long> howToModify;

    public ChainLong(P parent, long value, Consumer<Long> howToSet) {
        this.value = value;
        this.parent = parent;
        this.howToModify = howToSet;
    }

    public P add(long amount) {
        value += amount;
        howToModify.accept(value);
        return parent;
    }

    public long getValue() {
        return value;
    }
}
