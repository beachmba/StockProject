package jdstockmarket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/*
 * Responsibilities of class:  obtains the closing price yesterday, 
 * the most recent price, and the Date of the last price
 */
public class Recent {
	private double yesterdaysClose = -1;
	private double mostRecentPrice = -1;
	private Date mostRecentDateAndTime = null; //new SimpleDateFormat("yyyy-MM-dd").parse("2024-11-11");
    
	//constructor
	public Recent (String stockSymbol) throws Exception
	{
		AlphaVantageCloseChart tempAVChart = null;
		try 
		{
			//In order to get yesterday's price, must make a dataset that is for 5 days, not 1 day
			tempAVChart = new AlphaVantageCloseChart("", stockSymbol, new Interval(null, null, "5 Days"));
		} 
		catch (IOException e) 
		{
			System.out.println("Recent: IO exception: Could not construct chart");
			e.printStackTrace();
		}
		//This next field will be the basis for all 1-Day Interval calls
		this.mostRecentDateAndTime = tempAVChart.getDates().getFirst();
		this.mostRecentPrice = tempAVChart.getLastPrice();
		//Find yesterday's close . Step thru and find the close from the previous day
		//given the 5-day tempAVChart, which contains the fields (ArrayLists) "dates" and "closes"
		ArrayList <Date> fiveDates = tempAVChart.getDates(); 
		System.out.println("size of date array = " + fiveDates.size());
		//get the day of the week of the last data point
		int lastDayOfWeek = fiveDates.get(0).getDay();
		int indexOfDifferentDay = -1;

		// Iterate backward to find the first index with a different Date
		for (int i = 1; i < fiveDates.size(); i++) 
		{ // Start from the 2nd element (which is the next-to-last price)
			//System.out.println("Looking at " + i + " th element");
			if (fiveDates.get(i).getDay() != lastDayOfWeek) 
			{ // Compare the current Day of week to the last day of wk
				indexOfDifferentDay = i; // Store the index of the first different Date
				break; // Exit the loop as soon as a different Day or week				
			}
		}
		this.yesterdaysClose =  tempAVChart.getCloses().get(indexOfDifferentDay);
	}

	public double getYesterdaysClose() {
		return yesterdaysClose;
	}
	
	public double getMostRecentPrice() {
		return mostRecentPrice;
	}
	
	public Date getMostRecentDateAndTime() {
		return mostRecentDateAndTime;
	}
	

}

