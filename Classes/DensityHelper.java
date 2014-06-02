/**
 * Helper functions for the density estimation algorithm.
 * 
 * @author Daniel Weinand & Gedeon Nyengele
 * 
 */
import java.util.ArrayList;
import java.util.Collections;

import de.erichseifert.gral.data.DataTable;


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
		//NOT YET VETTED
		
		// The normalizing constant for the scaling basis functions
		double scaleNormalizer = Math.pow(2, Settings.startLevel/2.0)/Settings.windowSize;
		
		//Loop through the translations for the scaling basis functions
		int scaleInd = 0;
		for (double k : Transform.scalingTranslates) {
			
			// Get the translated & scaled data point
			double xScaled = Math.pow(2, Settings.startLevel*Xnew) - k;
			
			// If the wavelet supports the data point, update the coefficient
			if (Wavelet.inSupport(xScaled)) {
				double scaleNew = Transform.scalingCoefficients.get(scaleInd) 
						          + Wavelet.getPhiAt(xScaled);
				Transform.scalingCoefficients.set(scaleInd, scaleNew);
				
			}
			scaleInd++;
		}
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
		//NOT YET VETTED
		
		// Get the max & min values for the wavelet's support
		int[] waveletMinMax = Wavelet.getSupport();
		
		double kMin = Math.ceil(Math.pow(2,j*X) - waveletMinMax[0]);
		double kMax = Math.floor(Math.pow(2,j*X) - waveletMinMax[1]);
		double[] kMinMax = new double[2];
		kMinMax[0] = kMin;
		kMinMax[1] = kMax;
		return (kMinMax);
		
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
		
		// Set all scaling coefficients to 0
		Transform.scalingCoefficients = new ArrayList<Double>(Collections.nCopies(Transform.scalingTranslates.size(), 0.0));
		
		if (Settings.waveletFlag) {
			Transform.waveletCoefficients = new ArrayList<ArrayList<Double>> ();
			
			// Loop through resolutions
			for (int j = Settings.startLevel; j <= Settings.stopLevel; j++){
				
				// Set all wavelet at this resolution coefficients to 0
				ArrayList<Double> jCoefficients = new ArrayList<Double> (Collections.nCopies
													(Transform.waveletTranslates.get(j).size(), 0.0));
				Transform.waveletCoefficients.add(jCoefficients);
			}
		}
	} //end intializeCoefficients
	
	/**
	 * Calculates the density at each discrete point in the
	 * range pre-specified.
	 * 
	 * Pre: the coefficient matrices have been created and
	 *      are the proper size
	 * @return the normalized density estimate
	 */
	public static DataTable getDensity() {
		//NOT YET VETTED
		
		ArrayList<Double> density = new ArrayList<Double> ();
		
		// Calculate un-normalized density for each point in domain
		int scalIndex = 0;
		for (double i = Settings.getMinimumRange(); 
				i < Settings.getMaximumRange(); i += Settings.discretization) {
			
			// Density at point i
			double iDense = 0.0;
			
			// Cycle through translates for each point
			for (double k : Transform.scalingTranslates) {
				double Xi = Math.pow(2, Settings.startLevel * i) - k;
				
				// Only update if the point is supported
				if (Wavelet.inSupport(Xi)) {
					iDense += Transform.scalingCoefficients.get(scalIndex) 
							  * Wavelet.getPhiAt(Xi);
				}
			}
			density.add(iDense);
			scalIndex++;
		}
		
		//Normalize density
		density = normalizeDensity(density);
		
		//Convert to a data table
		DataTable densityTable = toDataTable(density);
		return(densityTable);
	}
	
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
	private static ArrayList<Double> normalizeDensity(ArrayList<Double> unNormDensity){
		
		ArrayList<Double> normDens = unNormDensity;
		int iter = 0;
		double threshold = Math.pow(10, -16);
		double densityDomainSize = Settings.getMaximumRange() - Settings.getMinimumRange();
		
		while (iter < 1000) {
			
			// Zero negative points
			for (int i = 0; i < unNormDensity.size(); i++) {
				normDens.set(i, Math.max(normDens.get(i),0.0));
			}
			
			// Sum over probability density over interval
			double integralSum = 0.0;
			for (int i = 0; i < unNormDensity.size(); i++) {
				integralSum += normDens.get(i);
			}
			
			// Return if error is under threshold
			if (Math.abs(integralSum - 1) < threshold) {
				return (normDens);
			}
			
			// Modify density so that it integrates to 1
			double normalizeConstant = (integralSum - 1) / densityDomainSize;
			for (int i = 0; i < unNormDensity.size(); i++) {
				normDens.set(i, normDens.get(i) - normalizeConstant);
			}
			
			iter++;
		}
		
		// Settle for the current approximation
		return normDens;
	} //end normalizeDensity
	
	/**
	 * Takes the normalized density over the supported range and
	 * returns the same, formatted for GRAL compatibility
	 * @param normDensity : The normalized density over the supported range
	 * @return the density over the support
	 */
	private static DataTable toDataTable(ArrayList<Double> normDensity) {
		//NOT YET VETTED
		
		// Create the data table to put the density in
		DataTable densityData = new DataTable(Double.class, Double.class);
        for (int i = 0; i < normDensity.size(); i++) {
            double Xi = Settings.getMinimumRange() + i*Settings.discretization;
            double Yi = normDensity.get(i);
            densityData.add(Xi, Yi);
        }
        
        return (densityData);
	}
	
}

