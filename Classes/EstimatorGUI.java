/**
 * Class to both display the current density estimate,
 * and allow the user to select various parameters.
 * 
 * @author Daniel Weinand & Gedeon Nyengele
 * 
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

public class EstimatorGUI extends JApplet implements ActionListener {
	
	
	// The option buttons
	private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
	private JButton settingsButton;
	
	private DataTable densityTable;  // The density information
	private DensityBuffer densBuf;   // The buffer for the user data
	private boolean stopped;         // The user has selected the stop button
	private static final int MAX_SAMPLES = 10000000; // Maximum samples which can be read
	
	/**
	 * Create the applet frame
	 */
	public void init() {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	
                	// Initialize algorithm variables and GUI
                	try {
						Wavelet.init("db2");
					} catch (IOException e) {
						e.printStackTrace();
					}
                	DensityHelper.initializeTranslates();
                	DensityHelper.initializeCoefficients();
                	initializeGUI();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Creates the basic GUI components,
	 * the buttons and the data plot
	 */
	public void initializeGUI() {
		setSize(500,600);
    	JPanel GUI = new JPanel();
    	GUI.setLayout(new BorderLayout());
    	
    	// Set up the options panel
    	JPanel optionsPanel = new JPanel();
    	optionsPanel.setAlignmentX(0);
    	optionsPanel.setAlignmentY(0);
    	
    	// Create and add the windows to the panel
    	startButton = new JButton ("Start");
    	startButton.addActionListener(this);
    	stopButton = new JButton ("Stop");
    	stopButton.addActionListener(this);
    	resetButton = new JButton ("Reset");
    	settingsButton = new JButton ("Settings");
    	optionsPanel.add(startButton);
    	optionsPanel.add(stopButton);
    	optionsPanel.add(resetButton);
    	optionsPanel.add(settingsButton);
    	GUI.add(optionsPanel, BorderLayout.NORTH);
    	
    	// Create the dataTable to store density
    	densityTable = new DataTable(Double.class, Double.class);
        for (double x = Settings.getMinimumRange(); 
        		x <= Settings.getMaximumRange() + Settings.discretization;
        		x += Settings.discretization) {
            double y = 0.0;
            densityTable.add(x, y);
        }
        DensityHelper.updateDensity(densityTable);
        
        // Create the plot for the data
        XYPlot dataPlot = new XYPlot(densityTable);
        //Axis newAxis = dataPlot.getAxis("y");
        //newAxis.setMin(-.1);
        //dataPlot.setAxis("y", newAxis);
        InteractivePanel dataPanel = new InteractivePanel(dataPlot);        
        LineRenderer lines = new DefaultLineRenderer2D();
        dataPlot.setLineRenderer(densityTable, lines);
        //Color color = new Color(0.0f, 0.3f, 1.0f, 0);
        //dataPlot.getPointRenderer(densityTable).setColor(color);
        GUI.add(dataPanel, BorderLayout.CENTER);
        
        // Add the frame to the display
        add(GUI);
	}

	/**
	 * User clicked a GUI button
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton) {
			stopped = false;
			onlineEstimation();
	        
		}
		
		if (e.getSource() == stopButton) {
			stopped = true;
		}
		
	}
	
	/**
	 * Runs the density estimation algorithm
	 * and plots the estimates at intervals
	 */
	public void onlineEstimation() {
		
		// How many samples have been read in
		int sampInd = 0;
		while (densBuf.hasNext() && !stopped && sampInd < MAX_SAMPLES) {

			double Xnew = densBuf.getNext();
			double NOT_YET_USED = -10000.0;
			DensityHelper.updateCoefficients(Xnew, NOT_YET_USED);
			
			// Update plot if the appropriate number of samples have been read
			if (sampInd % Settings.updateFrequency == 0) {
				DensityHelper.updateDensity(densityTable);
				this.repaint();
				
			}
	        sampInd++;
		}
	}
}
