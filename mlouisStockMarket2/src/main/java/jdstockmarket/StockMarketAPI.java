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
 * String stockData = api.fetchLiveStockData("AAPL", "1 Day");
 * }
 * </pre>
 *
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see OkHttpClient
 * @see Request
 * @see Response
 */
public class StockMarketAPI 
{

	// Alpha Vantage API key for authentication
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
	 * Fetches live stock data for a specified stock symbol and interval from the Alpha Vantage API.
	 *
	 * Constructs a URL using the provided stock symbol, and issues an HTTP GET request
	 * to the Alpha Vantage API. Parses the HTTP response and returns the response body
	 * as a string.
	 *
	 * @param stockSymbol The stock symbol for which to fetch data.
	 * @return A string containing the JSON response from the Alpha Vantage API.
	 * @throws IOException If an I/O error occurs while handling the request or response.
	 */
	public String fetchLiveStockData(String stockSymbol, Interval interval) throws IOException 
	{
		// Get the URL for the Alpha Vantage API request
		String apiQuery = interval.getApiCallParams().getApiQuery() + "&symbol=" + stockSymbol + "&apikey=" + API_KEY;;
		System.out.println(apiQuery);
		
		//  Build the HTTP request
		Request request = new Request.Builder()
				.url(apiQuery)
				.build();

		// Execute the HTTP request and handle the response
		try (Response response = client.newCall(request).execute()) {
			// Check for a successful response, throw an IOException for an unsuccessful response
			if (!response.isSuccessful()) {
				throw new IOException("Error: Response to API query: " + apiQuery + " failed...\n" + response);
			}
			// Return the response body as a string
			String reply = response.body().string(); 
			System.out.println("Successful Query, returns: ");
			System.out.println(	reply );
			System.out.println("Successful Query reply end.");

			return reply;       
		}
	}
}
