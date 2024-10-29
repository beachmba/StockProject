package jdstockmarket;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;

/**
 * The {@code InstructionDialog} class extends {@link JDialog} to provide a modal dialog window displaying instructions for the JD Stock Market application.
 *
 * Key Features:
 * - Displays a text area containing step-by-step instructions and tips for using the application.
 * - Includes details on how to fetch stock information, add stocks to a portfolio, and handle API limitations.
 * - Offers guidance on special features, like the 'Live Portfolio' button and information about Congressional stock data.
 *
 * Usage:
 * This dialog is intended to be shown to the user upon starting the application, offering initial guidance on how to interact with the application's features.
 * The dialog is modal, meaning it blocks input to other windows in the application while it is open.
 *
 * Layout and Design:
 * - The dialog contains a non-editable text area with wrapped text for better readability.
 * - A scroll pane is added to the text area to accommodate longer instruction texts.
 * - The dialog's size and layout are set to enhance readability and user experience.
 *
 * Note: The dialog is set to be non-resizable and is positioned relative to its parent frame (typically the main application window).
 * 
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see JDialog
 * @see JTextArea
 * @see JScrollPane
 */
public class InstructionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	public InstructionDialog(JFrame parentFrame) {
        super(parentFrame, "Instructions", true); // 'true' for modal dialog
        initComponents();
    }

    private void initComponents() {
        // Set the size and layout of the dialog
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Create a text area for instructions
        JTextArea instructionText = new JTextArea();
        instructionText.setText("Instructions and tips:\n\n" +
                                "1. Enter a known stock ticker to fetch information.\n" +
                                "2. Add stocks you like to your portfolio with 'Add Stock'.\n" +
                                "3. As of currently, this applicaiton has a limit of 25 API stock calls\n    per 24 hour period. This limitation does not extend to\n    congressinal stock data in the bottom display\n" +
                                "4. The 'Live Portfolio' button updates current stock data for all\n    stocks in your portolio, if turned on when the 'Display Portfolio'\n    button is clicked\n" +
                                "5. Nancy Pelosi ? Try AAPL" +
                                "\n");
        instructionText.setEditable(false);
        instructionText.setWrapStyleWord(true);
        instructionText.setLineWrap(true);
        instructionText.setCaret(new DefaultCaret() { @Override public boolean isVisible() { return false; }});

        // Add a scroll pane to the text area
        JScrollPane scrollPane = new JScrollPane(instructionText);
        add(scrollPane, BorderLayout.CENTER);

        // Prevent window from being resized
        setResizable(false);

        // Set the dialog location relative to the parent frame
        setLocationRelativeTo(getParent());
    }
}

