

/**
 * Implementation of the Runge Kutta 4th Order Algorithm
 * @author cbaglieri
 */
public class RungeKuttaImplementation {

	/**
	 * Main entry point to the algorithm
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO: improve algorithm flexibility by taking in a PLAS file
		// ensuring that this implementation remains flexible,
		// an additional argument to this implementation could
		// be a path to a file that contained the set of equations
		// and constant values that described the S-System.  adhering
		// to the PLAS format would allow this class to consume
		// any PLC file maximizing its usefullness.
		
		double initialStep = 0;
		double endStep = 10;
		double stepSize = 0.01;
		double currentStep;
		
		// y values
		double x1_YValue;
		double x2_YValue;
		double x3_YValue;
		double x4_YValue = 0.5;
		
		// k values
		double x1_KValue;
		double x2_KValue;
		double x3_KValue;
		double x4_KValue = x4_YValue;

		x1_YValue = 1;
		x2_YValue = 1;
		x3_YValue = 1;
		currentStep = initialStep;
		
		// calculate X1
		System.out.println("X1 ANALYSIS");
		System.out.println(x1_YValue + "\t" + currentStep);
		while ( currentStep <= endStep ) {
			x1_KValue = stepSize * (0.5 * Math.pow(x3_YValue, -2) * x4_YValue - 0.5 * Math.pow(x1_YValue, 0.5) );
			currentStep = currentStep + stepSize/2;
			double z = x1_YValue + x1_KValue/2;
			x2_KValue = stepSize * (0.5 * Math.pow(x1_YValue, 0.5) - 4 * z);
			z = x1_YValue + x2_KValue/2;
			x3_KValue = stepSize * (4 * x2_YValue - 2 * Math.pow(z, 0.75));
			currentStep = currentStep + stepSize/2;
			x1_YValue = x1_YValue + ((x1_KValue + 2*x2_KValue + 2*x3_KValue + x4_KValue)/6);
			System.out.println(x1_YValue + "\t" + currentStep);
		}
		
		x1_YValue = 1;
		x2_YValue = 1;
		x3_YValue = 1;
		currentStep = initialStep;

		// calculate X2
		System.out.println("\n\nX2 ANALYSIS");
		System.out.println(x2_YValue + "\t" + currentStep);
		while ( currentStep <= endStep ) {
			x1_KValue = stepSize * (0.5 * Math.pow(x3_YValue, -2) * x4_YValue - 0.5 * Math.pow(x1_YValue, 0.5) );
			currentStep = currentStep + stepSize/2;
			double z = x2_YValue + x1_KValue/2;
			x2_KValue = stepSize * (0.5 * Math.pow(x1_YValue, 0.5) - 4 * z);
			z = x2_YValue + x2_KValue/2;
			x3_KValue = stepSize * (4 * x2_YValue - 2 * Math.pow(z, 0.75));
			currentStep = currentStep + stepSize/2;
			x2_YValue = x2_YValue + ((x1_KValue + 2*x2_KValue + 2*x3_KValue + x4_KValue)/6);
			System.out.println(x2_YValue + "\t" + currentStep);
		}
		
		x1_YValue = 1;
		x2_YValue = 1;
		x3_YValue = 1;
		currentStep = initialStep;

		// calculate X3
		System.out.println("\n\nX3 ANALYSIS");
		System.out.println(x3_YValue + "\t" + currentStep);
		while ( currentStep <= endStep ) {
			x1_KValue = stepSize * (0.5 * Math.pow(x3_YValue, -2) * x4_YValue - 0.5 * Math.pow(x1_YValue, 0.5) );
			currentStep = currentStep + stepSize/2;
			double z = x3_YValue + x1_KValue/2;
			x2_KValue = stepSize * (0.5 * Math.pow(x1_YValue, 0.5) - 4 * z);
			z = x3_YValue + x2_KValue/2;
			x3_KValue = stepSize * (4 * x2_YValue - 2 * Math.pow(z, 0.75));
			currentStep = currentStep + stepSize/2;
			x3_YValue = x3_YValue + ((x1_KValue + 2*x2_KValue + 2*x3_KValue + x4_KValue)/6);
			System.out.println(x3_YValue + "\t" + currentStep);
		}
		
	}
	
}
