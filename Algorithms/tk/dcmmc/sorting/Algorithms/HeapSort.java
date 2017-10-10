package tk.dcmmc.sorting.Algorithms;

import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;

/**
* 堆排序法
* Invernted by J.W.J. Walliams and refined by R.W. Floyd in 1946
* 时间O(NlogN)(最坏情况下的比较次数为~ 2NlogN), 空间O(1), 这是唯一一个时间和空间都比较optimal的排序算法, 不过每次比较都很难是
* 比较数组中相邻的位置上的元素, 这会导致较低的cache performance, 低于mergesort, quicksort甚至shellsort, 因为后面这些都能保证
* 大多数情况都是比较临近的元素. 不过HeapSort是in-place的, 空间消耗极小而且代码量少, 可以适用于嵌入式系统和老式手机...
* Floyd改进了Sortdown中过多的比较次数(使之与mergesort相近), 对某些性能比较依赖比较时间的情况有帮助(比如String的比较, 比较耗费时间)
* Create on 2017/08/27
* @author DCMMC
* @since 1.5
*/
class HeapSort {
	/* Heap Sort */

	/**
	* Heap Sort
	* time O(N logN)
	* sapce O(1) (in-place)
	* @param a
	*		目标数组
	*/
	public static void heapsort(Comparable[] a) {
		int N = a.length;

		//phase 1, Heap construction
		//可以从第二个元素开始, 从左往右进行swim, 当前指针之前的元素就是heap-ordered的了, 后面的元素是待构造的元素, 指针到最后
		//一个元素的时候, 整个数组就是heap-ordered的了, 时间消耗O(NlogN)
		//一个更加高效的方法是从右往左进行sink, 从数组的floor(N/2)位置开始(N对应的是heap的最后一个元素, 而N/2代表最后一个元素的父节点,
		//这个结点也就是最后一个拥有子节点的结点了, 后面的结点都是最后一个level的)进行sink, 最开始这几个sink相当于reheapifying
		//三个元素的subheap, 然后逐渐reheapifying 7个元素的heaps... (结合heap的树形表示), 这样sink最后一个元素相当于reheapifying
		//整个数组代表的tree, 这时候就已经是heap-ordered的了.
		//sink-based heap construction在最坏情况下的交换次数为N, 比较次数为2N(因为每次sink调用要比较两次), 这个可以通过heap的树形图
		//来理解, N个元素的heap的binary tree的`!$\lfloor \log N \rfloor + 1$`层, N/2这个位置是在倒数第二层(也就是
		//`!$\lfloor \log N \rfloor$`层的最后一个有子结点的结点, 简称为k层), 然后最多有`!$2^k$`个有三个元素的subheap需要sink(也
		//就是最后一层全满的情况下), 遍历完这些最多3个元素的subheap之后需要遍历`!$2^{k - 1}$`个最多7个元素的subheap(也就是三层的树),
		//... 一直到最后sink整个树, 加入每次都需要交换, 则最多需要`!$2^k \sum_{i = 0}^{i = k} \frac {i + 1} {2^i} \dot{=} N$`
		//例如一个127个元素的heap, 需要sink 32个大小为3的subheaps, 16个大小为7的subheaps, 8个大小为15的subheaps, 4个大小为31的subheaps,
		//2个大小为63的subheaps, 一个大小为127的heap, 最坏情况下的交换次数就是32*1 + 16*2 + 8*3 + 4*4 + 2*5 + 1*5 = 120.
		for (int k = N / 2; k >= 1; k--)
			sink(a, k, N);

		//phase 2, Sortdown
		//因为Heap construction之后数组中的元素是按照heap binary tree的顺序存储的, 所以为了调整为由小到大的顺序, 需要将前面的较大的
		//元素移动到数组的后面去, 第一次把第一个元素(也就是最大的元素)和最后一个元素交换, 这样pq[N]就是最大的元素了, 然后在sink(1, N - 1)
		//把最后一个元素(这叫已排序的部分)之前的元素重新reheapifying, 然后在把第一个元素和第N - 1个元素交换... 这样就可以使整个数组变成由小到大的
		//顺序了, 按照前面证明的sink的比较次数为2logN, 可以得到Sortdown的比较次数为2NlogN
		while (N > 1) {
			exch(a, 1, N--);
			sink(a, 1, N);
		}

		//所以总的比较次数要少于2NlogN + 2N, 交换次数少于NlogN + N
	}

