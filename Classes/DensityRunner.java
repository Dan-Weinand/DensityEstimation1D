/**
 * Thread that performs all the calculations and updates the plot.
 * @author Gedeon Nyengele & Daniel Weinand.
 */

import javax.swing.JTextField;
import javax.swing.SwingWorker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.plots.XYPlot;

public class DensityRunner extends SwingWorker<Object, Integer>{
	
	private XYPlot dataPlot;
	private InteractivePanel dataPanel;
	private BufferedReader dataReader;
	private final int plotWidth;
	private final int plotHeight;
	private boolean paused;
	private DataTable densityTable;
	private JTextField sampleLabel;
	
	
	// Constructor.
	public DensityRunner(int plotWidth, int plotHeight, XYPlot dataPlot, InteractivePanel dataPanel, JTextField sampleLabel) throws IOException{
		this.plotWidth        = plotWidth;
		this.plotHeight       = plotHeight;
		this.dataPlot		  = dataPlot;
		this.dataPanel   	  = dataPanel;
		this.dataReader		  = new BufferedReader( new FileReader(Settings.dataFile) );
		this.paused			  = false;
		this.sampleLabel      = sampleLabel;
	}
	
	public Object doInBackground(){
				
		// Variables used.
		int sampInd       = 0;
		double maxHeight  = 0.0;
		double xNew;
		
		
		// Initialize the estimator variables.
		try
		{
			Wavelet.init(Settings.waveletType);
		} // end try
		catch(Exception ex){}
		DensityHelper.initializeTranslates();
		DensityHelper.initializeCoefficients();
		
		
		try
		{ 
			// Load buffer.
			dataReader = new BufferedReader( new FileReader( Settings.dataFile ) ); 
			
			while( !isCancelled() && dataReader.ready() )
			{
				// Sleep if the user paused the DensityRunner.
				if( paused )
				{
					try{ synchronized(this) { this.wait(1000);} }
					catch( InterruptedException ex ) {}
				} // end if( paused ).
				
				// Perform calculations and update plot.
				else
				{
					try { Thread.sleep(100); }
					catch( InterruptedException ex){}
					
					xNew = Double.parseDouble( dataReader.readLine() );
					
					// Update coefficients.
					DensityHelper.updateCoefficients(xNew);
					
					if(sampInd % Settings.updateFrequency == 0)
					{
						DensityHelper.updateDensity(densityTable);
						
						Axis yAx = dataPlot.getAxis(XYPlot.AXIS_Y);
						double curYMax = (Double) yAx.getMax();
						
						// Update the maximum height of the axis.
						if( curYMax > maxHeight )
						{
							maxHeight = curYMax;
						}
						else
						{
							Axis nYAx = new Axis();
							nYAx.setMax( maxHeight );
							nYAx.setMin( yAx.getMin() );
							dataPlot.setAxis( XYPlot.AXIS_Y, nYAx );
						}
						
						
						publish(Integer.valueOf(sampInd));
					}
					
					sampInd++;
					
					
					
				} // end else from if( paused ).
				
			} // end while ( !isCancelled() ).	
		}
		catch( IOException ex ){}
		
			
		
		
		return null;
	} // end method doInBackground().
	
	protected void done(){
		
	} // end method done().
	
	protected void process( List< Integer > publishedResults){
		sampleLabel.setText( String.valueOf( publishedResults.get( publishedResults.size() - 1 ) ) );
		dataPanel.paintImmediately( 0, 0, plotWidth, plotHeight );
	} // end method process().
	
	/*
	 * Allows the GUI to pause the DensityRunner.
	 */
	public void pause(){
		this.paused = true;
	} // end method pause().
	
	/*
	 * Allows the GUI to resume the DensityRunner from where it paused.
	 */
	public synchronized void resume(){
		this.paused = false;
		this.notify();
	} // end method resume();
}
