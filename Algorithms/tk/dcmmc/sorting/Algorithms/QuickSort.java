package tk.dcmmc.sorting.Algorithms;

import edu.princeton.cs.algs4.StdRandom;

/**
* 快速排序法
* Create on 2017/08/16
* @author DCMMC
* @since 1.5
*/
public class QuickSort extends Sort {
	/* 用于quickSortFaster的fields */
	//insertion sort的阀值
    private static final int INSERTION_SORT_CUTOFF = 9;
    //median-of-three的阀值
    private static final int MEDIAN_OF_3_CUTOFF = 40;

    
	/**
	* 快速排序
	* 优点: 实现比较简单, 每一轮的比较次数都是固定的N + 1, 时间复杂度和空间复杂度都相当优秀, 虽然Quicksort在一般情况下的比较
	* 次数(1.39N logN)大于mergesort()(1/2N logN ~ N logN), 不过Quicksort数据移动(exch)的次数相当少, 所以相对来说Quicksort会更加快.
	* 缺点: 很多小的细节容易导致严重的性能损失, 有时候甚至达到了N^2的时间复杂度
	* 算法分析:
	* 在最理想情况, 每一次j都是正好在subarray的中间位置, 也就是每次都能二分, 这样和mergesort一模一样, 时间复杂度为 ~ NlogN
	* 
	* Quicksort平均使用 ~ 2NlnN的比较次数(以及1/6的的交换)
	* Prooof. 
	* 在最理想情况, 每一次j都是正好在subarray的中间位置, 也就是每次都能二分, 这样和mergesort一模一样, 时间复杂度为 ~ NlogN
	*
	* Quicksort排序N个不同的数字平均使用 ~ 2NlogN (`!$\dot {=} 1.39N \log N$`)的比较次数(以及1/6的的交换)
	*
	**Prooof.**
	*
	* 设`!$C_N$`为排序N个(分散的)items所需要的平均比较次数, 易得`!$C_0 = C_1 = 0$`, 而且对于`!$N > 1$`, 有一下递归关系:
	*
	* ```mathjax!
	* $$C_N = N + 1 + \frac {\left(C_0 + C_1 + \cdot \cdot \cdot + C_{N - 2} + C_{N - 1} \right)} {N} +  
	* \frac {\left(C_{N - 1} + C_{N - 2} + \cdot \cdot \cdot + C_{1} + C_{0} \right)} {N}$$
	* ```
	*
	* N + 1是每一轮排序的固定比较次数, 第二部分是排序left subarray的平均比较次数, 第三部分是排序right subarray的平均比较次数.
	* 
	* 又将`!$C_N$`与`!$C_{N - 1}$`两式相减, 得到`!$NC_N = 2N + (N + 1)C_{N - 1}$`, 左右同除以N(N + 1), 得到 
	* `!$\frac {C_N} {N + 1} = \frac {2} {N + 1} + \frac {C_{N - 1}} {N}$`, 令`!$\lambda_N =  \frac {C_N} {N + 1}$`, 
	* 所以递推得到
	*
	* `!$C_N = 2(N + 1) \cdot \left( \sum_{i = 3}^{N + 1} \frac {1} {i} \right)$` ~ `!$2N\ln N$`
	*
	* 证毕.
	*
	* > 交换次数的证法与上面类似不过更加复杂.
	*
	* > 对于有重复数值的情况, 准确的分析复杂很多, 不过不难表明平均比较次数不大于`!$C_N$`, 后面将会有对这种情况的优化.
	*
	* @param a
	* 		要排序的数组
	*/
	public static void quickSort(Comparable[] a) {
		//把a打乱
		StdRandom.shuffle(a);

		quickSort(a, 0, a.length - 1);
	}

	/**
	* 快速排序指定部分
	* @param a
	* 		要排序的数组
	* @param lo
	*		要排序的范围的下界
	* @param hi
	* 		要排序的范围的上界
	*/
	private static void quickSort(Comparable[] a, int lo, int hi) {
		if (lo >= hi)
			return;

		//分区的分界位置
		int j = partition(a, lo, hi);

		quickSort(a, lo, j - 1);
		quickSort(a, j + 1, hi);
	}

