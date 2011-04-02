import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Class that represents a cluster composed of
 * a set of data points
 * 
 * @author cbaglieri
 *
 */
public class Cluster {
	
	/*
	 * Private members
	 */
	private ArrayList centerPoints;
	private Hashtable dataPoints;
	
	// TODO: improve data point storage mechanism
	// storing a high number of data points as a hash
	// of data point objects could hinder performance
	
	/**
	 * Constructs an empty cluster with the
	 * following center points
	 * @param centerPoints collection of center data points
	 */
	public Cluster( ArrayList centerPoints ) {
		this.centerPoints = centerPoints;
		this.dataPoints = new Hashtable();
	}
	
	/**
	 * Adds the specified data point to this cluster
	 * @param dataPoint DataPoint to add
	 */
	public void addDataPoint( DataPoint dataPoint ) {
		this.dataPoints.put( dataPoint.toString(), dataPoint );
	}
	
	/**
	 * Removes the specified data point from this cluster.
	 * Note that if the data point is not contained in this
	 * cluster, no points are removed and no exceptions are
	 * thrown.
	 * @param dataPoint DataPoint to remove
	 */
	public void removeDataPoint( DataPoint dataPoint ) {
		this.dataPoints.remove( dataPoint.toString() );
	}
	
	/**
	 * Removes the entire set of data points
	 * currently belonging to the cluster.
	 */
	public void removeAllDataPoints() {
		this.dataPoints.clear();
	}
	
	/**
	 * Based on the set of data points that belong to
	 * the cluster, finds the centroid within the data.
	 * @return centroid DataPoint
	 */
	public ArrayList determineCentroid() {
		
		int[] _pointCounts = new int[this.centerPoints.size()];
		double[] _yTotals = new double[this.centerPoints.size()];
		
		// iterate over each of the data points and tally up
		// the totals.  note only the y value will vary since
		// the x coordinate maps directly to a given experiment
		Iterator _dataPointIterator = this.dataPoints.values().iterator();
		while ( _dataPointIterator.hasNext() ) {
			DataPoint _dataPoint = (DataPoint) _dataPointIterator.next();
			int _experimentNumber = Integer.parseInt( 
				String.valueOf(_dataPoint.getXCoordinate()) );
			_yTotals[_experimentNumber-1] += 
				_dataPoint.getYCoordinate();
			_pointCounts[_experimentNumber-1] += 1;
		}
		
		// generate the new set of data points making up
		// the new centroid
		ArrayList _centerDataPoints = new ArrayList();
		for ( int _index = 0; _index < this.centerPoints.size(); _index++ ) {
			if ( _pointCounts[_index] == 0 ) {
				_centerDataPoints.add( (DataPoint) this.centerPoints.
					get(_index) );
			} else {
				_centerDataPoints.add( new DataPoint(_index+1, 
					NumberUtility.truncateDecimal(
					_yTotals[_index]/_pointCounts[_index], 3)) );
			}
		}
		
		return _centerDataPoints;

	}
	
	/**
	 * Gets the center of the cluster
	 * @return center ArrayList
	 */
	public ArrayList getCenter() {
		return this.centerPoints;
	}
	
	/**
	 * Sets the center of the cluster
	 * @param center new center DataPoints
	 */
	public void setCenter( ArrayList centerPoints ) {
		this.centerPoints = centerPoints;
	}
	
	/**
	 * Gets the number of data points in the cluster
	 * @return data point count
	 */
	public int getDataPointCount() {
		return this.dataPoints.size();
	}
	
	/**
	 * Gets the set of expression data points that
	 * belong to this cluster
	 * @return collection of expression data points
	 */
	public Collection getDataPoints() {
		return this.dataPoints.values();
	}
	
	/**
	 * Compares the centers between two clusters
	 * @param cluster cluster to compare against
	 * @return whether or not the centers are the same
	 */
	public boolean compareCenters( Cluster cluster ) {
		
		ArrayList _remoteCenterPoints = cluster.centerPoints;
		
		for ( int _pointIndex = 0; 
			_pointIndex < this.centerPoints.size(); 
			_pointIndex++ ) {
			
			DataPoint _remoteDataPoint = (DataPoint) _remoteCenterPoints.
				get(_pointIndex);
			DataPoint _localDataPoint = (DataPoint) this.centerPoints.
				get(_pointIndex);
			if ( !_remoteDataPoint.compare(_localDataPoint) ) {
				return false;
			}

		}
		
		return true;
	}
	
	/**
	 * Gets the name or unique identifier for this cluster
	 * @return cluster identifier
	 */
	public String toString() {
		
		StringBuffer _clusterReport = new StringBuffer();
		
		Iterator _centerPoints = this.centerPoints.iterator();
		_clusterReport.append( "CLUSTER DETAILS [ Center Points: ");
		while ( _centerPoints.hasNext() ) {
			DataPoint _centerPoint = (DataPoint) _centerPoints.next();
			_clusterReport.append( "(" + _centerPoint.toString() + ") " );
		}
		_clusterReport.append( "]\n" );
		
		// add data point details
		Iterator _dataPointIterator = this.dataPoints.values().iterator();
		while ( _dataPointIterator.hasNext() ) {
			DataPoint _dataPoint = (DataPoint) _dataPointIterator.next();
			_clusterReport.append( _dataPoint.toString() + "\n" );
		}
		
		_clusterReport.append( "\n\n" );
		
		return _clusterReport.toString();
		
	}

}
