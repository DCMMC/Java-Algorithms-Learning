package tk.dcmmc.sorting.Algorithms;

//debug
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;

/**
* 归并排序法
* Create on 2017/8/8
* @author DCMMC
* @since 1.5
*/
public class MergeSort extends Sort {
	/**
	* 内部类
	* 为了那该死的判断a[mid] <= a[mid + 1]的情况, 为了把auxLocal的成果转移到a去, 可是这Java所有方法参数都是passed by value的,
	* 不得不创建一个Pointer类来封装应用, 以达到C语言指针的效果...
	*/
	private static class Pointer<ReferenceType> {
		private ReferenceType p;

		private Pointer () { }

		Pointer(ReferenceType p) {
			this.p = p;
		}
	}

	//用于merge的暂存辅助数组
	//把暂存数组存储为静态域变量并不是一个好的选择, 这会造成在多个client同时调用mergeSort()的时候出现问题, see Ex 2.2.9
	private static Comparable[] aux;

	/**
	* 采用自顶向下的方法排序整个数组的所有元素
	* @param a
	*		要排序的数组
	*/
	public static void mergeSort(Comparable[] a) {
		//创建辅助数组, 只额外分配一次
		aux = new Comparable[a.length];

		mergeSort(a, 0, a.length - 1);
	}

	/**
	* Ex 2.2.9
	* 把aux作为方法内的局部变量并递归的传递给所有用到它的方法, 防止多client同时调用mergeSort()的时候出现问题
	* 采用自顶向下的方法排序整个数组的所有元素
	* @param a
	*		要排序的数组
	*/
	public static void mergeSortLocalAux(Comparable[] a) {
		//创建辅助数组, 只额外分配一次
		Comparable[] auxLocal = new Comparable[a.length];

		mergeSortLocalAux(auxLocal, a, 0, a.length - 1);
	}

	/**
	* Ex 2.2.11
	* top-down mergesort improvements
	* 优化:
	* 1. 辅助数组改为局部变量, 作为方法参数传递给所有需要使用辅助数组的方法中, 这样能够避免多client同时调用mergeSortImprove()
	* 的时候出现问题.
	* 2. 判断a[mid] <= a[mid + 1]的情况, 减少对于两个直接拼在一起就已经是排序好的子数组的多余的比较次数, 这样排序完全有序的序列
	* 的时候可以达到N的时间复杂度.
	* 3. 对于较小(长度小于15的子数组)的子数组, 采用InsertionSort进行排序, 能够提高10%-15%的时间消耗.
	* 4. 消除暂存数组的复制操作: 每一次merge都要对子序列进行复制, 这样会造成复制数据的时间开销(空间开销不变), 设计两次sort()调用,
	* 一次从数组中取出那些数据, 然后将归并好的结果放入暂存数组中, 另外一次就从暂存数组中取出数据然后将归并好的结果放入原数组, 
	* 这样两个数组同时工作在递归中, 减少复制的开销. 
	* @param a
	*		要排序的数组
	* @return 排序后的数组的引用, 可能已经和原来a的引用的地址不一样了, 而且a中的顺序也已经变成了烂尾工程状态...
	*		 我已经尽力了... 这东西返回的还只是Comparable[]还得强制向下转换一次..
	*/
	public static Comparable[] mergeSortImprove(Comparable[] a) {
		Pointer<Comparable[]> p = new Pointer<>(a);

		mergeSortImprove(new Pointer(a.clone()), p, 0, a.length - 1);

		return p.p;
	}

