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
		IntVar[] pizzas = new IntVar[n];
		for(int i = 0; i < n; i++){			
			pizzas[i] = new IntVar(store,"Pizza" + i, price[i], price[i]);
		}
		
		for(int i = 0; i < m; i++){
			
			for(int j = 0; j < m; j++){
				
			}
		}
		
		store.impose(c);

		store.impose(XlteqY());
		
		
		

	}

}
