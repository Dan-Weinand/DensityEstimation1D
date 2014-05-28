/**
 * Helper functions for the density estimation algorithm.
 * 
 * @author Daniel Weinand & Gedeon Nyengele
 * 
 */
import java.util.ArrayList;
import java.util.Collections;


public class DensityHelper {
	
	/**
	 * Checks that the sample point X is within the domain of the density function.
	 * @param X : the data point to check
	 * @return  : whether or not the point is in the domain
	 */
	public static boolean inRange (double X) {
		return (X > Settings.getMinimumRange() && X < Settings.getMaximumRange());
	} // end inRange
	
	/**
	 * Updates the function coefficients based on the incoming data point and
	 * the data point leaving the sliding window.
	 * 
	 * Post: the coefficients are updated as needed
	 * 
	 * @param Xnew : the new data point to update the coefficients based on
	 * @param Xold : the leaving data point to update the coefficients based on
	 */
	public static void updateCoefficients(double Xnew, double Xold){
		
	} // end updateCoefficients
	
	/**
	 * Find the maximum and minimum translation indices which
	 * support the incoming data point.
	 * 
	 * @param X : the data point
	 * @param j : the resolution level
	 * @return : An array containing the minimum and maximum
	 *           translation indices which support the data point
	 */
	public static double[] findRelevantKIndices(double X, int j) {
		
	} //end findRelevantKIndices
	
	/**
	 * Initializes the translates for the scaling basis functions,
	 * based off of the maximum/minimum values supported and
	 * the starting resolution level.
	 * 
	 * Post: the translates in Transform are set appropriately
	 * @return the translates for the scaling basis functions
	 */
	public static void initializeTranslates() {
		
		// Initialize the scaling translates
		Transform.scalingTranslates = new ArrayList<Double> ();
		int startTranslate = (int) Math.floor((Math.pow(2,Settings.startLevel)*Settings.getMinimumRange())-Wavelet.getSupport()[1]);
		int stopTranslate = (int) Math.ceil((Math.pow(2,Settings.startLevel)*Settings.getMaximumRange())-Wavelet.getSupport()[0]);
		for (double k = startTranslate; k <= stopTranslate; k++){
			Transform.scalingTranslates.add(k);
		}
		
		// Initialize the wavelet translates if wavelets are being used
		if (Settings.waveletFlag) {
			Transform.waveletTranslates = new ArrayList<ArrayList<Double>> ();
		
			// Loop through resolutions
			for (int j = Settings.startLevel; j <= Settings.stopLevel; j++){
				
				ArrayList<Double> jTranslates = new ArrayList<Double> ();
				int startWTranslate = (int) Math.floor((Math.pow(2,j)*Settings.getMinimumRange())-Wavelet.getSupport()[1]);
				int stopWTranslate = (int) Math.ceil((Math.pow(2,j)*Settings.getMaximumRange())-Wavelet.getSupport()[0]);
				for (double k = startWTranslate; k <= stopWTranslate; k++){
					jTranslates.add(k);
				}
				Transform.waveletTranslates.add(jTranslates);
			}
		}
	} //end initializeTranslates
	
	/**
	 * Initializes the arrays to hold the scaling basis function
	 * and wavelet basis function coefficients based off
	 * of the resolution levels.
	 * 
	 * Post: the coefficients arrays are of the appropriate size.
	 */
	public static void initializeCoefficients() {
		
	} //end intializeCoefficients
	
	/**
	 * Takes in an un-normalized density estimate and returns the
	 * normalized version, using the normalization procedure from
	 * Gajek (1986) 'On improving density estimators which are
	 * not Bona Fide functions'.
	 * 
	 * @param unNormDensity : the un-normalized density estimate
	 *                        over the domain range.
	 * @return the normalized density.
	 */
	public static ArrayList<Double> normalizeDensity(ArrayList<Double> unNormDensity){
		
	} //end normalizeDensity
	
	/**
	 * Takes the normalized density over the supported range and
	 * returns the same, formatted for GRAL compatibility
	 * @param normDensity : The normalized density over the supported range
	 * @return the density over the support
	 */
	public static DataTable getDataTable(ArrayList<Double> normDensity) {
		
	}
	
}

