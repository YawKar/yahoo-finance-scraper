package dev.yawkar;

import com.opencsv.CSVWriter;
import dev.yawkar.crawler.JSoupCrawler;
import dev.yawkar.model.Cryptocurrency;
import dev.yawkar.strategy.impl.ParseCryptocurrenciesStrategy;
import dev.yawkar.task.ParseTask;
import dev.yawkar.util.YahooFinanceHelper;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // using SingleThreadExecutor because I don't have proxies
        var crawlExecutor = Executors.newSingleThreadExecutor();
        var parseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Queue<Cryptocurrency> cryptocurrencies = new ConcurrentLinkedQueue<>();

        int numberOfCryptocurrenciesToParse = YahooFinanceHelper.getNumberOfCryptocurrencies();
        for (int hundredIndex = 0; hundredIndex < numberOfCryptocurrenciesToParse / 100; ++hundredIndex) {
            String targetUrl = "https://finance.yahoo.com/crypto?count=%d&offset=%d".formatted(100, hundredIndex * 100);
            // if you have proxies, you can rewrite it as JSoupCrawler.of(targetUrl, yourProxy).call() here and then remove Thread.sleep(5000) in the bottom
            CompletableFuture
                    .supplyAsync(() -> JSoupCrawler.of(targetUrl).call(), crawlExecutor)
                    .thenAcceptAsync(doc -> ParseTask.of(doc, new ParseCryptocurrenciesStrategy(), cryptocurrencies::add).run(), parseExecutor);
            // we're running without any proxy, therefore, we can be banned for too many requests in a short period of time
            Thread.sleep(5000);
        }

        int remainingCryptocurrencies = numberOfCryptocurrenciesToParse % 100;
        if (remainingCryptocurrencies > 0) {
            String targetUrl = "https://finance.yahoo.com/crypto?count=%d&offset=%d"
                    .formatted(remainingCryptocurrencies, numberOfCryptocurrenciesToParse / 100);
            CompletableFuture
                    .supplyAsync(() -> JSoupCrawler.of(targetUrl).call(), crawlExecutor)
                    .thenAcceptAsync(doc -> ParseTask.of(doc, new ParseCryptocurrenciesStrategy(), cryptocurrencies::add).run(), parseExecutor);
        }

        crawlExecutor.shutdown();
        while (!crawlExecutor.awaitTermination(5, TimeUnit.SECONDS));
        parseExecutor.shutdown();
        while (!parseExecutor.awaitTermination(5, TimeUnit.SECONDS));

        try (CSVWriter writer = new CSVWriter(new FileWriter("results-" + LocalDateTime.now() + ".csv"))) {
            writer.writeNext(new String[]{"Symbol", "Name", "Price", "Change", "Market Cap", "Circulating Supply"});
            for (var cryptocurrency : cryptocurrencies) {
                writer.writeNext(new String[]{
                        cryptocurrency.symbol(),
                        cryptocurrency.name(),
                        cryptocurrency.price().toPlainString(),
                        cryptocurrency.change().toPlainString(),
                        cryptocurrency.marketCap().toPlainString(),
                        cryptocurrency.circulatingSupply().toPlainString()
                });
            }
        }
    }
}