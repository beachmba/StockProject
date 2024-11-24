package jdstockmarket;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toedter.calendar.*;

public class StockDataFrame extends JFrame {

	//private static final long serialVersionUID = 1L;
	
	// GUI Components
	private JPanel projectPane;
	private JTable portfolioTable;
	private JComboBox<String> symbolComboBox;
	private JComboBox<String> dateRangeComboBox;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;
	private JLabel lblCurrentPrice = new JLabel();
	private JLabel lblYesterdayClose = new JLabel();
	private JLabel lblGainToday = new JLabel();
	private JLabel lblGainOnChart= new JLabel();
	private ChartPanel myChartPanel;
	private JPanel graphAreaPanel = new JPanel(new BorderLayout());;
	
	//  model elements
	private AlphaVantageCloseChart myAVCloseChart;
	private TreeMap<String, Stock> stocks;
	private DefaultTableModel tableModel ;

	public StockDataFrame() throws JsonMappingException, JsonProcessingException {

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
		chartOptionsPanel.setLayout(new GridLayout(8, 2, 0, 0));
		leftPanel.add(chartOptionsPanel);

		chartOptionsPanel.add(new JLabel("<html>Choose A Stock Symbol<br>   And A Date Range</html>"));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel("Symbol: "));

		//Read in the stored portfolio to populate the drop-down combo box
		stocks = PortfolioManager.readPortfolioFromFile().getStocks();

		symbolComboBox = new JComboBox<>();
		//Make it editable 
		symbolComboBox.setEditable(true);
		//populate it
		for (Stock stock : stocks.values()) {
			symbolComboBox.addItem(stock.getStockSymbol());
		}
		chartOptionsPanel.add(symbolComboBox);

		chartOptionsPanel.add(new JLabel("Date Range: "));
		String[] dateRangeList = {"1 Day", "5 Days", "1 Month", "6 Months", "Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		dateRangeComboBox = new JComboBox<>(dateRangeList);
		chartOptionsPanel.add(dateRangeComboBox);

		JLabel chooseStart = new JLabel("Choose Start Date");
		chartOptionsPanel.add(chooseStart);
		chooseStart.setEnabled(false);

		JLabel chooseEnd = new JLabel("Choose End Date");
		chartOptionsPanel.add(chooseEnd);
		chooseEnd.setEnabled(false);

		startDateChooser = new JDateChooser();
		chartOptionsPanel.add(startDateChooser);
		endDateChooser = new JDateChooser();
		chartOptionsPanel.add(endDateChooser);

		startDateChooser.setEnabled(false);
		endDateChooser.setEnabled(false);

		//date choosers are disabled.  Only enable them if the date range combo box = "Custom Range"
		dateRangeComboBox.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String selectedRange = (String) dateRangeComboBox.getSelectedItem();
				boolean enableDateChoosers = "Custom Range".equals(selectedRange);

				chooseStart.setEnabled(enableDateChoosers);
				chooseEnd.setEnabled(enableDateChoosers);
				startDateChooser.setEnabled(enableDateChoosers);
				endDateChooser.setEnabled(enableDateChoosers);
			}
		});

		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel(""));

		JButton redrawChartButton = new JButton("Re-Draw Chart");
		chartOptionsPanel.add(redrawChartButton);

		JPanel portfolioPanel = new JPanel();
		leftPanel.add(portfolioPanel);
		portfolioPanel.setLayout(new BorderLayout(0, 0));

		JButton findPortfolioValueButton = new JButton("Find Portfolio Value");
		portfolioPanel.add(findPortfolioValueButton, BorderLayout.NORTH);

		//myAVCloseChart = makeAndPutChart();

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

		this.tableModel = (DefaultTableModel) portfolioTable.getModel();
		this.tableModel.addRow(new Object[]{"Symbol", "Price", "# Shares", "Market Value"});

		putPortfolioTableValues(false);   //false means no recalc, no calling the AV site 10 times

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
		//this.graphAreaPanel.add(new JLabel());

//		this.myChartPanel = null; //
//		//this.myChartPanel = new ChartPanel(myAVCloseChart.getResultChart());
//		this.graphAreaPanel.add(myChartPanel, BorderLayout.CENTER);

		rightPanel.add(graphAreaPanel, gbc);

		gbc.gridy = 1;
		gbc.weighty = 0.15;
		JPanel pricesPanel = new JPanel();
		pricesPanel.setBackground(Color.cyan);
		pricesPanel.setLayout(new GridLayout(4, 2, 0, 0));

		JLabel lblNewLabel_1 = new JLabel("Currently:  ");
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

		//redrawChartButton.doClick();
