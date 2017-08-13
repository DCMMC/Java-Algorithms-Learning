package tk.dcmmc.sorting.Exercises;

import tk.dcmmc.sorting.Algorithms.MergeSort;
import edu.princeton.cs.algs4.StdRandom;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import tk.dcmmc.fundamentals.Algorithms.BinarySearch;

/**
* 归并排序算法练习
* Ex 2.2
* Create on 2017/8/9
* Finish on 2017/8/
* @author DCMMC
* @since 1.5
*/
class Mergesort {
	/**************************************
     * Methods                            *
     **************************************/
	/**
	* Ex 2.2.11
	* 验证数组已经由小到大排序好了
	* @param a
	*		数组
	* @return
	*		如果数组已经由小到大排序好了就返回true
	*/
	@SuppressWarnings("unchecked")
	private static boolean isSorted(Comparable[] a) {
		Comparable last = a[0];

		for (int i = 1; i < a.length; i++) {
			if (last.compareTo(a[i]) > 0)
				return false;

			last = a[i];
		}

		return true;
	}

    /**
    * Ex 2.2.18
    * test shuffle
    */
    private static void testShuffle() {
        //13张牌
        //保存测试结果
        int[][] result = new int[13][13];

        //原始数组
        int[] poker = new int[13];
        for (int i = 0; i < 13; i++)
            poker[i] = i + 1;

        //测试次数
        int trials = 13000;
        for (int i = 0; i < trials; i++) {
            DoubleLinkedList<Integer> pokerList = new DoubleLinkedList<>(poker);

            pokerList.shuffle();

            //统计洗牌之后每张牌的位置
            int index = 0;
            for (int p : pokerList)
                result[index++][p - 1]++;
        }

        //打印结果
        of("      ");
        for (int i = 0; i < 13; i++)
            of("%6d", i + 1);
        o();

        for (int i = 0; i < 13; i++) {
            of("%6d", i + 1);

            for (int j = 0; j < 13; j++) {
                of("%6d", result[i][j]);
            }
            o();
        }
    }

    /**
    * Ex 2.2.19
    * 分治法统计序列中的逆序数对数(线代的一个术语)
    * @param a
    *        target array
    */
    private static int inversions(Comparable[] a) {
        return inversions(a.clone(), a.clone(), 0, a.length - 1);
    }

    /**
    * Ex 2.2.19
    * O(NlogN)
    * 分治法统计序列中的逆序数对数(线代的一个术语)
    * @param a
    *        target array
    * @param lo
    *        low index
    * @param hi
    *        high index
    */ 
    @SuppressWarnings("unchecked")
    private static int inversions(Comparable[] a, Comparable[] aux, int lo, int hi) {
        //递归边界条件
        if (lo >= hi)
            return 0;

        int mid = lo + (hi - lo) / 2;

        int inversionPairs = inversions(a, aux, lo, mid) + inversions(a, aux, mid + 1, hi);

        //统计跨越中点的逆序数对数(i.e., i <= mid < j)
        //这里和mergesort的merge很像, 只不过是在merge的基础上添加了统计逆序对
        //复制到aux中
        System.arraycopy(a, lo, aux, lo, hi - lo + 1);

        //统计逆序对
        int i = mid;
        int j = hi;

        while (i >= lo && j >= mid + 1) {
            if (a[i].compareTo(a[j]) <= 0)
                j--;
            else {
                i--;
                inversionPairs += (j - mid);
            }
        }

        //排序
        int k = lo;
        //i, j 分别从两部分的第一个元素开始
        j = mid + 1;
        i = lo;
        while (k <= hi) {
            if (i > mid) {
                a[k++] = aux[j++];
            } else if (j > hi) {
                a[k++] = aux[i++];
            } else if (aux[j].compareTo(aux[i]) < 0) {
                a[k++] = aux[j++];
            } else 
                a[k++] = aux[i++];
        }

        return inversionPairs;
    }

    /**
    * Ex 2.2.20
    * Indirect mergesort
    * @param a
    *       target array
    * @return
    *       不改变a的值, 输出一个a中值如果按照由小到大的顺序排列的下标
    * @param lo
    *        low index
    * @param hi
    *        high index
    */ 
    private static int[] indirectMergeSort(final Comparable[] a) {
        return indirectMergeSort(a, 0, a.length - 1);
    }

    /**
    * Ex 2.2.20
    * Indirect mergesort
    * @param a
    *       target array
    * @return
    *       不改变a的值, 输出一个a中值如果按照由小到大的顺序排列的下标
    */
    @SuppressWarnings("unchecked")
    private static int[] indirectMergeSort(final Comparable[] a, int lo, int hi) {
        //递归边界
        if (lo == hi) {
            int[] result = new int[a.length];
            result[lo] = lo;
            return result;
        } 

        int mid = lo + (hi - lo) / 2;

        int[] left = indirectMergeSort(a, lo, mid);
        int[] right = indirectMergeSort(a, mid + 1, hi);

        int[] result = new int[a.length];
        //i, j分别为两部分的第一个元素的下标
        int i = lo;
        int j = mid + 1;
        //归并
        for (int k = lo; k <= hi; k++) {
            if (i > mid)
                result[k] = right[j++];
            else if (j > hi)
                result[k] = left[i++];
            else if (a[right[j]].compareTo(a[left[i]]) < 0)
                result[k] = right[j++];
            else 
                result[k] = left[i++];
        }

        return result;
    }

