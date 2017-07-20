/**
* 本文件包括Exercise 1.2中部分习题的解答
* Created by DCMMC on 2017/7/20
* Finished on 2017/7/
*/

//因为是sublime text3的编译脚本, 所以不用管包的相对路径和classpath的问题
//package tk.dcmmc.fundamentals.Exercise;


/**
* class commit
* Algorithm Chapter 1.2 Exercise
* @Author DCMMC
* 
*/
class DataAbstraction {

	/**************************************
	* 习题要用到的方法                     *
	**************************************/	

	/**
	* Ex 1.2.6
	* 判断是否是一对字符串是否是回环变位的, i.e., 一个字符串是另外一个字符串循环移动位置之后的字符串
	* e.g. ACDGG 和 DGGAC 就是一组回环变位的
	* @param s 
	* 		 String1 非空
	* @param t 
	* 		 String2 非空
	* @throws NullPointerException
	* 		  两个参数都不允许为空
	* 
	* 如果是回环变位的话就返回true
	*/
	private static boolean checkCirclarRotation(String s, String t) throws NullPointerException {
		if (s == null || t == null)
			throw new NullPointerException("参数不允许为空");

		return s.length() == t.length() && s.concat(s).indexOf(t) >= 0;
	}


	/**************************************
     * 我的一些方法和client测试方法         *
     **************************************/

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * @param s 要输出的String.
     * @throws IllegalArgumentException 参数不能为空
     */
    private static void o(Object obj) throws IllegalArgumentException {
        if (obj == null)
        	throw new IllegalArgumentException("参数不能为空!");

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
     * 		  a format string.
     * @param args 
     *		  由format中格式说明符指定的内容
     * @throws 
     *		  NullPointerException format不能为null
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
        final int LEN = 40;
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
	* Client Method. 测试入口
	* @param args cmdline arguments.
	*/
	public static void main(String[] args) {
		//Ex 1.2.6
		title("Ex 1.2.6");

		o("ACDGG 和 DGGAC " + (checkCirclarRotation("ACDGG", "DGGAC") ? "是" : "不是") 
			+ "回环变位的");
	}

}