//		this.myChartPanel = new ChartPanel(myAVCloseChart.getResultChart());
//		this.graphAreaPanel.add(myChartPanel, BorderLayout.CENTER);
		this.myAVCloseChart = makeAndPutChart();
		populatePricePanel();

		rightPanel.add(pricesPanel, gbc);

		this.projectPane.add(rightPanel);

		//********Button Listeners   **********************
		
		//when the re-draw button is clicked, get the symbol, period, and replace the existing chart
		redrawChartButton.addActionListener(e -> 
		{
			myAVCloseChart = makeAndPutChart();
			populatePricePanel();
		});
		
		//when the Find Portfolio values button is clicked, do all the api calls and repopulate table 
		findPortfolioValueButton.addActionListener(e -> putPortfolioTableValues(true));
	}

	private void populatePricePanel()
	{
		String stockSymbol = (String) symbolComboBox.getSelectedItem();
		//  This method ALSO this.todaysClose = tempAVChart.getClosingPrice("today");
		//		lblCurrentPrice.setText(String.valueOf(this.todaysClose));
		double todaysClose = this.myAVCloseChart.getYesterdayOrTodayClose(stockSymbol,  "today");
		lblCurrentPrice.setText(String.valueOf(todaysClose));

		//how to find yesterday's price?  What if it's a custom chart?
		// and the last point on the graph is not yesterday?Monday now? 
		double yesterdaysClose = this.myAVCloseChart.getYesterdayOrTodayClose(stockSymbol, "yesterday");
		lblYesterdayClose.setText(String.format("%.2f", yesterdaysClose));
		
		lblGainToday.setText(				String.format("%.2f",
				(todaysClose - yesterdaysClose) / yesterdaysClose  * 100) + "%");

		double graphFirstPrice = this.myAVCloseChart.getFirstPrice();
		lblGainOnChart.setText(				String.format("%.2f",
				(this.myAVCloseChart.getLastPrice() - graphFirstPrice ) / graphFirstPrice * 100) + "%");
	}

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

	public AlphaVantageCloseChart makeAndPutChart() {
		String stockSymbol = (String) symbolComboBox.getSelectedItem();
		String period = (String) dateRangeComboBox.getSelectedItem();
		Interval interval;
		if ("Custom Range".equals(period)) 
		{
			// "Custom Range selected"
			interval = new Interval(startDateChooser.getDate(), endDateChooser.getDate(), "Custom Range");
		} 
		else 
		{
			//the period "1 Day" ... "5 Years" has been selected
			interval = new Interval(null, null, period);
		}

		//  Make the Chart!!
		AlphaVantageCloseChart tempAVChart = null;
		try {
			tempAVChart = new AlphaVantageCloseChart("", stockSymbol, interval);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.graphAreaPanel.removeAll();
		this.myChartPanel = new ChartPanel(tempAVChart.getResultChart());
		this.graphAreaPanel.add(myChartPanel, BorderLayout.CENTER);
		this.graphAreaPanel.revalidate();
		this.graphAreaPanel.repaint();

		return tempAVChart;
	}

	// populates the table of portfolio values and sums the values
	public void putPortfolioTableValues(boolean newValues) {
		// Clear existing data row in the table model
		tableModel.setRowCount(1);

		// Recalculate the latest stock prices when the button is clicked
	//	AlphaVantageCloseChart tempAVCC; 
		for (Stock stock : this.stocks.values()) 
		{
			double lastPrice;
		
			if (newValues)  //true = updating to latest values. 
			{
				// Does an API call for each entry in the table
				lastPrice = myAVCloseChart.getYesterdayOrTodayClose(
						stock.getStockSymbol(), "today");
				//and change the closing price in the stock Object
				stock.setClosingPrice(lastPrice);
			}
	
			// Add the updated stock data to the table
			Object[] rowData = {
					stock.getStockSymbol(),
					stock.getClosingPrice(),
					stock.getShares(),
					String.format("$%,.2f", stock.getMarketValue()),
			};
			tableModel.addRow(rowData);
		}

		// Compute and add the total row
		Object[] totalRow = {null, null, "Total: ", String.format("$%,.2f", calculateTotalValue(stocks))};
		tableModel.addRow(totalRow);
	}

}
