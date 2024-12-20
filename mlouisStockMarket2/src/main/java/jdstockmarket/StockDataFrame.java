package jdstockmarket;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartPanel;

import com.toedter.calendar.*;

public class StockDataFrame extends JFrame {

	// GUI Components
	private JPanel projectPane;
	private JTable portfolioTable;
	private JComboBox<String> symbolComboBox;
	private JComboBox<String> dateRangeComboBox;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private JLabel lblCurrentTime = new JLabel();
	private JLabel lblCurrentPrice = new JLabel();
	private JLabel lblYesterdayClose = new JLabel();
	private JLabel lblGainToday = new JLabel();
	private JLabel lblGainOnChart= new JLabel();
	private ChartPanel myChartPanel;
	private JPanel graphAreaPanel = new JPanel(new BorderLayout());;

	//  model elements
	private AlphaVantageCloseChart myAVCloseChart;
	private TreeMap<String, Stock> stocks;   // <stock symbol, Stock Object>

	public StockDataFrame() throws Exception {

		//First, set the end of all non-custom intervals
		// This need only be done ONCE
		new LastDateOfValidData();

		this.setTitle("Stock Graph and Portfolio Display Program - Michael Louis ");
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 5, 730, 530);

		this.projectPane = new JPanel();
		this.projectPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(projectPane);
		this.projectPane.setLayout(new GridLayout(1, 2, 0, 0)); 

		JPanel leftPanel = new JPanel();
		this.projectPane.add(leftPanel);
		leftPanel.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel chartOptionsPanel = new JPanel();
		chartOptionsPanel.setBorder(new LineBorder(new Color(0, 64, 128)));
		chartOptionsPanel.setLayout(new GridLayout(10, 2, 0, 0));
		leftPanel.add(chartOptionsPanel);

		//Read in the stored portfolio to populate the drop-down combo box
		this.stocks = PortfolioManager.readPortfolioFromFile().getStocks();

