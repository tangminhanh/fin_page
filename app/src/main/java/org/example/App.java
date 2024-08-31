package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;
    private String symbol = "IBM";
    private String apiKey = "ABCDEDF"; // Replace with your actual Alpha Vantage API key
    private int waitTimeMs = 60000; // Wait time in milliseconds (1 minute)

    @Override
    public void start(Stage stage) {
        stage.setTitle("IBM Stock Data");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Stock Price (USD)");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("IBM Stock Price (Monthly)");
        series = new XYChart.Series<>();
        lineChart.getData().add(series);
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
        new Thread(this::updateData).start();
    }

    private void updateData() {
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=" + symbol + "&apikey="
                + apiKey;

        while (true) {
            try {
                Request request = new Request.Builder()
                        .url(baseUrl)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();

                    // Parse JSON response using Gson
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    JsonObject monthlyTimeSeries = jsonObject.getAsJsonObject("Monthly Time Series");

                    series.getData().clear();
                    int dayCount = 0;

                    for (String date : monthlyTimeSeries.keySet()) {
                        JsonObject priceData = monthlyTimeSeries.getAsJsonObject(date);
                        double closePrice = Double.parseDouble(priceData.get("4. close").getAsString());

                        int finalDayCount = dayCount;
                        javafx.application.Platform.runLater(() -> {
                            series.getData().add(new XYChart.Data<>(finalDayCount, closePrice));
                        });

                        dayCount++;
                        if (dayCount >= 12)
                            break; // Limit to 12 months of data
                    }

                    System.out.println("Data updated at " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    System.out.println("Error: " + response.code() + " - " + response.message());
                }

            } catch (IOException e) {
                System.out.println("Failed to connect. Trying again...");
                e.printStackTrace();
            }

            // Wait before repeating the query
            try {
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

// package org.example;
// import java.io.IOException;
// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.LinkedList;
// import java.util.Queue;

// import javafx.application.Application;
// import javafx.scene.chart.LineChart;
// import javafx.scene.chart.NumberAxis;
// import javafx.scene.chart.XYChart;
// import javafx.scene.Scene;
// import javafx.stage.Stage;

// import yahoofinance.YahooFinance;
// import yahoofinance.Stock;

// public class App extends javafx.application.Application {
// // The URL for the API
// private static final String YAHOO_FINANCE_API =
// "https://finance.yahoo.com/quote/";
// // Stock symbol for the Dow Jones Industrial Average
// private String symbol = "^DJI";
// // Wait time in milliseconds between queries
// private int waitTimeMs = 10000;

// private LineChart<Number, Number> lineChart;
// private XYChart.Series<Number, Number> series;

// @Override
// public void start(Stage stage) {
// stage.setTitle("Dow Jones Industrial Average");
// final NumberAxis xAxis = new NumberAxis();
// final NumberAxis yAxis = new NumberAxis();
// xAxis.setLabel("Time (seconds)");
// yAxis.setLabel("Stock Price (USD)");
// lineChart = new LineChart<>(xAxis, yAxis);
// lineChart.setTitle("Dow Jones Industrial Average Stock Price");
// series = new XYChart.Series<>();
// //series.setName("Line");
// lineChart.getData().add(series);
// Scene scene = new Scene(lineChart, 800, 600);
// stage.setScene(scene);
// stage.show();

// // Start a background thread for querying and updating the chart data
// new Thread(this::updateData).start();
// }

// private void updateData() {
// // Queue for containing timestamps and stock price
// Queue<ArrayList<Object>> stockDataQueue = new LinkedList<>();
// long startSeconds = Instant.now().getEpochSecond();

// // This is the loop for querying data
// while (true) {

// // Try to query the stock information
// try {
// Stock stock = YahooFinance.get(symbol);
// // Get the current stock price
// BigDecimal price = stock.getQuote().getPrice();
// // Record the timestamp for the query
// Date timestamp = new Date();
// long currSeconds = Instant.now().getEpochSecond();

// // Add the stockData to the queue, in the form (timestamp, price)
// ArrayList<Object> stockData = new ArrayList<Object>();
// stockData.add(timestamp);
// stockData.add(price);
// stockDataQueue.add(stockData);

// // Print the stockData
// System.out.println(stockData);

// //Update the chart data
// long secSinceStart = currSeconds - startSeconds;
// series.getData().add(new XYChart.Data(secSinceStart, price));

// // Update the chart on the JavaFX Application Thread
// lineChart.getData().retainAll();
// lineChart.getData().add(series);
// }

// // Catch exception if there is a connection error
// catch(IOException e) {
// System.out.println("Failure to connect. Trying again.");
// }

// // Wait before repeating the query
// try {
// Thread.sleep(waitTimeMs);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }

// }
// }

// public static void main(String[] args) {
// launch(args);
// }
// }
