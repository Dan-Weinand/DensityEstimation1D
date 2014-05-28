import java.util.ArrayList;
/**
 * Represents the wavelet including both the scaling and wavelet functions.
 * 
 * @author Gedeon Nyengele & Daniel Weinand
 * 
 */
public class Wavelet {
	
	// Type of the wavelet.
	protected String waveletType;
	
	// The support for the wavelet used.
	private static ArrayList<Double> supp;
	
	// The wavelet function for the wavelet used.
	private static ArrayList<Double> psi;
	
	// The scaling function for the wavelet used.
	private static ArrayList<Double> phi;
	
	/**
	 * Constructor.
	 * @param waveletType     : character name for the wavelet to be used.
	 */
	public Wavelet(String waveletType){
		initializeSupport(waveletType);
		
	} // end constructor method.
	
	/**
	 * Initializes the support to the appropriate size given
	 * a valid wavelet type.
	 * 
	 * Post: support is set
	 * @param waveletType     : character name for the wavelet to be used.
	 */
	public static void initializeSupport(String waveletType) {
		String waveFamily = "";
		
		// Create the string for the wavelet family
		int ordBegin = -1;
		for (int i = 0; i < waveletType.length(); i++) {
			char currentChar = waveletType.charAt(i);
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
			int order = Integer.parseInt(waveletType.substring(ordBegin));
		}
		
		case (waveFamily) {
			case "db" :
				support = new double[] {0 2*order - 1};
				break;
			case "sym" :
				support = new double[] {0 2*order - 1};
				break;
			case "coif" :
				support = new double[] {0 6*order - 1};
				break;
			case "dmey" :
				support = new double[] {0 101};
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
	public static int[] getSupport() {
		assert(!supp.isEmpty());
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
	
	
	

} // end class Wavelet..
