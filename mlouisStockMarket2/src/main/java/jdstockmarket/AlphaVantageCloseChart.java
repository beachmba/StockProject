package jdstockmarket;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.DefaultHighLowDataset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class AlphaVantageCloseChart extends ApplicationFrame {

	private JFreeChart resultChart;
	private ArrayList<Double> closes;

	public JFreeChart getResultChart() {
		return resultChart;
	}

	public AlphaVantageCloseChart(String title, String stockSymbol, Interval interval) throws IOException {
		super(title);
		StockMarketAPI api = new StockMarketAPI();
		String stockData = api.fetchLiveStockData(stockSymbol, interval);

		// Convert the stock data to a JSON string
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(stockData);
		JsonNode timeSeries = root.get(interval.getApiCallParams().getJsonFilter());  //"Time Series (5min)");

		// Data lists
		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<Double> highs = new ArrayList<>();
		ArrayList<Double> lows = new ArrayList<>();
		this.closes = new ArrayList<>();
		ArrayList<Double> volumes = new ArrayList<>();

		// Ensure that beginDate is set
		if (interval.getBeginDate() != null) 
		{
			System.out.println("BEGIN: " + interval.getBeginDate().toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));

			timeSeries.fieldNames().forEachRemaining(time -> 
			{
				JsonNode dataPoint = timeSeries.get(time);
				try 
				{
					Date parsedDate = dateFormat.parse(time);
					if (!parsedDate.before(interval.getBeginDate()) 
							&& !parsedDate.after(interval.getEndDate()))
					{
						dates.add(parsedDate);
						highs.add(dataPoint.get("2. high").asDouble());
						lows.add(dataPoint.get("3. low").asDouble());
						closes.add(dataPoint.get("4. close").asDouble());
						volumes.add(dataPoint.get("5. volume").asDouble());
					} 
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			});
		}
		else 
		{
			System.out.println("Failed to calculate the date range. Begin Date is null. Check the selected period.");
		}


		// Convert ArrayLists to arrays
		Date[] dateArray = dates.toArray(new Date[0]);
		double[] highArray = highs.stream().mapToDouble(Double::doubleValue).toArray();
		double[] lowArray = lows.stream().mapToDouble(Double::doubleValue).toArray();
		double[] closeArray = closes.stream().mapToDouble(Double::doubleValue).toArray();
		double[] volumeArray = volumes.stream().mapToDouble(Double::doubleValue).toArray();

		// Create the dataset
		DefaultHighLowDataset dataset = new DefaultHighLowDataset(
				stockSymbol, dateArray, highArray, lowArray, closeArray, closeArray, volumeArray
				);

		// Create the High-Low chart
		JFreeChart chart = createHighLowChart(dataset, stockSymbol, interval);
		this.resultChart = chart;
	}

	private static JFreeChart createHighLowChart(DefaultHighLowDataset dataset, String stockSymbol, Interval interval) {
		JFreeChart chart = ChartFactory.createHighLowChart(
				stockSymbol,  // Title
				"Time",       // X-Axis Label
				"Price",      // Y-Axis Label
				dataset,      // Dataset
				false        // No legend
				);
				//		true,         // Tooltips
				//		false         // URLs

		XYPlot plot = chart.getXYPlot();

		// Customize the renderer
		HighLowRenderer renderer = new HighLowRenderer();
		renderer.setDefaultToolTipGenerator((dataset1, series, item) -> {
			Number high = ((DefaultHighLowDataset) dataset1).getHigh(series, item);
			Number low = ((DefaultHighLowDataset) dataset1).getLow(series, item);
			Number close = ((DefaultHighLowDataset) dataset1).getClose(series, item);
			return String.format("High: %.2f, Low: %.2f, Close: %.2f", high.doubleValue(), low.doubleValue(), close.doubleValue());
		});
		plot.setRenderer(renderer);

//		// Configure the X-axis
//		DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
//		String displayTimeFormat = interval.getPeriod().equals("1 Day") ? "HH:mm" : "MM/dd/yy";
//		domainAxis.setDateFormatOverride(new SimpleDateFormat(displayTimeFormat));
	      // Configure the X-axis
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        String displayTimeFormat = interval.getPeriod().equals("1 Day") ? "HH:mm" : "MM/dd/yy";
        domainAxis.setDateFormatOverride(new SimpleDateFormat(displayTimeFormat));
        domainAxis.setTimeZone(TimeZone.getTimeZone("US/Pacific")); // Display in Eastern Time
		
		// Configure the Y-axis
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(false);

		return chart;
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
