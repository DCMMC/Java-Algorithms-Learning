//package tk.dcmmc.Fundamentals;

/**
* Algorithm Chapter 1 Exercise
* @Author DCMMC
*/

class ExerciseChapterOne {
	/**
	* A static method in Exercise 1.1.12
	* @param n an int number.
	* @return the result of this recursion method.
	*/
	private static String exR1(int n) {
		if (n <= 0)
			return "";

		return exR1(n-3) + n + exR1(n-2) + n;
	}

	/**
	* Exercise 1.1.18
	* @param a A int number.
	* @param b A int number.
	* @return the result of this recursion method.
	*/
	private static int mystery(int a, int b) {
		if(b == 0)
			return 0;
		if (b % 2 == 0)
			return mystery(a+a, b/2);

		return mystery(a+a, b/2) + a; 
	}

	/**
	* Exercise 1.1.19
	* Fibonacci Function.
	* @param n a int number.
	* @return the result of Fibonacci Function.
	*/ 
	private static long Fibonacci(int n) {
		if (n == 0)
			return 0;
		if (n == 1)
			return 1;

		return Fibonacci(n-1) + Fibonacci(n-2); 
	}


	/**
	* 那个控制台输出的语句太长啦, 搞个方便一点的.
	* @param s 要输出的String.
	*/
	private static void o(String s) {
		System.out.println(s);
	}

	/**
	* 那个控制台输出的语句太长啦, 搞个方便一点的.
	* @param i 要输出的Integer.
	*/
	private static void o(Integer i) {
		System.out.println(i);
	}

	/**
	* 那个控制台输出的语句太长啦, 搞个方便一点的.
	* @param d 要输出的Double.
	*/
	private static void o(Double d) {
		System.out.println(d);
	}

	/**
	* Entry point of class ExerciseChapterOne.
	* 
	* @param args the command-line arguments
	*/
	public static void main(String[] args) {
		//Exercise 1.1.1 (c)
		o(" true && false || true && true = " + (true && false || true && true) );

		//Exercise 1.1.16
		o(exR1(6));

		//Exercise 1.1.18
		o(mystery(2,25));

		//Exercise 1.1.19
		//for (int n = 0; n < 100; n++) {
		//	o(n + " " + Fibonacci(n));
		//}

	}

}	