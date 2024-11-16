package jdstockmarket;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.*;

public class StockDataFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel projectPane;
	//	private JTextField emptyTextField;
	//	private JTextField txtSymbol;
	private JTable portfolioTable;
	private String stockSymbol = null ;//"GOOG"; // Default Stock Symbol
	protected Container graphAreaPanel;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//EventQueue.invokeLater(new Runnable() {
		//	public void run() {
		try {
			StockDataFrame frame = new StockDataFrame(); 
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public StockDataFrame() throws JsonMappingException, JsonProcessingException {

		this.setTitle("Stock Graph and Portfolio Display Program - Michael Louis ");
		// Set the location of the JFrame: (x, y) = (120, 20)
		//this.setLocation(120, 20);
		this.setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 10, 860, 662);
	//                     , width, height)
		setBounds(50, 5, 630, 531); //50,100));
		
		//setBounds(defaultCloseOperation, defaultCloseOperation, defaultCloseOperation, defaultCloseOperation)
		projectPane = new JPanel();
		projectPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(projectPane);
		// Left and Right
		projectPane.setLayout(new GridLayout(1, 2, 0, 0));   //entire frame, 1 row 2 columns

		JPanel selectionsPanel = new JPanel();
		projectPane.add(selectionsPanel);

		//Upper = Symbol/Date   Lower = Portfolio List
		selectionsPanel.setLayout(new GridLayout(2, 1, 0, 0));   // 2 rows, 1 column

		JPanel chartOptionsPanel = new JPanel();
		chartOptionsPanel.setBorder(new LineBorder(new Color(0, 64, 128)));
		chartOptionsPanel.setLayout(new GridLayout(8, 2, 0, 0));

		selectionsPanel.add(chartOptionsPanel);

		//Jlabel w multiple lines 
		chartOptionsPanel.add(new JLabel("<html>Choose A Stock Symbol<br>And A Date Range</html>"));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel("Symbol: "));
		
		// Retrieve stocks from PortfolioManager
		TreeMap<String, Stock> stocks = PortfolioManager.readPortfolioFromFile().getStocks();

		// Populate the combo box stock symbols
		JComboBox<String> symbolComboBox = new JComboBox<>();
		for (Stock stock : stocks.values()) {
		    symbolComboBox.addItem(stock.getStockSymbol());
		}
		
		// Allow the combo box to be editable so users can type their own input
		symbolComboBox.setEditable(true);

		chartOptionsPanel.add(symbolComboBox);

		chartOptionsPanel.add(new JLabel("Date Range: "));
		
		// Date range JComboBox
		String[] dateRangeList = {"1 Day", "5 Days", "1 Month", "Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		JComboBox<String> dateRangeComboBox = new JComboBox<>(dateRangeList);
		chartOptionsPanel.add(dateRangeComboBox);

		JLabel chooseStart = new JLabel("Choose Start Date");
		chartOptionsPanel.add(chooseStart);
		chooseStart.setEnabled(false);
	
		JLabel chooseEnd = new JLabel("Choose End Date");
		chartOptionsPanel.add(chooseEnd);
		chooseEnd.setEnabled(false);

		// Date choosers
		JDateChooser dateChooser = new JDateChooser();
		chartOptionsPanel.add(dateChooser);
		JDateChooser dateChooser_1 = new JDateChooser();
		chartOptionsPanel.add(dateChooser_1);

		// Initially disable the date choosers
		dateChooser.setEnabled(false);
		dateChooser_1.setEnabled(false);

		// Add ActionListener to enable/disable date choosers based on selected date range
		dateRangeComboBox.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String selectedRange = (String) dateRangeComboBox.getSelectedItem();
		        boolean enableDateChoosers = "Custom Range".equals(selectedRange);
		        
				chooseStart.setEnabled(enableDateChoosers);
				chooseEnd.setEnabled(enableDateChoosers);

		        // Enable or disable the date choosers based on selection
		        dateChooser.setEnabled(enableDateChoosers);
		        dateChooser_1.setEnabled(enableDateChoosers);
		    }
		});

	
		// Now add a DisplayChart button in the 6th row, 1st column by adding empty placeholders if necessary
	    chartOptionsPanel.add(new JLabel("")); // empty placeholders to keep btnDisplayChart in the 6th row
	    chartOptionsPanel.add(new JLabel("")); // empty placeholders to keep btnDisplayChart in the 6th row
	    chartOptionsPanel.add(new JLabel("")); // empty placeholders to keep btnDisplayChart in the 6th row
			
	    JButton btnDisplayChart = new JButton("Re-Draw Chart");
		chartOptionsPanel.add(btnDisplayChart); // 6th row, 1st column


		//  Lower Panel is for Portfolio display
		JPanel portfolioPanel = new JPanel();
		selectionsPanel.add(portfolioPanel);
		portfolioPanel.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton_1 = new JButton("Find Portfolio Value");
		portfolioPanel.add(btnNewButton_1, BorderLayout.NORTH);

		// Retrieve stocks from PortfolioManager
		stocks = PortfolioManager.readPortfolioFromFile().getStocks();

		// Define columns for the portfolio table
		String[] columns = {"Symbol", "Price", "# Shares", "Market Value"};

		// Initialize the portfolioTable and set its initial model
		portfolioTable = new JTable(new DefaultTableModel(columns, 0));
		portfolioPanel.add(portfolioTable, BorderLayout.CENTER); // Add to the portfolioPanel first

		// Define the table model and add stock data
		DefaultTableModel tableModel = (DefaultTableModel) portfolioTable.getModel();
		tableModel.addRow(new Object[] {"Symbol", "Price", "# Shares", "Market Value"}); // Header row
		for (Stock stock : stocks.values()) {
		    Object[] rowData = {
		        stock.getStockSymbol(),
		        stock.getClosingPrice(),
		        stock.getShares(),
		        String.format("$%,.2f", stock.getMarketValue()),
		    };
		    tableModel.addRow(rowData);
		}

		// Add a final row for total portfolio value
		Object[] totalRow = {null, null,  "Total: ",  
				 String.format("$%,.2f", calculateTotalValue(stocks))};
		tableModel.addRow(totalRow);
		
		portfolioPanel.add(portfolioTable, BorderLayout.CENTER);

		//************This completes the left-hand portion of the frame.************
		
		// Panel "chartPanel" in the second column of the JFrame
        stockSymbol = (String) symbolComboBox.getSelectedItem();
        System.out.println("abt to make chart " + stockSymbol);
		AlphaVantageCloseChart myAVCloseChart = new AlphaVantageCloseChart ("test chart", stockSymbol);
		JFreeChart myChart = myAVCloseChart.getResultChart();

		// Place the chart into a JPanel
		JPanel graphAreaPanel = new JPanel(new BorderLayout());
		ChartPanel chartContainer = new ChartPanel(myChart);
		graphAreaPanel.add(chartContainer, BorderLayout.CENTER);

		JPanel myChartPanel = new JPanel();
		myChartPanel.setLayout(new GridBagLayout());  // GridBagLayout to control the proportions
		GridBagConstraints gbc = new GridBagConstraints();

		// Panel "graphAreaPanel" takes 80% of the height
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;  // Full width
		gbc.weighty = 0.85;  // 80% of the height
		gbc.fill = GridBagConstraints.BOTH;  // Fill both directions
	
		btnDisplayChart.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // Update the stock symbol based on the selected item
		        stockSymbol = (String) symbolComboBox.getSelectedItem();
		        System.out.println("New Selected stock symbol: " + stockSymbol);

		        // Remove the previous chart if there is one
	        	graphAreaPanel.removeAll();

		        // Create a new AlphaVantageCloseChart instance with the updated stockSymbol
		        AlphaVantageCloseChart myAVCloseChart = null;
		        try {
		            myAVCloseChart = new AlphaVantageCloseChart("Updated Chart for " + stockSymbol, stockSymbol);
		        } catch (JsonMappingException e1) {
		            e1.printStackTrace();
		        } catch (JsonProcessingException e1) {
		            e1.printStackTrace();
		        }

		        // Get the new chart and display it
		        JFreeChart myChart = myAVCloseChart.getResultChart();
		        ChartPanel chartContainer = new ChartPanel(myChart);

		        // Add the new chart to the graphAreaPanel
		        graphAreaPanel.add(chartContainer, BorderLayout.CENTER);

		        // Refresh the graphAreaPanel to show the new chart
		        graphAreaPanel.revalidate();
		        graphAreaPanel.repaint();
		    }
		});
		
		myChartPanel.add(graphAreaPanel, gbc);

		// Panel "pricesPanel" takes 20% of the height
		gbc.gridy = 1;
		gbc.weighty = 0.15;  // 20% of the height
		JPanel pricesPanel = new JPanel();
		pricesPanel.setBackground(Color.cyan);  // Panel "pricesPanel" color

		pricesPanel.setLayout(new GridLayout(3, 2, 0, 0));

		JLabel lblNewLabel_1 = new JLabel("Currently:  ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_1);

		double todaysClose = myAVCloseChart.getLastPrice();
		JLabel lblCurrentPrice = new JLabel(String.valueOf(todaysClose));
		pricesPanel.add(lblCurrentPrice);

		JLabel lblNewLabel_4 = new JLabel("% Gain Today:  ");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_4);

		//find yesterday's closing price
		//compute yesterday's date
		//form request
		//call api
		//extract yesterday's closing price
		double yesterdaysClose = myAVCloseChart.getLastPrice() * 0.90;

		JLabel lblGainToday = new JLabel(
				String.format("%.2f",
						(todaysClose - yesterdaysClose)	/ todaysClose*100) + "%");
		pricesPanel.add(lblGainToday);

		JLabel lblNewLabel_3 = new JLabel("% Gain On Chart:  ");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_3);

		JLabel lblGainOnChart = new JLabel(
				String.format("%.2f",
						(todaysClose - myAVCloseChart.getFirstPrice())
						/ todaysClose*100) + "%");
		pricesPanel.add(lblGainOnChart);

		myChartPanel.add(pricesPanel, gbc);

		// Add panel "chartPanel" to the second column of the JFrame
		projectPane.add(myChartPanel);
	}
	
	private Double calculateTotalValue(TreeMap<String, Stock> stocks) {
	    double total = 0.0;
	    for (Stock stock : stocks.values()) {
	        total += stock.getMarketValue();
	    }
	    return total;
	}


}
