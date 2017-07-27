package tk.dcmmc.fundamentals.Exercises;

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;


/**
 * 本文件包括Exercise 1.3中部分习题的解答
 * Created by DCMMC on 2017/7/27
 * Finished on 2017/7/
 * @author DCMMC
 * @since 1.5
 */
class AnalysisOfAlgorithms {
	/**
	* Ex 1.4.41
	* 暴力算法TwoSum
	* 时间增长倍数: O(N^2)
	* @param a
	*			要处理的数组
	* @return 
	*			数组中两个元素之和为0的组数
	*/
	private static int countTwoSum(int[] a) {
		int count = 0;

		//时间增长倍数: O(N^2)
		for (int i = 0; i < a.length; i++)
			for (int j = i + 1; j < a.length; j++)
				if (a[i] + a[j] == 0)
					count++;

		return count;
	}

	/**
	* Ex 1.4.41
	* 归并排序+二分搜索算法TwoSum
	* 时间增长倍数: O(N*logN)
	* @param a
	*			要处理的数组
	* @return 
	*			数组中两个元素之和为0的组数
	*/
	private static int countTwoSumFast(int[] a) {
		int count = 0;

		//时间增长倍数: O(logN)
		//先归并排序
		Arrays.sort(a);

		//时间增长倍数: O(NlogN)
		//如果在a[i]的后面找到了值为-a[i]的元素, count就加一
		for (int i = 0; i < a.length; i++)
			if (BasicProgModel.rank(-a[i], a, 0, a.length - 1) > i)
				count++;

		return count;
	}

	/**
	* Ex 1.4.41
	* 暴力算法ThreeSum
	* 时间增长倍数: O(N^3)
	* @param a
	*			要处理的数组
	* @return 
	*			数组中三个元素之和为0的组数
	*/
	private static int countThreeSum(int[] a) {
		int count = 0;

		//时间增长倍数: O(N^3)
		for (int i = 0; i < a.length; i++)
			for (int j = i + 1; j < a.length; j++)
				for (int k = j + 1; k < a.length; k++)
					if (a[i] + a[j] + a[k] == 0)
						count++;

		return count;
	}

	/**
	* Ex 1.4.41
	* 归并排序+二分搜索算法ThreeSum
	* 时间增长倍数: O(N^3)
	* @param a
	*			要处理的数组
	* @return 
	*			数组中三个元素之和为0的组数
	*/
	private static int countThreeSumFast(int[] a) {
		int count = 0;

		//时间增长倍数: O(logN)
		//先归并排序
		Arrays.sort(a);

		//时间增长倍数: O(N^2*logN)
		for (int i = 0; i < a.length; i++)
			for (int j = i + 1; j < a.length; j++)
				if (BasicProgModel.rank(-a[i]-a[j], a, 0, a.length - 1) > j)
					count++;
				

		return count;
	}



	/**
	* Ex 1.4.41
	* DoublingTest
	* @throws RuntimeException
	*				读取文件的时候如果出问题就抛出RuntimeException
	*/
	private static void doublingTest() throws RuntimeException {
		String[] testFiles = {"1Kints.txt", "2Kints.txt", "4Kints.txt", "8Kints.txt"};

		//做4轮测试, 为了节省时间, 还是调成3算了
		for (int i = 0; i < 3; i++) {
			of( "%8dints:\n", (int)(Math.pow(2, i)*1000) );

			int[] ints = new int[(int)(Math.pow(2, i)*1000)];

			//从文件读取ints到ints数组
			try {
				File intsFile = new File(AnalysisOfAlgorithms.class.getResource(testFiles[i]).toURI());

				if (intsFile.exists() && intsFile.canRead()) {
					Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(intsFile)), "UTF-8");

					//以回车符, 空白符分隔
					sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

					int index = 0;
					while ( sc.hasNext() && index < (int)(Math.pow(2, i)*1000) ) {
						ints[index++] = Integer.parseInt(sc.next());
					} 
				} else {
					//Debug...
					o("File not found....");
				}
			} catch(IOException ioe) {
				//IOException....
				o("IOException");
				throw new RuntimeException(ioe);
			} catch (Exception e) {
				//Exception
				throw new RuntimeException(e);
			}

			//TwoSum
			long startTime = System.currentTimeMillis();

			countTwoSum(ints);

			of("      TwoSum:  %5.8fs\n", (System.currentTimeMillis() - startTime) / 1000.0);

			//TwoSumFast
			startTime = System.currentTimeMillis();

			countTwoSumFast(ints);

			of("  TwoSumFast:  %5.8fs\n", (System.currentTimeMillis() - startTime) / 1000.0);

			//ThreeSum
			startTime = System.currentTimeMillis();

			countThreeSum(ints);

			of("    ThreeSum:  %5.8fs\n", (System.currentTimeMillis() - startTime) / 1000.0);

			//ThreeSumFast
			startTime = System.currentTimeMillis();

			countThreeSumFast(ints);

			of("ThreeSumFast:  %5.8fs\n", (System.currentTimeMillis() - startTime) / 1000.0);

			String header = "";
			//打印分隔符
			for (int m = 0; m < 40; m++)
			header += "-";
				o(header);
		}

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
	* Test my solutions to this Exercises
	* @param args
	*			command-line arguments
	*/
	public static void main(String[] args) {
		//Ex 1.4.41
		title("Ex 1.4.41");

		doublingTest();

	}

}