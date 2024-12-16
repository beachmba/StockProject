package jdstockmarket;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Interval 
{
	private Date beginDate = null;
	private Date endDate = null;
	String period = "";  		 // "1 Day", "5 Days", ... "5 years"
	private MakeAPICallString apiCallParams;

	//constructor
	public Interval(Date beginDate, Date endDate, String period) throws IOException
	{
		this.period = period;
		this.endDate = endDate;
		this.beginDate = beginDate;

		// computes beginning and ending date, given the period string "1 Day", "5 Days",... 
		//    ... "5 Years" or "Custom Range".  In the case of "Custom Range, the beginning
		//  and ending dates have already been set.
		if (period == "Custom Range") 
		{
			// "beginDate" and "endDate" are already set.  compute diff in days
			long diffInMillis = Math.abs(endDate.getTime() - beginDate.getTime());
			// Convert milliseconds to days
			long diffDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

			//form the correct api call and Json filter based on the # of days 
			// difference in the custom range
			if (diffDays <3)
				this.apiCallParams = new MakeAPICallString("1 Day");
			else if (diffDays < 18)
				this.apiCallParams = new MakeAPICallString("5 Days");
			else if (diffDays < 105)
				this.apiCallParams = new MakeAPICallString("1 Month");
			else if (diffDays < 270)
				this.apiCallParams = new MakeAPICallString("6 Months");
			else if (diffDays < (3 * 365) )
				this.apiCallParams = new MakeAPICallString("1 Year");
			else 
				this.apiCallParams = new MakeAPICallString("5 Years");
		}
		else
		{
			// there is a fixed range chosen . "1 Day", "5 Days",..."5 Years" back from today
			// beginDate and endDate are null to begin with 

			// First, set the http call string and JSON filter
			this.apiCallParams = new MakeAPICallString(period);

			//Compute the starting and ending dates
			//first, the ending date.  This is easy, it's today or, 
			//get today's time in milliseconds since the epoch
			this.endDate = businessDate();  //get today's date.  Set time to after business hours
			long endtime = this.endDate.getTime();  // # milliseconds since epoch

			// and define a constant equal to one day
			final long oneDayInMillis = 24L * 60 * 60 * 1000;

			// Determine the date range based on the selected value
			switch (period) 
			{  // Assume 'selectedPeriod' holds one of the predefined string values
			case "1 Day":
				this.beginDate = new Date(endtime - 1 * oneDayInMillis); // 1 day ago. 
				break;
			case "5 Days":
				this.beginDate = new Date(endtime - 7 * oneDayInMillis); // 5 business days ago
				break;
			case "1 Month":
				this.beginDate = new Date(endtime - 30 * oneDayInMillis); // 1 month ago
				break;
			case "6 Months":
				this.beginDate = new Date(endtime -  182 * oneDayInMillis); // Approx. 6 months ago
				break;
			case "Year-To-Date":
				Calendar ytd = Calendar.getInstance();
				ytd.set(Calendar.MONTH, 0);  // January
				ytd.set(Calendar.DAY_OF_MONTH, 1);  // First day of the year
				ytd.set(Calendar.HOUR_OF_DAY, 1);
				this.beginDate = ytd.getTime();
				break;
			case "1 Year":
				this.beginDate = new Date(endtime -  365 * oneDayInMillis); // 1 year ago
				break;
			case "5 Years":
				this.beginDate = new Date(endtime -  5 * 365 * oneDayInMillis); // 5 years ago
				break;
			default:
				System.out.println("Invalid period selected.");
				System.exit(0);  // Exit if the period is invalid
			}

			System.out.println("Interval Date Span " + this.beginDate.toString() //);
			+ " to " + this.endDate.toString());
		}
	}

	public Date businessDate() throws IOException
	// return current Date... Set to 9PM EST  
	{
		// Get the current date and time in US/Eastern time zone
		return LastDateOfValidData.getLastDateValidData();
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getPeriod() {
		return period;
	}

	public MakeAPICallString getApiCallParams() {
		return apiCallParams;
	}
}