package tk.dcmmc.sorting.Algorithms;


/**
* MaxPriorityQueue Library
* two operations: remove (any) the maximum key and insert.
* Create on 2017/8/24
* Finish on 2017/8/25
* @author DCMMC
* @since 1.5
*/
public class MaxPQ<Key extends Comparable<Key>> {
	/* Fields */

	//store binary heap
	//pq[0]为unused, 从1开始, 一直到N.
	//binary heap也就是Complete heap-ordered binary tree按照level从小到大, 每层level从左到右存储在数组中.
	private Key[] pq;

	//size, i.e. pq.length - 1
	private int size = 0;

	//defaule cap = 10000
	private static int capacity = 10_000;

	/* Constructors */

	/**
	* Default Constructor
	* create a priority queue
	*/
	@SuppressWarnings("unchecked")
	public MaxPQ() {
		pq = (Key[])new Comparable[capacity];
	}

	/**
	* create a priority queue of initial capacity {@code cap}
	* @param cap
	*		initial capacity
	*/
	@SuppressWarnings("unchecked")
	public MaxPQ(int cap) {
		pq = (Key[])new Comparable[cap + 1];
		this.capacity = cap;
	}

	/**
	* create a priority queue from the keys in a[]
	* @param a
	*		initial keys
	*/
	@SuppressWarnings("unchecked")
	public MaxPQ(Key[] a) {
		pq = (Key[])new Comparable[capacity <= a.length ? a.length + 1 : capacity];

		for (Key k : a)
			insert(k);
	}

	/* public methods */

	/**
	* insert a key into the priority queue
	* @param v
	*		key to insert
	*/
	public void insert(Key v) {
		if (this.size >= this.capacity) {
			System.out.println("PriorityQueue is Full!");
			return;
		}

		//先放在最后一个元素后面的位置上, 也就是最后一个level的最后一个node后面, 然后swim到正确的位置上
		pq[++size] = v;
		swim(size);
	}

	/**
	* (only) return the largest key
	* @return 
	*		the largest key
	*/
	public Key max() {
		if (!isEmpty())
			return pq[1];
		else
			return null;
	}

	/**
	* return and remove the largest key
	* @return 
	*		the largest key
	*/
	public Key delMax() {
		if (size == 0) {
			System.out.println("PrioeityQueue is Empty!");
			return null;
		}

		//先把最后一个元素交换到root的位置上去, 然后sink到正确的位置上.
		Key max = pq[1];
		exch(1, size--);
		pq[size + 1] = null;
		sink(1);

		return max;
	}

	/**
	* is the priority queue empty?
	* @return 
	* 		return true if the priority queue is empty, otherwise return false
	*/
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	* number of keys in priority queue
	* @return 
	*		number of keys in priority queue
	*/
	public int size() {
		return size;
	}

	/* Private method */

	/**
	* less
	* @param i 要比较的元素的index 1 <= i <= N
	* @param j 要比较的另外一个元素的index 1 <= j <= N
	* @return 如果i对应的元素小于j对应的元素, 就返回true, 否则返回false
	*/
	@SuppressWarnings("unchecked")
	private boolean less(int i, int j) {
		return pq[i].compareTo(pq[j]) < 0;
	}

	/**
	* exch
	*/
	private void exch(int i, int j) {
		Key t = pq[i];
		pq[i] = pq[j];
		pq[j] = t;
	}

	/**
	* Bottom-up reheapifying (swim)
	* O(logN)
	* 如果k位置上的node比父节点还大, 就要让k上的这个node游到heap中更高的位置上去, 通过不断与比它小的父节点交换直到已经到了root或者
	* 父节点的key已经比它要大了.
	* @param k 要操作的node的位置 1 <= k <= size
	*/
	private void swim(int k) {
		//floor(k/2)就是k的父节点的index, 易证
		while (k > 1 && less(k / 2, k)) {
			exch(k / 2, k);
			k /= 2;
		}
	}

	/**
	* Top-down reheapofying (sink)
	* O(logN)
	* 通过不断与其较大的子节点进行交换直到两个子节点的key都要小于等于该node, 或者是已经到了heap的bottom
	* k节点的两个子节点的index分别为2k和2k + 1
	* @param k 要操作的node的位置 1 <= k <= size
	*/
	private void sink(int k) {
		//其左边那个子节点要存在, 不然的话就已经到了heap的bottom了
		while (k * 2 <= this.size) {
			int j = 2 * k;
			if (j < this.size && less(j, j + 1))
				++j;
			if (!less(k, j)) 
				break;
			exch(k, j);
				k = j;
		}
	}


	/**
	* test client
	* @param args 
	*			commaneline arguments
	*/
	public static void main(String[] args) {
		Integer[] test = new Integer[]{1, 5, 6, 8, 45, -10, 56, 8, 100};

		MaxPQ<Integer> pq = new MaxPQ<>(test);

		for (Integer i : test)
			pq.insert(i);

		for (int i = 0; i < 5; i++)
			System.out.print(pq.delMax() + " ");

		System.out.println("\n" + pq.max());

	}	
}///~