	/**
	* 原地分区(In-place Partition)
	* 把目标范围中的第一个元素a[lo]放在指定的位置: 左边的subarray的所有元素都小于等于a[lo], 右边subarray的所有元素都大于等于a[lo], 
	* 然后把a[lo]换到这个位置来.
	*
	* 基本策略: i, j这两个下标分别从要分区的范围的下界和上界开始, 逐渐向中心递推, 知道遇到a[i]大于(等于)a[lo], a[j]小于(等于)a[lo],
	* 这时候如果i < j(也就是没有交叉), 就将a[i]和a[j]交换, 知道没有可以交换的, 这时候j的位置就是a[lo]的最终位置.
	*
	* 一些坑:
	* 1. 这里采用的是原地排序, 如果使用额外的数组会更加容易实现, 不过这中间产生的复制数组的时间消耗会非常大.
	* 2. 两个inner loop都有边界检查, 防止出现partition是数组中的最大或者最小值倒是超出范围的情况.
	* 3. 保持随机性: 对于程序运行时间的可预测性至关重要, 相当于每一次都是随机得对待子数组中的所有元素, 另外一个方法是在partition()
	* 中随机的选取partition item来保持随机性. 保持随机性是为了防止partition树极度不平衡的情况, 也就是像上文中的缺点所指出的那种
	* 情况, 避免产生过多的partition次数, 至少要避免连续产生这种糟糕的partition.
	* 4. 避免死循环, 控制loop的出口(i >= j), 很多时候因为subarray中有与partition item相同的值的元素造成死循环.
	* 5. 处理好subarray中的与partition item相同的值的元素, inner loop的条件一定不能是 <=, 因为在遇到大量与partition item相同值
	* 的元素的情况, 例如所有元素都是一样的值, 这时候两轮inner都会该死的遍历所有元素, 并且j还tm就是lo, 这样效率爆炸般得达到了N^2, 
	* 如果元素再多一点(>2.5w个), 就直接爆栈了. 如果是<而不是<=的话, 就算遇到这种情况, j也是(lo + hi) / 2的样子, 有种二分的感觉,
	* 虽然exch()的调用看起来有点冗余, 不过至少比爆栈好... 见到Ex 2.3.11
	* 6. 注意递归边界
	* @param a
	*		要分区的数组
	* @param lo
	*		分区范围的下界
	* @param hi
	*		分区范围的上界
	*/
	@SuppressWarnings("unchecked")
	private static int partition(Comparable[] a, int lo, int hi) {
		//i, j的初始值都要分别比第一个要处理的小一位和大一位
		//左边从lo+1开始遍历, 右边从hi开始遍历, a[lo]为partition item
		int i = lo, j = hi + 1;

		while (true) {
			//i向后推进, 直到遇到大于等于a[lo]或者遍历完所有的值的时候就退出循环
			//条件一定不能是<=
			while (a[++i].compareTo(a[lo]) < 0)
				if (i == hi)
					break;

			//j向前推进, 直到遇到小于等于a[lo]或者遍历完所有的值的时候就退出循环
			while (a[lo].compareTo(a[--j]) < 0)
				if (j == lo)
					break;

			//判断此时i, j是否交叉, 如果交叉就说明找到了a[lo]的最终位置了
			if (i >= j)
				break;

			//i, j没有交叉说明还未分区完, 交换不符合分区结果的这两个值(因为此时的a[i] >= a[lo], a[j] <= a[lo])
			exch(a, i, j);
		}

		//此时的a[j] <= a[lo]且a[j + 1] > a[lo](或者没有a[j + 1]这个元素)
		exch(a, lo, j);

		//返回a[lo]的最终位置
		/* a[j]又被称为pivot */
		return j;
	} 


	/**
	* Improved QuickSort with CUTOFF and 3-way partitioning
	* @param a
	* 		要排序的数组
	* @param a
	*		要分区的数组
	*/
	public static void quickSortImprove(Comparable[] a) {
		quickSortImprove(a, 0, a.length - 1);
	}

	/**
	* Quicksort with CUTOFF and 3-way partitioning
	* @param a
	*		要分区的数组
	* @param lo
	*		分区范围的下界
	* @param hi
	*		分区范围的上界
	*/
	public static void quickSortImprove(Comparable[] a, int lo, int hi) {
		quickSortImprove(a, lo, hi, 12);	
	}

	/**
	* 优化版本
	* Ex 2.3.25
	* 比较不同的CUTOFF对算法性能的影响
	* Quicksort with CUTOFF and 3-way partitioning(在有大量重复数值的时候, 可以优化到O(N) (~2ln2*N*H))
	* @param a
	*		要分区的数组
	* @param lo
	*		分区范围的下界
	* @param hi
	*		分区范围的上界
	* @param CUTOFF 
	*		Cutoff的临界值
	*/
	public static void quickSortImprove(Comparable[] a, int lo, int hi, final int CUTOFF) {
		//CUTOFF在5 ~ 15才有效果, 原理见MergeSort
		//cutoff to insertion sort
		if (lo + CUTOFF >= hi) {
			InsertionSort.insertionSort(a, lo, hi);
			return;
		}

		//3-way partitioning
		int lt = lo, i = lo + 1, gt = hi;
		//最开始的a[lo]为partition item
		Comparable v = a[lo];

		while(i <= gt) {
			int cmp = a[i].compareTo(v);

			if (cmp < 0)
				exch(a, lt++, i++);
			else if (cmp > 0)
				exch(a, i, gt--);
			else
				i++;
		}

		//那些与partition item的值相等的部分就不用再排序了
		quickSortImprove(a, lo, lt - 1);
		quickSortImprove(a, gt + 1, hi);		
	}

