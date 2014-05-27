/**
 * Represents the wavelet including both the scaling and wavelet functions.
 * 
 * @author Gedeon Nyengele & Daniel Weinand
 * 
 */
public class Wavelet {
	
	// Type of the wavelet.
	protected String waveletType;
	// Path to data file containing the look-up tables
	protected String waveletDataFile;
	
	/**
	 * Constructor.
	 * @param waveletType     : 3 - 4 character name for the wavelet to be used.
	 * @param waveletDataFile : path to the file containing the wavelet look-up tables.
	 */
	public Wavelet(String waveletType, String waveletDataFile){} // end constructor method.
	
	/**
	 * Returns the support of the given wavelet.
	 * @param None.
	 * @return Two-element array with the endpoints of the wavelet support.
	 */
	public int[] getSupport() {}// end method getSupport().
	
	/**
	 * Returns the interpolated value of the scaling function psi at the given position.
	 * @param position: the result from (2^j)*x - k for a given level j and translate k.
	 * @return the interpolated value of phi at the location.
	 */
	public double getPhiAt(int position) {}// end method getPhiAt().
	
	/**
	 * Returns the interpolated value of the wavelet function psi at the given position.
	 * @param position: the result from (2^j)*x - k for a given level j and translate k.
	 * @return the interpolated value of psi at the location.
	 */
	public double getPsiAt(int position) {} // end method getPsiAt().
	
	
	

} // end class Wavelet.
