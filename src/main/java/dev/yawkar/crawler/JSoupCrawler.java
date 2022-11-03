package dev.yawkar.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.Callable;

public class JSoupCrawler implements Callable<Document> {

    public static JSoupCrawler of(String url, Proxy proxy) {
        return new JSoupCrawler(url, proxy);
    }

    public static JSoupCrawler of(String url) {
        return new JSoupCrawler(url, Proxy.NO_PROXY);
    }

    private final String url;
    private final Proxy proxy;

    private JSoupCrawler(String url, Proxy proxy) {
        this.url = url;
        this.proxy = proxy;
    }

    @Override
    public Document call() {
        try {
            return Jsoup.connect(url).proxy(proxy).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
