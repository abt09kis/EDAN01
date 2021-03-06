import org.jacop.constraints.*;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.*;

public class Pizza {
    public static void main(String[] args) {
        long T1, T2, T;

        T1 = System.currentTimeMillis();
        SolveForData(3);
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\n\t*** Execution time = " + T + " ms");
    }

    private static void FindSolution(int n, int[] price, int m, int[] buy, int[] free) {
        Store store = new Store();
        IntVar[] boughtPizzas = new IntVar[n];
        IntVar[] freePizzas = new IntVar[n];
        IntVar[][] voucherB = new IntVar[m][n];
        IntVar[][] voucherF = new IntVar[m][n];

        //Populera paidPizzaz och freePizzas
        //Samt lägg till constraint för att
        //en pizza kan inte både köpas och fås.
        for(int i = 0; i < n; i++){
            boughtPizzas[i] = new IntVar(store, "Paid pizza"+(i+1), 0,1);
            freePizzas[i] = new IntVar(store, "Free pizza"+(i+1), 0,1);
            store.impose(new XneqY(boughtPizzas[i], freePizzas[i]));
        }

        //Summan av köpta och gratis pizzor ska vara = n.
        IntVar bought = new IntVar(store, "bought", n, n);
        store.impose(new SumInt(store, mergeVector(boughtPizzas, freePizzas), "==", bought));

        //Populera voucherB och voucherF
        //Samt att en pizza kan inte fås av en voucher om den
        //användes för att aktivera vouchern.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                voucherB[i][j] = new IntVar(store, "Paid pizza" + ((i+1) * 10 + j), 0, 1);
                voucherF[i][j] = new IntVar(store, "Free pizza" + ((i+1)*10+j), 0, 1);
                store.impose(new Not(new XplusYeqC(voucherB[i][j], voucherF[i][j], 2)));
            }
        }

        //En pizza får inte användas i två olika vouchers.
        for(int i = 0; i<n; i++){
            store.impose(new SumInt(store, getCol(voucherB, i), "<=", new IntVar(store, 1, 1)));
            store.impose(new SumInt(store, getCol(voucherF, i), "<=", new IntVar(store, 1, 1)));
            store.impose(new SumInt(store, getCol(voucherB, i), "==", boughtPizzas[i]));
            store.impose(new SumInt(store, getCol(voucherF, i), "==", freePizzas[i]));
        }

        //Antalet gratizpizzor får inte överstiga antalet som vouchern erbjuder.
        for(int i = 0; i < m; i++){
            store.impose(new SumInt(store, voucherF[i], "<=", new IntVar(store, free[i], free[i])));
        }

        //Du får inte ta fler gratispizzor än vouchern tillåter samt
        //du får inte ta gratispizzor om du inte betalar för tillräckligt många pizzor.
        for(int i =0; i < m; i ++){
            PrimitiveConstraint nbrPaid = new SumInt(store, voucherB[i],">=", new IntVar(store, buy[i], buy[i]));
            PrimitiveConstraint nbrFree = new SumInt(store, voucherF[i],"<=", new IntVar(store, free[i], free[i]));
            PrimitiveConstraint zero = new SumInt(store, voucherF[i], "==", new IntVar(store, 0, 0));
            store.impose(new IfThenElse(nbrPaid, nbrFree, zero));
        }

        //Pizza som tas gratis får inte vara dyrare än den billigaste som köpts.
        for(int i = 0; i < m; i++){
            for(int j = 0; j<n; j++){
                for(int k = j-1; k >= 0; k--){
                    PrimitiveConstraint c1 = new XeqC(voucherB[i][j], 1);
                    PrimitiveConstraint c2 = new XeqC(voucherF[i][k], 1);
                    store.impose(new Not(new And(c1, c2)));
                }
            }
        }

        IntVar cost = new IntVar(store, "Cost ", 0, sum(price));
        sort(price);
        store.impose(new SumWeight(boughtPizzas, price, cost));

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(mergeMatrix(voucherB,voucherF), null, new IndomainMin<IntVar>());

        boolean result = search.labeling(store, select, cost);

        if (result) {
            System.out.println("Solution : ");
            System.out.println("Paid pizzas vector:");
            printVector(boughtPizzas);
            System.out.println("Prices:");
            printIntVector(price);
            System.out.println("Voucher bought matrix:");
            printMatrix(voucherB);
            System.out.println("Voucher free matrix:");
            printMatrix(voucherF);
        } else {
            System.out.println("No solution found.");
        }
    }

    private static IntVar[] getCol(IntVar[][] matrix, int i) {
        IntVar[] col = new IntVar[matrix.length];
        for (int j = 0; j < matrix.length; j++) {
            col[j] = matrix[j][i];
        }
        return col;
    }

    private static IntVar[][] mergeMatrix(IntVar[][] v1, IntVar[][] v2){
        int rows = v1.length + v2.length;
        int cols = v1[0].length;
        int indexInRow = 0;
        IntVar[][] mergedMatrix = new IntVar[rows][cols];
        for(int i = 0; i < v1.length; i++) {
            for (int j = 0; j < v1[0].length; j++){
            mergedMatrix[indexInRow][j] = v1[i][j];
            }
            indexInRow++;
        }
        for(int i = 0; i < v2.length; i++){
            for(int j = 0; j < v2[0].length; j++){
                mergedMatrix[indexInRow][j] = v2[i][j];
            }
            indexInRow++;
        }
        return mergedMatrix;
    }

    private static IntVar[] mergeVector(IntVar[] v1, IntVar[] v2){
        int size = v1.length+v2.length;
        int index = 0;
        IntVar[] merged = new IntVar[size];
        for(int i = 0; i < v1.length; i++){
            merged[index] = v1[i];
            index++;
        }
        for(int i = 0; i < v2.length; i++){
            merged[index] = v2[i];
            index++;
        }
        return merged;
    }

    public static void sort(int[] array) {

        int o = array.length;
        int temp = 0;

        for (int i = 0; i < o; i++) {
            for (int j = 1; j < (o - i); j++) {

                if (array[j - 1] < array[j]) {
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }
            }
        }
    }

    public static int sum(int[] array){
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum+= array[i];
        }
        return sum;
    }

    public static void SolveForData(int ex ){
        switch(ex) {
            case 1:
                int n = 4;
                int[] price = {10, 5, 20, 15};
                int m = 2;
                int[] buy = {1, 2};
                int[] free = {1, 1};
                FindSolution(n, price, m, buy, free);
                break;
            case 2:
                int n2 = 4;
                int[] price2 = {10, 15, 20, 15};
                int m2 = 7;
                int[] buy2 = {1, 2, 2, 8, 3, 1, 4};
                int[] free2 = {1, 1, 2 ,9, 1, 0, 1};
                FindSolution(n2, price2, m2, buy2, free2);
                break;
            case 3:
                int n3 = 10;
                int[] price3 = {70, 10, 60, 60, 30, 100, 60, 40, 60, 20};
                int m3 = 4;
                int[] buy3 = {1, 2, 1, 1};
                int[] free3 = {1, 1, 1, 0};
                FindSolution(n3, price3, m3, buy3, free3);
                break;
            case 4:
                int n4 = 6;
                int[] price4 = {20,15,10,5,10,10};
                int m4 = 4;
                int[] buy4 = {1, 1, 1, 1};
                int[] free4 = {1, 0, 0, 0};
                FindSolution(n4, price4, m4, buy4, free4);
                break;
        }
    }

    private static void printMatrix(IntVar[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j].value() + " ");
            }
            System.out.print("\n");
        }
    }

    private  static void printVector(IntVar[] vector){
        for(int i = 0; i < vector.length; i ++){
            System.out.print(vector[i].value()+" ");
        }
        System.out.print("\n");
    }

    private  static void printIntVector(int[] vector){
        for(int i = 0; i < vector.length; i ++){
            System.out.print(vector[i]+" ");
        }
        System.out.print("\n");
    }
}