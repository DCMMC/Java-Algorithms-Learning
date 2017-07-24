/**
 * 本文件包括Exercise 1.1中部分习题的解答
 * Created by DCMMC on 2017/7/17
 * Finished on 2017/7/20
 */

package tk.dcmmc.fundamentals.Exercises;

import java.util.*;

//导入随书配套使用的库
import edu.princeton.cs.algs4.*;

/**
 * class commit
 * Algorithm Chapter 1.1 Exercise
 * @Author DCMMC
 *
 */

public class BasicProgModel {
    /**************************************
     * 域变量                              *
     **************************************/
    //static int depth;

    //嵌套类(静态内部类)
    private static class DepthCount {
        //静态域变量
        static int depth = 1;

    }

    //BinarySearch(嵌套类)
    private static class BinarySearch {
        /**
         * Ex 1.1.29
         * 用二分搜索法找到数组中任意一个a的index然后再往前去找到最前面的那一个a的index(如果有的话),
         * 返回在这个已排序数组中小于a的元素的个数
         * @param key a int num.
         * @param a 必须是由小到大排序好的int数组, 数组中可能会有重复的元素.
         * @return 在这个已排序数组中小于a的元素的个数
         */
        static int rank(int key, int[] a) {
            //先调用外部类中的rank()方法来获得a中(多个)key的任意一个offset.
            int anyOffsetInA = BasicProgModel.rank(key, a);

            //开始向前查找是否还有key
            while (a[--anyOffsetInA] == key)
                ;//空语句

            return ++anyOffsetInA;
        }

        /**
         * Ex 1.1.29
         * 由上面rank()得出的结果的offset开始, 向后遍历数组, 返回数组中key的个数.
         * @param key a int num.
         * @param a 必须是由小到大排序好的int数组, 数组中可能会有重复的元素.
         * @return 返回数组中key的个数.
         */
        static int count(int key, int[] a) {
            //key的个数
            int cnt = 0;
            //数组a中第一个key的index
            int firstIndex = rank(key, a);

            while (a[firstIndex++] == key)
                cnt++;

            return cnt;
        }

    }


    /**
     * Ex 1.1.31
     * 点的信息
     */
    static class Point {
        //x, y分别是点x, y轴坐标(按照Algorithm中给的那个StdDraw库)
        //记住一定不能用static, static是所有实例中都共用一个值
        double x;
        double y;

        //不允许用默认构造器来实例化Point
        private Point() {

        }

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }



    /**************************************
     * 显式的静态初始化                     *
     **************************************/
    //static {
    //	depth = 1;
    //}

    /**************************************
     * 练习中的静态方法                     *
     **************************************/

    /**
     * A static method in Exercise 1.1.12
     * @param n an int number.
     * @return the result of this recursion method.
     */
    private static String exR1(int n) {
        if (n <= 0)
            return "";

        return exR1(n-3) + n + exR1(n-2) + n;
    }

    /**
     * Exercise 1.1.18
     * @param a A int number.
     * @param b A int number.
     * @return the result of this recursion method.
     */
    private static int mystery(int a, int b) {
        if(b == 0)
            return 0;
        if (b % 2 == 0)
            return mystery(a+a, b/2);

        return mystery(a+a, b/2) + a;
    }

    /**
     * Exercise 1.1.19
     * Fibonacci Function.
     * @param n 斐波那契数的阶数.
     * @return the result of Fibonacci Function.
     */
    private static long fibonacci(int n) {
        if (n == 0)
            return 0;
        if (n == 1)
            return 1;

        return fibonacci(n-1) + fibonacci(n-2);
    }

    /**
     * 把计算出的斐波那契数存在数组里面, 比上面的实现要更好一点点.
     * @param n 斐波那契数的阶数
     * @param array 记录了前n-1阶斐波那契数的数组
     * @return 第n阶斐波那契数的值
     * @throws IllegalArgumentException 数组必须足够大1以容纳n个值
     */
    private static long fibonacciArray(int n, long[] f) throws IllegalArgumentException {
        //数组必须要容纳的下n个值
        if(n > f.length)
            throw new IllegalArgumentException("Argument n = " + n + " 大于数组f的长度");

        //recursion的base case
        if (n == 1) {
            return f[0] = 1;
        }
        if (n == 2) {
            return f[1] = 1;
        }

        return f[n-1] = fibonacciArray(n-1, f) + fibonacciArray(n-2, f);
    }

