package dev.yawkar.strategy;

import java.util.function.Consumer;

public interface ParseStrategy<E, T> {

    void parse(E source, Consumer<? super T> out);
}
