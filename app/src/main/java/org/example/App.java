package org.example;

import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.Stock;
import yahoofinance.quotes.stock.StockQuote;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    private static final Queue<StockData> stockQueue = new LinkedList<>();

    public static void main(String[] args) {
        // Create a scheduled executor to run the task every 5 seconds
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(App::queryStock, 0, 5, TimeUnit.SECONDS);
    }

    // Method to query the DJIA stock price and store it in the queue
    private static void queryStock() {
        try {
            // Fetch stock data for DJIA
            Stock stock = YahooFinance.get("DJIA"); // DJIA ticker symbol
            if (stock != null) {
                StockQuote quote = stock.getQuote();
                double price = quote.getPrice().doubleValue();
                long timestamp = System.currentTimeMillis();
                
                // Store stock value and timestamp in the queue
                stockQueue.add(new StockData(price, timestamp));
                
                // Print the stored data (for debugging purposes)
                System.out.println("Stock Value: " + price + " Timestamp: " + timestamp);
            } else {
                System.out.println("Failed to retrieve stock data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class to store stock data
    private static class StockData {
        private final double price;
        private final long timestamp;

        public StockData(double price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }

        public double getPrice() {
            return price;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
