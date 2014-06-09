/**
 * Class to both display the current density estimate,
 * and allow the user to select various parameters.
 * 
 * @author Daniel Weinand & Gedeon Nyengele
 * 
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class EstimatorGUI extends JApplet implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	// The option buttons
	private JButton startButton;
	private JButton stopButton;
	private JButton settingsButton;
	private JButton resetButton;
	private JPanel optionsPanel;
	private JTextField sampleLabel;
	
	// The settings panel
	SettingsUI SettingsFrame;	
	
	private XYPlot dataPlot;						 // The plot of the density
	private InteractivePanel dataPanel;				 // The panel storing the density plot
	private DataTable densityTable;                  // The density information
	
	// The window size
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGHT = 600;
	
	private double maxYHeight = 0.0;               // The maximum y height reached
	
	private BufferedReader dataReader;               // The reader for the user file
	
	private TestRunner runner;
	
	
	
	/**
	 * Create the applet frame
	 */
	public void init() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		
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
		
    	JPanel GUI = new JPanel();
    	GUI.setLayout(new BorderLayout());
    	
    	// Set up the options panel
    	optionsPanel = new JPanel();
    	optionsPanel.setAlignmentX(0);
    	optionsPanel.setAlignmentY(0);
    	
    	// Create and add the buttons to the panel
    	Dimension btnSize = new Dimension(85, 25);
    	startButton = new JButton ("Start");
    	startButton.addActionListener(this);
    	startButton.setPreferredSize(btnSize);
    	stopButton = new JButton("Stop");
    	stopButton.setEnabled(false);
    	stopButton.setPreferredSize(btnSize);
    	stopButton.addActionListener(this);    	
    	settingsButton = new JButton ("Settings");
    	settingsButton.setPreferredSize(btnSize);
    	settingsButton.addActionListener(this);
    	resetButton = new JButton("Reset");
    	resetButton.addActionListener(this);
    	resetButton.setPreferredSize(btnSize);
    	
    	sampleLabel = new JTextField();
    	sampleLabel.setText("Sample index ");
    	sampleLabel.setEditable(false);
    	sampleLabel.setPreferredSize(new Dimension(150, 25) );
    	sampleLabel.setHorizontalAlignment(JTextField.CENTER);
    	optionsPanel.add(sampleLabel);
    	optionsPanel.add(startButton);
    	optionsPanel.add(stopButton);
    	optionsPanel.add(resetButton);
    	optionsPanel.add(settingsButton);
    	GUI.add(optionsPanel, BorderLayout.NORTH);
        
        // Create the plot for the data
        dataPlot = new XYPlot();
        initializePlot();
        dataPanel = new InteractivePanel(dataPlot);
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
			
			startButton.setEnabled(false);
			settingsButton.setEnabled(false);
			stopButton.setEnabled(true);
			startDensityEstimation();
	        
		} // End start button
		
		else if( e.getSource() == stopButton ){
			if( e.getActionCommand() == "Stop" )
			{
				stopButton.setText("Resume");
				runner.pause();
			}
			
			else
			{
				stopButton.setText("Stop");
				runner.resume();
			}
		}
		
		
		else if (e.getSource() == settingsButton) {
			SettingsFrame.setVisible(true);
		}
		
		else if(e.getSource() == resetButton)
		{
			runner.terminate();
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
    	initializePlot();
		
		// How many samples have been read in
		int sampInd = 1;
		
		while (dataReader.ready()) {

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
				sampleLabel.setText("" + sampInd);
				
			}
	        sampInd++;
		}
		
	}// End online density estimation

	// Properly initializes the plot of the PDF
	private void initializePlot() {
		
		if (dataPlot.getData().size() > 0) {
			dataPlot.remove(densityTable);
		}
		
    	densityTable = new DataTable(2, Double.class);
        for (double x = Settings.getMinimumRange(); 
        		x <= Settings.getMaximumRange() + Settings.discretization;
        		x += Settings.discretization) {
            double y = 0.0;
            densityTable.add(x, y);
        }
        DensityHelper.updateDensity(densityTable);
        dataPlot.add(densityTable);
        
        LineRenderer lines = new DefaultLineRenderer2D();
        AreaRenderer area = new DefaultAreaRenderer2D();
        dataPlot.setLineRenderer(densityTable, lines);
        dataPlot.setAreaRenderer(densityTable, area);
        dataPlot.setInsets(new Insets2D.Double(5,50,40,40));
        dataPlot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
        Color invis = new Color(0.0f, 0.3f, 1.0f, 0);
        Color lineColor = new Color(0.0f, 0.3f, 1.0f);
        Color areaColor = new Color(0.0f, 0.3f, 1.0f, 0.3f);
        dataPlot.getPointRenderer(densityTable).setColor(invis);
        dataPlot.getLineRenderer(densityTable).setColor(lineColor);
        dataPlot.getAreaRenderer(densityTable).setColor(areaColor);
		
	}
	
//	private void startDensityEstimation()
//	{
//		// Create the reader				
//		try { 
//			dataReader = new BufferedReader( new FileReader(Settings.dataFile) );
//		} 
//		catch (FileNotFoundException e1) {
//			// NOTE: should eventually replace with kind warning text
//			// for users
//			e1.printStackTrace();
//		}
//		
//		
//		// Begin the density estimation process
//		try {
//			onlineEstimation();
//		} catch (NumberFormatException | IOException e1) {
//			// NOTE: should eventually replace with kind warning text
//			// for users
//			e1.printStackTrace();
//		}			
//	}
	
	private void startDensityEstimation(){
		
		runner = new TestRunner(sampleLabel, this.getWidth(), this.getHeight(), startButton, stopButton, settingsButton, dataPlot, dataPanel);
		runner.execute();
		
		
	}
}
