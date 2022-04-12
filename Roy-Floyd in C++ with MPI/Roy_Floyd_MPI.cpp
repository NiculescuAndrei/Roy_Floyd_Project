#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <iostream>
#include <fstream>

using namespace std;

#define INFINITY_VALUE 1001 

void Read_matrix(int local_mat[], int n, int e, int my_rank, int p, MPI_Comm comm);
void Print_matrix(int local_mat[], int n, int e, double durationRoyFloyd, double durationProgram, int my_rank, int p, MPI_Comm comm);
void Floyd(int local_mat[], int n, int my_rank, int p, MPI_Comm comm);
int Owner(int k, int p, int n);
void Copy_row(int local_mat[], int n, int p, int row_k[], int k);

int main(int argc, char* argv[]) {
    int n;
    int e;
    int* local_mat;
    MPI_Comm comm;
    int p, my_rank;

    MPI_Init(&argc, &argv);
    comm = MPI_COMM_WORLD;
    MPI_Comm_size(comm, &p);
    MPI_Comm_rank(comm, &my_rank);

    srand(time(NULL));

    double startProgram = MPI_Wtime();
    if (my_rank == 0) {
        ifstream myInputFile("E:/Visual Studio 2019 programe/LaboratorAPD/LaboratorAPD/input files/file10in.txt");
        if (myInputFile.is_open())
        {
            myInputFile >> n;
            myInputFile >> e;
        }
        myInputFile.close();
    }

    MPI_Bcast(&n, 1, MPI_INT, 0, comm);
    MPI_Bcast(&e, 1, MPI_INT, 0, comm);

    local_mat = (int*)malloc(n * n / p * sizeof(int));

    Read_matrix(local_mat, n, e, my_rank, p, comm);

    double startRoyFloyd = MPI_Wtime();
    Floyd(local_mat, n, my_rank, p, comm);
    double endRoyFloyd = MPI_Wtime();
    double durationRoyFloyd = endRoyFloyd - startRoyFloyd;

    double endProgram = MPI_Wtime();
    double durationProgram = endProgram - startProgram;

    Print_matrix(local_mat, n, e, durationRoyFloyd, durationProgram, my_rank, p, comm);

    free(local_mat);
    MPI_Finalize();

    return 0;
}

/*---------------------------------------------------------------------
 * Function:  Read_matrix
 * Purpose:   Read in the local_matrix on process 0 and scatter it using a
 *            block row distribution among the processes. Each process
 *            will have n/p rows.
 */
void Read_matrix(int local_mat[], int n, int e, int my_rank, int p, MPI_Comm comm)
{
    int i, j;
    int* temp_mat = NULL;

    if (my_rank == 0)
    {
        temp_mat = (int*)malloc(n * n * sizeof(int));

        for (i = 0; i < n; i++)
        {
            for (j = 0; j < n; j++)
            {
                if (i == j)
                {
                    temp_mat[i * n + j] = 0;
                }
                else
                {
                    temp_mat[i * n + j] = INFINITY_VALUE;
                }
            }
        }

        for (i = 1; i <= e; i++)
        {
            int randomI = rand() % n;
            int randomJ = rand() % n;
            int randomCost = rand() % 451 + 50;

            while (temp_mat[randomI * n + randomJ] != INFINITY_VALUE)
            {
                randomI = rand() % n;
                randomJ = rand() % n;
            }

            temp_mat[randomI * n + randomJ] = randomCost;
        }

        MPI_Scatter(temp_mat, n * n / p, MPI_INT, local_mat, n * n / p, MPI_INT, 0, comm);
        free(temp_mat);
    }
    else
    {
        MPI_Scatter(temp_mat, n * n / p, MPI_INT, local_mat, n * n / p, MPI_INT, 0, comm);
    }

}

/*---------------------------------------------------------------------
 * Function:  Print_matrix
 * Purpose:   Gather the distributed matrix onto process 0 and print it.
 */
void Print_matrix(int local_mat[], int n, int e, double durationRoyFloyd, double durationProgram, int my_rank, int p, MPI_Comm comm)
{
    int i, j;
    int* temp_mat = NULL;

    if (my_rank == 0)
    {
        temp_mat = (int*)malloc(n * n * sizeof(int));
        MPI_Gather(local_mat, n * n / p, MPI_INT, temp_mat, n * n / p, MPI_INT, 0, comm);

        ofstream myOutputFile("E:/Visual Studio 2019 programe/LaboratorAPD/LaboratorAPD/output files/file10out.txt");
        myOutputFile << "The graph has " << n << " nodes \n";
        myOutputFile << "The graph has " << e << " edges \n";
        myOutputFile << "There are " << p << " processes working in parallel \n\n";

        for (i = 0; i < n; i++)
        {
            for (j = 0; j < n; j++)
            {
                if (temp_mat[i * n + j] != INFINITY_VALUE)
                {
                    myOutputFile << "The path from node " << i << " to node " << j << " costs " << temp_mat[i * n + j] << "\n";
                }
            }
        }
        myOutputFile << "\nTime taken by Roy-Floyd function is " << durationRoyFloyd << "s \n";
        myOutputFile << "Time taken by whole program is " << durationProgram << "s \n";
        myOutputFile.close();
        free(temp_mat);
    }
    else
    {
        MPI_Gather(local_mat, n * n / p, MPI_INT, temp_mat, n * n / p, MPI_INT, 0, comm);
    }
}

/*---------------------------------------------------------------------
 * Function:    Floyd
 * Purpose:     Implement a distributed version of Floyd's algorithm for
 *              finding the shortest path between all pairs of vertices.
 *              The adjacency matrix is distributed by block rows.
 */
void Floyd(int local_mat[], int n, int my_rank, int p, MPI_Comm comm) {
    int global_k, local_i, global_j, temp;
    int root;
    int* row_k = (int*)malloc(n * sizeof(int));

    for (global_k = 0; global_k < n; global_k++)
    {
        // Find the process that owns the row with the global_k index.
        root = Owner(global_k, p, n);
        // If the ranks are same, the process calculates the local index
        // based on the global_k, and he copies the row in order to
        // broadcast it to all other processes that can not acces it
        // since it's in his process memory.
        if (my_rank == root)
        {
            Copy_row(local_mat, n, p, row_k, global_k);
        }

        MPI_Bcast(row_k, n, MPI_INT, root, comm);

        for (local_i = 0; local_i < n / p; local_i++)
        {
            for (global_j = 0; global_j < n; global_j++)
            {
                temp = local_mat[local_i * n + global_k] + row_k[global_j];

                if (temp < local_mat[local_i * n + global_j])
                {
                    local_mat[local_i * n + global_j] = temp;
                }
            }
        }
    }
    free(row_k);
}

/*---------------------------------------------------------------------
 * Function:  Owner
 * Purpose:   Return rank of process that owns global row k
 */
int Owner(int k, int p, int n)
{
    return k / (n / p);
}

/*---------------------------------------------------------------------
 * Function:  Copy_row
 * Purpose:   Copy the row with *global* subscript k into row_k
 */
void Copy_row(int local_mat[], int n, int p, int row_k[], int k)
{
    int j;
    int local_k = k % (n / p);

    for (j = 0; j < n; j++)
    {
        row_k[j] = local_mat[local_k * n + j];
    }

}