		chartOptionsPanel.add(new JLabel("Choose A Stock Symbol:"));
		symbolComboBox = new JComboBox<>();
		//Make it editable 
		symbolComboBox.setEditable(true);
		//populate it
		for (Stock stock : stocks.values()) {
			symbolComboBox.addItem(stock.getStockSymbol());
		}
		chartOptionsPanel.add(symbolComboBox);
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel("Choose A Date Range:"));

		String[] dateRangeList = {"1 Day", "5 Days", "1 Month", "6 Months", "Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		dateRangeComboBox = new JComboBox<>(dateRangeList);
		chartOptionsPanel.add(dateRangeComboBox);

		//Custom Range Area 
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));
		JLabel lblCustomRange = new JLabel("Custom Range: ");
		chartOptionsPanel.add(lblCustomRange);
		chartOptionsPanel.add(new JLabel(""));

		JLabel lblChooseStart = new JLabel("Choose Start Date");
		chartOptionsPanel.add(lblChooseStart);

		JLabel lblChooseEnd = new JLabel("Choose End Date");
		chartOptionsPanel.add(lblChooseEnd);

		startDateChooser = new JDateChooser();
		chartOptionsPanel.add(startDateChooser);
		endDateChooser = new JDateChooser();
		chartOptionsPanel.add(endDateChooser);

		lblCustomRange.setEnabled(false);
		lblChooseEnd.setEnabled(false);
		lblChooseStart.setEnabled(false);
		startDateChooser.setEnabled(false);
		endDateChooser.setEnabled(false);

		//Custom Range labels and date choosers are disabled initially.  
		//Only enable them if the date range combo box = "Custom Range"
		dateRangeComboBox.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String selectedRange = (String) dateRangeComboBox.getSelectedItem();
				boolean enableDateChoosers = "Custom Range".equals(selectedRange);

				lblCustomRange.setEnabled(enableDateChoosers);
				lblChooseStart.setEnabled(enableDateChoosers);
				lblChooseEnd.setEnabled(enableDateChoosers);
				startDateChooser.setEnabled(enableDateChoosers);
				endDateChooser.setEnabled(enableDateChoosers);
			}
		});

		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));

		JButton redrawChartButton = new JButton("Re-Draw Chart");
		chartOptionsPanel.add(redrawChartButton);

		//The lower left of the frame is the user's portfolio, shares, prices, and values
		JPanel portfolioPanel = new JPanel();
		leftPanel.add(portfolioPanel);
		portfolioPanel.setLayout(new BorderLayout(0, 0));

		JButton findPortfolioValueButton = new JButton("Find Current Portfolio Value");
		portfolioPanel.add(findPortfolioValueButton, BorderLayout.NORTH);

		String[] columnTitles = {"Symbol", "Price", "# Shares", "Market Value"};
		portfolioTable = new JTable(new DefaultTableModel(columnTitles, 0));

		// Set row height and adjust column alignment/width
		portfolioTable.setRowHeight(13);
		portfolioTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		portfolioTable.getColumnModel().getColumn(3).setPreferredWidth((int) (portfolioTable.getPreferredSize().width * 0.4));

		// Right-align the last column
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		portfolioTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

		portfolioPanel.add(portfolioTable, BorderLayout.CENTER);

		DefaultTableModel tableModel = (DefaultTableModel) portfolioTable.getModel();
		tableModel.addRow(new Object[]{"Symbol", "Price", "# Shares", "Market Value"});

		//**  PUt portfolio values that were read from file into the table. Makes many API calls to do this
		populatePortfolioTable(tableModel, false);   //false means no recalc, no calling the AV site 10 times

		portfolioPanel.add(portfolioTable, BorderLayout.CENTER);

		// ******** This completes the construction of the left panel.    

		//make a right hand panel for the frame
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.85;
		gbc.fill = GridBagConstraints.BOTH;

		//create a panel just for the graph
		this.graphAreaPanel = new JPanel(new BorderLayout());
		rightPanel.add(graphAreaPanel, gbc);

		// Lower 15% of right panel is for the current, yesterday prices, and gains
		gbc.gridy = 1;
		gbc.weighty = 0.15;
		JPanel pricesPanel = new JPanel();
		pricesPanel.setBackground(Color.cyan);
		pricesPanel.setLayout(new GridLayout(5, 2, 0, 0));

		JLabel lblNewLabel_0 = new JLabel("As Of:  ");
		lblNewLabel_0.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_0);

		pricesPanel.add(lblCurrentTime);

		JLabel lblNewLabel_1 = new JLabel("Current Price:  ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_1);

		pricesPanel.add(lblCurrentPrice);

		JLabel lblNewLabel_2 = new JLabel("Yesterday:  ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_2);

		pricesPanel.add(lblYesterdayClose);

		JLabel lblNewLabel_4 = new JLabel("% Gain Today:  ");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_4);

		pricesPanel.add(lblGainToday);

		JLabel lblNewLabel_3 = new JLabel("% Gain On Chart:  ");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_3);
		pricesPanel.add(lblGainOnChart);

		makeAndPutChartAndPopulatePricePanel();

		rightPanel.add(pricesPanel, gbc);

		this.projectPane.add(rightPanel);

		//********Button Listeners   **********************

		//when the re-draw button is clicked, get the symbol, period, and replace the existing chart
		redrawChartButton.addActionListener(e -> {
			try {
				makeAndPutChartAndPopulatePricePanel();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		//when the Find Current portfolio values button is clicked, do all the api calls and repopulate table 
		findPortfolioValueButton.addActionListener(e -> populatePortfolioTable(tableModel, true));
	}

	private void makeAndPutChartAndPopulatePricePanel() throws Exception 
	{
		String stockSymbol = (String) symbolComboBox.getSelectedItem();
		String period = (String) dateRangeComboBox.getSelectedItem();

		//Form the Interval object based on user's entry in dateRange Combo Box
		Interval interval;
		if ("Custom Range".equals(period)) 
		{
			// "Custom Range selected"
			Date dateBegin = startDateChooser.getDate();
			Date dateEnd = endDateChooser.getDate();
			if (!dateEnd.after(dateBegin))
			{
				JOptionPane.showMessageDialog(null, "End Date Must Be After Start Date",
						"SDF: Bad Date Parameters", JOptionPane.ERROR_MESSAGE);
				return;
			}
			interval = new Interval(dateBegin, dateEnd, "Custom Range");
		} 
		else 
		{
			//the period "1 Day", "5 Days",  ..., or  "5 Years" has been selected
			interval = new Interval(null, null, period);
		}

		//  Make the Chart!!
		AlphaVantageCloseChart tempAVChart = null;
		try {
			//test to see if a chart can be returned for this symbol 
			tempAVChart = new AlphaVantageCloseChart("", stockSymbol, interval);
			if (tempAVChart.getResultChart() == null)
			{
				JOptionPane.showMessageDialog(null, "SDF: '" + stockSymbol + "' Is A Either Bad Ticker Symbol or Not Valid For This Time Frame!",
						"Cannot Produce Chart!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} 
		catch (IOException e) 
		{
			System.out.println("SDF:  Error in ceating chart!");
			e.printStackTrace();
		}
		this.myAVCloseChart = tempAVChart;
		if (tempAVChart.getCloses().size() == 0)
		{
			System.out.println("SDF: Error ! Zero Closing Prices in Array of Chart!");
		}
		//	System.out.println("Pts on Chart = " + this.myAVCloseChart.getCloses().size());
		this.graphAreaPanel.removeAll();
		this.myChartPanel = new ChartPanel(this.myAVCloseChart.getResultChart());
		this.graphAreaPanel.add(myChartPanel, BorderLayout.CENTER);
		this.graphAreaPanel.revalidate();
		this.graphAreaPanel.repaint();

		//produce the current, yesterday, % gain today and % gain on chart
		populatePricePanel();
		return; 
	}

	//produce the current, yesterday, % gain today and % gain on chart
	private void populatePricePanel() throws Exception     //AlphaVantageCloseChart tempAVChart)
	{
		String stockSymbol = (String) symbolComboBox.getSelectedItem();
		Recent recent = new Recent(stockSymbol);

		Date timeOfCurrent = recent.getMostRecentDateAndTime();
		lblCurrentTime.setText(timeOfCurrent.toString().substring(0,16) + " EST");

		double todaysClose = recent.getMostRecentPrice();
		lblCurrentPrice.setText(String.valueOf(todaysClose));

		double yesterdaysClose = recent.getYesterdaysClose();
		lblYesterdayClose.setText(String.format("%.2f", yesterdaysClose));

		lblGainToday.setText(	String.format("%.2f",
				(todaysClose - yesterdaysClose) / yesterdaysClose  * 100) + "%");

		double graphFirstPrice = this.myAVCloseChart.getFirstPrice();
		double graphLastPrice = this.myAVCloseChart.getLastPrice();
		lblGainOnChart.setText(		String.format("%.2f",
				(graphLastPrice - graphFirstPrice ) / graphFirstPrice * 100) + "%");
	}


	// populates the table of portfolio market values and sums the values
	private void populatePortfolioTable(DefaultTableModel tableModel, boolean newValues) {

		// Display a foreground calculating message dialog in a modeless manner
		JDialog dialog = new JDialog(this, "Please Wait", true);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(400, 100);
		dialog.setLocationRelativeTo(this);
		dialog.add(new JLabel("Calculating Current Portfolio Value, Please wait...", SwingConstants.CENTER));
		dialog.setModal(false);
		dialog.setVisible(true);

		//make a copy of the stock TreeMap for use in the Background task
		TreeMap<String, Stock> tempStocks =this.stocks;   // <stock symbol, Stock Object>

		// Use SwingWorker to perform the calculation in the background
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() 
		{
			@Override
			protected Void doInBackground()   // note capital Void
			{
				// Perform the time-consuming task
				// Clear existing data rows in the table model
				tableModel.setRowCount(1);
				String mostRecentDateTime = null;
				
				// Populate the table with stock market values
				//System.out.println(tempStocks.toString());
				for (Stock stock : tempStocks.values()) 
				{
					Recent recent = null;
					String mktVal = "";
					if (newValues)  //true = Recalculate the latest stock prices 
					{
						// Does an API call for each entry in the table
						try {
							recent = new Recent (stock.getStockSymbol());
						} catch (Exception e) {
							System.out.println("PPT: Could not get Recent data for " + stock.getStockSymbol());
							e.printStackTrace();
						}
						stock.setRecent(recent);
						//and change the closing price in the stock Object
						stock.setClosingPrice(recent.getMostRecentPrice());
						// Update the entry in the TreeMap with the updated stock object
						tempStocks.put(stock.getStockSymbol(), stock);
						//	lastDate = stock.getRecent().getMostRecentDateAndTime().toString();
						mktVal = String.format("$%,.2f", stock.getMarketValue());
						mostRecentDateTime = recent.getMostRecentDateAndTime().toString();
					}

					// Add the updated stock data to the table
					Object [] rowData = 
						{
								stock.getStockSymbol(),
								newValues ? stock.getClosingPrice() : "",
								String.format("%,7d" , stock.getShares()),
								mktVal,
						};
					tableModel.addRow(rowData);
				}

				//Only put total Row if calculating Current Portfolio Value
				if (newValues)
				{
					Object [] totalRow = {
							"<html><b>" + mostRecentDateTime.substring(0,10) + "</b></html>",
							"<html><b>" + mostRecentDateTime.substring(11,16) + " EST</b></html>",
							"<html><b>Total:</b></html>",
							String.format("<html><b>$%,.2f</b></html>", calculateTotalValue(stocks))
					};
					tableModel.addRow(totalRow);
				}		
				return null;
				//End Background Task
			}

			@Override
			protected void done() 
			{
				// Close the dialog when the calculation is done
				dialog.dispose();
			}
		};

		//replace the field with the working copy
		this.stocks = tempStocks;

		// Execute the SwingWorker
		worker.execute();
	}



	//sum the market value of all stocks
	private Double calculateTotalValue(TreeMap<String, Stock> stocks) {
		// calculates total value of portfolio
		double total = 0.0;
		for (Stock stock : stocks.values()) 
		{
			total += stock.getMarketValue();
		}
		return total;
	}

	// ********    MAIN METHOD   *********************
	public static void main(String[] args) {
		try {
			StockDataFrame frame = new StockDataFrame();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


