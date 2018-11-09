package tk.dcmmc.sorting.Exercises;

import tk.dcmmc.sorting.Algorithms.IndexMinPQ;

/**
* Priority Queue练习
* Ex 2.4
* Create on 2017/09/02
* Finish on 2017/09/
* @author DCMMC
* @since 1.5
*/
class PriorityQueueEx {
	/**************************************
     * Methods                            *
     **************************************/


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
	* Entry point
	* @param args
	*		commandline args
	*/
	public static void main(String[] args) {
		//Ex 2.4.4
		//否, 因为heap只是确保每一条path都是按照顺序的, 然而对于最后一个level没满的heap-ordered complete binary tree, 最后一个level
		//可能还会大于上一个level中没有children的node.

		//Ex 2.4.10
		// floor((k + 1) / 2) - 1

		//Ex 2.4.12
		//heap > sorted array > unsorted array

		//Ex 2.4.13
		//change k * 2 to k * 2 + 1 在while语句块后面做一次k是否为 N / 2的判断, 这样能都减少对j的边界检查

		//Ex 2.4.15
		//从N/2位置从右往左开始sink, 类似于heapsort的O(N)的那个reheapifying的实现
		//或者递归判断也可以

		//Ex 2.4.17
		//N - k次replace the minimum就相当于去掉了N - k个最小的元素, 剩下了k个最大的元素

		//Ex 2.4.18
		//如果insert和remove the max这两个操作的路径完全一样, 就不会有变化
		//所以在高度大于等于4的树两个情况都有可能, 树高小于等于3的话, 肯定不会有变化(因为第二层有一个原来的max, 会引导remove的path
		//还是跟insert一样)

		//Ex 2.4.19
		//见到MaxPQ中的构造器

		//Ex 2.4.20
		//见(https://github.com/DCMMC/Notes/blob/master/%E7%AE%97%E6%B3%95%20Algorithm.md)中的排序算法->Priority Queue
		//->Heapsort中相关简证.

		/* Create Problems */
		
		//Ex 2.4.23

		//Ex Ex 2.4.33 2.4.34 IndexMinPQ
		title("Ex 2.4.33 2.4.34");

		IndexMinPQ.main(null);

		/* Experiments */

	}
}///~
