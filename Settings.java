/**
 * Encapsulates all the parameter variables used in the density estimator.
 * 
 * @author Gedeon Nyengele & Daniel Weinand.
 */
public class Settings {
	
	// Start level for both the scaling and wavelet functions.
	public static int startLevel       = 0;
	
	// Stop level for both the scaling and wavelet functions.
	public static int stopLevel        = 0;
	
	// Flag that determines which aging mechanism to use.
	//      0: no aging.
	//      1: Caudle aging method.
	//      2: Window method.
	public static int agingFlag        = 0;
	
	// Size of the window for data aging using the window method.
	public static int windowSize       = 600;
	
	// Caudle and Wegman's aging theta.
	public static double agingTheta    = 0;
	
	// Minimum and Maximum values on the domain of the density function.
	public static double[] densityRange = {-3.5, 3.5};
	
	// Minimum and Maximum values on the domain of the sample vector.
	public static double[] sampleRange = {0, 0};
	
	// Flag that determines whether or not the wavelet function should also be used.
	//      false: wavelet is OFF -> density approximation done with scaling function only.
	//      true : wavelet is ON  -> density approximation done with both scaling and wavelet functions.
	public static boolean waveletFlag      = true;

}
