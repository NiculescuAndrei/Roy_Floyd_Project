#include <iostream>
#include <stdio.h>
#include <fstream>
#include <vector>
#include <algorithm>
#include <execution>
#include <chrono>
#include <string>
using namespace std::chrono;

#define INFINITY_VALUE 1001 

void readMatrix(std::vector<std::vector<int>> &graph, int numberOfNodes, int numberOfEdges);
void printMatrix(std::vector<std::vector<int>>& graph, int numberOfNodes, int numberOfEdges, milliseconds durationRoyFloyd, milliseconds durationProgram, int testNumber);
void royFloyd(std::vector<std::vector<int>>& graph, int numberOfNodes);

int main()
{
    std::vector<std::vector<int>> graph;
    int numberOfNodes;
    int numberOfEdges;
    srand(time(NULL));

    for (int i = 1; i <= 10; i++)
    {
        std::cout << "Test number " << i << " started " << std::endl;

        auto startProgram = high_resolution_clock::now();

        std::string path = "E:/Visual Studio 2019 programe/RoyFloydSTL/RoyFloydSTL/input files/file" + std::to_string(i) + "in.txt";
        std::ifstream myInputFile(path);

        if (myInputFile.is_open())
        {
            myInputFile >> numberOfNodes;
            myInputFile >> numberOfEdges;
        }
        myInputFile.close();

        readMatrix(graph, numberOfNodes, numberOfEdges);

        auto startRoyFloyd = high_resolution_clock::now();
        royFloyd(graph, numberOfNodes);
        auto endRoyFloyd = high_resolution_clock::now();
        auto durationRoyFloyd = duration_cast<milliseconds>(endRoyFloyd - startRoyFloyd);

        auto endProgram = high_resolution_clock::now();
        auto durationProgram = duration_cast<milliseconds>(endProgram - startProgram);

        printMatrix(graph, numberOfNodes, numberOfEdges, durationRoyFloyd, durationProgram, i);
        graph.clear();
        std::cout << "Test number " << i << " ended " << std::endl;
    }
}

// Function that initializes the graph
void readMatrix(std::vector<std::vector<int>>& graph, int numberOfNodes, int numberOfEdges)
{
    std::vector <int> localVector;

    // Put 0 where i == j and INFINITY_VALUE on other positions
    for (int i = 0; i < numberOfNodes; i++)
    {
        for (int j = 0; j < numberOfNodes; j++)
        {
            if (i == j)
            {
                localVector.push_back(0);
            }
            else
            {
                localVector.push_back(INFINITY_VALUE);
            }
        }
        graph.push_back(localVector);
        localVector.clear();
    }

    // Randomly generate 2 indexes that point to a position in the adjacency matrix
    // If the value on that position is INFINITY_VALUE, then replace it with the randomCost variable
    // Otherwise generate another pair of indexes until we find a position that has INFINITY_VALUE assigned to it
    for (int i = 1; i <= numberOfEdges; i++)
    {
        int randomI = rand() % numberOfNodes;
        int randomJ = rand() % numberOfNodes;
        int randomCost = rand() % 451 + 50;

        while (graph[randomI][randomJ] != INFINITY_VALUE)
        {
            randomI = rand() % numberOfNodes;
            randomJ = rand() % numberOfNodes;
        }

        graph[randomI][randomJ] = randomCost;
    }
}

// Function that writes in the specific output file, depending on testNumber variable, the following informations: the numberOfNodes, numberOfEdges,
// the shortest path from a node to another only if it is not equal to INFINITY_VALUE, time taken by RoyFloyd function and time taken by whole program.
void printMatrix(std::vector<std::vector<int>>& graph, int numberOfNodes, int numberOfEdges, milliseconds durationRoyFloyd, milliseconds durationProgram, int testNumber)
{
    std::string path = "E:/Visual Studio 2019 programe/RoyFloydSTL/RoyFloydSTL/output files/file" + std::to_string(testNumber) + "out.txt";
    std::ofstream myOutputFile(path);

    myOutputFile << "The graph has " << numberOfNodes << " nodes \n";
    myOutputFile << "The graph has " << numberOfEdges << " edges \n";

    for (int i = 0; i < numberOfNodes; i++)
    {
        for (int j = 0; j < numberOfNodes; j++)
        {
            if (graph[i][j] != INFINITY_VALUE)
            {
                myOutputFile << "The path from node " << i << " to node " << j << " costs " << graph[i][j] << "\n";
            }
        }
    }

    myOutputFile << "\nTime taken by Roy-Floyd function is " << durationRoyFloyd.count() << " ms \n";
    myOutputFile << "Time taken by whole program is " << durationProgram.count() << " ms \n";
    myOutputFile.close();
}

// Function that implements the Roy Floyd algorithm
void royFloyd(std::vector<std::vector<int>>& graph, int numberOfNodes)
{
    // In order to parallelize the i-loop, I used a vector that holds elements referring to one row each.
    // This vector is an "index array" that holds all i-values exactly once.
    // I use for_each executed in parallel where each partition of the vector refers to a partition of the matrix (to a row)
    std::vector <int> indexArray;

    for (int i = 0; i < numberOfNodes; i++)
    {
        indexArray.push_back(i);
    }

    for (int k = 0; k < numberOfNodes; k++)
    {
        std::for_each(std::execution::par_unseq, std::begin(indexArray), std::end(indexArray), [&](int i)
            {
                for (int j = 0; j < numberOfNodes; j++)
                {
                    if (graph[i][k] + graph[k][j] < graph[i][j])
                    {
                        graph[i][j] = graph[i][k] + graph[k][j];
                    }
                }
            });
    }
}