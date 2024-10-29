package jdstockmarket;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

/**
 * The {@code StockJSONHandler} class provides a concrete implementation of the {@link JSONHandler} interface
 * for parsing JSON data and retrieving values from JSON objects. This class is specifically tailored to handle
 * JSON data in the context of stock market information.
 *
 * <p>This class implements the {@code parseJSON} method to convert a JSON string into a {@code JSONObject},
 * and the {@code getValue} method to retrieve a {@code JSONObject} associated with a specified key from a
 * given {@code JSONObject}.
 *
 * <p>Example usage:
 * <pre>
 * {@code 
 * JSONHandler jsonHandler = new StockJSONHandler();
 * JSONObject jsonObject = jsonHandler.parseJSON(jsonString);
 * JSONObject timeSeries = jsonHandler.getValue(jsonObject, "Time Series (5min)");
 * }
 * </pre>
 *
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see JSONHandler
 * @see JSONObject
 */

public class StockJSONHandler implements JSONHandler {
	
	// Named constants
	private static final String TIME_SERIES_KEY = "Time Series (5min)";

    /**
     * Parses the specified JSON string and returns a {@code JSONObject}.
     *
     * @param jsonData The JSON string to be parsed.
     * @return A {@code JSONObject} representing the parsed JSON data.
     */
    @Override
    public JSONObject parseJSON(String jsonData) {
        return new JSONObject(jsonData);
    }
    
    @Override
    public JSONArray parseJSONArray(String jsonData) {
    	return new JSONArray(jsonData);
    }

    /**
     * Retrieves a {@code JSONObject} associated with the specified key from the given {@code JSONObject}.
     *
     * @param jsonObject The {@code JSONObject} from which to retrieve the value.
     * @param key The key whose associated value is to be returned.
     * @return A {@code JSONObject} representing the value associated with the specified key.
     */
    @Override
    public JSONObject getValue(JSONObject jsonObject, String key) {
        return jsonObject.getJSONObject(key);
    }
    
    public JSONObject fetchStockData(StockMarketAPI stockAPI, String stockSymbol) throws IOException {
        String stockData = stockAPI.fetchLiveStockData(stockSymbol);
        return parseJSON(stockData);
        
    }
    
    public JSONArray fetchCongressData(CongressStockAPI congressAPI, String stockSymbol) throws IOException {
    	String stockData = congressAPI.fetchCongressTrades(stockSymbol);
    	return parseJSONArray(stockData);
    }

    public String displayStockInfo(JSONObject stockJSON, String stockSymbol) {
    	
        if (stockJSON.has("Time Series (5min)")) {
            JSONObject timeSeries = getValue(stockJSON, TIME_SERIES_KEY);
            ArrayList<String> timeStamps = new ArrayList<>(timeSeries.keySet());
            Collections.sort(timeStamps);
            String latestTimeStamp = timeStamps.get(timeStamps.size() - 1);
            JSONObject latestData = timeSeries.getJSONObject(latestTimeStamp);
            String mostRecentPrice = latestData.getString("4. close");
            String todaysHigh = latestData.getString("2. high");
            String todaysLow = latestData.getString("3. low");
            
            return formatStockInfo(stockSymbol, mostRecentPrice, todaysHigh, todaysLow);
        } else if (stockJSON.has("Information")){ 
            return " API request limit reached today...\n";
        } else if (stockJSON.keySet().isEmpty()) {
        	return " ERROR: API returned an empty object\n";
        } else if (stockJSON.has("Error Message")){
        	System.out.println(stockJSON);
        	return "  Invalid API call, please try again\n";
        } else {
        	System.out.println(stockJSON);
        	return "  ERROR: error with displayStockInfo in StockJSONHandler\n";
        }
    }
    
    public String displayCongressInfo(JSONArray stockJSON, String stockSymbol) {
    	return formatCongressInfo(stockJSON, stockSymbol);
    }

    private String formatStockInfo(String stockSymbol, String mostRecentPrice, String todaysHigh, String todaysLow) {
        return "  STOCK TICKER: " + "[ " + stockSymbol + " ]"
             + "\n\n" 
             + "  - Current price: $" + mostRecentPrice + "\n"
             + "  - Today's high:  $" + todaysHigh + "\n" 
             + "  - Today's low:   $" + todaysLow + "\n\n";
    }
    
    private String formatCongressInfo(JSONArray jsonArray, String stockSymbol) {
        StringBuilder formattedInfo = new StringBuilder();
        boolean nancyPelosiTraded = false;
        StringBuilder nancyPelosiTrades = new StringBuilder();
        
        if (jsonArray.isEmpty()) {
        	formattedInfo.append("  Error with Input: Please enter a valid stock symbol or try again");
        	return formattedInfo.toString();
        }

        // Check if Nancy Pelosi has any trades
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String representative = jsonObject.getString("Representative");

            if ("Nancy Pelosi".equals(representative)) {
                nancyPelosiTraded = true;
                appendTradeInfo(nancyPelosiTrades, jsonObject);
            }
        }

        // Based on whether Nancy Pelosi traded or not, format the output
        if (nancyPelosiTraded) {
            formattedInfo.append("  Nancy Pelosi has recently traded " + stockSymbol + " !\n\n");
            formattedInfo.append(nancyPelosiTrades);
        } else {
            formattedInfo.append("  Nancy Pelosi has not recently traded " + stockSymbol + "\n  Here are the most recent congressional trades:\n\n");
            for (int i = 0; i < Math.min(20, jsonArray.length()); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                appendTradeInfo(formattedInfo, jsonObject);
            }
        }
        
        return formattedInfo.toString();
    }

    private void appendTradeInfo(StringBuilder builder, JSONObject jsonObject) {
        String representative = jsonObject.getString("Representative");
        String transactionDate = jsonObject.getString("TransactionDate"); 
        String ticker = jsonObject.getString("Ticker");
        String transaction = jsonObject.getString("Transaction");
        String range = jsonObject.getString("Range");
        String house = jsonObject.getString("House");
        String party = jsonObject.getString("Party");
        
        builder.append("  [Representative]: ").append(representative + "\n")
               .append("  Party: ").append(party + "\n")
               .append("  House: ").append(house + "\n")
               .append("  Ticker: ").append(ticker + "\n")
               .append("  Transaction: ").append(transaction + "\n")
               .append("  Transaction Date: ").append(transactionDate + "\n")
               .append("  Range: ").append(range + "\n\n");
    }


    
}

