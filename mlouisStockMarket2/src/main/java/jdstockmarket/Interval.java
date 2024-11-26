package jdstockmarket;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Interval 
{
	private Date beginDate;
	private Date endDate;
	String period;  		 // "1 Day", "5 Days", ... "5 years"
	private ApiCallParams apiCallParams;

	//constructor
	public Interval(Date beginDate, Date endDate, String period)
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
			//			System.out.println("num of days diff = " + diffDays);

			//form the correct api call and Json filter based on the # of days 
			// difference in the custom range
			if (diffDays <3)
				this.apiCallParams = new ApiCallParams("1 Day");
			else if (diffDays < 18)
				this.apiCallParams = new ApiCallParams("5 Days");
			else if (diffDays < 105)
				this.apiCallParams = new ApiCallParams("1 Month");
			else if (diffDays < 270)
				this.apiCallParams = new ApiCallParams("6 Months");
			else if (diffDays < (3 * 365) )
				this.apiCallParams = new ApiCallParams("1 Year");
			else 
				this.apiCallParams = new ApiCallParams("5 Years");
		}
		else
		{
			// there is a fixed range chosen . "1 Day", "5 Days",..."5 Years" back from today
			// beginDate and endDate are null to begin with 

			// Define date range variables using an array for beginDate (to avoid final/effectively final issue)
			final Date[] tempBeginningDate = new Date[1];

			// First, set the http call string and JSON filter
			this.apiCallParams = new ApiCallParams(period);

			//Compute the starting and ending dates
			//first, the ending date.  This is easy, it's today or, 
			// if today is a weekend day, it's the most recent Friday
			//get today's time in milliseconds since the epoch
			this.endDate = businessDate();  //get today's date.  If a weekend, make it the most recent Friday, 11pm 
			long endtime = this.endDate.getTime();  // # milliseconds since epoch

			// and define a constant equal to one day
			final long oneDayInMillis = 24L * 60 * 60 * 1000;

			// Determine the date range based on the selected value
			switch (period) 
			{  // Assume 'selectedPeriod' holds one of the predefined string values
			case "1 Day":
				tempBeginningDate[0] = new Date(endtime - 1 * oneDayInMillis); // 1 day ago. 
				break;
			case "5 Days":
				tempBeginningDate[0] = new Date(endtime - 7 * oneDayInMillis); // 5 business days ago
				break;
			case "1 Month":
				tempBeginningDate[0] = new Date(endtime - 30 * oneDayInMillis); // 1 month ago
				break;
			case "6 Months":
				// subtract 1 from end date
				tempBeginningDate[0] = new Date(endtime -  180 * oneDayInMillis); // Approx. 6 months ago
				//	this.endDate = new Date( endtime - oneDayInMillis);
				break;
			case "Year-To-Date":
				Calendar ytd = Calendar.getInstance();
				ytd.set(Calendar.MONTH, 0);  // January
				ytd.set(Calendar.DAY_OF_MONTH, 1);  // First day of the year
				ytd.set(Calendar.HOUR_OF_DAY, 1);
				ytd.set(Calendar.MINUTE, 1);
				ytd.set(Calendar.SECOND, 1);
				ytd.set(Calendar.MILLISECOND, 1);
				tempBeginningDate[0] = ytd.getTime();
				break;
			case "1 Year":
				tempBeginningDate[0] = new Date(endtime -  365 * oneDayInMillis); // 1 year ago
				break;
			case "5 Years":
				tempBeginningDate[0] = new Date(endtime -  5 * 365 * oneDayInMillis); // 5 years ago
				break;
			default:
				System.out.println("Invalid period selected.");
				System.exit(0);  // Exit if the period is invalid
			}
			// store the temporary variable into the object's beginning date
			this.beginDate = tempBeginningDate[0];
			System.out.println("Interval Begin Date " + this.beginDate.toString());
			System.out.println("Interval End Date " + this.endDate.toString());
		}
	}


	public Date businessDate()
	// return current Date  If it's a weekend or before noon on Monday, adjust to 
	// previous Friday 11pm.
	{
		// Get the current date and time in US/Eastern time zone
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

		// Check if it's Saturday, Sunday, or Monday before 10am
		int daysToAdjust = 0;
		//if (dayOfWeek == Calendar.SATURDAY)  daysToAdjust = 1;  
	//	if (dayOfWeek == Calendar.SUNDAY) daysToAdjust = 2;  
		//if(dayOfWeek == Calendar.MONDAY )daysToAdjust = 3;  
//		
		calendar.add(Calendar.DAY_OF_MONTH, -daysToAdjust); // Go to Friday or before
//
//			// Set curTime to 11pm on the previous Friday
////			calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 23); // 11 PM
			calendar.set(Calendar.MINUTE, 0);
//			calendar.set(Calendar.SECOND, 0);
//			calendar.set(Calendar.MILLISECOND, 0);
//		}

		//System.out.println("Right now business Time " + (Date) calendar.getTime());
		return (Date) calendar.getTime();  //returns 
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

	public ApiCallParams getApiCallParams() {
		return apiCallParams;
	}
}