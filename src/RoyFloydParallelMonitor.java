import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RoyFloydParallelMonitor {
	List<Node> nodeList = new ArrayList<Node>();
	ReentrantLock graphLock = new ReentrantLock();
	
	public RoyFloydParallelMonitor(List<Node> graph)
	{
		nodeList = graph;
	}
	
	

}