	/**
	* Floyd优化版本
	* 只在comparisons比较耗时的情况下有性能提升, 譬如String的比较
	*/
	public static void heapsortFloyd(Comparable[] a) {
		int N = a.length;

		for (int k = N / 2; k >= 1; k--)
			sink(a, k, N);

		while (N > 1) {
			exch(a, 1, N--);
			sinkFloyd(a, 1, N);
		}
	}

	/* Private method */

	/**
	* less
	* 比较a[i - 1]和a[j - 1]
	* @param a 目标数组
	* @param i 要比较的元素的index 1 <= i <= N
	* @param j 要比较的另外一个元素的index 1 <= j <= N
	* @return 如果i - 1对应的元素小于j - 1对应的元素, 就返回true, 否则返回false
	*/
	@SuppressWarnings("unchecked")
	protected static boolean less(Comparable[] a, int i, int j) {
		return a[i - 1].compareTo(a[j - 1]) < 0;
	}

	/**
	* exch
	* 交换a[i - 1] 和 a[j - 1]
	* 不检查i, j的合法性
	*/
	protected static void exch(Comparable[] a, int i, int j) {
		Comparable t = a[i - 1];
		a[i - 1] = a[j - 1];
		a[j - 1] = t;
	}

	/**
	* Bottom-up reheapifying (swim)
	* O(logN)
	* 如果k位置上的node比父节点还大, 就要让k上的这个node游到heap中更高的位置上去, 通过不断与比它小的父节点交换直到已经到了root或者
	* 父节点的key已经比它要大了.
	* @param 目标数组
	* @param k 要操作的node的位置 1 <= k <= N
	*/
	private static void swim(Comparable[] a, int k) {
		//floor(k/2)就是k的父节点的index, 易证
		while (k > 1 && less(a, k / 2, k)) {
			exch(a, k / 2, k);
			k /= 2;
		}
	}

	/**
	* Top-down reheapofying (sink)
	* O(logN)
	* 通过不断与其较大的子节点进行交换直到两个子节点的key都要小于等于该node, 或者是已经到了heap的bottom
	* k节点的两个子节点的index分别为2k和2k + 1
	* @param a 目标数组
	* @param k 要操作的node的位置 1 <= k <= N
	* @param N binart heap的最后一个元素的index
	*/
	private static void sink(Comparable[] a, int k, int N) {
		//其左边那个子节点要存在, 不然的话就已经到了heap的bottom了
		while (k * 2 <= N) {
			int j = 2 * k;
			if (j < N && less(a, j, j + 1))
				++j;
			if (!less(a, k, j)) 
				break;
			exch(a, k, j);
			k = j;
		}
	}

	/**
	* sink improved by Floyd (1964)
	* 一直与children中较大的那个交换直到没有children.
	*/
	private static void sinkFloyd(Comparable[] a, int k, int N) {
		//其左边那个子节点要存在, 不然的话就已经到了heap的bottom了
		while (k * 2 <= N) {
			int j = 2 * k;
			if (j < N && less(a, j, j + 1))
				++j;
			exch(a, k, j);
			k = j;
		}

		swim(a, k);
	}


	/**
	* test client
	* heapsort
	* @param args 
	*			commaneline arguments
	*/
	public static void main(String[] args) {
		/* heap sort */
		Integer[] test = new Integer[]{5, 6, 1, 5, 10};

		heapsort(test);

		System.out.println(new DoubleLinkedList<>(test));

		String[] strTest = {"hello", "hell", "world", "war"};

		heapsortFloyd(strTest);
		
		System.out.println(new DoubleLinkedList<>(strTest));
	}
}///~