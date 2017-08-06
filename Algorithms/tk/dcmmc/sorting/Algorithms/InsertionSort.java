package tk.dcmmc.sorting.Algorithms;

/**
* 插入排序法
* @author DCMMC
* @since 1.5
*/
class InsertionSort extends Sort {
	/**
	* 插入排序法
	* 时间复杂度比较取决于目标数组, 目标数组越接近于完全正序, 时间复杂度就越低
	* 交换的次数就是逆序数对的对数, 比较的次数就是交换的次数+(N-1)
	* 平均情况下(这里取的是每一个item都是进行到一半就找到了位置), 比较 ~ 1/4N^2, 交换 ~ 1/4N^2
	* 在最坏情况(完全倒序), 比较 ~ 1/2N^2, 交换 ~ 1/2N^2
	* 最好情况(完全正序), 比较 ~ N-1, 交换 0
	*/
	public static void insertionSort(Comparable[] a) {
		for (int i = 1; i < a.length; i++) {
			//找到a[i]前面的序列(前面的序列都是由大到小排序好了的)中, 在a[i]应该所在的位置插入(这里通过一个一个交换来实现, 如果用
			//链表效率肯定会高一些 见Ex 2.1.25)
			for (int j = i; j > 0 && less(a[j], a[j - 1]); j--)
				exch(a, j, j - 1);
		}
	}
}///~