package jdstockmarket;

import java.awt.*;
import java.awt.event.ActionListener;
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

	private static final long serialVersionUID = 1L;
	private JPanel projectPane;
	private JTable portfolioTable;
	private String stockSymbol = null;
	protected Container graphAreaPanel;
	private JComboBox<String> symbolComboBox;
	private AlphaVantageCloseChart myAVCloseChart;

	public StockDataFrame() throws JsonMappingException, JsonProcessingException {

		this.setTitle("Stock Graph and Portfolio Display Program - Michael Louis ");
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 5, 630, 531);

		projectPane = new JPanel();
		projectPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(projectPane);
		projectPane.setLayout(new GridLayout(1, 2, 0, 0)); 

		JPanel leftPanel = new JPanel();
		projectPane.add(leftPanel);
		leftPanel.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel chartOptionsPanel = new JPanel();
		chartOptionsPanel.setBorder(new LineBorder(new Color(0, 64, 128)));
		chartOptionsPanel.setLayout(new GridLayout(8, 2, 0, 0));
		leftPanel.add(chartOptionsPanel);

		chartOptionsPanel.add(new JLabel("<html>Choose A Stock Symbol<br>And A Date Range</html>"));
		chartOptionsPanel.add(new JLabel(""));
		chartOptionsPanel.add(new JLabel("Symbol: "));

		TreeMap<String, Stock> stocks = PortfolioManager.readPortfolioFromFile().getStocks();
		symbolComboBox = new JComboBox<>();
		for (Stock stock : stocks.values()) {
			symbolComboBox.addItem(stock.getStockSymbol());
		}
		symbolComboBox.setEditable(true);
		chartOptionsPanel.add(symbolComboBox);

		chartOptionsPanel.add(new JLabel("Date Range: "));
		String[] dateRangeList = {"1 Day", "5 Days", "1 Month", "Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		JComboBox<String> dateRangeComboBox = new JComboBox<>(dateRangeList);
		chartOptionsPanel.add(dateRangeComboBox);

		JLabel chooseStart = new JLabel("Choose Start Date");
		chartOptionsPanel.add(chooseStart);
		chooseStart.setEnabled(false);

		JLabel chooseEnd = new JLabel("Choose End Date");
		chartOptionsPanel.add(chooseEnd);
		chooseEnd.setEnabled(false);

		JDateChooser dateChooser = new JDateChooser();
		chartOptionsPanel.add(dateChooser);
		JDateChooser dateChooser_1 = new JDateChooser();
		chartOptionsPanel.add(dateChooser_1);

		dateChooser.setEnabled(false);
		dateChooser_1.setEnabled(false);

		dateRangeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedRange = (String) dateRangeComboBox.getSelectedItem();
				boolean enableDateChoosers = "Custom Range".equals(selectedRange);

				chooseStart.setEnabled(enableDateChoosers);
				chooseEnd.setEnabled(enableDateChoosers);
				dateChooser.setEnabled(enableDateChoosers);
				dateChooser_1.setEnabled(enableDateChoosers);
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

		JButton btnNewButton_1 = new JButton("Find Portfolio Value");
		portfolioPanel.add(btnNewButton_1, BorderLayout.NORTH);

		String[] columns = {"Symbol", "Price", "# Shares", "Market Value"};
		portfolioTable = new JTable(new DefaultTableModel(columns, 0));

		// Set row height and adjust column alignment/width
		portfolioTable.setRowHeight(14);
		portfolioTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		portfolioTable.getColumnModel().getColumn(3).setPreferredWidth((int) (portfolioTable.getPreferredSize().width * 0.4));

		// Right-align the last column
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		portfolioTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

		portfolioPanel.add(portfolioTable, BorderLayout.CENTER);

		DefaultTableModel tableModel = (DefaultTableModel) portfolioTable.getModel();
		tableModel.addRow(new Object[]{"Symbol", "Price", "# Shares", "Market Value"});

		for (Stock stock : stocks.values()) {
			Object[] rowData = {
					stock.getStockSymbol(),
					stock.getClosingPrice(),
					stock.getShares(),
					String.format("$%,.2f", stock.getMarketValue()),
			};
			tableModel.addRow(rowData);
		}

		Object[] totalRow = {null, null, "Total: ", String.format("$%,.2f", calculateTotalValue(stocks))};
		tableModel.addRow(totalRow);

		portfolioPanel.add(portfolioTable, BorderLayout.CENTER);

		// ******** This completes the construction of the left panel.    

		//make a right hand panel for the pane
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.85;
		gbc.fill = GridBagConstraints.BOTH;

		//create a panel just for the graph
		graphAreaPanel = new JPanel(new BorderLayout());
		//create and put up a chart
		myAVCloseChart = makeAndPutChart();

		//when the re-draw button is clicked, get the symbol, period, and replace the existing chart
		redrawChartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphAreaPanel.removeAll();
				//create and put up a chart
				myAVCloseChart = makeAndPutChart();
				graphAreaPanel.revalidate();
				graphAreaPanel.repaint();
			}
		});

		rightPanel.add(graphAreaPanel, gbc);

		gbc.gridy = 1;
		gbc.weighty = 0.15;
		JPanel pricesPanel = new JPanel();
		pricesPanel.setBackground(Color.cyan);
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

		double yesterdaysClose = myAVCloseChart.getLastPrice() * 0.90;

		JLabel lblGainToday = new JLabel(
				String.format("%.2f",
						(todaysClose - yesterdaysClose) / todaysClose * 100) + "%");
		pricesPanel.add(lblGainToday);

		JLabel lblNewLabel_3 = new JLabel("% Gain On Chart:  ");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		pricesPanel.add(lblNewLabel_3);

		JLabel lblGainOnChart = new JLabel(
				String.format("%.2f",
						(todaysClose - myAVCloseChart.getFirstPrice()) / todaysClose * 100) + "%");
		pricesPanel.add(lblGainOnChart);

		rightPanel.add(pricesPanel, gbc);

		projectPane.add(rightPanel);
	}

	private Double calculateTotalValue(TreeMap<String, Stock> stocks) {
		double total = 0.0;
		for (Stock stock : stocks.values()) {
			total += stock.getMarketValue();
		}
		return total;
	}

	public static void main(String[] args) {
		try {
			StockDataFrame frame = new StockDataFrame();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AlphaVantageCloseChart makeAndPutChart()
	{
		// Get the period and stock symbol, call the API, and make a chart
		stockSymbol = (String) symbolComboBox.getSelectedItem();
		// code to get the period from the combo box or datechoosers
		AlphaVantageCloseChart newAVChart = null;
		try {
			newAVChart = new AlphaVantageCloseChart("", stockSymbol);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		ChartPanel myChartPanel = new ChartPanel(newAVChart.getResultChart());
		//put the chart panel into the graph area (top part) of the right panel
		graphAreaPanel.add(myChartPanel, BorderLayout.CENTER);
		
		return newAVChart;
	}



}
