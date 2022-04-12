import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Application {

	public static void main(String[] args) {

// v RoyFloyd secvential ---------------------------------------------------
		
		
//		File inputFile;
//		Scanner myScanner;
//		
//		for(int i=1;i<=10;i++)
//		{
//			System.out.println("test " + i + " began");
//			inputFile = new File("file"+i+"in.txt");
//			
//			int numberOfVertices=0;
//			int numberOfEdges=0;
//			
//			try {
//				myScanner = new Scanner(inputFile);
//				numberOfVertices = myScanner.nextInt();
//				numberOfEdges = myScanner.nextInt();
//				myScanner.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			BufferedWriter writer=null;
//			try {
//				writer = new BufferedWriter(new FileWriter("file"+i+"out.txt"));
//				writer.write("The graph has " + numberOfVertices + " nodes");
//				writer.newLine();
//				writer.write("The graph has " + numberOfEdges + " edges");
//				writer.newLine();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			
//			long start = System.currentTimeMillis();
//			
//			RandomGraphGenerator generator = new RandomGraphGenerator();
//			generator.numberOfNodes = numberOfVertices;
//			generator.numberOfEdges = numberOfEdges;
//			List<Node> graph = generator.graphGenerator();
//			
//			long startRoyFloyd = System.currentTimeMillis();
//			RoyFloydSequential royFloydSequential = new RoyFloydSequential();
//			royFloydSequential.royFloydSequential(graph, writer);
//			long endRoyFloyd = System.currentTimeMillis();
//			
//			long end = System.currentTimeMillis();
//			
//			long duration = end - start;
//			long durationRoyFloyd = endRoyFloyd - startRoyFloyd;
//			
//			try {
//				writer.newLine();
//				writer.write("Time taken by Roy-Floyd function is: " + durationRoyFloyd + " ms");
//				writer.newLine();
//				writer.write("Time taken by whole program is: " + duration + " ms");
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}	
//			
//			System.out.println("test " + i + " ended");
//		}
		

// v RoyFloyd paralel ---------------------------------------------------
		File inputFile;
		Scanner myScanner;
		
		for(int i=1;i<=10;i++)
		{
			System.out.println("test " + i + " began");
			inputFile = new File("file"+i+"in.txt");
			
			int numberOfVertices=0;
			int numberOfEdges=0;
			
			try {
				myScanner = new Scanner(inputFile);
				numberOfVertices = myScanner.nextInt();
				numberOfEdges = myScanner.nextInt();
				myScanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			BufferedWriter writer=null;
			try {
				writer = new BufferedWriter(new FileWriter("fileparallel"+i+"out.txt"));
				writer.write("The graph has " + numberOfVertices + " nodes");
				writer.newLine();
				writer.write("The graph has " + numberOfEdges + " edges");
				writer.newLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			long start = System.currentTimeMillis();
			
			RandomGraphGenerator generator = new RandomGraphGenerator();
			generator.numberOfNodes = numberOfVertices;
			generator.numberOfEdges = numberOfEdges;
			List<Node> graph = generator.graphGenerator();
			
			int numberOfThreads = graph.size()/10;
			
			long startRoyFloydParallel = System.currentTimeMillis();
			
			RoyFloydParallelMonitor monitor = new RoyFloydParallelMonitor(graph);
			RoyFloydParallel list[] = new RoyFloydParallel[numberOfThreads];
			
			for (int j = 0; j < numberOfThreads; j++) {
				list[j] = new RoyFloydParallel(monitor, j);
			}
			
			for(int j = 0; j < numberOfThreads; j++)
			{
				list[j].start();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				for (int j = 0; j < numberOfThreads; j++) {
					list[j].join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			long endRoyFloydParallel = System.currentTimeMillis();
			
			long end = System.currentTimeMillis();
			
			long duration = end - start;
			long durationRoyFloydParallel = endRoyFloydParallel - startRoyFloydParallel;
			
			for(int j=0;j<monitor.nodeList.size();j++)
			{
				for(Edge edge : monitor.nodeList.get(j).adjacencyList)
				{
					try {
						writer.newLine();
						writer.write("The path from node " + monitor.nodeList.get(j).number + " to node " + edge.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				writer.newLine();
				writer.write("Time taken by Roy-Floyd function is: " + durationRoyFloydParallel + " ms");
				writer.newLine();
				writer.write("Time taken by whole program is: " + duration + " ms");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			
			System.out.println("test " + i + " ended");
		}
				
	}

}
