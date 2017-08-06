package tk.dcmmc.sorting.Algorithms;

class ArrayShuffle {
	/**
	* 随机打乱数组(Knuth法)
	* O(N)
	* @param a
	*		要打乱的数组
	* @throws IllegalArgumentException if {@code a} is {@code null}
	*/
	public static void shuffle(Object[] a) throws IllegalArgumentException {
		if (a == null) 
			throw new IllegalArgumentException("argument array is null");

		Random rnd = new Random();

		for (int i = 1; i < a.length; i++) {
			int r = rnd.nextInt(i);
			Object tmp = a[i];
			a[i] = a[r];
			a[r] = tmp;
		}
	}
}///~