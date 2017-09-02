package tk.dcmmc.sorting.Algorithms;

import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.net.URISyntaxException;

/**
* Ex 2.4.33
* Index minimum PriorityQueue Library
* 在实际使用中, 很有可能客户端程序员多个平行的数组来存储相同元素的各种类型的信息, 比如有id[N]来存储这N个元素的id, height[N]存储这
* N个元素的height, 这时候直接用排序index, 用index来访问所有这些平行的数组.
* Create on 2017/8/26
* Finish on 2017/8/27
* @author DCMMC
* @since 1.5
*/
public class IndexMinPQ<Key extends Comparable<Key>> {
	/* Fields */

	//number of elements on PQ
	private int size = 0;
	//binary heap using 1-based indexing hold
	private int[] pq;
	//inverse: qp[pq[i]] = pq[qp[i]] = i
	//qp[i] = -1表示i这个index不在queue中
	//qp存储的是client存入的index在pq的位置
	private int[] qp;
	//hold the keys value with priorities
	private Key[] keys;
	//capacity
	private int capacity;

	/* Constructors */

	//cannot be instanced by default construction
	private IndexMinPQ() {

	}

	/**
	* create a priority queue of initial capacity {@code cap} with possible indices between 0 and capacity - 1
	* @param cap
	*		initial capacity
	*/
	@SuppressWarnings("unchecked")
	public IndexMinPQ(int cap) {
		this.capacity = cap;

		keys = (Key[])new Comparable[cap + 1];
		pq = new int[cap + 1];
		qp = new int[cap + 1];

		//现在没有任何index在queue中
		for (int i = 0; i < cap + 1; i++)
			qp[i] = -1;
	}


	/* public methods */

	/**
	* insert item
	* @param k 
	*		associated index
	* @param key
	*		item to insert
	*/
	public void insert(int k, Key key) {
		if (size + 1 > capacity) {
			System.out.println("This priority queue is FULL!");
			return;
		}

		//先添加在最后一个元素后面, 然后swim上去
		qp[k] = ++size;
		pq[size] = k;
		keys[k] = key;
		swim(size);
	}

	/**
	* change the item associated with k to {@code item}
	* @param k
	*		associated index
	* @param key
	*		new value in item associated with index k
	* @throws NullPointerException key must NotNull
	*/
	public void change(int k, Key key) throws NullPointerException {
		if (!contains(k)) {
			System.out.println("index k not associated with any item!");
			return;
		}

		if (key == null) 
			throw new NullPointerException("argument key is null!");

		//如果改成了一个更加大的数, 就对这个位置进行sink
		if (keys[k].compareTo(key) < 0) {
			keys[k] = key;
			sink(qp[k]);
		} else {
			//否则就是向上swim
			keys[k] = key;
			swim(qp[k]);
		}
	}

	/**
	* is k associated with some item?
	* @param k
	*		index to be jduged
	* @return 
	*		return true if k associated with some item, otherwise return false
	*/
	public boolean contains(int k) {
		return qp[k] != -1;
	}

	/**
	* remove k and its associated item
	* @param k
	*		the index of item to be removed
	*/
	public void delete(int k) {
		if (!contains(k)) {
			System.out.println("index k not associated with any item!");
			return;
		}

		keys[k] = null;
		//和pq中最后一个元素交换并且对这个元素进行sink
		exch(qp[k], size--);
		sink(qp[k]);
		qp[k] = -1;
	}

	/**
	* return a minimal item
	* @return 
	*		the minimal item
	*/
	public Key min() {
		return isEmpty() ? null : keys[pq[1]];
	}

	/**
	* return a minimal item's index
	* @return 
	*		the index of minimal item, -1表示queue没空, 没有最小的元素的index
	*/
	public int minIndex() {
		return isEmpty() ? -1 : pq[1];
	}

	/**
	* remove a minimal item and return its index
	* @return 
	*		the index of minimal item, 如果PQ为空就返回-1
	*/
	public int delMin() {
		if (isEmpty())
			return -1;

		int indexOfMin = pq[1];
		exch(1, size--);
		sink(1);
		keys[pq[size + 1]] = null;
		qp[pq[size + 1]] = -1;
		return indexOfMin;
	}	

	/**
	* is the priority queue empty?
	* @return
	*		true if the priority queue is empty
	*/
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	* number of items in the priority queue
	* @return
	*		items' number
	*/
	public int size() {
		return this.size;
	}

