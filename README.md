# Yahoo!Finance cryptocurrencies scraper

## Algorithm explanation

We use 2 execution services - one for pulling HTMLs from URLs and the other one for parsing actual data.
https://github.com/YawKar/yahoo-finance-scraper/blob/b9c60ecc7485e45d40117bc8f634fb1f56df4513/src/main/java/dev/yawkar/Main.java#L17-L20

First of all, we get the number of cryptocurrencies in the Yahoo!Finance list.
https://github.com/YawKar/yahoo-finance-scraper/blob/b9c60ecc7485e45d40117bc8f634fb1f56df4513/src/main/java/dev/yawkar/Main.java#L23

After we got it we pull 100 cryptocurrencies per page every 5 seconds (with proxy we can make it in a moment) and create CompletableFuture that parses the pulled Document, populates Cryptocurrency instances and adds them into ConcurrentLinkedQueue.
https://github.com/YawKar/yahoo-finance-scraper/blob/b9c60ecc7485e45d40117bc8f634fb1f56df4513/src/main/java/dev/yawkar/Main.java#L23-L41

After all tasks complete we write results down into the csv file named in the following format:
"results-YYYY-MM-DDThh:mm:ss.SSSSSSSSS.csv"
https://github.com/YawKar/yahoo-finance-scraper/blob/b9c60ecc7485e45d40117bc8f634fb1f56df4513/src/main/java/dev/yawkar/Main.java#L43-L60
