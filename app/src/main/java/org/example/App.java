
package org.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yahoofinance.YahooFinance;
import yahoofinance.Stock;

public class App extends Application {
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Dow Jones Industrial Average");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Stock Price (USD)");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Dow Jones Industrial Average Stock Price");
        series = new XYChart.Series<>();
        lineChart.getData().add(series);
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
        new Thread(this::updateData).start(); // start a background thread for querying and updating the chart data
    }

    private void updateData() {
        // Queue for containing timestamps and stock price
        Queue<ArrayList<Object>> stockDataQueue = new LinkedList<>();
        long startSeconds = Instant.now().getEpochSecond();

        // This is the loop for querying data
        while (true) {
            // Try to query the stock information
            try {
                String symbol = "^DJI"; // stock symbol for the Dow Jones Industrial Average
                Stock stock = YahooFinance.get(symbol);
                BigDecimal price = stock.getQuote().getPrice(); // get the current stock price
                Date timestamp = new Date(); // record the timestamp for the query
                long currSeconds = Instant.now().getEpochSecond();

                // Add the stockData to the queue, in the form (timestamp, price)
                ArrayList<Object> stockData = new ArrayList<>();
                stockData.add(timestamp);
                stockData.add(price);
                stockDataQueue.add(stockData);

                // Print the stockData
                System.out.println(stockData);

                // Update the chart data
                long secSinceStart = currSeconds - startSeconds;
                series.getData().add(new XYChart.Data<>(secSinceStart, price));

                // Update the chart on the JavaFX Application Thread
                lineChart.getData().retainAll();
                lineChart.getData().add(series);
            }

            // Catch exception if there is a connection error
            catch (IOException e) {
                System.out.println("Failed to connect. Trying again...");
            }

            // Wait before repeating the query
            try {
                int waitTimeMs = 5000; // wait time in milliseconds between queries
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}