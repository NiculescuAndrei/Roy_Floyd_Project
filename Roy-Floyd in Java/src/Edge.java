
public class Edge 
{
	// weight of the edge
	public int cost;
	// "destination" of the edge. 
	public final Node target;
	
	public Edge(Node target, int cost)
	{
		this.target = target;
		this.cost = cost;
	}
	
	public String toString()
	{
		return target.number + " costs " + cost;
	}
}

