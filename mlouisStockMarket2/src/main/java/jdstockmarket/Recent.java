package jdstockmarket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/*
 * Responsibilities of class:  obtains the closing price yesterday, 
 * the most recent price, and the Date of the last price
 */
public class Recent {
	private double yesterdaysClose;
	private double mostRecentPrice;
	private Date mostRecentDate = null; //new SimpleDateFormat("yyyy-MM-dd").parse("2024-11-11");
    
	//constructor
	public Recent (String stockSymbol)
	{
		AlphaVantageCloseChart tempAVChart = null;
		try 
		{
			tempAVChart = new AlphaVantageCloseChart("", stockSymbol, new Interval(null, null, "5 Days"));
		} 
		catch (IOException e) 
		{
			System.out.println("Recent: IO exception: Could not construct chart");
			e.printStackTrace();
		}

		this.mostRecentPrice = tempAVChart.getLastPrice();
		this.mostRecentDate = tempAVChart.getDates().getFirst();
		//This is code to handle finding yesterday's close
		//step thru and find the close from the previous day
		//given the 5-day tempAVChart, which contains the fields (ArrayLists) "dates" and "closes"
		ArrayList <Date> fiveDates = tempAVChart.getDates(); 
		//get the day of the week of the last data point
		int lastDay = fiveDates.get(0).getDay();
		int indexOfDifferentDay = -1;

		// Iterate backward to find the first index with a different Date
		for (int i = 1; i < fiveDates.size(); i++) 
		{ // Start from the 2nd element (which is the next-to-last price)
			//System.out.println("Looking at " + i + " th element");
			if (fiveDates.get(i).getDay() != lastDay) 
			{ // Compare the current Day of week to the last day of wk
				indexOfDifferentDay = i; // Store the index of the first different Date
				break; // Exit the loop as soon as a different Day or week				
			}
		}
		// return yesterday's close
		this.yesterdaysClose =  tempAVChart.getCloses().get(indexOfDifferentDay);
	}

	public double getYesterdaysClose() {
		return yesterdaysClose;
	}
	
	public double getMostRecentPrice() {
		return mostRecentPrice;
	}
	
	public Date getMostRecentDate() {
		return mostRecentDate;
	}
	
	public void setMostRecentDate(Date mostRecentDate) 
	{
		this.mostRecentDate = mostRecentDate;
	}
}

