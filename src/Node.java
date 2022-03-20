import java.util.ArrayList;
import java.util.List;

public class Node 
{
	int number;
	
	public Node(int number) {
		this.number = number;
	}
	
	// Every node has a list of the edges leading to nearby nodes.
	public List<Edge> adjacencyList = new ArrayList<Edge>();
		
	// Function to build the edges between the nodes.
	public void addNeighbour(Node neighbour, int cost)
	{
		Edge edge = new Edge(neighbour,cost);
		adjacencyList.add(edge);
	}
	
	public String toString()
	{
	    return "Node " + number;
    }
}
