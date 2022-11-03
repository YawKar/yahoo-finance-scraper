package dev.yawkar.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class YahooFinanceHelper {

    public static int getNumberOfCryptocurrencies() {
        try {
            Document doc = Jsoup.connect("https://finance.yahoo.com/crypto?count=1&offset=0").get();
            return Integer.parseInt(
                    doc.selectXpath("/html/body/div[1]/div/div/div[1]/div/div[2]/div/div/div[6]/div/div/section/div/div[1]/div[1]/span[2]/span")
                            .get(0).text().split(" ")[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
