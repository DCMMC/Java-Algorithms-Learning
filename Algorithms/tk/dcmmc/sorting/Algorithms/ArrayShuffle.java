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
			//要求每个元素被放到任何一个位置的概率都相等（即1/n）
			//把a[i]和a[0...i - 1]互换, 因为要使可能的排列种类为n!种, 所以不能是a[i]和a[0...a.length-1]互换
			int r = rnd.nextInt(i);
			Object tmp = a[i];
			a[i] = a[r];
			a[r] = tmp;
		}
	}
}///~