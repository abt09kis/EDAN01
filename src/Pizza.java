import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Pizza {
    public static void main(String[] args) {
        long T1, T2, T;
        T1 = System.currentTimeMillis();
        calc();
        T2 = System.currentTimeMillis();
        T = T2 - T1;
        System.out.println("\nExecution time = " + T + " ms");
    }

    public static void calc() {
        Store store = new Store();

        int n = 4;
        int[] price = {10, 5, 20, 15};
        int m = 2;
        int[] buy = {1, 2};
        int[] free = {1, 1};
        IntVar[] paid = new IntVar[n];
        IntVar[] forFree = new IntVar[n];
        IntVar[][] voucherUsed = new IntVar[m][n];
        IntVar nbrVouchers = new IntVar(store, m, m);
        IntVar zero = new IntVar(store, 0, 0);
        for (int i = 0; i < n; i++) {
            paid[i] = new IntVar(store, "Bought Pizza" + i, 0, 1);
            forFree[i] = new IntVar(store, "Free Pizza" + i, 0, 1);
            //En pizza kan inte vara både bought och forfree
            store.impose(new XneqY(paid[i], forFree[i]));
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                voucherUsed[i][j] = new IntVar(store, "Used Vouchers" + i + j, 0, 1);
            }
        }

        //I matrisen: max en etta per kolumn, max m ettor per rad ty
        //en pizza kan max användas en gång för voucher och max m vouchers på alla pizzor

        //Max m ettor per rad
        for (int i = 0; i < m; i++) {
            store.impose(new SumInt(store, voucherUsed[i], "<=", new IntVar(store, m, m)));
        }

        //Max en etta per kolumn; Ta ut en kolumn och kolla så max ett värde är ett
        for (int i = 0; i < n; i++) {
            IntVar[] temp = new IntVar[m];
            for (int j = 0; j < m; j++) {
                temp[j] = voucherUsed[j][i];
            }
            store.impose(new SumInt(store, temp,"<=", new IntVar(store, 1, 1)));
        }

        //Om vi har köpt fler eller lika många pizzor som en voucher behöver, då får vi free[i] gratispizzor
        for (int i = 0; i < m; i++) {
            PrimitiveConstraint c1 = new SumInt(store, paid, ">=", new IntVar(store, buy[i], buy[i]));
            PrimitiveConstraint c2 = new SumInt(store, forFree, "<=", new IntVar(store, free[i], free[i]));
            store.impose(new IfThen(c1, c2));
        }

        //Måste kolla vilka pizzor vi får lov att köpa. Skapa matris med pris på pizzorna ? Värden mellan noll och ett,
        //på varje rad så ska det vara lika många eller fler som vi köpt med vouchern, (free[i] ist för buy[i]).





        IntVar[] vouchers = new IntVar[m];
        for (int i = 0; i < m; i++) {
            vouchers[i] = new IntVar(store, "Voucher" + i, 0, 1);
        }
        // Less than or equals to.
        store.impose(new LinearInt(store, paid, price, "<", getSumOfArray(price)));

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(paid,
                new LargestDomain<>(),
                new IndomainMin<IntVar>());
        search.setSolutionListener(new PrintOutListener<IntVar>());

        search.setSolutionListener(new PrintOutListener<IntVar>());
        boolean Result = search.labeling(store, select);
        if (Result) {
            System.out.println("\n*** Yes");
            System.out.println("Solution : "+ java.util.Arrays.asList(paid));
        }else System.out.println("\n*** No");

    }



    public static int getSumOfArray(int[] array) {
        int sumOfArray = 0;
        for (int i = 0; i < array.length; i++) {
            sumOfArray += array[i];
        }
        return sumOfArray;
    }
    /*
    public static int getMaxValue(int[] array){
        int maxValue = array[0];
        for(int i=1;i < array.length;i++){
            if(array[i] > maxValue){
                maxValue = array[i];

            }
        }
        return maxValue;
    }
    */

}
