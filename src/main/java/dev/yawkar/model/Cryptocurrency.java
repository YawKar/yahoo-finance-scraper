package dev.yawkar.model;

import java.math.BigDecimal;

public record Cryptocurrency(String symbol, String name, BigDecimal price, BigDecimal change, BigDecimal marketCap,
                             BigDecimal circulatingSupply) {
}
