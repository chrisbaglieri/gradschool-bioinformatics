

/**
 * Class that represents an expression data point
 * on a given  2-dimensional graph
 * 
 * @author cbaglieri
 *
 */
public class ExpressionDataPoint extends DataPoint {

	/*
	 * Private members
	 */
	private String gene;
	private String experiment;
	private double expressionLevel;
	
	/**
	 * Default empty constructor
	 */
	public ExpressionDataPoint() {
	}
	
	/**
	 * Constructs an expression data point
	 * @param x X coordinate (experiment number)
	 * @param y Y coordinate (normalized expression value)
	 */
	public ExpressionDataPoint( int x, double y ) {
		super.xCoordinate = x;
		super.yCoordinate = y;
	}
	
	/**
	 * Constructs an expression data point
	 * @param x X coordinate (experiment number)
	 * @param y Y coordinate (normalized expression value)
	 * @param gene name of the particular gene
	 * @param experiment name of the experiment
	 * @param expressionLevel level of expression
	 */
	public ExpressionDataPoint( int x, double y, 
		String gene, String experiment, double expressionLevel ) {
		super.xCoordinate = x;
		super.yCoordinate = y;
		this.gene = gene;
		this.experiment = experiment;
		this.expressionLevel = expressionLevel;
	}
	
	/**
	 * Gets the name of the gene
	 * @return gene name
	 */
	public String getGene() {
		return this.gene;
	}
	
	/**
	 * Sets the name of the gene
	 * @param gene gene name
	 */
	public void setGene( String gene ) {
		this.gene = gene;
	}
	
	/**
	 * Gets the name of the experiment
	 * @return experiment name
	 */
	public String getExperiment() {
		return this.experiment;
	}
	
	/**
	 * Sets the name of the experiment
	 * @param experiment name of the experiment
	 */
	public void setExperiment( String experiment ) {
		this.experiment = experiment;
	}

	/**
	 * Gets the expression level
	 * @return expression level
	 */
	public double getExpressionLevel() {
		return this.expressionLevel;
	}
	
	/**
	 * Sets the expression level
	 * @param expressionLevel expression level
	 */
	public void setExpressionLevel( double expressionLevel ) {
		this.expressionLevel = expressionLevel;
	}	

	/**
	 * String representation of the expression data point
	 */
	public String toString() {
		return this.gene + " (" + this.experiment + ") : " + this.expressionLevel; 
	}
	
}