    /**
     * Exercise 1.1.20
     * A static method to computer ln(N!)
     * @param n int number.
     * @return ln(n!)
     */
    private static double ln(int n) {
        if (n == 0 || n == 1)
            return 0;

        return Math.log(n) + ln(n-1);
    }

    /**
     * Exercise 1.1.22
     * @param key 要找的那个数值
     * @param a 已经由小到大排序好的int数组
     * @return 找不到就返回-1, 找得到就返回key在a中的index
     */
    private static int rank(int key, int[] a) {
        return rank(key, a, 0, a.length -1, new BasicProgModel.DepthCount());
    }

    /**
     * Exercise 1.1.22
     * 在数组中由参数指定的范围查找key的index.
     * @param key 要找的那个数值
     * @param a 已经由小到大排序好的int数组
     * @param lo 查找范围的下限index
     * @param hi 查找范围的上线offset
     * @param depthCnt 存储递归的深度的嵌套类
     * @return 找不到就返回-1, 找得到就返回key在a中的index
     */
    private static int rank(int key, int[] a, int lo, int hi, BasicProgModel.DepthCount depthCnt) {
        if (lo > hi) {
            o("Depth: " + depthCnt.depth + " lo: " + lo + " hi: " + hi);
            return -1;
        }

        int mid = lo + (hi - lo) / 2;

        if (key < a[mid]) {
            o("Depth: " + (depthCnt.depth++) + " lo: " + lo + " hi: " + hi);
            return rank(key, a, lo, mid - 1, depthCnt);
        } else if (key > a[mid]) {
            o("Depth: " + (depthCnt.depth++) + " lo: " + lo + " hi: " + hi);
            return rank(key, a, mid + 1, hi, depthCnt);
        } else {
            o("Depth: " + depthCnt.depth + " offset of key in array a: " + mid);
            return mid;
        }

    }

    /**
     * Ex 1.1.25
     * 欧几里德辗转相除求最大公约数
     * @param p nonzero int num.
     * @param q nonzero int num.
     * @return gcd of p and q.
     * @throws IllegalArgumentException p, q非0且|p| >= |q|.
     */
    private static int gcd(int p, int q) throws IllegalArgumentException {
        //取绝对值
        p = Math.abs(p);
        q = Math.abs(q);

        if (p < q || p == 0 || q == 0)
            throw new IllegalArgumentException("要求: p, q非0且|p| >= |q|\n但是, 参数p = " + p + ", q = " + q);

        if(p % q == 0)
            return q;
        else
            return gcd(q, p % q);
    }

