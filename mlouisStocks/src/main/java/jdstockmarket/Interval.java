package jdstockmarket;

import java.util.Calendar;
import java.util.Date;

public class Interval {
	String period;
	private Date beginDate;
	private Date endDate;
	public Interval(Date beginDate, Date endDate, String period)
	{
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.period = period;
	}
	public String getPeriod() 
	{
		return this.period;
	}
	public Date getBeginDate() 
	{
		return this.beginDate;
	}
	public Date getEndDate() 
	{
		return this.endDate;	// TODO Auto-generated method stub
	}
	
	public void setBeginEnd() {
		// computes beginning and ending date, given the period string "1 Day", "5 Days",... ,"5 years"
		// Get the current date
		this.endDate = new Date();
		long endtime = endDate.getTime();
		long oneDayInMillis = 24L * 60 * 60 * 1000;

		// Define date range variables using an array for beginDate (to avoid final/effectively final issue)
		final Date[] dateRange = new Date[1];

		// Determine the date range based on the selected value
		switch (this.period) {  // Assume 'selectedPeriod' holds one of the predefined string values
		    case "1 Day":
		        dateRange[0] = new Date(endtime - 3 * oneDayInMillis); // 1 day ago. use 2 days.
		        break;
		    case "5 Days":
		        dateRange[0] = new Date(endtime - 5 * oneDayInMillis); // 5 days ago
		        break;
		    case "1 Month":
		        dateRange[0] = new Date(endtime - 30 * oneDayInMillis); // Approx. 1 month ago
		        break;
		    case "6 Months":
		        dateRange[0] = new Date(endtime -  180 * oneDayInMillis); // Approx. 6 months ago
		        break;
		    case "Year-To-Date":
		        Calendar ytd = Calendar.getInstance();
		        ytd.set(Calendar.MONTH, 0);  // January
		        ytd.set(Calendar.DAY_OF_MONTH, 1);  // First day of the year
		        ytd.set(Calendar.HOUR_OF_DAY, 0);
		        ytd.set(Calendar.MINUTE, 0);
		        ytd.set(Calendar.SECOND, 0);
		        ytd.set(Calendar.MILLISECOND, 0);
		        dateRange[0] = ytd.getTime();
		        break;
		    case "1 Year":
		        dateRange[0] = new Date(endtime -  365 * oneDayInMillis); // 1 year ago
		        break;
		    case "5 Years":
		        dateRange[0] = new Date(endtime -  5 * 365 * oneDayInMillis); // 5 years ago
		        break;
		    default:
		        System.out.println("Invalid period selected.");
		        System.exit(0);  // Exit if the period is invalid
		}
		this.beginDate = dateRange[0];
		System.out.println("begin date = " +this.beginDate.toString());
		System.out.println("end date = " +this.endDate.toString());

		
		
	}



}
