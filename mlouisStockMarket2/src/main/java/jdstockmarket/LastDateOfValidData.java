package jdstockmarket;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LastDateOfValidData 
{
	/*
	 * Has one field, the date of the last Valid Data supplied by the website AlphaVantage
	 */
	private static Date lastDate;

	public LastDateOfValidData() throws IOException, ParseException
	{
		String dummycall = new MakeAPICallString("1 Day").getApiQuery() + "&symbol=IBM";
		
		StockMarketAPI dummyAPI = new StockMarketAPI();
		String dummyResultOfAPICall = dummyAPI.resultOfAPICall(dummycall, "IBM");
	//	Date date = null;	
		// Parse JSON using Jackson
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(dummyResultOfAPICall);

		// Navigate to "Last Refreshed"
		String lastRefreshed = root.get("Meta Data").get("3. Last Refreshed").asText();
		System.out.println("Date& Time Of Last Valid Trade: " + lastRefreshed);

		// Parse the date string into a Date object
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         dateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
         // Adjust the time to 9 PM EST
         String newDateString = lastRefreshed.substring(0, 10) + " 21:00:00";
    //     date = dateFormat.parse(newDateString);

		//Save the Date object
		this.lastDate = dateFormat.parse(newDateString);
	}

	public static Date getLastDateValidData() 
	{
		return lastDate;
	}
}
