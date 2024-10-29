package jdstockmarket;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.json.JSONArray;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The {@code CongressStockAPI} class is responsible for fetching Congressional stock trade data.
 * This class interfaces with the Quiver Quant API to retrieve historical trading data for specific stock symbols.
 *
 * Key Features:
 * - Connects to the Quiver Quant API using OkHttp for HTTP requests.
 * - Handles network operations, such as sending requests and receiving responses.
 * - Implements retry logic to handle network timeouts or connectivity issues.
 *
 * Usage:
 * An instance of this class can be used to fetch data about Congressional stock trades for a given stock symbol.
 * It handles the construction and execution of HTTP requests to the Quiver Quant API and processes the received JSON data.
 *
 * The method {@code fetchCongressTrades} performs the primary operation of fetching the trade data. It includes error handling
 * for various scenarios like network timeouts, unsuccessful responses, and unexpected data formats.
 *
 * Error Handling:
 * The class includes robust error handling to manage network-related exceptions, including retries for timeout exceptions.
 * In case of a network timeout, the method retries the request up to a maximum number of times before throwing an exception.
 *
 * Note: This class requires an API key for authentication with the Quiver Quant API.
 * 
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see OkHttpClient
 * @see Request
 * @see Response
 */
public class CongressStockAPI {

    // Quiver API key for authentication
	private static final String API_KEY = "b360f7ff3b906aefab37ba412b7ad38ad487ac07";
    // OkHttp Client object variable
	private OkHttpClient client;

	
	// Constructor
	public CongressStockAPI() {
		this.client = new OkHttpClient();
	}
	
	
	public String fetchCongressTrades(String stockSymbol) throws IOException {
	    int maxRetries = 3; // Maximum number of retries
	    int retryDelay = 2000; // Delay between retries in milliseconds (2 seconds)

	    for (int attempt = 0; attempt < maxRetries; attempt++) {
	        try {
	            String url = "https://api.quiverquant.com/beta/historical/congresstrading/" + stockSymbol;
	            Request request = new Request.Builder()
	                    .url(url)
	                    .get()
	                    .addHeader("Accept", "application/json")
	                    .addHeader("Authorization", "Bearer " + API_KEY)
	                    .build();

	            try (Response response = client.newCall(request).execute()) {
	                if (!response.isSuccessful()) {
	                    throw new IOException("Error: Response failed...\n" + response);
	                }

	                String responseBody = response.body().string();
	                if (responseBody.startsWith("[")) {
	                    JSONArray jsonArray = new JSONArray(responseBody);
	                    return jsonArray.toString();
	                } else {
	                    throw new IOException("Unexpected response format: " + responseBody);
	                }
	            }
	        } catch (SocketTimeoutException ste) {
	            if (attempt == maxRetries - 1) {
	                throw ste; // Rethrow the exception on the last attempt
	            }
	            try {
	                Thread.sleep(retryDelay); // Wait before retrying
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt(); // Restore the interrupted status
	                throw new IOException("Interrupted during retry delay", ie);
	            }
	        }
	    }
	    return "Error fetching data"; // Return error message if all retries fail
	}
}