	/**
	* Ex 2.2.16
	* Natural mergesort
	* 我实现的看起来有点冗杂, 不过时间复杂度好像也差不多是NlogN
	* 充分利用序列本身已经排序好的子序列, 把那些相邻的子序列merge起来, 知道整个序列都是已经排序的了
    * e.g.
    * Start       : 3--4--2--1--7--5--8--9--0--6
    * Select runs : 3--4  2  1--7  5--8--9  0--6
    * Merge       : 2--3--4  1--5--7--8--9  0--6
    * Merge       : 1--2--3--4--5--7--8--9  0--6
    * Merge       : 0--1--2--3--4--5--6--7--8--9
    * 
    * @param a
	*		要排序的数组
    */
    @SuppressWarnings("unchecked")
    public static void mergeSortNatural(Comparable[] a) {
    	//创建辅助数组, 只额外分配一次
		aux = new Comparable[a.length];

		int lo, mid, hi;

		//int debug = 0;

		do {
			//debug...
			//debug++;

			lo = 0;
			mid = -1;
			hi = -1;

			for (int i = 1; i < a.length; i++) {
				if (a[i].compareTo(a[i - 1]) < 0) {
					if (mid == -1) {
						mid = i - 1;

						//如果mid正好是倒数第二个元素, 那剩下这最后一个元素就直接成为hi了
						if (i == a.length - 1)
							hi = i;
					} else
						//如果mid已经有了值, 而且hi还是-1, 那就把当前元素作为hi.
						hi = i - 1;
				}


				//有可能已经找到了mid, 不过后面的所有数都是排序好的了, 这时候hi就是a.length - 1
				if ( mid != -1 && (hi != -1 || i == a.length - 1) ) {
					//归并
					merge(a, lo, mid, hi == -1 ? a.length - 1 : hi);

					//重置
					mid = -1;
					hi  = -1;
					lo = i;
				}
			}
		} while ( !(lo == 0 && hi == -1) );

		//System.out.println("debug: " + debug);
			
    }

    /**
    * Ex 2.2.25
    * Multiway mergesort
    * @param a
    *		要排序的数组
    * @param ways
    *		每次mergesort会将数组拆分为ways个分支, ways为大于等于2的整数
    */
    public static void mergeSortMultiway(Comparable[] a, int ways) {
    	if (ways < 2 || ways > a.length) {
    		System.out.println("ways值非法!");
    		return;
    	}

    	//创建辅助数组, 只额外分配一次
		aux = new Comparable[a.length];

		mergeSortMultiway(a, 0, a.length - 1, ways);
    }
    /**
    * Ex 2.2.25
    * Multiway mergesort
    * @param a
	*		目标数组
	* @param lo
	*		要归并的部分的起始下标
	* @param hi
	*		要归并的部分的最后一个元素的下标
	* @param ways
	*		每次mergesort会将数组拆分为ways个分支, ways为大于等于2的整数, 这里不作合法性校验
    */
    @SuppressWarnings("unchecked")
    private static void mergeSortMultiway(Comparable[] a, int lo, int hi, int ways) {
    	//递归边界, 数组个数不足以分成不为空的ways个子数组的时候, 就是边界
    	if (lo >= hi || (hi - lo + 1) / ways == 0)
    		return;

    	//len = ceil(length(array) / ways), 也就是分为ways块, 且每块的元素都是len或者小于len(最后一块小于len)
    	int len = (int)Math.ceil((hi - lo + 1) / ways);
    	if (len >= 2) {
    		for (int i = 0; i < ways - 1; i++) {
    			mergeSortMultiway(a, lo + i * len, lo + (i + 1) * len - 1, ways);
    		}
    		mergeSortMultiway(a, lo + (ways - 1) * len, hi, ways);
    	}
    	

    	//multiway merge implemented by bottom-top iterative 2-way merge
    	//\Theta(n log(ways)) time
    	//TODO 优化当ways个子数组中除了有一个子数组的元素不止一个之外其他的数组的元素都只有一个的时候, 据说(Wikipedia)可以优化到
    	//TODO O(N)的时间复杂度
    	//\Theta(n) space
    	for (int sz = len; sz < hi - lo + 1; sz *= 2)
    		//i就是每一次2-way merge的左半部分的第一个元素的下标, i + sz 表示右半部分的第一个元素的下标, 
    		//如果最后一块恰巧是作为左半部分单出来的, 那么i + sz就是不存在于数组中的元素下标
    		for (int i = lo; i + sz < hi + 1; i += 2 * sz)
    		{
    			//debug...
    			//System.out.printf("merge: (%d, %d, %d)\n", i, i + sz - 1, Math.min(hi, i + 2 * sz - 1));
    			//System.out.println(new DoubleLinkedList<>(a));

    			//merge最后两个部分的时候可能出现最后一块的元素个数小于len的情况
    			merge(a, i, i + sz - 1, Math.min(hi, i + 2 * sz - 1));
    		}
    }

