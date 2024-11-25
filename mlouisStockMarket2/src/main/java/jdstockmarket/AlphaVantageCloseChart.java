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
	private ArrayList<Date> dates;
	private Interval interval;
	private String stockSymbol;

	//constuctor
	public AlphaVantageCloseChart(String title, String stockSymbol, Interval interval) throws IOException {
		super(title);
		this.interval = interval;
		this.stockSymbol = stockSymbol;
		StockMarketAPI api = new StockMarketAPI();
		String stockData = api.fetchLiveStockData(stockSymbol, interval);
		if (!"Bad Stock Symbol".equals(stockData))
		{
			//System.out.println(stockData.substring(0,300));   //show the first few lines
			// Convert the stock data to a JSON string
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(stockData);
			JsonNode timeSeries = root.get(interval.getApiCallParams().getJsonFilter());  //"Time Series (5min)");
			// Data lists
			DefaultHighLowDataset dataset = makeDHLDataset(timeSeries);
			// Create the High-Low chart
			JFreeChart chart = createHighLowChart(dataset, stockSymbol, interval);
			this.resultChart = chart;
		}
		else
		{
			//bad ticker symbol or other problem
			System.out.println("returning null chart");
			this.resultChart =  null;
		}
	}

	public DefaultHighLowDataset makeDHLDataset( JsonNode timeSeries) 
	{
		this.dates = new ArrayList<>();
		ArrayList<Double> highs = new ArrayList<>();
		ArrayList<Double> lows = new ArrayList<>();
		this.closes = new ArrayList<>();
		ArrayList<Double> volumes = new ArrayList<>();

		// Ensure that beginDate is set
		if (interval.getBeginDate() != null) 
		{
			//			System.out.println("BEGIN: " + interval.getBeginDate().toString());
			//			System.out.println("END: " + interval.getEndDate().toString());		

			// Declare dateFormat outside as final so no compiler err
			final SimpleDateFormat dateFormat;
			if (interval.getApiCallParams().getApiQuery().contains("INTRADAY"))
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} 
			else 
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			}					
			dateFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));

			//step through the data, adding the points which fit the timespan criteria
			timeSeries.fieldNames().forEachRemaining(time -> 
			{
				//				System.out.println("time = " + time.toString());
				JsonNode dataPoint = timeSeries.get(time);
				//			System.out.println("Data point = " + dataPoint.toPrettyString());
				try 
				{
					Date parsedDate;
					parsedDate = dateFormat.parse(time);

					// Check if the date is within the specified range and between 09:30 and 16:00 ET
					if (!parsedDate.before(interval.getBeginDate()) 
							&& !parsedDate.after(interval.getEndDate()))
						//							&& ((hour > 9 && hour < 12) || (hour == 9 && minute >= 30) || (hour == 16 && minute == 0))) 
					{					
						this.dates.add(parsedDate);
						highs.add(dataPoint.get("2. high").asDouble());
						lows.add(dataPoint.get("3. low").asDouble());
						this.closes.add(dataPoint.get("4. close").asDouble());
						//volumes.add(dataPoint.get("5. volume").asDouble());
					} 
					//			System.out.println("# pts = " + this.closes.size());
				} 
				catch (Exception e) 
				{
					System.out.println(e.getMessage());
					System.out.println("exception caught.  time = " + time.toString());
					e.printStackTrace();
					System.exit(0);
				}
			});
		}
		else 
		{
			System.out.println("Failed to calculate the date range. Begin Date is null. Check the selected period.");
		}

		// Convert ArrayLists to arrays
		Date[] dateArray = this.dates.toArray(new Date[0]);
		double[] highArray = highs.stream().mapToDouble(Double::doubleValue).toArray();
		double[] lowArray = lows.stream().mapToDouble(Double::doubleValue).toArray();
		double[] closeArray = this.closes.stream().mapToDouble(Double::doubleValue).toArray();
		double[] volumeArray = volumes.stream().mapToDouble(Double::doubleValue).toArray();
		//double[] volumeArray = null; //volumes.stream().mapToDouble(Double::doubleValue).toArray();

		// Create the dataset
		DefaultHighLowDataset dataset = new DefaultHighLowDataset(
				stockSymbol, dateArray, highArray, lowArray, closeArray, closeArray, volumeArray
				);
		return dataset;
	}

	private static JFreeChart createHighLowChart(DefaultHighLowDataset dataset, String stockSymbol, Interval interval) {
		JFreeChart chart = ChartFactory.createHighLowChart(
				stockSymbol + "      " + interval.getPeriod(),  // Title
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
			Date toolTipDate = ((DefaultHighLowDataset) dataset1).getXDate(series, item); 
			String toolTipDateString = toolTipDate.toString();
			//System.out.println(toolTipDateString);
			Number high = ((DefaultHighLowDataset) dataset1).getHigh(series, item);
			Number low = ((DefaultHighLowDataset) dataset1).getLow(series, item);
			Number close = ((DefaultHighLowDataset) dataset1).getClose(series, item);
			return String.format("%s: High: %.2f, Low: %.2f, Close: %.2f", 
					toolTipDateString, 
					high.doubleValue(), 
					low.doubleValue(), 
					close.doubleValue());
		});
		plot.setRenderer(renderer);

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

	//returns last price in dataset.  It is actually the FIRST price in the array!

	public double getLastPrice()
	{
		return this.closes.getFirst();
	}

	//returns first price in dataset.  It is actually the LAST price in the array!
	public double getFirstPrice()
	{
		return this.closes.getLast();
	}

	public JFreeChart getResultChart() 
	{
		return resultChart;
	}

	public ArrayList<Date>  getDates()
	{
		return this.dates;
	}

	public ArrayList<Double> getCloses()
	{
		return this.closes;
	}

	//returns closing price of a stock, either today (param = "today") or yesterday (param = "yesterday")
	public static double getYesterdayOrTodayClose(String stockSymbol, String todayOrYesterday)
	{
		AlphaVantageCloseChart tempAVChart = null;
		try 
		{
			tempAVChart = new AlphaVantageCloseChart("", stockSymbol, new Interval(null, null, "5 Days"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		if (todayOrYesterday == "today")
		{
			return tempAVChart.getLastPrice();
		}
		else
		{
			//step thru and find the close from the previous day
			//given the 5-day tempAVChart, which contains the fields (ArrayLists) "dates" and "closes"
			ArrayList <Date> fiveDates = tempAVChart.getDates(); 
			//get the day of the week of the last data point
			int lastDay = fiveDates.get(0).getDay();
			int indexOfDifferentDay = -1;

			// Iterate backward to find the first index with a different Date
			for (int i = 1; i < fiveDates.size(); i++) 
			{ // Start from the 2nd element (which is the next-to-last price)
				if (fiveDates.get(i).getDay() != lastDay) 
				{ // Compare the current Day of week to the last day of wk
					indexOfDifferentDay = i; // Store the index of the first different Date
					break; // Exit the loop as soon as a different Day or week				
				}
			}
			// return yesterday's close
			return tempAVChart.closes.get(indexOfDifferentDay);
		}
	}

}


