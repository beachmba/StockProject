package jdstockmarket;

import java.io.IOException;

/**
 * The {@code Stock} class encapsulates the essential data pertaining to a particular stock.
 * It contains fields to hold the stock's ticker symbol, company name, closing price. 
 * This class provides a constructor for initializing a {@code Stock} object with
 * specific values and a set of getter and setter methods for accessing and modifying these fields.
 * 
 * <p>Example usage:
 * <pre>
 * {@code 
 * Stock appleStock = new Stock("AAPL", "Apple Inc.", 145.09, 146.00, 144.00);
 * String ticker = appleStock.getTicker();  // Returns "AAPL"
 * Double closingPrice = appleStock.getPriceClosing();  // Returns 145.09
 * }
 * </pre>
 * 
 * @author Michael Louis
 * @version 1.0/October 29, 2024
 */

public class Stock {
	
	private String stockSymbol;
	private Double closingPrice;
	private int shares;
	
	public Stock(String stockSymbol, Double closingPrice, int shares) {
		
		this.stockSymbol = stockSymbol;
		this.closingPrice = closingPrice;
		this.shares = shares;
		
	}

	/**
	 * @return the stock symbol
	 */
	public String getStockSymbol() {
		return stockSymbol;
	}

	/**
	 * @param symbol to set
	 */
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}


	/**
	 * @return the priceClosing
	 */
	public Double getClosingPrice() {
		return closingPrice;
	}

	/**
	 * @param closingPrice the priceClosing to set
	 */
	public void setClosingPrice(Double closingPrice) {
		this.closingPrice = closingPrice;
	}

	/**
	 * @return the shares
	 */
	public int getShares() {
		return shares;
	}

	/**
	 * @param shares the shares to set
	 */
	public void setShares(int shares) {
		this.shares = shares;
	}
	
	public Double getMarketValue()
	{
	return this.shares * this.closingPrice;
	}
	
	public void findLatestClosingPrice()
	{
		//put the current price in the object
		StockMarketAPI api = new StockMarketAPI();
		try 
		{
			String stockData = api.fetchLiveStockData(this.stockSymbol, new Interval(null, null, "1 Day"));

			int closeIndex = stockData.indexOf("\"4. close\":");
			// Extract the substring starting at the value
			int startIndex = stockData.indexOf("\"", closeIndex + 11) + 1;
			int endIndex = stockData.indexOf("\"", startIndex);

			// Parse the value as a Double
			String closeValueString = stockData.substring(startIndex, endIndex);
			Double lastClose = Double.parseDouble(closeValueString);
			System.out.println("The value of '4. close' is: " + lastClose);
			this.closingPrice = lastClose;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

