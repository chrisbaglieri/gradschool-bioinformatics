/**
 * Class that represents a data point
 * on a given two dimensional graph.
 * 
 * @author cbaglieri
 * 
 */
public class DataPoint {

	/*
	 * Private members
	 */
	protected int xCoordinate;
	protected double yCoordinate;
	
	/**
	 * Empty default constructor
	 *
	 */
	public DataPoint() {
	}
	
	/**
	 * Constructs a data point with the provided coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public DataPoint( int x, double y ) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}
	
	/**
	 * Gets the X coordinate for this data point
	 * @return X coordinate
	 */
	public int getXCoordinate() {
		return this.xCoordinate;
	}
	
	/**
	 * Sets the X coordinate for this data point
	 * @param x X coordinate
	 */
	public void setXCoordinate( int x ) {
		this.xCoordinate = x;
	}
	
	/**
	 * Gets the Y coordinate for this data point
	 * @return Y coordinate
	 */
	public double getYCoordinate() {
		return this.yCoordinate;
	}

	/**
	 * Sets the Y coordinate for this data point
	 * @param x Y coordinate
	 */
	public void setYCoordinate( double y ) {
		this.yCoordinate = y;
	}
	
	/**
	 * Compares two data points and determines whether
	 * or not they are the same point in space.
	 * @param dataPoint DataPoint to compare against
	 * @return whether or not the points are the same
	 */
	public boolean compare( DataPoint dataPoint ) {
		boolean _isSame = false;
		if ( dataPoint.xCoordinate == this.xCoordinate &&
			 dataPoint.yCoordinate == this.yCoordinate ) {
			_isSame = true;
		}
		return _isSame;
	}
	
	/**
	 * Generates a string representation of a data point
	 * in the format "xCoordinate,yCoordinate"
	 * @return data point string representation
	 */
	public String toString() {
		return this.xCoordinate + "," + this.yCoordinate;
	}

}
