import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Implementation of the K-Means clustering
 * algorithm.  Usage follows:
 * 
 * KMeansAlgorithm <sourceFile> <numberOfCluster> [plot]
 * 
 * sourceFile - full qualified path to the source data points
 * numberOfClusters - the number of desired clusters
 * matrixSize - assuming an NxN matrix, the size of the matrix (e.g. N)
 * 
 * @author cbaglieri
 *
 */
public class KMeansAlgorithm {
	
	/*
	 * Constants
	 */
	private static final String USAGE = 
		"KMeansAlgorithm <sourceFile> <numberOfCluster> [plot]";
	private static final String FILE_NOT_FOUND_MESSAGE = 
		"Data point source file not found.";
	private static final String OUTPUT_FILE_ERROR = 
		"Unable to generate algorithm output results file";
	private static final String ALGORITHM_OUTPUT_FILE = 
		"algorithmResults";
	private static final String PLOT_READY_OUTPUT_FILE = 
		"clusterPlots";
		
	/*
	 * Private members
	 */
	private ArrayList clusters;
	private PCLSourceFile dataPointFile;

	/**
	 * Main entry point to the K-Means Algorithm command
	 * line application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		String _dataPointSourceFilepath;
		int _numberOfClusters;
	
		// retrieve the arguments from the command line
		// if not provided, display the usage and quit
		if ( args.length >= 2 ) {
			_dataPointSourceFilepath = args[0];
			_numberOfClusters = Integer.parseInt( args[1] );
		} else {
			System.out.println(USAGE);
			return;
		}
		
		// generate a data point source file object using
		// the source file argument passed from the command
		// line.  display system error if file not found
		PCLSourceFile _dataPointFile = null;
		try {
			_dataPointFile = new PCLSourceFile(_dataPointSourceFilepath);
		} catch( FileNotFoundException fnfe ) {
			System.out.println( FILE_NOT_FOUND_MESSAGE );
			return;
		} catch( IOException ioe ) {
			System.out.println( FILE_NOT_FOUND_MESSAGE );
			return;
		}
		
		// generate the set of clusters randomly
		// TODO: improve the cluster center generation mechanism
		System.out.println("GENERATING INTIAL CLUSTER CENTERS...");
		ArrayList _clusters = KMeansAlgorithm.
			generateRandomClusters(_numberOfClusters,
			_dataPointFile.getNumberOfExperiments() );
			
		// run the algorithm output to the file system
		KMeansAlgorithm _kMeans = new KMeansAlgorithm( _clusters, 
			_dataPointFile );
		System.out.println("RUNNING K MEANS ALGORITHM...");
		_kMeans.runAlgorithm();
		
		// send the results of the algorithm to the default output file
		System.out.println("GENERATING ALGORITHM RESULTS FILE...");
		KMeansAlgorithm.generateAlgorithmOutputFile( _kMeans.clusters,
			ALGORITHM_OUTPUT_FILE );
		
		// check if the client requested the plot-ready output files
		if ( args.length > 2 ) {
			
			// generate just the plot ready output file
			System.out.println("GENERATING PLOT-READY RESULTS FILE...");
			KMeansAlgorithm.generatePlotReadyFile( PLOT_READY_OUTPUT_FILE,
				_kMeans.clusters );
					
		} // END ALTERNATE FILE FORMAT IF
		
	} // END MAIN METHOD
	
	/**
	 * Generates a random set of clusters for the expression data
	 * @param numberOfClusters number of clusters to generate
	 * @param numberOfExperiments number of experiments run
	 * @return random set of clusters
	 */
	private static ArrayList generateRandomClusters(int numberOfClusters,
		int numberOfExperiments) {
		
		Random _randomNumberGenerator = new Random();
		ArrayList _newClusterSet = new ArrayList();

		// generate the specified number of clusters
		for ( int _clusterCount = 0; 
			_clusterCount < numberOfClusters; 
			_clusterCount++ ) {
			
			// generate a new data point for each experiment
			ArrayList _newCenterPoints = new ArrayList();
			for ( int _experimentCount = 1; 
				_experimentCount <= numberOfExperiments; 
				_experimentCount++ ) {
				
				double _yCoordinate = _randomNumberGenerator.nextDouble() - 
					_randomNumberGenerator.nextDouble();
				
				_newCenterPoints.add( new DataPoint(_experimentCount, 
					NumberUtility.truncateDecimal(_yCoordinate, 5)) );
					
			}
				
			// add the new cluster to the list
			_newClusterSet.add( new Cluster(_newCenterPoints) );
						
		}
		
		return _newClusterSet;
		
	}

