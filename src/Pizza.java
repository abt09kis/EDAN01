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
        IntVar[] voucherUsed = new IntVar[n];
        IntVar nbrVouchers = new IntVar(store,m,m);

		for(int i = 0; i < n; i++){			
			paid[i] = new IntVar(store,"Bought Pizza" + i, 0, 1);
            forFree[i] = new IntVar(store,"Free Pizza" + i, 0, 1);
            voucherUsed[i] = new IntVar(store, "Used voucher " + i, 0, 1);
            store.impose(new XneqY(paid[i],forFree[i]));
            store.impose(new XlteqY(voucherUsed[i],paid[i]));
		}

        IntVar[] vouchers = new IntVar[m];
		for(int i = 0; i < m; i++) {
            vouchers[i] = new IntVar(store, "Voucher" + i, 0, 1);
        }
        // Less than or equals to.
        store.impose(new LinearInt(store, paid, price, "<",getSumOfArray(price)));
        // The sum of vouchers used cannot be larger than the number of vouchers.
        store.impose(new SumInt(store, voucherUsed, "<=", nbrVouchers));
        


	}
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
