package jdstockmarket;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.util.Scanner;

public class AlphaVantageCloseChart extends ApplicationFrame {
	
	//	public static void main(String[] args) throws Exception {

	private JFreeChart resultChart;
	private ArrayList<Double> closes;;
	
	public JFreeChart getResultChart() {
		return resultChart;
	}

	public AlphaVantageCloseChart(String title, String stockSymbol, Interval interval) throws JsonMappingException, JsonProcessingException 
	{
		super(title);
		String stockData = null; 
		StockMarketAPI api = new StockMarketAPI();

		try {
			stockData = api.fetchLiveStockData(stockSymbol, interval);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Convert the stock data to a JSON string
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(stockData.toString());
		System.out.println(root);
		
		// Parse stock data
		JsonNode timeSeries = root.get("Time Series (5min)");
		ArrayList<Date> dates = new ArrayList<>();
		//ArrayList<Double> closes = new ArrayList<>();
		this.closes = new ArrayList<>();

		// Ensure that beginDate is set
		if (interval.getBeginDate() != null) 
		{
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    dateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

		    // Filter data points within the calculated date range
		    timeSeries.fieldNames().forEachRemaining(time -> {
		        JsonNode dataPoint = timeSeries.get(time);
		        try {
		            Date parsedDate = dateFormat.parse(time);
		            if (!parsedDate.before(interval.getBeginDate()) && !parsedDate.after(interval.getEndDate())) {  // Include only dates within range
		                dates.add(parsedDate);
		                closes.add(dataPoint.get("4. close").asDouble());
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    });
		} 
		else 
		{
		    System.out.println("Failed to calculate the date range. Begin Date is null. Check the selected period.");
		}

		
		// Create the dataset using closing prices
		TimeSeriesCollection dataset = createTimeSeriesDataset(stockSymbol, dates, closes);

		// Create the XY time series chart
		JFreeChart chart = createXYChart(dataset, stockSymbol, interval);
		this.resultChart = chart;
	}	  

	//returns last price on graph.  It is actually the FIRST price in the array!
	public double getLastPrice()
	{
		return closes.getFirst();
	}

	//returns first price on graph.  It is actually the LAST price in the array!
	public double getFirstPrice()
	{
		return closes.getLast();
	}
	
	private static JFreeChart createXYChart(TimeSeriesCollection dataset, String stockSymbol, Interval interval) {
	    // Create a time series chart
	    JFreeChart chart = ChartFactory.createTimeSeriesChart(
	            stockSymbol,  // Title
	            "Time",       // X-Axis Label
	            "Price",      // Y-Axis Label
	            dataset,      // Dataset
	            false,        // No legend
	            true,         // Tooltips
	            false         // URLs
	    );

	    XYPlot plot = chart.getXYPlot();

	    // Configure the date axis (X-axis) to display time in "HH:mm" or "MM/dd/yy"
	    String displayTimeFormat = (interval.getPeriod() == "1 Day") ? "HH:mm" : "MM/dd/yy"; 
	    DateAxis axis = (DateAxis) plot.getDomainAxis();
	    axis.setDateFormatOverride(new SimpleDateFormat(displayTimeFormat));
	   
	    // Set a smaller font for the date axis tick labels
	    axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10)); // Adjust font size as needed

	    // Get the Y-axis (price axis)
	    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

	    return chart;
	}

	private static TimeSeriesCollection createTimeSeriesDataset(String stockSymbol, ArrayList<Date> dates, ArrayList<Double> closes) {
		TimeSeries timeSeries = new TimeSeries(stockSymbol);

		// Populate the time series with dates and closing prices
		for (int i = 0; i < dates.size(); i++) {
			timeSeries.addOrUpdate(new Minute(dates.get(i)), closes.get(i));
		}

		// Create the TimeSeriesCollection dataset
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(timeSeries);

		return dataset;
	}

	private static String getSymbolFromConsole(String defaultStockSymbol)
	{
		// Create a Scanner instance
		Scanner scanner = new Scanner(System.in);

		// Prompt the user for input
		System.out.print("Please enter a stock symbol or <Enter> for default "
				+ defaultStockSymbol + " : ");

		// Read the input word
		String tempStockSymbol = scanner.nextLine();

		// Close the scanner
		scanner.close();

		return tempStockSymbol=="" ?  defaultStockSymbol : tempStockSymbol;
	}
}
