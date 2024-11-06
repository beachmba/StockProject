package jdstockmarket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * The {@code StockMarketAPI} class encapsulates interactions with the Alpha Vantage API
 * for retrieving live stock data. It leverages the OkHttp library for handling HTTP 
 * requests and responses.
 *
 * <p>The primary method, {@code fetchLiveStockData}, fetches live stock data based on a 
 * provided stock symbol, using a specified API key to authenticate with the Alpha Vantage service.
 *
 * <p>Example usage:
 * <pre>
 * {@code 
 * StockMarketAPI api = new StockMarketAPI();
 * String stockData = api.fetchLiveStockData("AAPL");
 * }
 * </pre>
 *
 * @author Michael Louis, adapted from code by David Martindale and Jamshaid Ali
 * @version 1.0 (8 November 2024)
 * @see OkHttpClient
 * @see Request
 * @see Response
 */
public class StockMarketAPI {

	// Alpha Vantage API key for authentication
	//private static final String API_KEY = "UTY1ZRK0IBFZH4DE"; // David M's key
	//private static final String API_KEY = "CY5Z4VYMIRMAC0RE";  //Michael's basic key
	private static final   String API_KEY = "Z815S7QU1PEQEY5G";  //Michael's premium key
	
	// Http Client instance for executing HTTP requests
	private OkHttpClient client;

	/**
	 * Constructor initializes a new OkHttpClient instance.
	 */
	public StockMarketAPI() {
		this.client = new OkHttpClient();
	}

	/**
	 * Fetches live stock data for a specified stock symbol from the Alpha Vantage API.
	 *
	 * Constructs a URL using the provided stock symbol and period, 
	 * and issues an HTTP GET request
	 * to the Alpha Vantage API. Parses the HTTP response and returns the response body
	 * as a string.
	 *
	 * @param stockSymbol The stock symbol for which to fetch data.
	 * @return A string containing the JSON response from the Alpha Vantage API.
	 * @throws IOException If an I/O error occurs while handling the request or response.
	 */
	public String fetchLiveStockData(String stockSymbol, String period) throws IOException {

		// url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=5min"  
		//		 url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" 
		//url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
		//+ "&extended_hours=false"
		//+ "&symbol="+ stockSymbol + "&apikey=" + API_KEY;

		// Construct the URL for the Alpha Vantage API request
		String apiQuery = "https://www.alphavantage.co/query?function=";
		period = "1 Day";
		
		switch (period )
		{
		case "1 Day":
			// returns 30 days, 30x8x12 =3,000 pts, only need 30 - 500!
			apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=full"
					+ "&adjusted=true"
					+ "&extended_hours=false"
					+ "&interval=5min";
			break;

		case "5 Days":
			//returns 30 days, 30x8x12 = 3000 pts, only need 500 
			apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=full"
					+ "&adjusted=true"
					+ "&extended_hours=false"
					+ "&interval=5min";
			break;

		case "1 Month":
			//returns 30 days, 30x8x2 = 500 points.  use all
			apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=full"
					+ "&adjusted=true"
					+ "&extended_hours=false"
					+ "&interval=30min";
			break;

		case "6 Months":
			//returns 20 years= 20x250 =5000 pts!  only need 120 points
			apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
					+ "&outputsize=full";
			break;

		case "1 Year":
			//returns 20 years = 5000 pts!  only need 240 points
			apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
					+ "&outputsize=full";
			break;
		case "5 Years":
			//returns 20 years = 20x52 = 1100 pts!  only need 260 points
			apiQuery += "TIME_SERIES_WEEKLY_ADJUSTED"
					+ "&outputsize=full";
			break;


		case "Yesterday":
			//choose yesterday's close. returns 100 daily closes
			apiQuery += "TIME_SERIES_DAILY_ADJUSTED";  //compact by default
			break;

		}
		apiQuery += "&symbol="+ stockSymbol + "&apikey=" + API_KEY;
		
		//  Build the HTTP request
		Request request = new Request.Builder()
				.url(apiQuery)
				.build();

		// Execute the HTTP request and handle the response
		try (Response response = client.newCall(request).execute()) {
			// Check for a successful response, throw an IOException for an unsuccessful response
			if (!response.isSuccessful()) {
				throw new IOException("Error: Response failed...\n" + response);
			}
			// Return the response body as a string
			String reply = response.body().string();
			System.out.println(reply);
			return reply;       
		}
	}
}
