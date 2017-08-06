package tk.dcmmc.sorting.Algorithms;

/**
* 选择排序法
* @author DCMMC
* @since 1.5
*/
class SelectionSort extends Sort {
	/**
	* 选择排序法
	* 时间复杂度不太取决于目标数组, 反正效率都不高
	* 一次一次的找到每一轮的最小值并放到这一轮的第一个位置
	* ~ 1/2N^2(固定这么多次) compare, ~ N(最差情况下) exchange
	* 数据交换这一方面的开销小是优点, 总的效率低的缺点
	*/
	public static void selectionSort(Comparable[] a) {
		for (int i = 0; i < a.length; i++) {
			int min = i;

			for (int j = i + 1; j < a.length; j++)
				if ( less(a[j], a[min]) )
					min = j;

			exch(a, i, min);
		}
	}
}///~