    /**
    * Ex 2.2.21
    * O(NlogN) time
    * Triplicates 一式三份
    * 判断三个列表(三个列表等长)中是否有共有的名字
    * @param a
    *       List1
    * @param b
    *       List2
    * @param c
    *       List3
    * @return 
    *       第一个被找到的名字, 没有就返回null
    */
    @SuppressWarnings("unchecked")
    private static String triplicates(String[] a, String[] b, String[] c) {
        //排序
        MergeSort.mergeSort(b);
        MergeSort.mergeSort(c);

        //以a为基准遍历(如果a, b, c不等长的话就以最长的为基准), 开始二分查找
        for (String s : a) {
            if (BinarySearch.search(b, s, 0, b.length - 1) > -1 && BinarySearch.search(c, s, 0, c.length - 1) > -1)
                return s;
        }

        return null;
    }

	/**************************************
     * 我的一些方法和client测试方法         *
     **************************************/

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * @param obj 要输出的String.
     */
    private static void o(Object obj) throws IllegalArgumentException {
        System.out.println(obj);
    }

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * 重载的一个版本, 不接受任何参数, 就是为了输出一个回车.
     */
    private static void o() {
        System.out.println();
    }


    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * 格式化输出.
     * @param format
     *        a format string.
     * @param args
     *        由format中格式说明符指定的内容
     * @throws
     *        NullPointerException format不能为null
     */
    private static void of(String format, Object... args) {
        if (format == null)
            throw new NullPointerException("第一个参数不允许为空");

        System.out.printf(format, args);
    }


    /**
     * 为每道题的输出前面加一行Title, 这样看起来舒服一点
     * @param exName 题目名称
     */
    private static void title(String exName) {
        final int LEN = 80;
        String titleStr = "";

        int prefixLen = (LEN - exName.length()) / 2;
        int suffixLen =  LEN - prefixLen - exName.length();

        for (int i = 0; i < prefixLen; i++)
            titleStr += '#';
        titleStr += exName;
        for (int i = 0; i < suffixLen; i++)
            titleStr += '#';

        o("\n" + titleStr + "\n");
    }

	/**
	* Test Client
	* @param args
	* 			commandline arguments
	*/
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//Ex 2.2.4
		//merge只能用于已经排序好的字串
		//反例: 9 8 6 7 8 | 5 3 2 4 1的归并结果为5 3 2 4 1 9 8 6 7 8

		//Ex 2.2.8
		//见MergeSort.java源码

		//Ex 2.2.9
		//实现见MergeSort.java源码
		title("Ex 2.2.9");

		int size = 10000;
        Integer[] array = new Integer[size];
        for (int i = 0; i < array.length; i++)
            array[i] = i;
        StdRandom.shuffle(array);

        MergeSort.mergeSortLocalAux(array);

        //验证
        o(isSorted(array));

        //o(new DoubleLinkedList<>(array));

        //Ex 2.2.10
        //实现见MergeSort.java源码
        
        //Ex 2.2.11
        //我的解答有点问题, 把那三个优化弄在一起有点难受...
        //见MergeSort.java源码
        title("Ex 2.2.11");

        size = 100;

        array = new Integer[size];
        for (int i = 0; i < array.length; i++)
            array[i] = StdRandom.uniform(3);

        StdRandom.shuffle(array);

        array = (Integer[])MergeSort.mergeSortImprove(array);

        //验证
        o(isSorted(array));

        //Ex 2.2.12
        //有点难理解
        //TODO

        //Ex 2.2.16
        //有点难度
        //Natural merge sort
        title("Ex 2.2.16");

        array = new Integer[size];
        for (int i = 0; i < array.length; i++)
            array[i] = StdRandom.uniform(size);

        //array = new Integer[]{1, 3, 5, 2, 3};

        MergeSort.mergeSortNatural(array);

        o(isSorted(array));

        //Ex 2.2.17
        //实现见DoubleLinkedList.java
        title("Ex 2.2.17");

        array = new Integer[size];
        for (int i = 0; i < array.length; i++)
            array[i] = size - i;
        StdRandom.shuffle(array);
        DoubleLinkedList<Integer> list = new DoubleLinkedList<>(array);

        //o(list);

        list.mergesort();

        //o(list);
        o(isSorted(list.toArray()));

        //test mergesort with compator
        String[] strs = {"a", "c", "b", "A"};

        DoubleLinkedList<String> strList = new DoubleLinkedList<>(strs);

        strList.mergesort(String.CASE_INSENSITIVE_ORDER);

        o(strList);

        //Ex 2.2.18
        //有点难度
        //TODO https://stackoverflow.com/questions/12167630/algorithm-for-shuffling-a-linked-list-in-n-log-n-time
        //分治思想的随机洗牌算法
        title("Ex 2.2.18");

        DoubleLinkedList<Integer> intList = new DoubleLinkedList<>(1, 2, 3);
        intList.shuffle();
        o(intList);

        //测试随机算法的随机性
        testShuffle();

        //Ex 2.2.19
        title("Ex 2.2.19");

        o(inversions(new Integer[]{-1, 3, 3, 5, 5, 41, 5435, -11, 3423, 4432, -4421, 34432}));

        //Ex 2.2.20
        title("Ex 2.2.20");

        Integer[] test = new Integer[]{1, 3, 6, 1};

        o(new DoubleLinkedList<>(indirectMergeSort(test)));

        //Ex 2.2.21
        title("Ex 2.2.21");

        String[] a = "test string one".split(" ");
        String[] b = "two test in triplicates".split(" ");
        String[] c = "Three String test".split(" ");

        o(triplicates(a, b, c));

        /* Experiments */
        //Ex 2.2.23
        //根据近似函数分析, nlogn1/4n^2和交点大概在(16, 64), 所以CUTOFF应该选择在16以下
        //也可以通过SortCompare进行比较

        //Ex 2.2.25
        //分析算法

        //Ex 2.2.29

	}
}///~