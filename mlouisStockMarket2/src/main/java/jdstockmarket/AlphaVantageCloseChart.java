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
	private Date mostRecentQuoteDateTime;

	//constructor
	public AlphaVantageCloseChart(String title, String stockSymbol, Interval interval) throws IOException {
		super(title);
		this.interval = interval;
		this.stockSymbol = stockSymbol;
		StockMarketAPI api = new StockMarketAPI();
		String stockData = api.fetchLiveStockData(stockSymbol, interval);
		if (!"Bad Stock Symbol".equals(stockData))
		{
			//System.out.println(stockData.substring(0,300));   //show the first few lines
			// Convert the stock data string to a JSON object
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(stockData);
			JsonNode timeSeries = root.get(interval.getApiCallParams().getJsonFilter());  //"Time Series (5min)");
			//System.out.println("Time Series Length = " + timeSeries.toPrettyString().length());
			//Given the Json root node, convert the time series to a dataset
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
		final SimpleDateFormat dateFormat;
		final String JSONcategory ;
		if (interval.getBeginDate() != null) 
		{
			//			System.out.println("BEGIN: " + interval.getBeginDate().toString());
			//			System.out.println("END: " + interval.getEndDate().toString());		

			// Declare dateFormat outside as final so no compiler err
			if (interval.getApiCallParams().getApiQuery().contains("INTRADAY"))
			{
				// these are the params for intraday
				dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				JSONcategory = "4. close";
			}
			else
				//not intraday, use yyyy-MM-dd format and adjusted close data
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				JSONcategory = "5. adjusted close";
			}
			dateFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));

			//step through the data, adding the points which fit the timespan criteria
			timeSeries.fieldNames().forEachRemaining(time -> 
			{
					JsonNode dataPoint = timeSeries.get(time);
				try 
				{
					Date parsedDate;
					parsedDate = dateFormat.parse(time);
					//System.out.println("Time = " + parsedDate.toString());
					// Check if the date is within the specified range and between 09:30 and 16:00 ET
					if (!parsedDate.before(interval.getBeginDate()) 
							&& !parsedDate.after(interval.getEndDate()))
					{					
						this.dates.add(parsedDate);
						highs.add(dataPoint.get("2. high").asDouble());
						lows.add(dataPoint.get("3. low").asDouble());
						//System.out.println("Adding a close point" );
						this.closes.add(dataPoint.get(JSONcategory).asDouble());   //either "4. close" or "5. adjusted close"
						//volumes.add(dataPoint.get("5. volume").asDouble());
					} 
				} 
				catch (Exception e) 
				{
					System.out.println(e.getMessage());
					System.out.println("exception caught.  time = " + time.toString());
					e.printStackTrace();
					System.exit(0);
				}
			});
			if (this.closes.size() == 0) System.out.println("AVCC: ERROR: Zero data points!");
		}
		else 
		{
			//null interval begin date
			System.out.println("AVCC: Null Interval Begin Date!");
			System.out.println("AVCC: Failed to calculate the date range.Check the selected period.");
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
		if (interval.getPeriod() == "1 Day")
		{
			this.mostRecentQuoteDateTime = dateArray[0];
			System.out.println("The most recent quote date is : " + this.mostRecentQuoteDateTime);
		}
		
		return dataset;   //This is what the JFreeChart needs to make the graph
	}

	private static JFreeChart createHighLowChart(DefaultHighLowDataset dataset, String stockSymbol, Interval interval) {
		//periods other than daily are adjusted closing prices
		String adjustedClose = interval.getPeriod().contains("Day") ? "" : "(Adjusted Close)"; 
		String timeAxis = interval.getPeriod().contains("1 Day") ? "Time" : "Date"; 
		JFreeChart chart = ChartFactory.createHighLowChart(
				stockSymbol + "    " + interval.getPeriod(),  // Title
				timeAxis,       // X-Axis Label
				"Price  " + adjustedClose,      // Y-Axis Label
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

	//returns last price in graph dataset.  It is actually the FIRST price in the array!
	public double getLastPrice()
	{
		return this.closes.getFirst();
	}

	public Date getMostRecentQuoteDateTime() 
	{
		return this.mostRecentQuoteDateTime;
	}
	//returns first price in graph dataset.  It is actually the LAST price in the array!
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

}


