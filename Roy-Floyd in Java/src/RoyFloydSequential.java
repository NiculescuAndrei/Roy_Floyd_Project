import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class RoyFloydSequential {
	
	public void royFloydSequential(List<Node> nodeList, BufferedWriter writer)
	{
		for(int k = 0; k < nodeList.size(); k++)
		{
			for(int i = 0; i < nodeList.size(); i++)
			{
				for(int j = 0; j < nodeList.size(); j++)
				{
					int distIK=1001;
					int distKJ=1001;
					int distIJ=1001;
					
					// Search in the adjacenyList of node i for an edge to the node k. If such an edge exists, save the cost of it in distIK
					for(Edge e : nodeList.get(i).adjacencyList)
					{
						if(e.target == nodeList.get(k))
						{
							distIK = e.cost;
							break;
						}
					}
					
					// Search in the adjacenyList of node k for an edge to the node j. If such an edge exists, save the cost of it in distKJ
					for(Edge e : nodeList.get(k).adjacencyList)
					{
						if(e.target == nodeList.get(j))
						{
							distKJ = e.cost;
							break;
						}
					}
					
					boolean check = false;
					// Search in the adjacenyList of node i for an edge to the node j. If such an edge exists, save the cost of it in distIJ
					// If the distance from node i to k plus the distance from node k to j is less than the distance from node i to j,
					// update the distance of the edge from node i to j.
					for(Edge e : nodeList.get(i).adjacencyList)
					{
						if(e.target == nodeList.get(j))
						{
							distIJ = e.cost;
							if(distIK + distKJ < distIJ)
							{
								e.cost = distIK+distKJ;
							}
							check = true;
							break;
						}
					}
					// If there is no edge between node i and j but the distance from node i to k plus the distance from node k to j
					// is less than 999 (which means there are 2 edges forming a path from i to j through k), then add an edge
					// in the adjacenylist of node i which leads to node k.
					if(distIK + distKJ<999 && check == false)
					{
						nodeList.get(i).addNeighbour(nodeList.get(j), distIK + distKJ);
					}
				}
			}
		}
		
		for(int i=0;i<nodeList.size();i++)
		{
			for(Edge edge : nodeList.get(i).adjacencyList)
			{
				try {
					writer.newLine();
					writer.write("The path from node " + nodeList.get(i).number + " to node " + edge.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
