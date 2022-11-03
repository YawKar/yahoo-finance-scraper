package dev.yawkar.strategy.impl;

import dev.yawkar.model.Cryptocurrency;
import dev.yawkar.strategy.ParseStrategy;
import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class ParseCryptocurrenciesStrategy implements ParseStrategy<Document, Cryptocurrency> {

    @Override
    public void parse(Document source, Consumer<? super Cryptocurrency> out) {
        var table = source.selectXpath("/html/body/div[1]/div/div/div[1]/div/div[2]/div/div/div[6]/div/div/section/div/div[2]/div[1]/table/tbody").get(0);
        for (var entry : table.children()) {
            try {
                String symbol = entry.child(0).child(2).text();
                String name = entry.child(1).text();
                BigDecimal price = new BigDecimal(entry.child(2).text().replaceAll(",", ""));
                BigDecimal change = new BigDecimal(entry.child(3).text().replaceAll(",", ""));
                BigDecimal marketCap = parseScaledValue(entry.child(5).text().replaceAll(",", ""));
                BigDecimal circulatingSupply = parseScaledValue(entry.child(9).text().replaceAll(",", ""));
                out.accept(new Cryptocurrency(
                        symbol,
                        name,
                        price,
                        change,
                        marketCap,
                        circulatingSupply
                ));
            } catch (RuntimeException exc) {
                System.out.println(exc);
            }
        }
    }

    private static BigDecimal parseScaledValue(String value) {
        if (Character.isDigit(value.charAt(value.length() - 1))) {
            return new BigDecimal(value);
        }
        for (var scale : Scale.values()) {
            if (value.charAt(value.length() - 1) == scale.symbol) {
                return scale.multiplier.multiply(new BigDecimal(value.substring(0, value.length() - 1)));
            }
        }
        throw new NumberFormatException("Unknown scale '%c' for value: %s".formatted(value.charAt(value.length() - 1), value));
    }

    private enum Scale {
        MILLION('M', new BigDecimal(1_000_000)),
        BILLION('B', MILLION.multiplier.multiply(BigDecimal.valueOf(1000))),
        TRILLION('T', BILLION.multiplier.multiply(BigDecimal.valueOf(1000))),
        QUADRILLION('Q', TRILLION.multiplier.multiply(BigDecimal.valueOf(1000)));

        public final char symbol;
        public final BigDecimal multiplier;

        Scale(char symbol, BigDecimal multiplier) {
            this.symbol = symbol;
            this.multiplier = multiplier;
        }
    }
}