	/**
	* Ex 2.3.22 Ex 2.3.23
	* QuickSort Faster
	* with Bentlry-McIlroy fast 3-way partition, CUTOFF to insertion sort, sentinels and Tukey ninther Median-of-medians
	* @param a 要排序的数组
	*/
	public static void quickSortFaster(Comparable[] a) {
		//把a打乱
        StdRandom.shuffle(a);

        //找到最大的item并且交换到a[length - 1]
        //sentinel2
        int indexOfMax = 0;
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[indexOfMax]) > 0)
                indexOfMax = i;
        exch(a, indexOfMax, a.length - 1);

        quickSortFasterRange(a, 0, a.length - 1);
	}


	/**
	* 对指定部分[lo, hi) (兼容java.util.Arrays.sort())进行QuickSort Faster
	* with Bentlry-McIlroy fast 3-way partition, CUTOFF to insertion sort, sentinels and Tukey ninther Median-of-medians
	* @param a 要排序的数组
	* @param lo 排序的部分的下界(包括)
	* @param hi 排序的部分的上界(不包括)
	*/
	@SuppressWarnings("unchecked")
	public static void quickSortFaster(Comparable[] a, int lo, int hi) {
		//把a的指定部分打乱
		ArrayShuffle.shuffle(a, lo, hi - 1);

        //找到最大的item并且交换到a[hi]
        //sentinel2
        int indexOfMax = lo;
        for (int i = lo + 1; i < hi - 1; i++)
            if (a[i].compareTo(a[indexOfMax]) > 0)
                indexOfMax = i;
        exch(a, indexOfMax, hi - 1);

        quickSortFasterRange(a, lo, hi - 1);
	}

	/**
	* Ex 2.3.22 Ex 2.3.23
	* QuickSort Faster
	* with Bentlry-McIlroy fast 3-way partition, CUTOFF to insertion sort, sentinels and Tukey ninther Median-of-medians
	* @param a
    *       要排序的数组
    * @param lo
    *       排序范围的下界
    * @param hi
    *       排序范围的上界
    */
    @SuppressWarnings("unchecked")
    private static void quickSortFasterRange(Comparable[] a, int lo, int hi) {
    	//递归边界
        if (lo >= hi)
            return;

        //lo到hi的长度
        int n = hi - lo + 1;

    	//cutoff to insertion sort
		if (n <= INSERTION_SORT_CUTOFF) {
			InsertionSort.insertionSort(a, lo, hi);
			return;
		} else if (n <= MEDIAN_OF_3_CUTOFF) {
        	// use median-of-3 as partitioning element
            exch(a, medianOfThree(a, lo, lo + n/2, hi), lo);
        } else {
			// use Tukey ninther as partitioning element
        	int eps = n/8;
            int mid = lo + n/2;
            int m1 = medianOfThree(a, lo, lo + eps, lo + eps + eps);
            int m2 = medianOfThree(a, mid - eps, mid, mid + eps);
            int m3 = medianOfThree(a, hi - eps - eps, hi - eps, hi); 
            exch(a, medianOfThree(a, m1, m2, m3), lo);
		}

		// Bentley-McIlroy 3-way partitioning
		//我也没看出来这个比Dijkstra's 3-way partitioning优秀多少, 有时间好好研究一下
		/*
		* 处理中的状态
		* -----------------------------------------------------
		* |  == v  |   < v   |   待处理     |    > v   |  = v  |
		* -----------------------------------------------------
		* ^        ^         ^              ^          ^       ^
		* |        |         |              |          |       |
		* lo      p->        i              j         <-q      hi
		* 
		*/
        int i = lo, j = hi+1;
        int p = lo, q = hi+1;
        Comparable v = a[lo];
        while (true) {
        	//a[lo]作为sentinel1
            while (less(a[++i], v))
                continue;
            //左边子数组的最左边的元素或者a[length - 1]作为sentinel2
            while (less(v, a[--j]))
                continue;

            // pointers cross
            if (i == j && a[i].compareTo(v) == 0)
                exch(a, ++p, i);
            if (i >= j) break;

            exch(a, i, j);
            if (a[i].compareTo(v) == 0) exch(a, ++p, i);
            if (a[j].compareTo(v) == 0) exch(a, --q, j);
        }


        //把左右两侧的 == v的那些元素全部交换到中间去
        i = j + 1;
        for (int k = lo; k <= p; k++)
            exch(a, k, j--);
        for (int k = hi; k >= q; k--)
            exch(a, k, i++);

        quickSortFasterRange(a, lo, j);
        quickSortFasterRange(a, i, hi);
    }

    /**
    * median of three using 3 comparisons at the same time sorted them
    * 为了提高一点效率, 不会做边界检查, 毕竟是自用的方法
    * @param a 目标数组
    * @param p 三个元素中的第一个元素的下标
    * @param r 三个元素中间的那一个元素的下标
    * @param q 三个元素中的最后一个元素的下标
    * @return 中位数的下标
    */
    @SuppressWarnings("unchecked")
    private static int medianOfThree(Comparable[] a, int p, int q, int r) {
    	//排序并找出subarray中前三个数的中位数作为partition item
        if (a[p].compareTo(a[r]) > 0)
            exch(a, p, r);
        if (a[q].compareTo(a[p]) < 0)
            exch(a, q, p);
        if (a[q].compareTo(a[r]) > 0)
            exch(a, q, r);

        return q;
    }
}///~