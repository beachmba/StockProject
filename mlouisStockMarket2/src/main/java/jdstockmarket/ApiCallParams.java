package jdstockmarket;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ApiCallParams 
{

	private String apiQuery;
	private String jsonFilter;
	private static final   String API_KEY = "Z815S7QU1PEQEY5G";  //Michael's premium key

	//constructor
	public ApiCallParams (String period)
	{
		//beginning of http call string
		this.apiQuery = "https://www.alphavantage.co/query?function=";

		//build the api http call and save the JSON filter too
		switch (period)
		{  
		// Assume 'period' holds one of the predefined string values
		case "1 Day":
			//			dateRange[0] = new Date(endtime - 1 * oneDayInMillis); // 1 day ago. use 2 days.
			this.apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=compact"   //full?
					+ "&extended_hours=false"  //true?  false? 
					+ "&interval=5min";  //96/day
			this.jsonFilter = "Time Series (5min)";
			break;
		case "5 Days":
			this.apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=full"   //full?
					+ "&extended_hours=false"  //true?  false? 
					+ "&interval=30min";  //16/day
			this.jsonFilter = "Time Series (30min)";    //80 total
			break;
		case "1 Month":
			this.apiQuery += "TIME_SERIES_INTRADAY"
					+ "&outputsize=full"  
					+ "&extended_hours=false" 
					+ "&interval=60min"; //8/day
			this.jsonFilter = "Time Series (60min)";   //160 total
			break;
		case "6 Months":
			this.apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
					+ "&outputsize=full" ;  
			this.jsonFilter = "Time Series (Daily)";   // 130 pts
			break;
		case "Year-To-Date":
			this.apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
					+ "&outputsize=full" ;  
			this.jsonFilter = "Time Series (Daily)";  //10-260 pts
			break;
		case "1 Year":
			this.apiQuery += "TIME_SERIES_WEEKLY_ADJUSTED"
					+ "&outputsize=full";   
			this.jsonFilter = "Weekly Adjusted Time Series";  //52 pts
			break;
		case "5 Years":
			this.apiQuery += "TIME_SERIES_MONTHLY_ADJUSTED";
			this.jsonFilter = "Monthly Adjusted Time Series";
			break;
		default:
			System.out.println("Invalid period selected: " + period);
			System.exit(0);  // Exit if the period is invalid
		}

		//complete the api http call string
		//this.apiQuery +=  "&symbol=" + stockSymbol + "&apikey=" + API_KEY;
	}


	public String getApiQuery() {
		return apiQuery;
	}

	public String getJsonFilter() {
		return jsonFilter;
	}
}