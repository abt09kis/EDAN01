import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;
import org.jacop.set.*;
import org.jacop.set.constraints.*;
import org.jacop.set.core.*;

public class Burger {
	public static void main(String[] args) {
		long T1, T2, T;
		T1 = System.currentTimeMillis();
		calc();
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}

	public static void calc() {
		IntervalDomain d = new IntervalDomain(1, 5);
		Store store = new Store();

		IntVar v1 = new IntVar(store, "Patty", 1, 5);
		IntVar v2 = new IntVar(store, "Bun", 1, 5);
		IntVar v3 = new IntVar(store, "Cheese", 1, 5);
		IntVar v4 = new IntVar(store, "Onions", 1, 5);
		IntVar v5 = new IntVar(store, "Pickles", 1, 5);
		IntVar v6 = new IntVar(store, "Lettuce", 1, 5);
		IntVar v7 = new IntVar(store, "Ketchup", 1, 5);
		IntVar v8 = new IntVar(store, "Tomato", 1, 5);
		int[] sodium = { 50, 330, 310, 1, 260, 3, 160, 3 };
		int[] fat = { 17, 9, 6, 2, 0, 0, 0, 0};
		int[] calories = { 220, 260, 70, 10, 5, 4, 20, 9};
		double[] cost = { 0.25, 0.15, 0.10, 0.09, 0.03, 0.04, 0.02, 0.04};
		
		store.impose(new XeqC(v6, v7.getSize()));
		store.impose(new XeqC(v5, v8.getSize()));

	}

}
