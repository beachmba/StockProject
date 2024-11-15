//package jdstockmarket;

//public class NewCode {



//		table = new JTable();
//		DefaultTableModel tableModel = new DefaultTableModel(
//				new Object[][] {
//					{"Symbol", "Price", "# Shares", "Market Value"}
//				},
//				new String[] {"Symbol", "Price", "# Shares", "Market Value"}
//				);
//
//		// Set the model and add the table to the panel
//		table.setModel(tableModel);
//		portfolioPanel.add(table, BorderLayout.WEST);
//
//		// Retrieve stocks from the portfolio and populate the table
//		TreeMap<String, Stock> stocks = PortfolioManager.readPortfolioFromFile().getStocks();
//		Double mktVal = 0.;
//		for (Stock stock : stocks.values()) {
//			mktVal += stock.getMarketValue();
//			tableModel.addRow(new Object[] {
//					stock.getStockSymbol(),
//					stock.getClosingPrice(),
//					stock.getShares(),
//					stock.getMarketValue()
//			});
//		}Xj

		// Add the total row at the end
	//	tableModel.addRow(new Object[] {null, "Total Portfolio", "Value:", null});
	

//********************




// **************************************

//	JButton chartButton = new JButton("Display Chart");
//
//	//create a listener for this button 
//	AlphaVantageCloseChart myAVCloseChart = null;
//	chartButton.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//
//			//call the method to get all fields from input boxes and create chart
//			AlphaVantageCloseChart myAVCloseChart = produceChart();  // get chart, put it into jFrame Later
//			//  for example stockSymbol = (String) symbolComboBox.getSelectedItem();
//
//		}
//	});
//***********************
//
//
//public AlphaVantageCloseChart produceChart()
//{
//	//		// Get the selected item from the JComboBox
//	//		stockSymbol = (String) symbolComboBox.getSelectedItem();
//	//		stockSymbol = "AVGO";  // Default Stock to use
//
//	// comment next line to use default Stock Symbol
//	//stockSymbol = getSymbolFromConsole(stockSymbol);  //arg is default stock symbol
//
//	AlphaVantageCloseChart myAVCloseChart = null;
//	try {
//		myAVCloseChart = new AlphaVantageCloseChart ("test chart", stockSymbol, period);
//	} catch (JsonProcessingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return myAVCloseChart;
//
//}
//
//*********************		
//double todaysClose;
//if (myAVCloseChart != null)
//{
//	todaysClose = myAVCloseChart.getLastPrice();
//
//	JLabel lblCurrentPrice = new JLabel(String.valueOf(todaysClose));
//	pricesPanel.add(lblCurrentPrice);
//
//	JLabel lblNewLabel_4 = new JLabel("% Gain Today:  ");
//	lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
//	pricesPanel.add(lblNewLabel_4);
//
//	//find yesterday's closing price
//	//compute yesterday's date
//	//form request
//	//call api
//	//extract yesterday's closing price
//	double yesterdaysClose = myAVCloseChart.getLastPrice() * 0.90;
//
//
//**************
//
//
//public String fetchLiveStockData(String stockSymbol, String period) throws IOException {
//	//twse
//			// url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=5min"  
//			//		 url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" 
//			//url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
//			//+ "&extended_hours=false"
//			//+ "&symbol="+ stockSymbol + "&apikey=" + API_KEY;
//
//			// Construct the URL for the Alpha Vantage API request
//			String apiQuery = "https://www.alphavantage.co/query?function=";
//			period = "1 Day";
//			
//			switch (period )
//			{
//			case "1 Day":
//				// returns 30 days, 30x8x12 =3,000 pts, only need 30 - 500!
//				apiQuery += "TIME_SERIES_INTRADAY"
//						+ "&outputsize=full"
//						+ "&adjusted=true"
//						+ "&extended_hours=false"
//						+ "&interval=5min";
//				break;
//
//			case "5 Days":
//				//returns 30 days, 30x8x12 = 3000 pts, only need 500 
//				apiQuery += "TIME_SERIES_INTRADAY"
//						+ "&outputsize=full"
//						+ "&adjusted=true"
//						+ "&extended_hours=false"
//						+ "&interval=5min";
//				break;
//
//			case "1 Month":
//				//returns 30 days, 30x8x2 = 500 points.  use all
//				apiQuery += "TIME_SERIES_INTRADAY"
//						+ "&outputsize=full"
//						+ "&adjusted=true"
//						+ "&extended_hours=false"
//						+ "&interval=30min";
//				break;
//
//			case "6 Months":
//				//returns 20 years= 20x250 =5000 pts!  only need 120 points
//				apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
//						+ "&outputsize=full";
//				break;
//
//			case "1 Year":
//				//returns 20 years = 5000 pts!  only need 240 points
//				apiQuery += "TIME_SERIES_DAILY_ADJUSTED"
//						+ "&outputsize=full";
//				break;
//			case "5 Years":
//				//returns 20 years = 20x52 = 1100 pts!  only need 260 points
//				apiQuery += "TIME_SERIES_WEEKLY_ADJUSTED"
//						+ "&outputsize=full";
//				break;
//
//
//			case "Yesterday":
//				//choose yesterday's close. returns 100 daily closes
//				apiQuery += "TIME_SERIES_DAILY_ADJUSTED";  //compact by default
//				break;
//
//			}
//			apiQuery += "&symbol="+ stockSymbol + "&apikey=" + API_KEY;
//			
//			//  Build the HTTP request
//			Request request = new Request.Builder()
//					.url(apiQuery)
//					.build();
//
//			
//
//}
