package tk.dcmmc.sorting.Algorithms;

import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import edu.princeton.cs.algs4.StdRandom;

/**
* 函数式接口, 用于接收方法引用
*/
@FunctionalInterface
interface SortFunc {
	void sort(Comparable[] a);
}

/**
* 对多种Sort实现进行测试
* @author DCMMC
* @since 1.8
*/
public class SortExample {
	/**
	* 使用方法引用来包装所有的Sort方法
	* @param sort
	*			接收符合void sort(Comparable[] a)的方法引用
	* @param a
	*			要排序的数组, 排序的大小顺序由a中的compareTo方法实现
	*/
	public static void sort(SortFunc sortFunc, Comparable[] a) {
		sortFunc.sort(a);
	}

	/**
	* 判断数组已经按照由小到大顺序将a排序好了
	* @param a
	*			要判断的数组
	* @return 如果已经按照由小到大顺序将a排序好了就返回true
	*/
	public static boolean isSorted(Comparable[] a) {
		for (int i = 1; i < a.length; i++) 
			if (less(a[i], a[i - 1]))
				return false;

		return true;
	}

	/**
	* 判断v是否小于w
	* @param v
	*			要比较的两个对象中较小的那一个
	* @param w
	*			要比较的两个对象中较大的那一个
	* @return 如果v由其中的compareTo实现得出小于w, 就返回true
	*/
	@SuppressWarnings("unchecked")
	private static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;
	}

	
	/**
	* 展示a中的内容
	* @param a
	*		要展示其内容的数组
	*/
	private static void show(Comparable[] a) {
		String result = "[";

		for (Comparable i : a)
			result += " " + i;

		System.out.println(result + "](共" + a.length + "个元素)");
	}

	/**
	* Ex 2.1.18
	* 可视化得展示a中的内容
	* @param a
	*		要展示其内容的数组
	*/
	private static void visualShow(Comparable[] a) {
		
	}

	/**
	* 对随机产生的数组进行排序, 重复试验多次
	* @param alg
	*			要测试的算法
	* @param arraySize
	*			随机产生的数组的大小
	* @param trials
	*			重复试验次数
	* @return 总花费时间(单位ms)
	*/
	private static double timeRandomInput(String alg, int arraySize, int trials) {
		double total = 0.0d;

		Double[] a = new Double[arraySize];

		for (int t = 0; t < trials; t++) {
			for (int i = 0; i < arraySize; i++)
				a[i] = StdRandom.uniform();

			long start = System.currentTimeMillis();

			switch(alg) {
				case "Insertion" : InsertionSort.insertionSort(a);break;
				case "Selection" : SelectionSort.selectionSort(a);break;
				case "Shell" 	 : ShellSort.shellSort(a);break;
				case "Merge"	 : MergeSort.mergeSort(a);break;
				case "Quick"	 : QuickSort.quickSort(a);break;
				case "Heap"		 : HeapSort.heapSort(a);break;
				default			 : break;
			}

			total += System.currentTimeMillis() - start;
		}

		return total;
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

        System.out.println("\n" + titleStr + "\n");
    }

	/**
	* Test client.
	* @param args
	*			commandline arguments
	*/
	public static void main(String[] args) {
		//从文件读取ints到DoubleLinkedList数组
        try {
            File intsFile = new File(SortExample.class.getResource("mediumG.txt").toURI());

            if (intsFile.exists() && intsFile.canRead()) {
                Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(intsFile)), "UTF-8");

                //以回车符, 空白符分隔
                sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

                DoubleLinkedList<Integer> a = new DoubleLinkedList<>();

                while ( sc.hasNext() ) {
                    a.addLast(sc.nextInt());
                }

                Integer[] array = a.toArray();

                sort(InsertionSort::insertionSort, array);

                //System.out.println(new DoubleLinkedList<>(array));
            } else {
                //Debug...
                System.out.println("File not found....");
            }
        } catch (Exception e) {
            //Exception
            throw new RuntimeException(e);
        }


        //比较各个算法的时间
        title("");

        String alg1 = "Shell";
        String alg2 = "Insertion";

        int arraySize = 100000;
        int trials = 1;

        double t1, t2;

        System.out.printf("For %d random Doubles\n %s is %.1f times faster than %s\n t1 = %.3f, t2 = %.3f\n", 
        	arraySize, alg1, (t2 = timeRandomInput(alg2, arraySize, trials)) / (t1 = timeRandomInput(alg1, arraySize, trials)), 
        	alg2, t1, t2 );

        
	}
}///~