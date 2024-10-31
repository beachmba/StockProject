package jdstockmarket;

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
	

}

