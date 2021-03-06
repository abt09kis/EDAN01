/**
 * Created by Kevin on 2016-02-03.
 */

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Logistics {
    public static void main(String[] args) {
        long T1, T2, T;
        T1 = System.currentTimeMillis();
        inputData(3);
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\n\t*** Execution time = " + T + " ms");
    }

    public static void solveForData(int graph_size, int start, int n_dests, int[] dest, int n_edges, int[] from, int[] to, int[] cost) {
        Store store = new Store();
        int[][] travelCost = new int[graph_size][graph_size];
        IntVar[][] paths = new IntVar[graph_size][graph_size];
        for(int i = 0; i < graph_size; i++){
                for(int j = 0; j < graph_size; j++){
                    paths[i][j] = new IntVar(store, 0, 1);
                    travelCost[i][j] = isConnected(i, j, from, to, cost);
                    if(travelCost[i][j] == 0) {
                    //Man kan inte välja en väg som inte har någon sträcka.
                    store.impose(new XeqC(paths[i][j],0));
                     }
                }
        }
        //Måste börja i startnoden.
        store.impose(new SumInt(store, paths[start - 1], ">=",new IntVar(store,1,1)));
        for(int i = 0; i < graph_size; i++){
            store.impose(new SumInt(store, getCol(paths,i), "<=", new IntVar(store, 1, 1)));
        }
        // Du får inte lämna en nod du aldrig kommit till.
        for(int i = 0; i < graph_size; i ++){
            if(i!=start-1) {
                PrimitiveConstraint c1 = new SumInt(store, getCol(paths, i), ">", new IntVar(store, 0, 0));
                PrimitiveConstraint c2 = new SumInt(store, paths[i], "<=", new IntVar(store, 1, 1));
                PrimitiveConstraint zero = new SumInt(store, paths[i], "==", new IntVar(store, 0, 0));
                store.impose(new IfThenElse(c1, c2, zero));
            }
        }

        // Du måste besöka slutnoderna.
        for (int i = 0; i < n_dests; i++) {
            store.impose(new SumInt(store, getCol(paths, dest[i]-1), ">=", new IntVar(store, 1, 1)));
        }


        IntVar destCost = new IntVar(store, "Cost", 0, sum(cost));
        store.impose(new SumWeight(matrixToVectorIntVar(paths), matrixToVectorInt(travelCost), destCost));

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(paths, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());



        boolean result = search.labeling(store, select, destCost);


        if (result) {
            System.out.println("Solution : " + java.util.Arrays.asList(destCost));
            printMatrix(paths);
            System.out.println("nodes: " +search.getNodes());
        } else {
            System.out.println("No solution found.");
        }


    }

    public static int isConnected(int f, int t, int[] from, int[] to, int[] cost) {
        for (int i = 0; i < from.length; i++) {
            if (t + 1 == from[i] && f + 1 == to[i]) {
                return cost[i];
            }
            if (f + 1 == from[i] && t + 1 == to[i]) {
                return cost[i];
            }
        }
        return 0;
    }

    public static int sum(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static IntVar[] matrixToVectorIntVar(IntVar[][] matrix) {
        IntVar[] vector = new IntVar[matrix.length * matrix[0].length];
        int index = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                vector[index] = matrix[i][j];
                index++;
            }
        }
        return vector;
    }

    public static int[] matrixToVectorInt(int[][] matrix) {
        int[] vector = new int[matrix.length * matrix[0].length];
        int index = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                vector[index] = matrix[i][j];
                index++;
            }
        }
        return vector;
    }

    private static IntVar[] getCol(IntVar[][] matrix, int i) {
        IntVar[] col = new IntVar[matrix.length];
        for (int j = 0; j < matrix.length; j++) {
            col[j] = matrix[j][i];
        }
        return col;
    }

    private static void printMatrix(IntVar[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j].value() + " ");
            }
            System.out.print("\n");
        }
    }

    public static void inputData(int ex) {
        switch (ex) {
            case 1:
                int graph_size1 = 6;
                int start1 = 1;
                int n_dests1 = 1;
                int[] dest1 = {6};
                int n_edges1 = 7;
                int[] from1 = {1, 1, 2, 2, 3, 4, 4};
                int[] to1 = {2, 3, 3, 4, 5, 5, 6};
                int[] cost1 = {4, 2, 5, 10, 3, 4, 11};
                solveForData(graph_size1, start1, n_dests1, dest1, n_edges1, from1, to1, cost1);
                break;
            case 2:
                int graph_size2 = 6;
                int start2 = 1;
                int n_dests2 = 2;
                int[] dest2 = {5, 6};
                int n_edges2 = 7;
                int[] from2 = {1, 1, 2, 2, 3, 4, 4};
                int[] to2 = {2, 3, 3, 4, 5, 5, 6};
                int[] cost2 = {4, 2, 5, 10, 3, 4, 11};
                solveForData(graph_size2, start2, n_dests2, dest2, n_edges2, from2, to2, cost2);
                break;
            case 3:
                int graph_size3 = 6;
                int start3 = 1;
                int n_dests3 = 2;
                int[] dest3 = {5, 6};
                int n_edges3 = 9;
                int[] from3 = {1, 1, 1, 2, 2, 3, 3, 3, 4};
                int[] to3 = {2, 3, 4, 3, 5, 4, 5, 6, 6};
                int[] cost3 = {6, 1, 5, 5, 3, 5, 6, 4, 2};
                solveForData(graph_size3, start3, n_dests3, dest3, n_edges3, from3, to3, cost3);
                break;
        }

    }
}
