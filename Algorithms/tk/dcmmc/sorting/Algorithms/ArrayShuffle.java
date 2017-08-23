package tk.dcmmc.sorting.Algorithms;

import java.util.Random;

class ArrayShuffle {
	/**
	* 随机打乱数组(Knuth法)
	* O(N)
	* @param a
	*		要打乱的数组
	* @throws IllegalArgumentException if {@code a} is {@code null}
	*/
	public static void shuffle(Object[] a) throws IllegalArgumentException {
		shuffle(a, 0, a.length - 1);
	}

	/**
	* 随机打乱数组中的指定部分
	* O(N)
	* @param a
	*		要打乱的数组
	* @param lo 指定部分的下界
	* @param hi 指定部分的上界
	* @throws IllegalArgumentException if {@code a} is {@code null}
	*/
	public static void shuffle(Object[] a, int lo, int hi) throws IllegalArgumentException {
		if (a == null) 
			throw new IllegalArgumentException("argument array is null");
		if (lo > hi || lo < 0 || hi < 0 || lo >= a.length || hi >= a.length)
			throw new IllegalArgumentException("argument lo = " + lo + " or hi = " + hi + " is not in range of a");

		Random rnd = new Random();

		for (int i = lo + 1; i <= hi; i++) {
			//要求每个元素被放到任何一个位置的概率都相等（即1/n）
			//把a[i]和a[lo...i - 1]互换, 因为要使可能的排列种类为n!种, 所以不能是a[i]和a[0...a.length-1]互换
            int r = lo + rnd.nextInt(i - lo);
			Object tmp = a[i];
			a[i] = a[r];
			a[r] = tmp;
		}
	}
}///~