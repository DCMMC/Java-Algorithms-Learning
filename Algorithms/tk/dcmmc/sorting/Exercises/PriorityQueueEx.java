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

		


		//Ex Ex 2.4.33 IndexMinPQ
		title("Ex 2.4.33");

		IndexMinPQ.main();
	}
}///~
