package jdstockmarket;

/**
 * The {@code Stock} class encapsulates the essential data pertaining to a particular stock.
 * It contains fields to hold the stock's ticker symbol, company name, closing price, highest price,
 * and lowest price. This class provides a constructor for initializing a {@code Stock} object with
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
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 */

public class Stock {
	
	private String ticker;
	private Double priceClosing;
	private int shares;
	
	public Stock(String ticker, Double closing, int shares) {
		
		this.ticker = ticker;
		this.priceClosing = closing;
		this.shares = shares;
		
	}

	/**
	 * @return the ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/**
	 * @param ticker the ticker to set
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}


	/**
	 * @return the priceClosing
	 */
	public Double getPrice() {
		return priceClosing;
	}

	/**
	 * @param priceClosing the priceClosing to set
	 */
	public void setPrice(Double priceClosing) {
		this.priceClosing = priceClosing;
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

