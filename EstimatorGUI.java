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

public class EstimatorGUI extends JApplet {
	
	/**
	 * Create the applet frame
	 */
	public void init() {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	initializeGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
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
    	JButton startButton = new JButton ("Start");
    	JButton stopButton = new JButton ("Stop");
    	JButton resetButton = new JButton ("Reset");
    	JButton settingsButton = new JButton ("Settings");
    	optionsPanel.add(startButton);
    	optionsPanel.add(stopButton);
    	optionsPanel.add(resetButton);
    	optionsPanel.add(settingsButton);
    	GUI.add(optionsPanel, BorderLayout.NORTH);
    	
    	// Add the plot to the frame
    	DataTable data = new DataTable(Double.class, Double.class);
        for (double x = -0.5; x <= 4.5; x+=0.1) {
            double y = Math.max((5.0*Math.sin(x)+Math.random()*.5)/6.0,0.0);
            data.add(x, y);
        }
        XYPlot dataPlot = new XYPlot(data);
        Axis newAxis = dataPlot.getAxis("y");
        newAxis.setMin(-.1);
        dataPlot.setAxis("y", newAxis);
        InteractivePanel dataPanel = new InteractivePanel(dataPlot);        
        LineRenderer lines = new DefaultLineRenderer2D();
        dataPlot.setLineRenderer(data, lines);
        Color color = new Color(0.0f, 0.3f, 1.0f, 0);
        dataPlot.getPointRenderer(data).setColor(color);
        GUI.add(dataPanel, BorderLayout.CENTER);
        
        // Add the frame to the display
        add(GUI);
	}
}
