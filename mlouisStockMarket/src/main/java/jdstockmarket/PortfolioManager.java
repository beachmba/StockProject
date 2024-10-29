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
 * - {@code updatePortfolio(Stock)}: Adds a new stock to the portfolio or updates existing stock details.
 * - {@code readPortfolioFromFile()}: Reads the portfolio data from a file and constructs a Portfolio object.
 * - {@code writePortfolioToFile(Portfolio)}: Writes the Portfolio object's data back to a file.
 * - {@code fetchCurrentPrice(String)}: Fetches the current price of a stock from the StockMarketAPI.
 * - {@code updateStockPrices(Portfolio, boolean)}: Updates the prices of stocks in the portfolio if the updatePrices flag is true.
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
    protected static final String TIME_SERIES_KEY = "Time Series (5min)";

    public static void updatePortfolio(Stock newStock) {
        Portfolio portfolio = readPortfolioFromFile();
        
        Stock existingStock = portfolio.getStocks().get(newStock.getTicker());
        if (existingStock != null) {
        	// Update existing stock with new shares
        	int updateShares = existingStock.getShares() + newStock.getShares();
        	existingStock.setShares(updateShares);
        } else {
        	// Update the portfolio with the new stock if stock not there
        	portfolio.getStocks().put(newStock.getTicker(), newStock);
        }

        // Write the updated portfolio back to the file
        writePortfolioToFile(portfolio);

    }

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

    private static void writePortfolioToFile(Portfolio portfolio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Stock stock : portfolio.getStocks().values()) {
                writer.write(stock.getTicker() + "," + stock.getPrice() + "," + stock.getShares());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // API call to fetch current price of a stock and returns price to Double
    private static Double fetchCurrentPrice(String ticker) {
    	StockMarketAPI stockAPI = new StockMarketAPI();
    	StockJSONHandler jsonHandler = new StockJSONHandler();
    	try {
			String stockData = stockAPI.fetchLiveStockData(ticker);
			JSONObject stockJSON = jsonHandler.parseJSON(stockData);
			
			if (stockJSON.has(TIME_SERIES_KEY)) {
	            JSONObject timeSeries = stockJSON.getJSONObject(TIME_SERIES_KEY);
	            ArrayList<String> timeStamps = new ArrayList<>(timeSeries.keySet());
	            Collections.sort(timeStamps);
	            String latestTimeStamp = timeStamps.get(timeStamps.size() - 1);
	            JSONObject latestData = timeSeries.getJSONObject(latestTimeStamp);
	            return Double.parseDouble(latestData.getString("4. close"));
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO update exception handling
			e.printStackTrace();
			return null;
		}
		
    }
    
    // Calls the fetchCurrentPrice method to update the poftolio object with up to
    // date stock prices`
    protected static void updateStockPrices(Portfolio portfolio, boolean updatePrices) {
    	if (updatePrices) {
    		for (Map.Entry<String, Stock> entry : portfolio.getStocks().entrySet()) {
    			// set each ticker to variable in each iteration
    			String ticker = entry.getKey();
    			// Fetch current price for ticker using API
    			double currentPrice = fetchCurrentPrice(ticker);
    			entry.getValue().setPrice(currentPrice);
    		}
    		writePortfolioToFile(portfolio);
    	}
    }
}
