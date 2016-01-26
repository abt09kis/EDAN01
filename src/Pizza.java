import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;
import org.jacop.set.*;
import org.jacop.set.constraints.*;
import org.jacop.set.core.*;

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
		int[] price = {10,5,20,15};
		int m = 2;
		int[] buy = {1,2};
		int[] free = {1,1};
		IntVar[] paid = new IntVar[n];
        IntVar[] forFree = new IntVar[n];

		for(int i = 0; i < n; i++){			
			paid[i] = new IntVar(store,"Bought Pizza" + i, 0, 1);
            forFree[i] = new IntVar(store,"Free Pizza" + i, 0, 1);
		}

        IntVar[] vouchers = new IntVar[m];
		for(int i = 0; i < m; i++) {
            vouchers[i] = new IntVar(store, "Voucher" + i, 0, 1);
        }
        //IntVar pizzaCost = new IntVar(store, "cost", getMaxValue(price),getSumOfArray(price));

        store.impose(new LinearInt(store, paid, price, "<=",getSumOfArray(price)));




	}
    // getting the maximum value
    public static int getSumOfArray(int[] array){
        int sumOfArray = 0;
        for(int i =0; i < array.length; i++){
            sumOfArray += array[i];
        }
        return sumOfArray;
    }

    public static int getMaxValue(int[] array){
        int maxValue = array[0];
        for(int i=1;i < array.length;i++){
            if(array[i] > maxValue){
                maxValue = array[i];

            }
        }
        return maxValue;
    }

}
