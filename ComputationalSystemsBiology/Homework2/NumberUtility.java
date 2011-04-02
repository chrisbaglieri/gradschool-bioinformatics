import java.text.DecimalFormat;

/**
 * Utility class that assists in number management
 * for the K Means algorithm implementation
 * 
 * @author cbaglieri
 *
 */
public class NumberUtility {

	/**
	 * Normalizes the  number using decimal scaling and 
	 * the specified k threshold member.  The following
	 * function is applied for decimal scaling:
	 * 
	 *  v'(i) = v(i)/10^k where k = max(|v(i)|) <= 1
	 * 
	 * @param number number to normalize
	 * @param kThreshold k value
	 * @param decimalPrecision precision value
	 * @return normalized data point in the range of [-k,k]
	 */
	public static double normalizeData( double number,
		int kThreshold,
		int decimalPrecision ) {
		double _threshold = Math.pow(10, kThreshold);
		return NumberUtility.truncateDecimal( 
			number/_threshold, 
			decimalPrecision );
	}
	
	/**
	 * Formats a decimal number with the specified precision
	 * @param decimal number to format
	 * @param decimalPrecision precision to format to
	 * @return formatted decimal
	 */
	public static double truncateDecimal( double decimal, 
		int decimalPrecision ) {
		DecimalFormat _format = new DecimalFormat();
		_format.setMaximumFractionDigits(decimalPrecision);
		return Double.parseDouble( _format.format(decimal) );
	}

}
