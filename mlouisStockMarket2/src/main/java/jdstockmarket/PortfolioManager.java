package jdstockmarket;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

/**
 * The {@code PortfolioManager} class provides functionality to manage a stock portfolio.
 * It includes methods for updating the portfolio with new stocks, reading the portfolio from a file, 
 * writing the updated portfolio back to a file, and updating stock prices in the portfolio.
 *
 * Key Functionalities:
 * - {@code readPortfolioFromFile()}: Reads the portfolio data from a file and constructs a Portfolio object.
  *
 * Usage:
 * This class is used to interact with the portfolio's persistence layer (e.g., a text file) and to update stock information.
 * It serves as an intermediary between the application's GUI and the data layer, ensuring data consistency and handling I/O operations.
 *
 * Error Handling:
 * The class includes error handling for I/O operations and API interactions. 
 * It prints error messages to the console in case of exceptions, ensuring that the application can recover from common errors like file access issues.
 *
 * Note: The class assumes a specific format for the portfolio data file (ticker, price, shares) and requires an existing StockMarketAPI and StockJSONHandler setup.
 * 
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see Portfolio
 * @see Stock
 * @see StockMarketAPI
 * @see StockJSONHandler
 */
public class PortfolioManager {

    private static final String FILE_NAME = "portfolio.txt";

    protected static Portfolio readPortfolioFromFile() {
        File file = new File(FILE_NAME);
        TreeMap<String, Stock> stocks = new TreeMap<>();

        if (!file.exists() || file.length() == 0) {
            return new Portfolio(new TreeMap<>()); 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read and parse the file content to reconstruct the Portfolio object
            String line;
            while ((line = reader.readLine()) != null) {
            	
            	// Check for and skip any empty lines
            	if (line.trim().isEmpty()) {
            		continue;
            	}
            	
                String[] parts = line.split(",");
                
                // Check to ensure that each line has the correct number of comma-separated values.
                if (parts.length == 3) {
                	// Parse the line to create Stock objects and add them to the stocks map
                    // Example: AAPL,150.00,10
					String ticker = parts[0];
					double price = Double.parseDouble(parts[1]);
					int shares = Integer.parseInt(parts[2]);
					stocks.put(ticker, new Stock(ticker, price, shares));
				} else {
					System.out.println("Warning: Malformed line '" + line + "' in file. Expected format: Ticker,Price,Shares");
				}
                
            }
            return new Portfolio(stocks);
        } catch (IOException e) {
            e.printStackTrace();
            return new Portfolio(new TreeMap<>()); 
        }
    }

}
