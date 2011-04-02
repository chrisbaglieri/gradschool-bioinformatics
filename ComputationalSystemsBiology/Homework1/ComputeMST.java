import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class ComputeMST {

	/*
	 * Private static constants
	 */
	private static final String USAGE = "USAGE: ComputeMST adjacencyMatrixFile startNode";
	private static final String MISSING_FILE = "Matrix data file cannot be found.";
	private static final String UNABLE_TO_READ = "Unable to open matrix data file.";
	private static final String INVALID_START_NODE = "Invalid start node.";
	
	/*
	 * Private members
	 */
	private Calendar algorithmStartTime;
	private Calendar algorithmEndTime;
	
	/**
	 * Main entry point to the ComputeMST class
	 * 
	 * @param args command line arguments
	 */
	public static void main( String[] args ) {
				
		String _adjacencyMatrixFile;
		String _startNode;
	
		// retrieve the arguments from the command line
		// if not provided, display the usage and quit
		if ( args.length == 2 ) {
			_adjacencyMatrixFile = args[0];
			_startNode = args[1];
		} else {
			System.out.println(USAGE);
			return;
		}
		
		// generate the undirected graph and load
		// the data from the matrix file
		UndirectedGraph _graph = new UndirectedGraph();
		try {
			_graph.importGraphData( _adjacencyMatrixFile );
		} catch (FileNotFoundException fnfe) {
			System.out.println(MISSING_FILE);
			return;
		} catch (IOException ioe) {
			System.out.println(UNABLE_TO_READ);
			return;
		}
		
		ComputeMST _mstCalculator = new ComputeMST();
		
		// run the Dijkstra-Prim MST Algorithm
		String _primResults = _mstCalculator.
			computeMSTByPrim(_graph, _startNode);
		
		// display the Dijkstra-Prim Algorithm results
		System.out.println("PRIM ALGORITHM RESULTS (Overall Runtime (ms) = " + 
			(_mstCalculator.algorithmEndTime.getTimeInMillis() - 
			_mstCalculator.algorithmStartTime.getTimeInMillis()) + ")" );
		System.out.println(_primResults);
		
		// run the Kruskal MST Algorithm
		String _kruskalResults = _mstCalculator.
			computeMSTByKruskal(_graph);
		
		// display the Kruskal Algorithm results
		System.out.println("KRUSKAL ALGORITHM RESULTS (Overall Runtime (ms) = " + 
			(_mstCalculator.algorithmEndTime.getTimeInMillis() - 
			_mstCalculator.algorithmStartTime.getTimeInMillis()) + ")" );
		System.out.println(_kruskalResults);
		
	}

	/**
	 * Computes the MST using the Dijkstra-Prim Algorithm
	 * 
	 * @param graph undirected graph to operate on
	 * @param startNode node to start from
	 * @return string representation of the MST
	 */
	private String computeMSTByPrim( UndirectedGraph graph,
		String startNode ) {
			
		// set the algorithm start time
		algorithmStartTime = Calendar.getInstance();

		// generate an empty fringe set
		Hashtable _fringeSet = new Hashtable();

		// generate a default empty graph
		UndirectedGraph _mst = new UndirectedGraph();
		_mst.initializeGraph( graph.getNodes() );

		// get the fringe set for the starting node
		_fringeSet = graph.getFringeEdgeSet( startNode );
		
		// validate that the supplied start node exists
		if ( _fringeSet == null ) {
			System.out.println(INVALID_START_NODE);
			System.exit(0);
		}

		// iterate over the set of unprocessed nodes
		int _unprocessedNodesCount = graph.getNodes().size() - 1;
		while ( _unprocessedNodesCount != 0 ) {

			// get the lightest edge in the fringe set and udpate the MST
			Edge _lightestEdge = (Edge) this.getLightestFringeEdges(
				_fringeSet).get(0);
			_mst.setEdge( _lightestEdge );

			// fetch the set of new fringe edges for the
			// lightest edge just processed
			Hashtable _newFringeEdgeSet = graph.getFringeEdgeSet( 
				_lightestEdge.getEndNode() );
				
			// remove the lightest edge from the fringe set
			// since this is an undirected graph and it has
			// already been processed
			_newFringeEdgeSet.remove(_lightestEdge.getStartNode());

			// remove the lightest edge from the fringe set
			_fringeSet.remove( _lightestEdge.getEndNode() );

			// consolidate the fringe set with the lowest weight edges
			this.consolidateFringeSet( _newFringeEdgeSet,
				_fringeSet );
				
			// decrement the unprocessed node count
			_unprocessedNodesCount--;

		}
		
		// set the algorithm end time
		algorithmEndTime = Calendar.getInstance();

		return _mst.toString();

	}
	
	/**
	 * Computes the MST using the Kruskal Algorithm
	 * 
	 * @param graph undirected graph to operate on
	 * @return string representation of the MST
	 */
	public String computeMSTByKruskal( UndirectedGraph graph ) {
		
		// set the algorithm start time
		algorithmStartTime = Calendar.getInstance();

		// generate a default empty graph
		UndirectedGraph _mst = new UndirectedGraph();
		_mst.initializeGraph( graph.getNodes() );
		
		// build nonprocessed edge and empty processed edge sets
		Hashtable _nonprocessedEdges = graph.getCompleteEdgeSet();
		
		// iterate over the set of nonprocessed edges
		// add the lightest to the MST without forming
		// any cycles between nodes.
		int _nodeCount = graph.getNodes().size()-1;
		while ( _nodeCount != 0 ) {
			
			// select the lightest edges that have not 
			// been processed yet
			ArrayList _lightestEdges = 
				this.getLightestFringeEdges(_nonprocessedEdges);
				
			// iterate over the lightest edge set. for
			// each edge, check if it forms a cycle in 
			// the current graph.  if so, skip to the
			// next edge.
			Iterator _lightestEdgeIterator = _lightestEdges.iterator();
			while ( _lightestEdgeIterator.hasNext() ) {
				
				// grab the edge
				Edge _lightestEdge = (Edge) _lightestEdgeIterator.next();
				
				// check if it forms a cycle.  if not, add it to the
				// final MST, remove the edge from the nonprocessed
				// list, and decrement the edge count.  otherwise,
				// do nothing and move to the next edge in the list.
				if ( !this.isCycle(_lightestEdge, _mst) ) {
					_mst.setEdge(_lightestEdge);
					_nonprocessedEdges.remove( _lightestEdge.getStartNode() + 
						_lightestEdge.getEndNode());
					_nodeCount--;
				} // END IF
				
			} // END INNER WHILE
		
		} // END OUTER WHILE
		
		// set the algorithm end time
		algorithmEndTime = Calendar.getInstance();
		
		return _mst.toString();
		
	}

	/**
	 * Gets the ligthest edge from the fringe set
	 * 
	 * @param fringeSet collection of fringe edges
	 * @return lightest edge set
	 */
	private ArrayList getLightestFringeEdges( Hashtable fringeSet ) {

		int _smallestWeight = -1;
		ArrayList _lightestEdges = new ArrayList();
		
		// iterate over the set of fringes to identify 
		// to smallest weight
		Enumeration _fringeEdges = fringeSet.elements();
		while ( _fringeEdges.hasMoreElements() ) {
			Edge _fringeEdge = (Edge) _fringeEdges.nextElement();
			if ( _smallestWeight != -1 ) {
				if ( _fringeEdge.getWeight() < _smallestWeight ) {
					_smallestWeight = _fringeEdge.getWeight();
				}
			} else {
				_smallestWeight = _fringeEdge.getWeight();
			}
		}
		
		// iterate over the fringe set again to aggregate
		// the set of edges into an array list
		_fringeEdges = fringeSet.elements();
		while ( _fringeEdges.hasMoreElements() ) {
			Edge _fringeEdge = (Edge) _fringeEdges.nextElement();
			if ( _fringeEdge.getWeight() == _smallestWeight ) {
				_lightestEdges.add(_fringeEdge);
			}
		}

		return _lightestEdges;

	}

	/**
	 * Consolidates the current fringe set with the lowest
	 * possible edges to the current set of connected nodes
	 * 
	 * @param newFringeSet new set of edges to consider
	 * @param currentFringeSet current set of fringes available
	 */
	private void consolidateFringeSet( Hashtable newFringeSet,
		Hashtable currentFringeSet ) {
		
		Enumeration _newFringeEdges = newFringeSet.elements();
		
		// iterate over the new fringe set and examine each
		// of the end nodes.  if the end node does not exist,
		// add it to the current set.  if the end node does
		// exist, examine the weight.  if the weight for the
		// end node in the current set is greater, remove
		// the edge from the current set and add the lighter
		while ( _newFringeEdges.hasMoreElements() ) {
			
			Edge _newFringeEdge = (Edge) _newFringeEdges.nextElement();
			
			if ( currentFringeSet.containsKey(
				_newFringeEdge.getEndNode()) ) {
					
				// get the edge from the current set
				Edge _currentFringeEdge = (Edge) currentFringeSet.
					get(_newFringeEdge.getEndNode());
					
				// compare the edge weights and remove
				// and add the new edge if lighter
				if ( _newFringeEdge.getWeight() < 
					_currentFringeEdge.getWeight() ) {
						
					currentFringeSet.remove( _currentFringeEdge.
						getEndNode() );
					currentFringeSet.put( _newFringeEdge.getEndNode(),
						_newFringeEdge );
				}
					
			} else {
				
				// add edge to current fringe set
				currentFringeSet.put( _newFringeEdge.getEndNode(),
					_newFringeEdge );
				
			} // END IF
			
		} // END WHILE
		
	} // END METHOD
	
	/**
	 * Checks whether or not a particular edge forms
	 * a cycle in the specified graph traversing through
	 * the nodes within the graph.
	 * 
	 * @param edge edge
	 * @param graph undirected graph to check against
	 * @return whether or not the edge produces a cycle
	 */
	private boolean isCycle( Edge edge, 
		UndirectedGraph graph ) {
		
		boolean _cycleExists = false;
			
		// get fringe edge sets
		Hashtable _startNodeFringeEdgeSet = graph.
			getFringeEdgeSet(edge.getStartNode());
		Hashtable _endNodeFringeEdgeSet = graph.
			getFringeEdgeSet(edge.getEndNode());
			
		// iterate over the start fringes and check for cycles
		// in the end node fringes
		Enumeration _startNodeFringes = _startNodeFringeEdgeSet.elements();
		while ( _startNodeFringes.hasMoreElements() && !_cycleExists ) {
			
			Edge _startNodeFringeEdge = (Edge) _startNodeFringes.nextElement();
			Enumeration _endNodeFringes = _endNodeFringeEdgeSet.elements();
			
			while ( _endNodeFringes.hasMoreElements() && !_cycleExists ) {
				Edge _endNodeFringeEdge = (Edge) _endNodeFringes.nextElement();
				if ( _startNodeFringeEdge.getEndNode().
					equals(_endNodeFringeEdge.getEndNode())
					&& (_startNodeFringeEdge.getWeight() == 0 
					|| _endNodeFringeEdge.getWeight() == 0) ) {
						_cycleExists = true;
				}
			}
			
		}
		
		return _cycleExists;
	}

} // END CLASS