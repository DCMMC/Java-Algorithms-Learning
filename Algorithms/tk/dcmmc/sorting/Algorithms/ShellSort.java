package tk.dcmmc.sorting.Algorithms;

import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;

/**
* 希尔排序法
* Created on 2017/8/5
* @author DCMMC
* @since 1.5
*/
class ShellSort extends Sort {
	/**
	* 在最糟糕的情况下时间复杂度为O(N^1.5), 一个小小的改进就能从InsertionSort的O(N^2)降低到O(N^1.5)(而且是最坏情况)
	* 平均下的时间复杂度不确定, 取决于increment sequence的选取.
	* 
	* InsertionSort在每一轮插入的时候需要跟相邻的元素一个一个交换, 这样很消耗资源, 希尔排序作为插入排序的扩展,
	* 每次比较并交换的间隔不再是1(h=1这一特殊情况下h-sorting就是InsertionSort), 而是大于1的不同的步长.
	* 定义(Def.): h-sorting
	* 如果数组中的元素任意间隔h都是有序的, 那就称为h-sorting array, e.g., 1 2 3 4 5 6 7,
	* 取h为2, 则子序列1 3 5 7和子序列2 4 6分别都是有序的(即有h个互相独立有序的子序列交错组合在一起), 则该数组是h-sorted array
	* h-sorting即以h为间隔对子序列进行排序, e.g., 对序列 1 5 8 2 3 4 6 7 6 8 9 (共11个元素), 以h = 3进行h-sorting,
	* 首先这个序列拆分为一下3个子序列: 1 2 6 7和5 3 7 9和8 6 4, 然后依次对这三个子序列进行InsertionSort, 得到1 2 6 7, 3 5 7 9
	* 和4 6 8这三个子序列, 合并之后为: 1 3 4 2 5 6 6 7 8 7 9, 这个序列就被称为原序列以h=3的h-sorted array
	* 一个更好的理解就是把序列放在一个h列的表中, 然后对其形成的二维表的每一列进行InsertionSort:
	* 1 5 8 2 3 4 6 7 6 8 9以h=3
	* 1 5 8 
	* 2 3 4
	* 6 7 6
	* 8 9
	* 然后依次对第一第二第三第四列进行InsertionSort:
	* 1 3 4
	* 2 5 6
	* 6 7 8
	* 7 9
	* 
	* 
	* ShellSort大体的原理是以一系列值(increment sequence)作为h(又叫步长), 由大的h到小的h来对序列进行h-sorting, 只要该h序列最后的h是1,
	* 就一定能得出排序好的序列, 比如 5 3 1就是一个h序列, 先以h=5对原序列进行h-sorting, 然后再以h=3进行h-sorting, 最后以h=1进行h-sorting.
	* 不过从１开始然后不断乘以２这样的序列的效率很低，因为１后面都是偶数，彼此ｈ-sorting排序都是错开的的.
	*
	* 而公认的最好步长序列是由Sedgewick(本书作者)提出的(1, 5, 19, 41, 109,...)，该序列的项来自9x4^i-9x2^i+1和2^(i+2)x(2^(i+2)-3)+1
	* 这两个算式.用这样步长序列的希尔排序比插入排序要快，甚至在小数组中比快速排序和堆排序还快，但是在涉及大量数据时希尔排序还是比快速排序慢。
	*
	* 另一个在大数组中表现优异的步长序列是（斐波那契数列除去0和1将剩余的数以黄金分区比的两倍的幂进行运算得到的数列）
	*
	* 不过一般使用的序列是Knuth提出的由递推公式h = h*3 + 1确定的数列(转成通项公式即为1/2(3^k-1)), 这个序列在元素数量比较大的时候,
	* 相比于SelectionSort和InsertionSort, 性能按照数组大小以2的次幂递增.
	* 使用Knuth提出序列的比较次数大概为N的若干倍再乘以这个序列的长度(差不多约为N^1.5, 由大量N很大的实验可以估算出)
	* 
	* 虽然使用最优的序列的时候, 在对小数组排序性能有时候可以超过heapsort和quicksort, 不过在大量数据的时候还是慢于后两个, 不过相比于
	* 后两者复杂一些的实现, ShellSort只需少量代码而且对资源的消耗也比较小, 所以适合用在嵌入式系统这些比较重视资源的场景中.
	*/
	public static void shellSort(Comparable[] a) {
		//先确定一个不大于a.length/3的一个数作为h序列的最大项
		int h = 1;

		while (h < a.length/3)
			h = h*3 + 1;//by Knuth

		//以这个h构成一个后项是前项的1/3(约)的序列
		while (h >= 1) {
			//以当前h做h-sorting
			for (int i = h; i < a.length; i++) {
				for (int j = i; j >= h && less(a[j], a[j - h]); j -= h) 
					exch(a, j, j - h);
			}

			h /= 3;
		}
	}
}///~