import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Class that represents the source PCL
 * file exposing numerous methods for iterating
 * over the set of points in a forward only
 * fashion.  This class aims at improving the
 * performance of the algorithm avoiding having
 * to read through the entire file and generating
 * a large set of ExpressionDataPoint objects.
 * 
 * @author cbaglieri
 *
 */
public class PCLSourceFile implements Enumeration {
	
	/*
	 * Private members
	 */
	private BufferedReader reader;
	private String sourceFile;
	private boolean hasMoreGenesFlag = true;
	private String nextGeneSet = null;
	private String[] experiments;
	
	/*
	 * Constants
	 */
	private final int EXPERIMENT_DATA_INDEX = 3;
	private final int PCL_DATA_PRECISION = 3;
	private final int K_THRESHOLD_VALUE = 1;
	
	// TODO: note that we assume that the decimal scaling
	// threshold to be "1" since expression data does not
	// exceed +/- 10 according to the following condition:
	// v'(i) = v(i)/10^k where k = max(|v(i)|) <= 1
	// if in the future this does not hold true, the
	// threshold value can be passed into the constructor
	// for this class.
	private final int DECIMAL_SCALING_THRESHOLD = 1;
	
	/**
	 * Constructs a PCL source file
	 * @param sourceFile fully qualified source filepath
	 */
	public PCLSourceFile( String sourceFile )
		throws FileNotFoundException, IOException {
			
		this.sourceFile = sourceFile;
		this.reader = new BufferedReader( new FileReader(this.sourceFile) );
		
		// extract the set of experiments from the file
		String[] _headerItems = this.reader.readLine().split("\t");
		this.experiments = new String[_headerItems.length - 
			EXPERIMENT_DATA_INDEX];
		for ( int _headerIndex = EXPERIMENT_DATA_INDEX; 
			_headerIndex < _headerItems.length; 
			_headerIndex++ ) {
			this.experiments[_headerIndex - EXPERIMENT_DATA_INDEX] = 
				_headerItems[_headerIndex];
		}
		
		// move to the expression data in the PCL file
		this.reader.readLine();
		
	}
	
	/**
	 * Initializes the sort file such that it
	 * is ready to be read from the beginning of
	 * the file.  Note that this method can be
	 * called anytime to reinitialize the file
	 * pointer to the start of the file.
	 */
	public void initializeSourceFile() {
		
		// when initializing the source file, we can
		// afford to squash the exception since this
		// will never happan unless the source file has
		// moved since instantiating this object.
		try {
			
			this.reader = new BufferedReader( 
				new FileReader(this.sourceFile) );
			
			// move to the expression data points
			this.reader.readLine();
			this.reader.readLine();
			
		} catch ( FileNotFoundException fnfe ) {
			// do nothing; see comment above
		} catch ( IOException ioe ) {
			// do nothing; see comment above
		}
		
	}
	
	/**
	 * Whether or not there are additional expression
	 * data points in the source file
	 * @return whether or not more expression data points exist
	 */
	public boolean hasMoreElements() {
		
		// only get the next item if we haven't already
		// reached the end of the PCL file
		if ( this.hasMoreGenesFlag ) {
		
			// default the next element to null in case
			// it already has a value from calling this
			// method earlier
			this.nextGeneSet = null;
	
			// if an IOException is thrown, assume there
			// is a problem with opening the file and notify
			// the client that no additional genes exist.
			// this method should throw the exception however
			// since we are extending Enumeration, we are
			// unable to do so.
			try {
				this.nextGeneSet = this.reader.readLine();
			} catch ( IOException ioe ) {
				this.hasMoreGenesFlag = false;
			}
			
			// check to see if a gene line existed, if so
			// do nothing, otherwise, set the flag to false
			if ( this.nextGeneSet == null ) {
				this.hasMoreGenesFlag = false;
			}
		
		}
		
		return this.hasMoreGenesFlag;
		
	}
	
	/**
	 * Returns the next vector of expression data points
	 * for a given gene.
	 * @return arraylist of expression data points for a gene
	 */
	public Object nextElement() {
		return this.constructExpressionDataPoints( this.nextGeneSet );
	}
	
	/**
	 * Gets the number of experiments for this data set
	 * @return the number of experiments
	 */
	public int getNumberOfExperiments() {
		return this.experiments.length;
	}
	
	/**
	 * Gets the set of experiment names for the
	 * PCL data file instance.
	 * @return list of experiment names
	 */
	public String[] getExperimentNames() {
		return this.experiments;
	}
	
	/**
	 * Contructs an array list of expression data points for
	 * a given gene across a set of expreriments.
	 * @param expressionData experimental set of expression values for a gene
	 * @return ArrayList of ExpressionDataPoints
	 */
	private ArrayList constructExpressionDataPoints( 
		String expressionData ) {
		
		String[] _geneData = expressionData.split("\t");
		String _geneName = _geneData[0];
		
		ArrayList _expressionDataPoints = new ArrayList();
		for ( int _dataIndex = EXPERIMENT_DATA_INDEX; 
			_dataIndex < _geneData.length; 
			_dataIndex++ ) {
				
			// define the x and y coordinates
			int _xCoordinate = _dataIndex - (EXPERIMENT_DATA_INDEX-1);
			double _expressionValue = 0;
			try {
				_expressionValue = Double.parseDouble(_geneData[_dataIndex]);
			} catch ( NumberFormatException nfe ) {
				// this exception is thrown when attempting to normalize a
				// missing expressioin data value. do nothing and assume 
				// the default value of zero
			}
			 
			double _yCoordinate = NumberUtility.normalizeData(
				_expressionValue,
				K_THRESHOLD_VALUE,
				PCL_DATA_PRECISION);
		
			// define the experiment name
			String _experimentName = this.experiments[_dataIndex - 
				EXPERIMENT_DATA_INDEX];
			
			ExpressionDataPoint _expressionDataPoint = 
				new ExpressionDataPoint( 
					_xCoordinate,
					_yCoordinate,
					_geneName,
					_experimentName,
					_expressionValue );
					
			_expressionDataPoints.add(_expressionDataPoint);
				
		}
		
		return _expressionDataPoints;
	}

}
