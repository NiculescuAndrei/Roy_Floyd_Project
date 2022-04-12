import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator {
	
	public Random rand = new Random();
	
	// Number of the vertices in the graph. This variable will receive a value from the input file
	public int numberOfNodes = 50;
	// Number of the edges in the graph. This variable will receive a value from the input file
	public int numberOfEdges = 100;
	
	// function to return a random int number between a given range [min, max]
	public double getRandomIntegerBetweenRange(double min, double max)
	{
		   double x = (int)(Math.random()*((max-min)+1))+min;
		   return x;
	}
	
	public List<Node> graphGenerator()
	{
		List<Node> list = new ArrayList<Node>();
		
		for(int i=0;i<numberOfNodes;i++)
		{
			list.add(new Node(i));
			list.get(i).addNeighbour(list.get(i), 0);
		}
		
		// With the remaining number of edges we randomly select 2 nodes and if there is no
		// edge between them, we build one.
		for(int i = 1; i <= numberOfEdges; i++)
		{
			// Variable used to remember if there is an edge or not between the 2 randomly generated nodes
			boolean checkExistingEdge = false;

			// We exit this loop only when there is an edge between the 2 cities. Otherwise we build one.
			while(true)
			{
				int randomNode1 = rand.nextInt(list.size());
				int randomNode2 = rand.nextInt(list.size());

				while(randomNode2 == randomNode1)
				{
					randomNode2 = rand.nextInt(list.size());
				}

				// Iterate through the neighbours of first node to check if there is an edge to the second generated node
				for(int j = 0; j < list.get(randomNode1).adjacencyList.size(); j++)
				{
					// Check if there is an edge between the 2 selected nodes
					if(list.get(randomNode1).adjacencyList.get(j).target == list.get(randomNode2))
					{
						checkExistingEdge = true;
						break;
					}
				}
				// If there is no edge then build one
				if(checkExistingEdge == false)
				{
					int randomNumber = (int) getRandomIntegerBetweenRange(50,500);
					list.get(randomNode1).addNeighbour(list.get(randomNode2), randomNumber);
					break;
				}
				checkExistingEdge = false;
			}
		}
				
		
		return list;
	}

}
