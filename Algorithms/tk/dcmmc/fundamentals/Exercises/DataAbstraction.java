package tk.dcmmc.fundamentals.Exercises;

/**
* 本文件包括Exercise 1.2中部分习题的解答
* Created by DCMMC on 2017/7/20
* Finished on 2017/7/
*/

//因为是sublime text3的编译脚本, 所以不能有package语句,不然的话java就会找"./tk/.../*.class"
//然而class文件就在当前目录, 所以只能用默认缺省的包名
//package tk.dcmmc.fundamentals.Exercises;

//导入随书配套使用的库
//import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdDraw;

import tk.dcmmc.fundamentals.Algorithms.EuclidGCD;

/**
* class commit
* Algorithm Chapter 1.2 Exercise
* @Author DCMMC
* @since 1.5
* 
*/
public class DataAbstraction {
    /**************************************
    * 静态内部类                           *
    **************************************/ 
    /**
    * 数据可视化 P95 累加器
    */
    private static class VisualAccumulator {
        //记录累加的总数
        private double total;
        //记录累加的次数
        private int count = 0;

        //最大值
        private final double MAX;
        //尝试次数
        private final int TRIALS;

        //不能通过默认构造器实例化VisualAccumulator
        private VisualAccumulator() {
            this(0, 0);
        }

        /**
        * 构造器
        * @param trials
        *        累加次数
        * @param max
        *        能添加进入累加器的最大值
        */
        VisualAccumulator(int trials, double max) {
            //设置画布大小
            StdDraw.setXscale(0, trials);
            StdDraw.setYscale(0, max);
            //设置画笔大小
            StdDraw.setPenRadius(.005);

            //设置max
            this.MAX = max;
            //设置尝试次数
            this.TRIALS = trials;
        }

        /**
        * 向累加器添加值
        * @param val
        *        val必须是[0, max]的double数
        * @throws 
        */
        void addDataValue(double val) throws IllegalArgumentException {
            if (val < 0 || val > MAX)
                throw new IllegalArgumentException("val 只能在[0, "+ MAX + "]区间");

            //add只多执行TRAILS次
            if (++count > TRIALS) {
                o("尝试次数已满!");
                return;
            }

            //Add
            total += val;
            StdDraw.setPenColor(StdDraw.DARK_GRAY);
            StdDraw.point(count, val);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.point(count, total/count);
        }

        /**
        * 返回所有累加进来的数的平均数
        */
        double mean() {
            return count == 0 ? 0 : total/count;
        }

        /**
        * 重载的toString()
        */
        @Override 
        public String toString() {
            return "这" + count + "个数的平均数为" + mean();
        }

    }


    /**
    * Ex 1.2.16  Ex 1.2.17
    * 为有理数实现一个不可变数据类型(类似于String)
    * @Author DCMMC
    * @since 1.5
    * Created on 2017/7/21
    */
    private static class Rational {
        /**************************************
        * Fields                              *
        **************************************/
        //分子
        private final int NUMERATOR;
        //分母
        private final int DENOMINATOR;




        /**************************************
        * Constructors                        *
        **************************************/
        //不允许使用默认构造器创建实例
        private Rational() {
            this.NUMERATOR = 0;
            this.DENOMINATOR = 1;
        }

        /**
        * 接收两个int表示分子分母
        * @param numerator 
        *        分子
        * @param denominator
        *        分母
        * @throws IllegalArgumentException
        *         分母不能为0
        */
        Rational(int numerator, int denominator) throws IllegalArgumentException {
            if (denominator == 0)
                throw new IllegalArgumentException("分母不能为0");

            //如果分母为负数, 就把分子分母的符号就换一下, 这样可以把只有分母存在负号的时候
            //把分母的符号转移到分子, 这样toString()会好看一点; 如果分子分母都是负数, 还可以变成正数.
            if(denominator < 0) {
                denominator = -denominator;
                numerator = -numerator;
            }

            //设置值
            this.NUMERATOR = numerator;
            this.DENOMINATOR = denominator;
        }   



        /**************************************
        * Public Methods                      *
        **************************************/
        /**
        * 该Rational与b的和
        * @param b
        *        要加上的那个Rational
        * @return 
        *        this + b得到的新的Rational, 这个新的Rational为最简分数
        */
        public Rational plus(Rational b) {
            return null;
        }

        /**
        * 该Rational与b的差
        * @param b
        *        要减去的那个Rational
        * @return 
        *        this - b得到的新的Rational, 这个新的Rational为最简分数
        */
        public Rational minus(Rational b) {
            return null;
        }

        
        /**
        * 该Rational与b的积
        * @param b
        *        要乘以的那个Rational
        * @return 
        *        this * b得到的新的Rational, 这个新的Rational为最简分数
        */
        public Rational times(Rational b) {
            //可能
            int tmpNumerator = this.getNumerator() * b.getNumerator();
            int tmpDenominator = this.getDenominator() * b.getDenominator();

            

            return new Rational(tmpNumerator, tmpDenominator);
        }

