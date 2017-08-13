package tk.dcmmc.fundamentals.Algorithms;

import java.util.Arrays;

/**
* 二分查找
* Create on 2017/8/13
* @author DCMMC
* @since 1.5
*/
public class BinarySearch {
	/**
	* 在数组整个范围中查找是否有key
	* @param a
	*		target array
	* @param key
	*		要查找的key
	* @return 找不到就返回-1, 找得到就返回key在a中的index
	*/
	public static int search(Comparable[] a, Comparable key) {
		Comparable[] aCopy = a.clone();
		Arrays.sort(aCopy);

		return search(aCopy, key, 0, a.length - 1);
	}

	/**
	* 在已排序数组给定范围中查找是否有key
	* @param a
	*		已排序数组
	* @param key
	*		要查找的key
	* @param lo
	*	    查找范围的下界
	* @param hi
	*		查找单位的上界
	* @return 找不到就返回-1, 找得到就返回key在a中的index
	*/
	public static int search(Comparable[] a, Comparable key, int lo, int hi) {
		if (lo > hi)
			return -1;

		int mid = lo + (hi - lo) / 2;

		if (key.compareTo(a[mid]) < 0)
			return search(a, key, lo, mid);
		else if (key.compareTo(a[mid]) > 0)
			return search(a, key, mid + 1, hi);
		else
			return mid;

	}
}///~