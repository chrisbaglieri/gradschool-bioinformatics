import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class UndirectedGraph {

	/*
	 * Private constants
	 */
	private final String ADJACENCY_MATRIX_DELIMETER = "\t";
	
	/*
	 * Private members
	 */
	private Hashtable matrix;
	private ArrayList nodes;

	/**
	 * Empty UndirectedGraph constructor
	 */
	public UndirectedGraph() {
		this.matrix = new Hashtable();
		this.nodes = new ArrayList();
	}

	/**
	 * Initializes the graph for the supplied set of
	 * nodes with a default edge weight of zero.
	 * 
	 * @param set of nodes
	 */
	public void initializeGraph( ArrayList nodes ) {

		this.nodes = nodes;
		this.matrix = new Hashtable();
		
		// iterate over the set of nodes.  for each node
		// generate a hash and add it to the matrix
		Iterator _nodeIterator = this.nodes.iterator();
		while ( _nodeIterator.hasNext() ) {

			// generate an entry for the node, defaulting
			// the weight to a value of zero
			Hashtable _nodeEntry = new Hashtable();
			Iterator _internalNodeIterator = nodes.iterator();
			while ( _internalNodeIterator.hasNext() ) {
				_nodeEntry.put( (String)_internalNodeIterator.next(), "0" );
			}

			// add the node entry hash to the primary matrix
			this.matrix.put( (String)_nodeIterator.next(), 
				_nodeEntry );

		}

	}

	/**
	 * Imports the graph data from a specified file.
	 * Note that the file must be a tab delimited
	 * undirected graph adjacency matrix and include
	 * the node names on both the X and Y axis.
	 * Example follows:
	 * 
	 * 			A	B	C	D
	 * 		A	0	1	2	3
	 * 		B	1	0	3	3
	 * 		C	2	3	0	4
	 * 		D	3	3	4	0
	 * 
	 * @param filename adjacency matrix filepath
	 * @exception FileNotFoundException the adjacency file does not exist
	 * @exception IOException unable to open the adjacency file
	 */
	public void importGraphData( String filename ) 
		throws FileNotFoundException, IOException {

		this.matrix = new Hashtable();
		BufferedReader _reader = new BufferedReader( 
			new FileReader(filename) );
		
		// generate the set of nodes
		String[] _startNodeList = _reader.readLine().split("\t");
		for( int _index = 0; _index < _startNodeList.length; _index++ ) {
			String _nodeHeaderItem = _startNodeList[_index].trim();
			if ( _nodeHeaderItem.length() != 0 ) {
				this.nodes.add( _nodeHeaderItem );
			}
		}
		
		// iterate over the graph data and build the matrix
		String _graphDataLine = _reader.readLine();
		while ( _graphDataLine != null ) {
			
			String[] _graphDataLineEntries = _graphDataLine.split("\t");
			String _endNode = _graphDataLineEntries[0].trim();
			
			// iterate over the start nodes and add the appropriate
			// weight for each edge.
			Iterator _startNodeIterator = this.nodes.iterator();
			int _edgeWeightIndex = 1;
			Hashtable _endNodeHash = new Hashtable();
			while( _startNodeIterator.hasNext() ) {
				String nodeName = (String)_startNodeIterator.next();
				_endNodeHash.put( nodeName,
					_graphDataLineEntries[_edgeWeightIndex++] );
			}

			// add the end node hash to the matrix
			this.matrix.put( _endNode, _endNodeHash );

			// read the next line from the file
			_graphDataLine = _reader.readLine();

		}

		// close the reader
		_reader.close();

	}

	/**
	 * Gets the set of nodes that make up the graph
	 */
	public ArrayList getNodes() {
		return this.nodes;
	}

	/**
	 * Gets the specified edge from the graph.
	 * 
	 * @param startNode start node name
	 * @param endNode end node name
	 * @return requested edge or null if not found
	 */
	public Edge getEdge( String startNode,
		String endNode ) {
		
		Edge _edge = null;
		Object _startNodeEntry = this.matrix.get(startNode);
		Object _endNodeEntry = this.matrix.get(endNode);

		if ( _startNodeEntry != null && _endNodeEntry != null ) {
			_edge = new Edge( startNode,
				endNode,
				Integer.parseInt( 
				(String)((Hashtable)_startNodeEntry).get(endNode)) );
		}

		return _edge;

	}

	/**
	 * Gets the set of edges that are connected
	 * to a specified node.
	 * 
	 * @param node node of interest
	 * @return set of connected edges
	 */
	public Hashtable getFringeEdgeSet( String nodeName ) {

		Hashtable _fringeEdgeSet = null;
		Object _nodeEntry = this.matrix.get(nodeName);

		if ( _nodeEntry != null ) {
			
			Hashtable _nodeHash = (Hashtable) _nodeEntry;
			
			// iterate over the items in the node entry hash.
			// add an edge for any item that has a weight greater
			// than zero.  note that since this is an undirected
			// graph, we can assume that the node passed into
			// this method can be either the start or end node
			_fringeEdgeSet = new Hashtable();
			Enumeration _keys = _nodeHash.keys();
			while ( _keys.hasMoreElements() ) {
				
				// get end node name
				String _endNodeName = (String)_keys.
					nextElement();
				
				// get edge weight
				int _weight = Integer.parseInt( 
					(String)_nodeHash.get(_endNodeName) );
				
				if ( _weight > 0 ) {
					_fringeEdgeSet.put( _endNodeName, 
						new Edge( nodeName, 
						_endNodeName,
						_weight) );
				} // END IF
				
			} // END WHILE

		} // END IF

		return _fringeEdgeSet;

	}
	
	/**
	 * Gets the complete set of edges for this graph
	 * 
	 * @return complete edge set
	 */
	public Hashtable getCompleteEdgeSet() {
		
		Hashtable _completeEdgeSet = new Hashtable();
		
		// iterate over the set of nodes and construct the
		// complete edge set
		Iterator _nodeList = this.nodes.iterator();
		while ( _nodeList.hasNext() ) {
			
			String _startNode = (String) _nodeList.next();
		
			Hashtable _weightList = (Hashtable) this.matrix.get(_startNode);
			Enumeration _keys = _weightList.keys();
			while ( _keys.hasMoreElements() ) {
			
				String _endNode = (String) _keys.nextElement();
				int _edgeWeight = Integer.parseInt( 
					(String)_weightList.get(_endNode) );
					
				// if an edge exists and has not already been added
				// in the "reverse direction", add it to the set
				if ( _edgeWeight > 0 && !this.CheckForReverse( 
					_startNode, _endNode, _completeEdgeSet) ) {
					_completeEdgeSet.put( _startNode + _endNode, 
						new Edge(_startNode, _endNode, _edgeWeight) );
				}
				
			}
			
		}
		
		return _completeEdgeSet;
	
	}
	
	/**
	 * Checks for a reverse edge in the set of edges
	 * 
	 * @param startNode start node
	 * @param endNode end node
	 * @param edgeSet set of edges
	 * @return whether or not the reverse edge exists
	 */
	private boolean CheckForReverse( String startNode,
		String endNode,
		Hashtable edgeSet ) {
			
		boolean _reverseExists = false;
			
		// iterate over each edge in the set
		// reverse the key and compare it with
		// the start and end nodes passed in
		Enumeration _keys = edgeSet.keys();
		while ( _keys.hasMoreElements() && !_reverseExists ) {
			_reverseExists = ((String)_keys.nextElement()).
				equals(endNode+startNode);
		}
			
		return _reverseExists;
	}

	/**
	* Updates the specified edge in the graph.  If
	* the edge does not exist, method does nothing.
	* 
	* @param edge item to update
	*/
	public void setEdge( Edge edge ) {
			
		Object _startNodeEntry = this.matrix.
			get( edge.getStartNode() );

		Object _endNodeEntry = this.matrix.
			get( edge.getEndNode() );

		if ( _startNodeEntry != null && _endNodeEntry != null ) {

			Hashtable _startNodeHash = (Hashtable) _startNodeEntry;
			Hashtable _endNodeHash = (Hashtable) _endNodeEntry;

			// remove the existing edges
			_startNodeHash.remove( edge.getEndNode() );
			_endNodeHash.remove( edge.getStartNode() );

			// add the new edges
			_startNodeHash.put( edge.getEndNode(), 
				String.valueOf(edge.getWeight()) );
			_endNodeHash.put( edge.getStartNode(), 
				String.valueOf(edge.getWeight()) );

		}

	}

	/**
	 * Converts the undirected graph to a string
	 */
	public String toString() {
		
		StringBuffer _matrixResults = new StringBuffer();
				
		// generate the first line of nodes
		_matrixResults.append( ADJACENCY_MATRIX_DELIMETER );
		Enumeration _nodeNameEnumeration = this.matrix.keys();
		while ( _nodeNameEnumeration.hasMoreElements() ) {
			_matrixResults.append( (String)_nodeNameEnumeration.nextElement() );
			_matrixResults.append( ADJACENCY_MATRIX_DELIMETER );
		}
		_matrixResults.append("\n");

		// iterate over each node in the matrix, retrieve the hashtable,
		// and append each entry to the overall matrix string
		_nodeNameEnumeration = this.matrix.keys();
		while ( _nodeNameEnumeration.hasMoreElements() ) {
	
			// append the node name currently being operated on
			String _nodeName = (String)_nodeNameEnumeration.nextElement();
			_matrixResults.append(_nodeName);
			_matrixResults.append( ADJACENCY_MATRIX_DELIMETER );
	
			// iterate over each of the node entry items appending them to
			// string buffer
			Hashtable _nodeEntry = (Hashtable) this.matrix.get(_nodeName);
			Enumeration _nodeEntryItems = _nodeEntry.elements();
			while ( _nodeEntryItems.hasMoreElements() ) {
				_matrixResults.append( (String)_nodeEntryItems.nextElement() );
				_matrixResults.append( ADJACENCY_MATRIX_DELIMETER );
			}
			_matrixResults.append("\n");
	
		}

		return _matrixResults.toString();

	}

}