    /**
     * Ex 1.1.36
     * 打乱数组
     * @param a 要打乱的数组
     */
    private static void shuffle(int[] a) {
        final int N = a.length;

        //将a[i]和a[i] ... a[N-1]
        for (int i = 0; i < N; i++) {
            int r = i + StdRandom.uniform(N-i);

            int temp =  a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Ex 1.1.36
     * 乱序检查
     * @param m 数组大小
     * @param n 打乱的次数
     * @throws IllegalArgumentException m, n都必须是大于0的整数
     */
    private static void shuffleCheck(int m, int n) {
        //检查参数
        if (m == 0 || n == 0)
            throw new IllegalArgumentException("m, n都必须是大于0的整数");

        //初始化存储结果的数组
        int[][] resultArray = new int[m][m];

        //打乱n次, 而且每次打乱之前都要初始化数组为int[i] = i.
        for (int i = 0; i < n; i++) {
            //初始化数组为int[i] = i那种
            int[] array = new int[m];
            for(int j = 0; j < m; j++)
                array[j] = j;

            //打乱数组
            shuffle(array);

            //统计打乱之后的元素都落在了哪里
            for (int j = 0; j < m; j++) {
                resultArray[j][array[j]]++;
            }


        }

        //输出结果
        o("(i, j)表示i位置上的元素在打乱之后落在j位置上的次数");
        for (int i = 0; i < m; i++) {
            for (int j = 0;j < m; j++) {
                System.out.printf("%5d", resultArray[i][j]);
            }

            o("");

        }


    }



    /**************************************
     * 我的一些方法和client测试方法         *
     **************************************/

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * @param obj 要输出的String.
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
     * Entry point of class ExerciseChapterOne.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        //Exercise 1.1.1 (c)
        title("Ex 1.1.1c");
        o(" true && false || true && true = " + (true && false || true && true) );

        //Exercise 1.1.16
        title("Ex 1.1.16");
        o(exR1(6));

        //Exercise 1.1.18
        title("Ex 1.1.18");
        o(mystery(2,25));

        //Exercise 1.1.19
        title("Ex 1.1.19");
        //for (int n = 0; n < 40; n++) {
        //	o(n + " " + fibonacci(n));
        //}
        o(fibonacciArray(40, new long[40]));

        //Exercise 1.1.20
        title("Ex 1.1.20");
        o(ln(10));

        //Ex 1.1.22
        title("Ex 1.1.22");
        int[] a = {1, 2, 3, 3, 6, 6, 7, 8, 9, 15, 33, 56, 56, 78};
        int key = 56;
        o(key + "的offset是" + rank(key, a) + ", 经过" + DepthCount.depth + "次递归");

        //Ex 1.1.25 用数学归纳法证明欧几里德算法能够求出两个非负数的最大公约数
        /**
         * 首先证明引理 gcd(a, b) = gcd(b, a mod b)
         * 假设 a = bq + r, 且a, b的最大公约数是d
         * 1) d|a, d|b, 所以d|(a - bq), 即d|r, 所以d也是r的约数, 所以d是b, r的最大公约数.
         * 2) 假设d是b和r的最大公约数, 即d|b, d|r, 所以d|(bq + r), 即d|a, 所以d是a, b的最大公约数.
         * 证毕
         *
         * 由上面的引理不难递归出 gcd(a, b) = gcd(b, r1) = gcd(r1, r2) = ... = gcd(rn-1, rn) = gcd(rn, 0);
         * 且b > r1 > r2 > ... > rn > 0 = rn+1, 以及rn-1能够被rn整除, 所以gcd(a, b) = gcd(rn-1, rn) = rn;
         */
        title("Ex 1.1.25");
        o("28, 24的最大公约数是: " + gcd(28, 24));

        //Ex 1.1.27
        /**
         * 感觉那题目逻辑问题啊, 命名只给了一个static方法而且那个方法有三个参数, 结果还调用了两个参数的一个重载版本
         * 而且这个重载版本没有给出来.
         */

        //Ex 1.1.29
        title("Ex 1.1.29");

        Collection<Integer> aList = new ArrayList<>(14);

        for (Integer i : a)
            aList.add(i);

        //就直接用Ex 1.1.22的那个数组来测试算了
        o("数组: " + aList +"中\nkey = "+ key + " 前面有" + BinarySearch.rank(key, a) + "个元素, " +
                "并且key = " + key + "有" + BinarySearch.count(key, a) + "个.");

        //Ex 1.1.31
        title("Ex 1.1.31");

        //n个points
        final int N = 5;
        //这N个点相连的概率
        final double P = 0.56;

        //画一个与画布相切的圆(画布是1.0x1.0的正方形, 中心点坐标(0.5, 0.5))
        StdDraw.circle(0.5, 0.5, 0.5);

        //每一个point在圆上相差的角度, 单位度
        final double ANGLE = 360.0 / N;

        //创建n个Points
        //P.S. 这里只是创建了n和Point对象的引用, 这些引用全是null, 后面必须指定new出来的对象
        Point[] points = new Point[N];

        //在圆上画点
        //设置画笔的大小为 .05, 并且设置画笔颜色为RED
        StdDraw.setPenRadius(.05);
        StdDraw.setPenColor(StdDraw.RED);

        //第一个点的坐标为(0.5, 1.0)
        for (int i = 0; i < N; i++) {

            //设置每一个点的x, y坐标
            points[i] = new Point(0.5 + 0.5 * Math.sin(i*ANGLE/360.0*2*Math.PI),
                    0.5 + 0.5 * Math.cos(i*ANGLE/360.0*2*Math.PI));

            //画点
            StdDraw.point(points[i].x, points[i].y);
        }

        //为连线设置画笔大小 .01, 颜色GARY
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setPenRadius(.01);


        //开始连线, 从N个点任取两个
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                //按照P的概率返回true
                if (StdRandom.bernoulli(P))
                    StdDraw.line(points[i].x, points[i].y, points[j].x, points[j].y);
            }
        }

        //Ex 1.1.34
        //怎么感觉所有实现都要所有n个数值

        //Ex 1.1.36
        title("Ex 1.1.36");

        //输出结果应该每一个都接近于n/m
        shuffleCheck(10, 4000);


    }

}///~
