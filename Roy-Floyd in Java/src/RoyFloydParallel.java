
public class RoyFloydParallel extends Thread {
	
	public RoyFloydParallelMonitor monitor;
	public int threadNumber;
	
	public RoyFloydParallel(RoyFloydParallelMonitor monitor, int number)
	{
		this.monitor = monitor;
		threadNumber = number;
	}
	
	public void run()
	{
		for(int k=threadNumber*10;k<(threadNumber+1)*10;k++)
		{
			for(int i = 0; i < monitor.nodeList.size(); i++)
			{
				for(int j = 0; j < monitor.nodeList.size(); j++)
				{
					int distIK=1001;
					int distKJ=1001;
					int distIJ=1001;
					
					// Search in the adjacenyList of node i for an edge to the node k. If such an edge exists, save the cost of it in distIK
					for(int x = 0; x<monitor.nodeList.get(i).adjacencyList.size();x++)
					{
						Edge aux = monitor.nodeList.get(i).adjacencyList.get(x);
						if(aux.target == monitor.nodeList.get(k))
						{
							distIK = aux.cost;
							break;
						}
					}
					
					// Search in the adjacenyList of node k for an edge to the node j. If such an edge exists, save the cost of it in distKJ
					for(int x = 0; x<monitor.nodeList.get(k).adjacencyList.size();x++)
					{
						Edge aux = monitor.nodeList.get(k).adjacencyList.get(x);
						if(aux.target == monitor.nodeList.get(j))
						{
							distKJ = aux.cost;
							break;
						}
					}
					
					boolean check = false;
					// Search in the adjacenyList of node i for an edge to the node j. If such an edge exists, save the cost of it in distIJ
					// If the distance from node i to k plus the distance from node k to j is less than the distance from node i to j,
					// update the distance of the edge from node i to j.
					for(int x = 0; x<monitor.nodeList.get(i).adjacencyList.size(); x++)
					{
						Edge aux = monitor.nodeList.get(i).adjacencyList.get(x);
						if(aux.target == monitor.nodeList.get(j))
						{
							distIJ = aux.cost;
							if(distIK + distKJ < distIJ)
							{
								try 
								{
									monitor.graphLock.lock();
									aux.cost = distIK+distKJ;
								}finally {
									monitor.graphLock.unlock();
								}
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
						try 
						{
							monitor.graphLock.lock();
							monitor.nodeList.get(i).addNeighbour(monitor.nodeList.get(j), distIK + distKJ);
						}finally {
							monitor.graphLock.unlock();
						}
					}
				}
			}
		}
		
		
	}
	

}
