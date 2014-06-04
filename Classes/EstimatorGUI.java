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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
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
	
	// The settings panel
	SettingsUI SettingsFrame;	
	
	private XYPlot dataPlot;						 // The plot of the density
	private InteractivePanel dataPanel;				 // The panel storing the density plot
	private DataTable densityTable;                  // The density information
	private boolean stopped;                         // The user has selected the stop button
	
	// The window size
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 600;
	
	private double maxYHeight = 0.0;               // The maximum y height reached
	
	private BufferedReader dataReader;               // The reader for the user file
	
	/**
	 * Create the applet frame
	 */
	public void init() {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	
                	// Initialize algorithm variables and GUI
                	try { Wavelet.init(Settings.waveletType); } 
            		catch (IOException e) {	e.printStackTrace();}
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
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
    	resetButton.addActionListener(this);
    	settingsButton = new JButton ("Settings");
    	settingsButton.addActionListener(this);
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
        dataPlot = new XYPlot(densityTable);
        dataPanel = new InteractivePanel(dataPlot);        
        LineRenderer lines = new DefaultLineRenderer2D();
        AreaRenderer area = new DefaultAreaRenderer2D();
        dataPlot.setLineRenderer(densityTable, lines);
        dataPlot.setAreaRenderer(densityTable, area);
        Color invis = new Color(0.0f, 0.3f, 1.0f, 0);
        Color lineColor = new Color(0.0f, 0.3f, 1.0f);
        Color areaColor = new Color(0.0f, 0.3f, 1.0f, 0.3f);
        dataPlot.getPointRenderer(densityTable).setColor(invis);
        dataPlot.getLineRenderer(densityTable).setColor(lineColor);
        dataPlot.getAreaRenderer(densityTable).setColor(areaColor);
        GUI.add(dataPanel, BorderLayout.CENTER);
        
        // Create the settings UI
        SettingsFrame = new SettingsUI();
        SettingsFrame.setVisible(false);
        
        // Add the frame to the display
        add(GUI);
	}

	/**
	 * User clicked a GUI button
	 */
	public void actionPerformed(ActionEvent e) {
		
		// User selects start button
		if (e.getSource() == startButton) {
			
			// Do nothing if already running
			if (stopped = false) {
				return;
			}
			stopped = false;
			
			// Create the reader				
			try { 
				dataReader = new BufferedReader( new FileReader(Settings.dataFile) );
			} 
			catch (FileNotFoundException e1) {
				// NOTE: should eventually replace with kind warning text
				// for users
				e1.printStackTrace();
			}
			
			
			// Begin the density estimation process
			try {
				onlineEstimation();
			} catch (NumberFormatException | IOException e1) {
				// NOTE: should eventually replace with kind warning text
				// for users
				e1.printStackTrace();
			}
	        
		} // End start button
		
		if (e.getSource() == stopButton) {
			stopped = true;
		}
		
		if (e.getSource() == settingsButton) {
			SettingsFrame.setVisible(true);
		}
		
	}
	
	/**
	 * Runs the density estimation algorithm
	 * and plots the estimates at intervals
	 * @throws IOException When reading from an empty file
	 * @throws NumberFormatException When file is incorrectly formatted
	 */
	public void onlineEstimation() throws NumberFormatException, IOException {
		
		// Initialize the algorithm variables
		try { Wavelet.init(Settings.waveletType); } 
		catch (IOException e) {	e.printStackTrace();}
    	DensityHelper.initializeTranslates();
    	DensityHelper.initializeCoefficients();
		
		// How many samples have been read in
		int sampInd = 0;
		
		while (dataReader.ready() && !stopped) {

			double Xnew = Double.parseDouble(dataReader.readLine());
			DensityHelper.updateCoefficients(Xnew);
			
			// Update plot if the appropriate number of samples have been read
			if (sampInd % Settings.updateFrequency == 0) {
				
				DensityHelper.updateDensity(densityTable);
				
				// Fix the maximum height of the axis
				Axis yAx = dataPlot.getAxis("y");
				double curYMax = (Double) yAx.getMax();
				if (curYMax > maxYHeight) {
					maxYHeight = curYMax;
				}
				else {
					Axis nYAx = new Axis();
					nYAx.setMax(maxYHeight);
					nYAx.setMin(yAx.getMin());
					dataPlot.setAxis("y",  nYAx);
				}
				
				dataPanel.paintImmediately(0,0, this.getWidth(), this.getHeight());
				
			}
	        sampInd++;
		}
		
		stopped = true;
	}// End online density estimation
	
}
