import java.io.*;
/**
 * Buffers sample data from user-provided data file.
 * @author Gedeon Nyengele & Daniel Weinand
 *
 */
public class DensityBuffer {

	private static DensityBuffer instance = null;
	private  String fileName;
	private  int windowSize;
	private static double[] buffer;
	private int lastPushed = 0;  // index of the last sample pushed from the buffer array.
	private int numOfSamples;           // number of samples in the given file.
	private int numOfBuffering;
	
	/**
	 * Constructor
	 * @param filename  : filename for file containing data samples.
	 * @throws IOException 
	 */
	private DensityBuffer(String filename, int wndSize) throws IOException{
		this.fileName       = filename;
		this.windowSize     = wndSize;
		     buffer         = new double[this.windowSize];
		this.numOfSamples   = this.initBuffer(buffer);
		this.numOfBuffering = (int) Math.ceil( (double)this.numOfSamples / (double) this.windowSize );
	}
	
	/**
	 * Initiliazes the Buffer.
	 * @param filename  : filename for file containing data samples.
	 * @param wndSize   : size of the window used for the estimator ( found in Settings class )
	 * @return
	 * @throws IOException 
	 */
	public static DensityBuffer init( String filename, int wndSize ) throws IOException{
		if(DensityBuffer.instance == null)
		{
			DensityBuffer.instance = new DensityBuffer(filename, wndSize);
			return DensityBuffer.instance;
		}
		else
		{
			return DensityBuffer.instance;
		}
	}// end init method.
	
	/**
	 * returns the only instance of the Buffer class.
	 * @return buffer instance.
	 */
	public static DensityBuffer getInstance(){		
		return DensityBuffer.instance;
	}// end getInstance method.
	
	/**
	 * Returns the next sample.
	 * @return next sample.
	 */
	public double getNext(){
		
	}
	
	/**
	 * Determines whether or not there is one more sample to read.
	 * @return
	 */
	public boolean hasNext(){
		// if lastPushed == windowsize - 1 and there is nothing else to read from the file
		// return false
		
		// otherwise return true
	}
	
	/**
	 * Counts the number of samples in the data file and initializes the buffer.
	 * @param bufferArray  : buffer array
	 * @return number of samples in the data file
	 * @throws IOException
	 */
	private int initBuffer(double[] bufferArray) throws IOException{
		int numOfSamps = 0;
		String lineContent;
		BufferedReader bfReader = new BufferedReader( new FileReader(this.fileName) );
		
		while((lineContent=bfReader.readLine()) != null)
		{
			if(numOfSamps < this.windowSize)
			{
				bufferArray[numOfSamps] = Double.parseDouble(lineContent);
			}
			++numOfSamps;
		}
		bfReader.close();
		
		return numOfSamps;
	}// end initBuffer method.
}
