package dev.yawkar.task;

import dev.yawkar.strategy.ParseStrategy;

import java.util.function.Consumer;

public class ParseTask<T, R> implements Runnable {

    public static <T, R> ParseTask<T, R> of(T source, ParseStrategy<T, R> strategy, Consumer<? super R> consumer) {
        return new ParseTask<>(source, strategy, consumer);
    }

    private final ParseStrategy<T, R> strategy;
    private final T source;
    private final Consumer<? super R> consumer;

    private ParseTask(T source, ParseStrategy<T, R> strategy, Consumer<? super R> consumer) {
        this.strategy = strategy;
        this.source = source;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        strategy.parse(source, consumer);
    }
}