	/**
	 * Generates the output file with the algorithm results
	 * @param clusters set of finalize clusters
	 * @param outputFile output filepath
	 */
	private static void generateAlgorithmOutputFile( ArrayList clusters,
		String outputFile ) {
			
		StringBuffer _outputBuffer = new StringBuffer();

		// iterate over each cluster
		Iterator _clusterList = clusters.iterator();
		while ( _clusterList.hasNext() ) {
			_outputBuffer.append("##########################################");
			_outputBuffer.append("########################################\n");
			_outputBuffer.append( ((Cluster)_clusterList.next()).toString() );
		}
	
		try {
		
			BufferedWriter _writer = 
				new BufferedWriter( new FileWriter(outputFile) );
			
			// write out the algorithm results to the file
			_writer.write( _outputBuffer.toString() );
			_writer.flush();
			_writer.close();
			
		} catch (IOException ioe) {
			System.out.println(OUTPUT_FILE_ERROR);
		}
		
	}

	/**
	 * Generates an plot ready algorithm output file
	 * @param outputFile annoated output file
	 * @param clustes set of finalized clusters
	 */
	private static void generatePlotReadyFile( String outputFile,
		ArrayList clusters ) {
		
		try {

			StringBuffer _outputBuffer = new StringBuffer();
			
			BufferedWriter _writer = 
				new BufferedWriter( new FileWriter(outputFile) );

			// iterate over each cluster
			Iterator _clusterList = clusters.iterator();
			while ( _clusterList.hasNext() ) {
			
				// iterate over each cluster's data points and extract
				// the annoated data from the ontology file.
				Cluster _cluster = (Cluster)_clusterList.next();
				Iterator _dataPointList = _cluster.getDataPoints().iterator();
				while ( _dataPointList.hasNext() ) {
					ExpressionDataPoint _dataPoint = 
						(ExpressionDataPoint)_dataPointList.next();
					_outputBuffer.append( _dataPoint.getGene() );
					_outputBuffer.append("\t");
					_outputBuffer.append( _dataPoint.xCoordinate );
					_outputBuffer.append("\t");
					_outputBuffer.append( _dataPoint.yCoordinate );
					_outputBuffer.append("\n");
				}
				
				_outputBuffer.append("\n\n");
				
			} // END CLUSTER ITERATOR
			
			// write out the algorithm results to the file
			_writer.write( _outputBuffer.toString() );
			_writer.flush();
			_writer.close();
			
		} catch (IOException ioe) {
			System.out.println(OUTPUT_FILE_ERROR);
		}
	
	}
	
	/**
	 * Constructs the K-Means Algorithm class
	 * 
	 * @param clusters empty cluster set
	 * @param dataPointFile data point source file
	 */
	public KMeansAlgorithm( ArrayList clusters, 
		PCLSourceFile dataPointFile ) {
		this.clusters = clusters;
		this.dataPointFile = dataPointFile;
	}
	