	/**
	* 采用自底向上的方法非递归的归并排序数组
	* 先把整个数组分为最小的情况(也就是每个子数组长度为1), 先这样进行归并, 然后按照数组长度为2进行归并, 子数组长度每次都是上一轮
	* 归并的子数组的长度的两倍. 直到能够归并整个数组.
	* 每一轮最多需要N次比较, 并且需要logN轮, 所以总的时间复杂度为NlogN.
	* @param a
	*		要排序的数组
	*/
	public static void mergeSortBottomUp(Comparable[] a) {
		//创建辅助数组
		aux = new Comparable[a.length];

		for (int sz = 1; sz < a.length; sz *= 2) 
			for (int lo = 0; lo < a.length - sz; lo += (2 * sz))
				merge(a, lo, lo + sz - 1, Math.min(lo + sz + sz - 1, a.length - 1));
	}

	/**
	* Abstract in-place Merge
	* 采用辅助数组的方法(会使用大量的额外空间)来归并两部分结果
	* 也就是归并a[lo...mid]和a[mid + 1...hi]
	* 整体思路就是分别用两个int表示这两部分的下标, 从这两个部分的第一个元素开始往后递推, 分别从两个子序列中选择最小的那个元素作为
	* 放在指定的位置
	* @param a
	*		目标数组
	* @param lo
	*		要归并的前半部分的起始下标
	* @param mid
	*		要归并的前半部分的最后一个元素的下标
	* @param hi
	*		要归并的后半部分的最后一个元素的下标
	*/
	private static void merge(Comparable[] a, int lo, int mid, int hi) {
		//先将数据暂存在辅助数组中
		for (int i = lo; i <= hi; i++)
			aux[i] = a[i];

		//i, j分别为两部分的第一个元素的下标
		int i = lo;
		int j = mid + 1;
		//归并
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = aux[j++];
			else if (j > hi)
				a[k] = aux[i++];
			else if (less(aux[j], aux[i]))
				a[k] = aux[j++];
			else 
				a[k] = aux[i++];
		}
	}

	/**
	* Ex 2.2.8
	* 在归并之前检查a[mid]是否小于等于a[mid + 1](也就是是否这两个子数组组合在一起就已经是按照顺序的了)
	* 这样在归并一个完全有序的序列的时候的时间复杂度为N, 不过在那种部分有序的序列中, 时间复杂度还是O(N logN)
	* @param a
	*		目标数组
	* @param lo
	*		要归并的前半部分的起始下标
	* @param mid
	*		要归并的前半部分的最后一个元素的下标
	* @param hi
	*		要归并的后半部分的最后一个元素的下标
	*/
	@SuppressWarnings("unchecked")
	private static void mergeChecked(Comparable[] a, int lo, int mid, int hi) {
		if (a[mid].compareTo(a[mid + 1]) <= 0) 
			return;

		//先将数据暂存在辅助数组中
		for (int i = lo; i <= hi; i++)
			aux[i] = a[i];

		//i, j分别为两部分的第一个元素的下标
		int i = lo;
		int j = mid + 1;
		//归并
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = aux[j++];
			else if (j > hi)
				a[k] = aux[i++];
			else if (less(aux[j], aux[i]))
				a[k] = aux[j++];
			else 
				a[k] = aux[i++];
		}
	} 

	/**
	* Abstract in-place Merge
	* Ex 2.2.9
	* 把aux作为方法内的局部变量, 防止多client同时调用mergeSort()的时候出现问题
	* @param auxLocal
	*		局部暂存数组
	* @param a
	*		目标数组
	* @param lo
	*		要归并的前半部分的起始下标
	* @param mid
	*		要归并的前半部分的最后一个元素的下标
	* @param hi
	*		要归并的后半部分的最后一个元素的下标
	*/
	private static void mergeLoaclAux(Comparable[] auxLocal, Comparable[] a, int lo, int mid, int hi) {
		//先将数据暂存在辅助数组中
		for (int i = lo; i <= hi; i++)
			auxLocal[i] = a[i];

		//i, j分别为两部分的第一个元素的下标
		int i = lo;
		int j = mid + 1;
		//归并
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = auxLocal[j++];
			else if (j > hi)
				a[k] = auxLocal[i++];
			else if (less(auxLocal[j], auxLocal[i]))
				a[k] = auxLocal[j++];
			else 
				a[k] = auxLocal[i++];
		}
	}

	/**
	* Abstract in-place Merge
	* Ex 2.2.11
	* 把aux作为方法内的局部变量, 防止多client同时调用mergeSort()的时候出现问题, 并且检查检查a[mid]是否小于等于a[mid + 1]
	* @param auxLocal
	*		局部暂存数组
	* @param a
	*		目标数组
	* @param lo
	*		要归并的前半部分的起始下标
	* @param mid
	*		要归并的前半部分的最后一个元素的下标
	* @param hi
	*		要归并的后半部分的最后一个元素的下标
	*/
	private static void mergeImprove(Comparable[] auxLocal, Comparable[] a, int lo, int mid, int hi) {
		//i, j分别为两部分的第一个元素的下标
		int i = lo;
		int j = mid + 1;
		//归并
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = auxLocal[j++];
			else if (j > hi)
				a[k] = auxLocal[i++];
			else if (less(auxLocal[j], auxLocal[i]))
				a[k] = auxLocal[j++];
			else 
				a[k] = auxLocal[i++];
		}
	}

	/**
	* Ex 2.2.10
	* Faster merge, but the resulting sort is not stable(P341)
	* 相比原始版本的merge, 这个版本减少了两个if语句, 似乎会快那么一丢丢(😂笑哭), 然而并不稳定
	* @param a
	*		目标数组
	* @param lo
	*		要归并的前半部分的起始下标
	* @param mid
	*		要归并的前半部分的最后一个元素的下标
	* @param hi
	*		要归并的后半部分的最后一个元素的下标
	*/
	private static void mergeFaster(Comparable[] a, int lo, int mid, int hi) {
		//先将数据暂存在辅助数组中
		for (int i = lo; i <= mid; i++)
			aux[i] = a[i];

		//将后半部分逆序放入辅助数组
		for (int i = mid + 1; i <= hi; i++)
			aux[i] = a[hi - i + mid + 1];

		//i, j分别为两部分的第一个元素的下标
		int i = lo;
		int j = hi;
		int k = lo;

		//归并
		while (j >= i) {
			if (less(aux[j], aux[i]))
				a[k++] = aux[j--];
			else
				a[k++] = aux[i++];
		}
	}


	/**
	* 采用自顶向下的方法(递归)排序数组中指定的部分
	* Recursive MergeSort
	* 采用递归方法和分治思想(devide-and-conquer)来进行排序
	* 可用数学归纳思想来证明算法的正确性: 如果能够单独的排序两个子数组, 那么就能够通过归并这两个子数组的结果得到完全排序好的数组
	*
	* 定义函数 `!$C(N)$` 表示排序一个长度为N的数组的比较次数, 显然: `!$C(0) = C(1) = 0$`, 
	* 而且对于 `!$N > 0$`, 在递归方法 mergeSort() 中, 有此上界:
	* ```mathjax!
	* $$C(N) \leq C\left( \lfloor \frac {N} {2} \rfloor \right) + C\left( \lceil \frac {N} {2} \rceil \right) + N $$
	* ```
	* 最后一个N表示merge花费的最多比较次数.
	* 并且同时有此下界:
	*
	* ```mathjax!
	* $$C(N) \ge C\left( \lfloor \frac {N} {2} \rfloor \right) + C\left( \lceil \frac {N} {2} \rceil \right) + \lfloor \frac {N} {2} \rfloor $$
	* ```
 	* `!$\lfloor \frac {N} {2} \rfloor$` 表示merge所花费的最少比较次数, 正好就是两个子序列直接合在一起(前后两部分反着合起来运算)
 	* 就是完全有序的了, merge还是需要花费一半的比较次数来比较前半部分, 到了i > mid或者j > hi的时候, 就不需要比较了.
 	*
 	* 为了方便计算, 这里假设 `!$N = 2^n , \ \therefore \lfloor \frac {N} {2} \rfloor = \lceil \frac {N} {2} \rceil = 2^{n - 1}$`
 	*
 	* 于是上界:
 	* `!$ C(N) = C(2^n) = 2C(2^{n - 1}) + 2^n$`
 	*
 	* 左右同除以 `!$2^n$`, 得到:
 	*
 	* `!$ \frac  {C(2^n)} {2^n} = \frac {C(2^{n - 1})} {2^{n - 1}} + 1$`, 这是一个等差数列, 
 	*
 	* 易得: `!$\frac {C(2^n)} {2^n} = \frac {C(2^0)} {2^0} + n, \Rightarrow C(N) = C(2^n)  = n2^n = N \log N $`
 	*
 	* 另外一个证明方法为:
 	*
 	* mergeSort采用递归和分治思想, 把整个序列分为了 在二叉树的kth level, 共有`!$2^k$`个merge调用, 而且每个merge调用都
 	* 需要最多比较`!$2^{n - k}$`次, 所以在n-level需要`!$2^k \cdot 2^{n - k} = 2^n$`次比较, 所以对于有n个level的二叉树状
 	* mergeSort中, 共需要`!$n2^n$`次比较, 又对于N个结点的二叉树, 其深度为`!$\log_2 N$`, 所以总共的最多比较次数为`!$N \log N$`.
 	*
	* @param a
	*		要排序的数组
	* @param lo
	*		要排序的部分的第一个元素的下标
	* @param hi
	*		要排序的部分的最后一个元素的下标
	*/
	private static void mergeSort(Comparable[] a, int lo, int hi) {
		//当只有一个元素的时候, 这个子序列一定是排序好的了, 所以这就作为递归结束的条件
		if (lo >= hi)
			return;

		int mid = lo + (hi - lo) / 2;

		//下述代码形成一个二叉树形结构, 或者用trace表示为一个自顶向下的结构(top-down)
		//sort left half
		mergeSort(a, lo, mid);
		//sort right half
		mergeSort(a, mid + 1, hi);

		//merge才是真正的比较的地方, 上面的代码只是会形成二叉树, 真正的比较是在merge中
		merge(a, lo , mid, hi);
	}

	/**
	* Ex 2.2.9
	* 把aux作为方法内的局部变量, 防止多client同时调用mergeSort()的时候出现问题
	* 采用自顶向下的方法(递归)排序数组中指定的部分
	* @param auxLocal
	*		局部暂存数组
	* @param a
	*		要排序的数组
	* @param lo
	*		要排序的部分的第一个元素的下标
	* @param hi
	*		要排序的部分的最后一个元素的下标
	*/
	private static void mergeSortLocalAux(Comparable[] auxLocal, Comparable[] a, int lo, int hi) {
		//当只有一个元素的时候, 这个子序列一定是排序好的了, 所以这就作为递归结束的条件
		if (lo >= hi)
			return;

		int mid = lo + (hi - lo) / 2;

		//下述代码形成一个二叉树形结构, 或者用trace表示为一个自顶向下的结构(top-down)
		//sort left half
		mergeSortLocalAux(auxLocal, a, lo, mid);
		//sort right half
		mergeSortLocalAux(auxLocal, a, mid + 1, hi);

		//merge才是真正的比较的地方, 上面的代码只是会形成二叉树, 真正的比较是在merge中
		mergeLoaclAux(auxLocal, a, lo , mid, hi);
	}

	/**
	* Ex 2.2.11
	* 优化版本
	* 采用自顶向下的方法(递归)排序数组中指定的部分
	* @param auxLocal
	*		局部暂存数组的指针封装类
	* @param a
	*		要排序的数组的指针封装类
	* @param lo
	*		要排序的部分的第一个元素的下标
	* @param hi
	*		要排序的部分的最后一个元素的下标
	*/
	private static void mergeSortImprove(Pointer<Comparable[]> auxLocal, Pointer<Comparable[]> a, int lo, int hi) {
		//当只有一个元素的时候, 这个子序列一定是排序好的了, 所以这就作为递归结束的条件
		if (lo >= hi)
			return;

		//CUTOFF
		//剪枝, 为了避免叶子过于庞大, 直接在子数组小于CUTOFF的时候用InsertionSort剪枝. 这样可以提高一些效率..
		//1/4N^2与NlogN的交点为(16, 64)
		final int CUTOFF = 15;
		if (hi <= lo + CUTOFF - 1) {
			InsertionSort.insertionSort(a.p, lo, hi);
			return;
		}


		int mid = lo + (hi - lo) / 2;
		

		//下述代码形成一个二叉树形结构, 或者用trace表示为一个自顶向下的结构(top-down)
		//sort left half
		mergeSortImprove(a, auxLocal, lo, mid);
		//sort right half
		mergeSortImprove(a, auxLocal, mid + 1, hi);

		//处理两部分直接合起来就是已经排序好的情况, 不过这里跟mergeFaster()中要处理的方法不一样, 因为这里的aux
		//和a要轮流使用, 所以不能直接返回而是要把aux中的结果转移到a中去
		if (!less(auxLocal.p[mid + 1], auxLocal.p[mid])) {
			//System.out.println("Fuck passed by value...");
			a.p = auxLocal.p;
			return;
		}

		//merge才是真正的比较的地方, 上面的代码只是会形成二叉树, 真正的比较是在merge中
		mergeImprove(auxLocal.p, a.p, lo , mid, hi);

	}

}///~