import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class TestRunner extends SwingWorker<Object, Integer>{
	private int counter;
	private JTextField sampLabel;
	private boolean paused;
	private boolean terminated;
	private JButton startButton, stopButton, settingsButton;
	private  final LineRenderer lines = new DefaultLineRenderer2D();
	private  final AreaRenderer area  = new DefaultAreaRenderer2D();
	private  final Color invis = new Color(0.0f, 0.3f, 1.0f, 0);
	private  final Color lineColor = new Color(0.0f, 0.3f, 1.0f);
	private  final Color areaColor = new Color(0.0f, 0.3f, 1.0f, 0.3f);
	private  DataTable densityTable = null;
	private  XYPlot dataPlot = null;
	private  InteractivePanel dataPanel = null;
	private  double maxHeight = 0.0;
	private  int MAX_WIDTH = 0;
	private  int MAX_HEIGHT = 0;
	private  JTextArea debug_area;
	
	
	public TestRunner ( JTextField smpLabel, int plotWidth, int plotHeight, JButton startButton, JButton stopButton, JButton settingsButton, XYPlot dtPlot, InteractivePanel dtPanel  )
	{
		counter              = 0;
		sampLabel            = smpLabel;
		paused               = false;
		terminated           = false;
		this.startButton     = startButton;
		this.stopButton      = stopButton;
		this.settingsButton  = settingsButton;
		dataPlot             = dtPlot;
		dataPanel			 = dtPanel;
		MAX_WIDTH            = plotWidth;
		MAX_HEIGHT           = plotHeight;
		
		// Initialize the density table
		densityTable = new DataTable(2, Double.class);
		for(double x = Settings.getMinimumRange(); x <= Settings.getMaximumRange(); x += Settings.discretization)
		{
			double y = 0.0;
			densityTable.add(x, y);
		}
		
	}
	
	public Object doInBackground(){
		// Initialize the algorithm variables
		try { Wavelet.init(Settings.waveletType); } 
		catch (IOException e) {	e.printStackTrace();}
    	DensityHelper.initializeTranslates();
    	DensityHelper.initializeCoefficients();
    	int sampInd = 1;
    	
		BufferedReader dataReader;
		try
		{
			dataReader = new BufferedReader( new FileReader(Settings.dataFile) ); 
			while( dataReader.ready() && !isCancelled() )
			{
				while( paused )
				{
					if( terminated ) 
						{
							dataReader.close();
							return null;
						}
					try
					{
						Thread.sleep(500);
					}catch(InterruptedException ex){}
				}
				double xNew = Double.parseDouble(dataReader.readLine());
				DensityHelper.updateCoefficients(xNew);
				
				if(sampInd % Settings.updateFrequency == 0)
				{
					DensityHelper.updateDensity(densityTable);
					
					//DataTable newDT = published.get( published.size() - 1 );
					// DataTable newDT = densityTable;
					dataPlot.clear();
					dataPlot.add(densityTable);
					dataPlot.setLineRenderer(densityTable, lines);
			        dataPlot.setAreaRenderer(densityTable, area);
			        dataPlot.setInsets(new Insets2D.Double(5,50,40,40));
			        dataPlot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
			        dataPlot.getPointRenderer(densityTable).setColor(invis);
			        dataPlot.getLineRenderer(densityTable).setColor(lineColor);
			        dataPlot.getAreaRenderer(densityTable).setColor(areaColor);
			        
					// Fix the maximum height of the axis
					Axis yAx = dataPlot.getAxis("y");
					double curYMax = (Double) yAx.getMax();
					if (curYMax > maxHeight) {
						maxHeight = curYMax;
					}
					else {
						Axis nYAx = new Axis();
						nYAx.setMax(maxHeight);
						nYAx.setMin(yAx.getMin());
						dataPlot.setAxis("y",  nYAx);
					}
					
					publish(sampInd);
				}
				
				
				
				try{
					Thread.sleep(1);
				}catch( InterruptedException ex){}
				
				sampInd++;
			}
			dataReader.close();
		} catch(Exception ex){}
		
		
		
		
		return null;
	}
	
	protected void done(){
		try
		{
			// sampLabel.setText( "Sample index" );
			if( startButton != null )
			{
				startButton.setEnabled(true);
				settingsButton.setEnabled(true);
				stopButton.setEnabled(false);
				if(stopButton.getText() == "Resume") stopButton.setText("Stop");
				debug_area.setText("");
			}
		}
		catch(Exception ex){}
		
	}
	
	protected void process( List<Integer> published ){
		
		Integer sampIndex = published.get( published.size() - 1 );
		sampLabel.setText( sampIndex.toString());
		
		
		// dataPanel.repaint();
		dataPanel.paintImmediately(0,0, MAX_WIDTH, MAX_HEIGHT);
	}
	
	public void pause()
	{
		paused = true;
	}
	
	public synchronized void resume(){
		paused = false;
		notify();
	}
	
	public void terminate()
	{
		terminated = true;
	}
	
}