	/**
	 * Runs the K-Means Algorithm on the current
	 * set of clusters and data point file. Note
	 * that the results of the algorithm display
	 * the set of clusters, their respective center
	 * points, and the set of data points that belong
	 * to each.
	 * 
	 * @return algorithm results
	 */
	public void runAlgorithm() {
		
		boolean _algorithmCompleted = false;
		ArrayList _previousClusters = new ArrayList();
		
		// check to see if the algorithm has been flagged as completed
		while ( !_algorithmCompleted ) {
			
			// initialize the data point source file
			// to ensure we start at the first point
			// in the set
			this.dataPointFile.initializeSourceFile();
			
			// iterate over each data point in the source file
			// and assign to the closest cluster.
			while ( this.dataPointFile.hasMoreElements() ) {
				
				// get the set of expression data points for the next gene
				ArrayList _expressionDataPoints = 
					(ArrayList) this.dataPointFile.nextElement();
				Iterator _pointIterator = _expressionDataPoints.
					iterator();
						
				// assign the expression data points to the appriate cluster
				while ( _pointIterator.hasNext() ) {
					this.assignToClosestCluster( 
						(DataPoint)_pointIterator.next() );
				}
				
			}

			// recalculate the centroid for each cluster and
			// reassign the cluster center appropriately.
			Iterator _clusterIterator = this.clusters.iterator();
			while ( _clusterIterator.hasNext() ) {
				Cluster _cluster = (Cluster) _clusterIterator.next();
				_cluster.setCenter( _cluster.determineCentroid() );
			}

			// check to see if the set of cluster centers have changed
			// over this data point iteration.  if so, update the
			// set of previousClusterIdentifiers.  if not, update the
			// _algorithmCompleted flag to signal algorithm completion
			if ( this.areClusterCentersDifferent(this.clusters,
				_previousClusters) ) {
				_previousClusters = (ArrayList) this.clusters.clone();
			} else {
				_algorithmCompleted = true;
			}
			
		}
		
	}
	
	/**
	 * Assigns the data point to the closest cluster
	 * @param dataPoint DataPoint to assign
	 */
	private void assignToClosestCluster( DataPoint dataPoint ) {
		
		double _closestDistance = -1;
		Cluster _closestCluster = null;
		int _experimentNumber = dataPoint.xCoordinate;
		
		Iterator _clusterIterator = this.clusters.iterator();
		while ( _clusterIterator.hasNext() ) {
			
			// fetch the cluster and calculate the distance between
			// the data point to be assigned and the cluster center
			Cluster _cluster = (Cluster) _clusterIterator.next();

			DataPoint _experimentDataPoint = (DataPoint) _cluster.
				getCenter().get( _experimentNumber-1 );
			double _distance = this.calculateEuclideanDistance( 
				dataPoint, _experimentDataPoint );

			// check to see if this is the first distance calculated
			// or is the lowest distance encountered thus far
			if ( _closestDistance == -1 || _distance < _closestDistance ) {
				_closestDistance = _distance;
				_closestCluster = _cluster;
			}
			
		}
		
		// assign the data point to the closest cluster
		_closestCluster.addDataPoint( dataPoint );
		
	}
	
	/**
	 * Determines whether or not the cluster centers between
	 * the two sets passed in are different.
	 * @param clusters set of clusters to check
	 * @param previousClusters set of clusters to compare against
	 * @return whether or not the cluster centers are different
	 */
	private boolean areClusterCentersDifferent( ArrayList clusters,
		ArrayList previousClusters ) {
		
		int _numberOfClusters = clusters.size();
		boolean _areClusterCentersDifferent = false;
		int _clustersIdentified = 0;
		
		// iterate over the set of clusters to compare.  if
		// all clusters have been accounted for then assume
		// that their centers are different.
		Iterator _clusterList = clusters.iterator();
		while ( _clusterList.hasNext() ) {
			Cluster _cluster = (Cluster) _clusterList.next();
			Iterator _existingClusterList = previousClusters.iterator();
			while ( _existingClusterList.hasNext() ) {
				if ( ((Cluster)_existingClusterList.next()).
					compareCenters(_cluster) ) {
					_clustersIdentified++;
					break;
				}
			}
		}
		
		if ( _clustersIdentified != _numberOfClusters ) {
			_areClusterCentersDifferent = true;
		}
		
		return _areClusterCentersDifferent;

	}
	
	// TODO: update this class to be more robust by implementing
	// additional distance calculation methods and allowing the
	// client to specify which method to employ.

	/**
	 * Calculates the euclidean distance between data
	 * points.
	 * 
	 * @param point1 data point 1
	 * @param point2 data point 2
	 * @return euclidean distance
	 */	
	private double calculateEuclideanDistance( DataPoint point1,
		DataPoint point2 ) {
		double px_qx = point1.getXCoordinate() - point2.getXCoordinate();
		double py_qy = point1.getYCoordinate() - point2.getYCoordinate();
		return Math.sqrt( (px_qx * px_qx) + (py_qy * py_qy) );
	}
	
}