	/**
	* exch
	* 交换pq中i和j位置上存储的信息, 同时相应的对qp中的元素交换
	* @param i 1 <= i <= size
	* @param j 1 <= j <= size
	*/
	private void exch(int i, int j) {
		qp[pq[i]] = j;
		qp[pq[i]] = i;

		int tmp = pq[i];
		pq[i] = pq[j];
		pq[j] = tmp;
	}

	/**
	* more
	* 比较pq中i和j的位置上存储的index对应的Key
	*/
	private boolean more(int i, int j) {
		return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
	}

	/**
	* Bottom-up reheapifying (swim)
	* O(logN)
	* @param k 要操作的node的位置 1 <= k <= size
	*/
	private void swim(int k) {
		//floor(k/2)就是k的父节点的index, 易证
		while (k > 1 && more(k / 2, k) ) {
			exch(k / 2, k);
			k /= 2;
		}

		//debug...
		//System.out.println("after swim " + k);
		//System.out.println("pq qp keys");
		//for (int i = 1; i <= size; i++)
		//	System.out.printf("%2d %2d %2s\n", pq[i], qp[pq[i]], keys[pq[i]]);
	}


	/**
	* Top-down reheapofying (sink)
	* O(logN)
	* 通过不断与其较小的子节点进行交换直到两个子节点的key都要大于等于该node, 或者是已经到了heap的bottom
	* k节点的两个子节点的index分别为2k和2k + 1
	* @param k 要操作的node的位置 1 <= k <= size
	*/
	private void sink(int k) {
		//其左边那个子节点要存在, 不然的话就已经到了heap的bottom了
		while (k * 2 <= size) {
			int j = 2 * k;
			if (j < size && more(j , j + 1))
				++j;
			if (!more(k, j)) 
				break;
			exch(k, j);
			k = j;
		}

		//debug..
		//System.out.println("after sink " + k + "\npq qp keys");
		//for (int i = 1; i <= size; i++)
		//	System.out.printf("%2d %2d %2s\n", pq[i], qp[pq[i]], keys[pq[i]]);
	}


	/**
	* test client
	* multiway merge with O(NlogM) time (N为所有ways个数组的元素之和, M为ways的大小, 和我在MergeSort那一节实现的
	* 方法是差不多的效率)
	* @param args 
	*			commaneline arguments
	*/
	public static void main(String[] args) {
		/* multiway merge */

		args = new String[]{"m1.txt", "m2.txt", "m3.txt"};

		try {
            Scanner[] streams = new Scanner[args.length];
            for (int i = 0; i < streams.length; i++) {
                String url = IndexMinPQ.class
                        .getResource(args[i])
                        .toExternalForm();
                streams[i] = new Scanner(new BufferedInputStream(new FileInputStream(new File(IndexMinPQ.class
                        .getResource(args[i])
                        .toURI()))));
            }

            IndexMinPQ<String> pq = new IndexMinPQ<>(streams.length);

            for (int i = 0; i < streams.length; i++)
                if (streams[i].hasNext())
                    pq.insert(i, streams[i].next());

            while (!pq.isEmpty()) {
                System.out.print(pq.min() + " ");

                int i = pq.delMin();
                if (streams[i].hasNext())
                    pq.insert(i, streams[i].next());
            }

            System.out.println("");
        } catch (FileNotFoundException fe) {
            //...
            System.out.println("Fuck0...");
        } catch (URISyntaxException ue) {
            System.out.println("Fuck1...");
        }

        /* Test all the other methods */
        IndexMinPQ<Character> queueByBraSize = new IndexMinPQ<>(10);

        String[] name = new String[]{"Candy", "Marri", "Sid", "Taylor", "Anna"};
        Character[] braSz = new Character[]{'A', 'C', 'B', 'C', 'D'};
        int[] height = new int[]{160, 165, 170, 171, 158};

        for (int i = 0; i < name.length; i++)
        	queueByBraSize.insert(i, braSz[i]);

        //change Sid's size to A
        queueByBraSize.change(2, 'A');

        //get one of the minimum's index
        System.out.println("minimul: " + name[queueByBraSize.minIndex()]);

        //delete the minimum
        queueByBraSize.delete(queueByBraSize.minIndex());

        //if the deleted was Candy then add Jesus
        name[0] = "Jesus";
        if (!queueByBraSize.contains(0))
        	queueByBraSize.insert(0, braSz[0]);
        System.out.println("minimul: " + name[queueByBraSize.minIndex()]);
	}
}///~