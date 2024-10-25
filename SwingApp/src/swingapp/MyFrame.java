package swingapp;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
//import com.toedter.calendar.*;
//import com.toedter.calendar.JDateChooser;


public class MyFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel projectPane;
	private JTextField emptyTextField;
	private JTextField txtSymbol;
	private JTable table;
	private final JLabel lblNewLabel_2 = new JLabel("New label");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame(); 
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create theTitle("\"Stocks\"");
		set frame.
	 */
	public MyFrame() {
		this.setTitle("My Stock Program");
		// Set the location of the JFrame: (x, y) = (120, 20)
		//this.setLocation(120, 20);
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
		chartOptionsPanel.setLayout(new GridLayout(6, 2, 0, 0));

		selectionsPanel.add(chartOptionsPanel);

		JButton btnNewButton = new JButton("Display Chart");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		chartOptionsPanel.add(btnNewButton);

		JLabel emptyLabel = new JLabel();
		chartOptionsPanel.add(emptyLabel);

		JLabel lblSymbol = new JLabel();
		lblSymbol.setText("Symbol");
		chartOptionsPanel.add(lblSymbol);
		//lblSymbol.setColumns(10);

		String[] symbolList = {"GOOG", "AVGO", "MSFT", "URI", "QQQ", "AAPL"};
		JComboBox<String> symbolComboBox = new JComboBox<>(symbolList);
		// Allow the combo box to be editable so users can type their own input
		symbolComboBox.setEditable(true);

		chartOptionsPanel.add(symbolComboBox);

		JLabel lblDateRange = new JLabel("Date Range");
		chartOptionsPanel.add(lblDateRange);

		String[] dateRangeList = {"1 Day", "5 Days", "1 Month",
				"Year-To-Date", "1 Year", "5 Years", "Custom Range"};
		JComboBox<String> dateRangeComboBox = new JComboBox<>(dateRangeList);
		// Allow the combo box to be editable so users can type their own input
		dateRangeComboBox.setEditable(true);

		chartOptionsPanel.add(dateRangeComboBox);

		//JPanel dateChooserPanel = new JPanel();
		//dateChooserPanel.setLayout(new GridLayout(1,2,0,0));
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

		JLabel emptyLabel1 = new JLabel();
		chartOptionsPanel.add(emptyLabel1);
		//selectionsPanel.add(dateChooserPanel);

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


		// Panel "chartPanel" in the second column of the JFrame
		JPanel chartPanel = new JPanel();
		chartPanel.setLayout(new GridBagLayout());  // GridBagLayout to control the proportions
		GridBagConstraints gbc = new GridBagConstraints();

		// Panel "graphAreaPanel" takes 80% of the height
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;  // Full width
		gbc.weighty = 0.8;  // 80% of the height
		gbc.fill = GridBagConstraints.BOTH;  // Fill both directions
		JPanel graphAreaPanel = new JPanel();
		graphAreaPanel.setBackground(Color.RED);  // Panel "graphAreaPanel" color
		graphAreaPanel.add(new JLabel("Graph Area Panel - 80% height"));
		chartPanel.add(graphAreaPanel, gbc);

		// Panel "pricesPanel" takes 20% of the height
		gbc.gridy = 1;
		gbc.weighty = 0.2;  // 20% of the height
		JPanel pricesPanel = new JPanel();
		pricesPanel.setBackground(Color.BLUE);  // Panel "pricesPanel" color
		//pricesPanel.add(new JLabel("Prices Panel - 20% height"));


		//				JPanel pricesPanel = new JPanel();
		pricesPanel.setLayout(new GridLayout(3, 2, 0, 0));

		JLabel lblNewLabel_1 = new JLabel("Currently");
		pricesPanel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("current prices here");
		pricesPanel.add(lblNewLabel_2);

		JLabel lblNewLabel_4 = new JLabel("% Gain Today:");
		pricesPanel.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("<put % gain today here");
		pricesPanel.add(lblNewLabel_5);

		JLabel lblNewLabel_3 = new JLabel("% Gain On Chart:");
		pricesPanel.add(lblNewLabel_3);

		JLabel lblNewLabel_6 = new JLabel("put % gain on chart here");
		pricesPanel.add(lblNewLabel_6);

		//				chartPanel.add(pricesPanel);




		chartPanel.add(pricesPanel, gbc);

		// Add panel "chartPanel" to the second column of the JFrame


		projectPane.add(chartPanel);
	}

}
