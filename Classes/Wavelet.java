import java.util.ArrayList;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
/**
 * Represents the wavelet including both the scaling and wavelet functions.
 * 
 * @author Gedeon Nyengele & Daniel Weinand
 * 
 */
public class Wavelet {
	
	// Type of the wavelet.
	private static String waveletType;
	
	// The domain for the wavelet used.
	// This called "supp" in the MATLAB implementation.
	private static ArrayList<Double> domain;
	
	// The wavelet function for the wavelet used.
	private static ArrayList<Double> psi;
	
	// The scaling function for the wavelet used.
	private static ArrayList<Double> phi;
	
	// The support of the wavelet used.
	private static double[] waveletSupport;
	
	
	/**
	 * Initializes the wavelet
	 * @param wavType : string for the wavelet type
	 * @throws IOException 
	 */
	public static void init(String wavType) throws IOException{
		
		// Set the wavelet type.
		waveletType = wavType;
		
		// Find the support for the given wavelet.
		initializeSupport(wavType);
		
		// Load phi function data.
		Wavelet.phi    = loadFunctionData( wavType + "PHI.csv" );
		
		// Load psi function data
		Wavelet.psi    = loadFunctionData( wavType + "PSI.csv " );
		
		// Load domain
		Wavelet.domain = loadFunctionData( wavType + "SUPP.csv " );
		
	} // end init method.
	
	
	/**
	 * Initializes the support to the appropriate size given
	 * a valid wavelet type.
	 * 
	 * Post: support is set
	 * @param wavType     : character name for the wavelet to be used.
	 */
	private static void initializeSupport(String wavType) {
		String waveFamily = "";
		
		// Create the string for the wavelet family
		int ordBegin = -1;
		for (int i = 0; i < wavType.length(); i++) {
			char currentChar = wavType.charAt(i);
			if (Character.isDigit(currentChar)) {
				ordBegin = i;
				break;
			}
			else {
				waveFamily += currentChar;
			}
			
		}
		
		int order = -1;
		// Determine wavelet order if appropriate
		if (ordBegin ~= -1) {
			int order = Integer.parseInt(wavType.substring(ordBegin));
		}
		
		case (waveFamily) {
			case "db" :
				waveletSupport = {0, 2*order - 1};
				break;
			case "sym" :
				waveletSupport = {0, 2*order - 1};
				break;
			case "coif" :
				waveletSupport = {0 6*order - 1};
				break;
			case "dmey" :
				waveletSupport = {0 101};
				break;
			default :
				// Unsupported wavelet type
				boolean unsupportedWavelet = true;
				assert(!unsupportedWavelet);
				break;
		}
	}
	
	/**
	 * Returns the support of the given wavelet.
	 * Pre: the support has been loaded in
	 * @param None.
	 * @return Two-element array with the endpoints of the wavelet support.
	 */
	public static double[] getSupport() {
		return waveletSupport;
	}// end method getSupport().
	
	/**
	 * Returns the interpolated value of the scaling function psi at the given position.
	 * Pre: the phi has been loaded in
	 * @param position: the result from (2^j)*x - k for a given level j and translate k.
	 * @return the interpolated value of phi at the location.
	 */
	public static double getPhiAt(double position) {
		assert(!phi.isEmpty());
	}// end method getPhiAt().
	
	/**
	 * Returns the interpolated value of the wavelet function psi at the given position.
	 * Pre: Pre: the psi has been loaded in
	 * @param position: the result from (2^j)*x - k for a given level j and translate k.
	 * @return the interpolated value of psi at the location.
	 */
	public static double getPsiAt(double position) {
		assert(!psi.isEmpty());
	} // end method getPsiAt().
	
	
	/**
	 * Checks if the given sample is in the domain of the density or not.
	 * @param x : sample
	 * @return True  : if sample is in the domain.
	 *         False : if sample is not in the domain.
	 */
	public static boolean inSupport(double x){
		
	} // end inSupport method.
	
	
	/**
	 * Returns the look-up table for the function expanded in the given csv file.
	 * @param filename : name of the file where function data is located.
	 * @return an arraylist of doubles containing the function data.
	 */
	private static ArrayList<Double> loadFunctionData( String filename ) throws IOException{
		
		CSVReader reader = new CSVReader(new FileReader(filename));
		List<String[]> myEntries = reader.readAll();
		ArrayList<Double> fnData = new ArrayList<Double>();
		
		for(int i = 0; i < myEntries.size(); i++)
		{
			String[] content = myEntries.get(i);
			
			for(int j = 0; j< content.length; j++)
			{
				fnData.add( Double.parseDouble( content[j]) );
			}
		}
		
		reader.close();
		
		return fnData;
	}// end loadFunctionData method.
	
	

} // end class Wavelet..