        /**
        * 该Rational与b的商
        * @param b
        *        要除以的那个Rational
        * @return 
        *        this / b得到的新的Rational, 这个新的Rational为最简分数
        */
        public Rational divides(Rational b) {
            return null;
        }

        /**
        * 比较that和该Rational是否相等
        * @param that
        *        要比较的那个Rational
        * @return 
        *        如果that和该Rational表示的值一样就返回true
        */
        @Override
        public boolean equals(Object obj) {
            //比较hashCode
            if (obj == this)
                return true;

            //为空永远返回false
            if (obj == null)
                return false;

            int[] irreducibleThis = irreducible(this.NUMERATOR, this.DENOMINATOR);
            int[] irreducibleObj = 
                irreducible( ((Rational)obj).getNumerator(), ((Rational)obj).getDenominator());

            return irreducibleThis[0] == irreducibleObj[0] 
                && irreducibleThis[1] == irreducibleObj[1];
        }

        /**
        * 描述该Rational
        * @return 
        *        人类可读的关于该Rational的信息
        */
        @Override
        public String toString() {
            return "该有理数为 " + getNumerator() + " / " + getDenominator() + "\n" +
                    "(近似)值为" + getNumerator() / getDenominator();
        }

        /**
        * 返回该Rational的分子
        * @return 
        *        该Rational的分子
        */
        public int getNumerator() {
            return this.NUMERATOR;
        }

        /**
        * 返回该Rational的分母
        * @return 
        *        该Rational的分母
        */
        public int getDenominator() {
            return this.DENOMINATOR;
        }


        /**************************************
        * Private Methods                     *
        **************************************/
        /**
        * Ex 1.1.25 
        * 欧几里德辗转相除求最大公约数
        * @param p nonzero int num.
        * @param q nonzero int num. 且|p| >= |q|
        * @return gcd of p and q.
        */
        private static int gcd(int p, int q) {
            return EuclidGCD.gcd(p, q);
        }

        /**
        * 求出两个数的最简形式
        * @param a
        *        A nonzero int num.
        * @param b
        *        A nonzero int num.
        * @return 
        *        存有结果的数组
        * @throws 
        *        IllegalArgumentException a, b不能为0
        *        
        */
        private static int[] irreducible(int a, int b) throws IllegalArgumentException {
            int tmp = 1;
            while ((tmp = gcd(a, b)) != 1) {
                a /= tmp;
                b /= tmp;
            }


            return new int[] {a, b};
        }

    }



    /**************************************
    * 习题要用到的方法                     *
    **************************************/ 

    /**
    * Ex 1.2.6
    * 判断是否是一对字符串是否是回环变位的, i.e., 一个字符串是另外一个字符串循环移动位置之后的字符串
    * e.g. ACDGG 和 DGGAC 就是一组回环变位的
    * @param s 
    *        String1 非空
    * @param t 
    *        String2 非空
    * @throws NullPointerException
    *         两个参数都不允许为空
    * 
    * 如果是回环变位的话就返回true
    */
    private static boolean checkCirclarRotation(String s, String t) throws NullPointerException {
        if (s == null || t == null)
            throw new NullPointerException("参数不允许为空");

        /**
        * 如果s, t互为回环变位, 则两个s相连之后的字符串中 一定包含t.
        */
        return s.length() == t.length() && s.concat(s).indexOf(t) >= 0;
    }

    /**
    * Ex 1.2.7
    * 一个递归方法
    * @param s
    *        字符串
    */
    private static String mystery(String s) {
        final int N = s.length();

        if (N <= 1)
            return s;

        /**
        * s.substring(a, b) 返回 s中index为a到b-1(记得是b-1)的部分
        */
        String a = s.substring(0, N / 2),
               b = s.substring(N / 2, N);

        return mystery(b) + mystery(a);
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

        //Ex 1.2.7
        //就是反转字符串
        title("1.2.7");

        o("\"Algorithm Java\" 递归后为: " + mystery("Algorithm Java"));

        //类似于1.2.10 不过我没写1.2.10 只是按照P95写了个类似的数据可视化的例子
        title("可视化累加器");

        int trials = 2000;
        VisualAccumulator va = new VisualAccumulator(trials, 1.0);
        for (int t = 0; t < trials; t++) {
            va.addDataValue(StdRandom.uniform());
        }
        o(va);

        //Ex 1.2.16 & Ex 1.2.17
        title("Ex 1.2.16&17");
        
        
    }

}