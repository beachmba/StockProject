package jdstockmarket;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
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
	private JTable table;
	private String stockSymbol = "AVGO"; // Default Stock Symbol
	//private String stockSymbol = null; // Default Stock Symbol

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockDataFrame frame = new StockDataFrame(); 
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public StockDataFrame() throws JsonMappingException, JsonProcessingException {

		this.setTitle("Stock Graph and Portfolio Display Program - Michael Louis ");
		this.setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 10, 860, 662);
		//setBounds(defaultCloseOperation, defaultCloseOperation, defaultCloseOperation, defaultCloseOperation)
		projectPane = new JPanel();
		projectPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(projectPane);
		// Left and Right
		projectPane.setLayout(new GridLayout(1, 2, 0, 0));

		JPanel selectionsPanel = new JPanel();
		projectPane.add(selectionsPanel);

		//Upper = Symbol/Date   Lower = Portfolio List
		selectionsPanel.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel chartOptionsPanel = new JPanel();
		chartOptionsPanel.setBorder(new LineBorder(new Color(0, 64, 128)));
		chartOptionsPanel.setLayout(new GridLayout(7, 2, 0, 0));

		selectionsPanel.add(chartOptionsPanel);



		//Jlabel w multiple lines 
		//new JLabel("<html>This is line 
		//    one.<br>This is line two.<br>This is line three.</html>");
		//	JLabel emptyLabel = new JLabel();
		//	chartOptionsPanel.add(emptyLabel);

		JLabel lblSymbol = new JLabel();
		lblSymbol.setText("Symbol");
		chartOptionsPanel.add(lblSymbol);
		//lblSymbol.setColumns(10);

		String[] symbolList = { "AVGO", "GOOG", "MSFT", "URI", "QQQ", "AAPL"};
		JComboBox<String> symbolComboBox = new JComboBox<>(symbolList);
		// Allow the combo box to be editable so users can type their own input
		symbolComboBox.setEditable(true);

		chartOptionsPanel.add(symbolComboBox);

		// Add ActionListener to get the selected item from the JComboBox
		//		symbolComboBox.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				// Get the selected item from the JComboBox
		//				stockSymbol = (String) symbolComboBox.getSelectedItem();
		//
		//			}
		//		});

		JLabel lblDateRange = new JLabel("Date Range");
		chartOptionsPanel.add(lblDateRange);

		String[] dateRangeList = {"1 Day", "5 Days", "1 Month",
				"Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		JComboBox<String> dateRangeComboBox = new JComboBox<>(dateRangeList);
		// Allow the combo box to be editable so users can type their own input
		dateRangeComboBox.setEditable(true);

		chartOptionsPanel.add(dateRangeComboBox);

		JLabel beginDateTextField = new JLabel();
		beginDateTextField.setText("Choose Start Date");
		chartOptionsPanel.add(beginDateTextField);

		JLabel endDateTextField = new JLabel();
		endDateTextField.setText("Choose End Date");
		chartOptionsPanel.add(endDateTextField);

		JDateChooser dateChooser = new JDateChooser();
		chartOptionsPanel.add(dateChooser);

		JDateChooser dateChooser_1 = new JDateChooser();
		chartOptionsPanel.add(dateChooser_1);

		JLabel emptyLabel1 = new JLabel(" ");
		chartOptionsPanel.add(emptyLabel1);
		JLabel emptyLabel2 = new JLabel(" ");
		chartOptionsPanel.add(emptyLabel2);

		JButton btnDisplayChart = new JButton("Display Chart");
		btnDisplayChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				stockSymbol = (String) symbolComboBox.getSelectedItem();

			}
		});
		chartOptionsPanel.add(btnDisplayChart);

		//		JLabel emptyLabel1 = new JLabel();
		//		chartOptionsPanel.add(emptyLabel1);
		//		chartOptionsPanel.add(emptyLabel1);

		//  Lower Panel is for Portfolio diplay
		JPanel portfolioPanel = new JPanel();
		selectionsPanel.add(portfolioPanel);
		portfolioPanel.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton_1 = new JButton("Find Portfolio Value");
		portfolioPanel.add(btnNewButton_1, BorderLayout.NORTH);

		table = new JTable();
		table.setModel(new DefaultTableModel(
				new Object[][] {
					{"Symbol", "Price", "# Shares", "Market Value"},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, null, null, null},
					{null, "Total Porfolio", "Value:", null},
				},
				new String[] {
						"New column", "New column","New column", "New column"
				}
				));
		portfolioPanel.add(table, BorderLayout.CENTER);

		//		String stockSymbol = "AVGO";  // Default Stock to use
		//		String stockSymbol = symbolComboBox.get"AVGO";  // Default Stock to use

		// comment next line to use default Stock Symbol
		//stockSymbol = getSymbolFromConsole(stockSymbol);  //arg is default stock symbol

		// Panel "chartPanel" in the second column of the JFrame
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
		//		JPanel graphAreaPanel = new JPanel();
		//	graphAreaPanel.setBackground(Color.RED);  // Panel "graphAreaPanel" color
		//graphAreaPanel.add(new JLabel("Graph Area Panel - 80% height"));
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

}
