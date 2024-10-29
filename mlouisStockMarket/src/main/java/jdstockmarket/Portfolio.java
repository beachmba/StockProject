package jdstockmarket;

import java.util.TreeMap;

/**
 * The {@code Portfolio} class represents a collection of stocks, organized in a TreeMap.
 * This class provides a structured way to manage a stock portfolio, with the ability to retrieve and update stock information.
 *
 * Key Features:
 * - Manages a TreeMap where each key-value pair corresponds to a stock symbol and its respective {@link Stock} object.
 * - Offers methods to get and set the entire stock collection, enabling flexibility in managing the portfolio.
 *
 * Usage:
 * An instance of this class can be used to store and manage a user's stock holdings. 
 * The TreeMap structure ensures that stocks are stored in a sorted order based on their symbols, 
 * which can be useful for display or retrieval purposes.
 *
 * Constructor Overview:
 * - The constructor initializes the portfolio with a given TreeMap of stocks. 
 *   This allows for the creation of a portfolio with pre-existing stock data.
 *
 * Note: The effectiveness and suitability of the TreeMap for storing stocks depend on the specific requirements of the application.
 * Considerations might include the need for sorted data, frequency of access, and performance requirements.
 * 
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see TreeMap
 * @see Stock
 */
public class Portfolio {

	// Instance variables
	// Idk about these, we'll see i guess
	private TreeMap<String, Stock> stocks;
	
	// TODO is this a good constructor? idk
	public Portfolio(TreeMap<String, Stock> stocks) {
	
		this.stocks = stocks;
		
	}

	/**
	 * @return the stocks
	 */
	public TreeMap<String, Stock> getStocks() {
		return stocks;
	}

	/**
	 * @param stocks the stocks to set
	 */
	public void setStocks(TreeMap<String, Stock> stocks) {
		this.stocks = stocks;
	}

	
}
