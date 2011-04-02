public class Edge {

	/*
	 * Private members
	 */
	private String startNode;
	private String endNode;
	private int weight;

	/**
	 * Empty default constructor
	 */
	public Edge() {
	}

	/**
	 * Edge constructor
	 * 
	 * @param startNode start node name
	 * @param endNode end nod ename
	 * @param weight edge weight
	 */
	public Edge( String startNode,
		String endNode,
		int weight ) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.weight = weight;
	}

	/**
	 * Gets the edge start node
	 * 
	 * @return start node name
	 */
	public String getStartNode() {
		return this.startNode;
	}

	/**
	 * Sets the edge start node
	 * 
	 * @param startNode start node name
	 */
	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	/**
	 * Gets the edge end node
	 * 
	 * @return end node name
	 */
	public String getEndNode() {
		return this.endNode;
	}

	/**
	 * Sets the edge end node
	 * 
	 * @param endNode end node name
	 */
	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	/**
	 * Gets the edge weight
	 * 
	 * @return edge weight
	 */
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Sets the edge weight
	 * 
	 * @param weight edge weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

}