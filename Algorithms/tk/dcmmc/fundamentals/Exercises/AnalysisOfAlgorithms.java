package tk.dcmmc.fundamentals.Exercises;

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;


/**
 * 本文件包括Exercise 1.3中部分习题的解答
 * Created by DCMMC on 2017/7/27
 * Finished on 2017/7/
 * @author DCMMC
 * @since 1.5
 */
class AnalysisOfAlgorithms {
    /**
     * Ex 1.4.9
     * 暴力算法ThreeSum
     * 时间增长倍数: O(N^3)
     * @param n
     *			要处理的元素个数
     * @return
     *			花的时间
     */
    private static double threeSum(int n) {
        int[] a  = generateIntArray(n);

        long start = System.currentTimeMillis();

        int count = 0;

        //时间增长倍数: O(N^3)
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                for (int k = j + 1; k < a.length; k++)
                    if (a[i] + a[j] + a[k] == 0)
                        count++;

        return (System.currentTimeMillis() - start) / 1000.0;
    }

    /**
     * Ex 1.4.9
     * 时间倍率计算
     * @param ratios
     *			倍率比例, 即测试数据规模与上一次测试数据规模的比例, 两次测试的时间消耗之比(T(N)/T(N-1)=ratios^b)就可
     * 求出幂次法则的数学模型中b的值, 一般ratios设为2
     */
    private static void doublingRatio(int ratios) {
        o("倍率比例: " + ratios);

        o("数据规模     时间(s)     倍率(" + ratios + "^b)");
        double previous = 0.0d;

        //为了控制时间, 把原来的8000改成了500
        for (int i = 250; i <= 500; i *= ratios) {
            double time = threeSum(i);

            if (previous == 0.0d)
                of("%6d : %7.3f\n", i, time);
            else
                of("%6d : %7.3f %8d\n", i, time, (int)(time/previous));

            previous = time;
        }
    }

    /**
     * Ex 1.4.15
     * twoSumFaster
     * O(N)
     * @param n 测试数组或者给定数据规模的数量也行
     * @return 匹配对数
     */
    private static int twoSumFaster(int... n) {
        int[] a;

        if (n.length == 1) {
            if (n[0] > 8000)
                return 0;
            else {
                a = new int[n[0]];
                //从文件读取ints到ints数组
                try {
                    File intsFile = new File(AnalysisOfAlgorithms.class.getResource("8Kints.txt").toURI());

                    if (intsFile.exists() && intsFile.canRead()) {
                        Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(intsFile)), "UTF-8");

                        //以回车符, 空白符分隔
                        sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

                        int index = 0;
                        while ( sc.hasNext() && index < n[0] ) {
                            a[index++] = Integer.parseInt(sc.next());
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
            }
        } else  {
            a = n;
        }


        //Debug... 测试效率(计算数组访问次数)
        int debug = 0;

        //时间消耗: O(NlogN)
        Arrays.sort(a);

        //上一个匹配成功的i
        int lastI = -1;
        //上一匹配成功的i匹配到的数字个数
        int lastCnt = 0;

        //累积匹配到的整数对的数量
        int cnt = 0;

        //相对于a[a.length - 1]的偏移量
        int offset = 0;

        //左边从开头开始, 遍历a中的所有负数, 试图在所有正数中找到匹配的
        int i;

        //最差情况: 全是负数:时间消耗N
        for (i = 0; i < a.length && a[i] < 0; i++) {
            debug++;

            //跳过比|a[i]|大的
            while(a[a.length - 1 - offset] > -a[i]) {
                offset++;
            }

            if (-a[i] == a[a.length - 1 - offset]) {
                cnt++;
                lastI = i;
                //不管前面有没有记录过别的数对的记录, 都还原成1
                lastCnt = 1;
                //看看a[i]能不能在正数区匹配到更多的数
                while(a[a.length - 1 - (++offset)] == -a[i]) {
                    cnt++;
                    lastCnt++;
                    debug++;
                }
            } else if (lastI > -1 && a[i] == a[lastI]) {
                //-a[i] > a[a.length - 1 - offset]
                //如果a[i+1]还是和a[i]一样的值, 而且原来a[i]匹配成功过, 就直接给这一个也算上之前匹配的对数
                cnt += lastCnt;
            }
        }

        //记数0的个数
        //最差情况: 全是0: 时间消耗:N
        int cntZero = 0;

        while (i < a.length)
            if (a[i++] == 0) {
                debug++;
                cntZero++;
            }

        //cntZero取2的组合
        cnt += cntZero * (cntZero - 1) / 2;

        //debug...
        o("数组访问次数: " + debug);

        return cnt;
    }

    /**
     * Ex 1.4.15
     * threeSumFaster
     * O(N^2)
     * @param n 测试数据规模, 不超过8000
     * @return 匹配对数
     */
    private static int threeSumFaster(int n) {
        if (n > 8000)
            return 0;

        int[] a  = generateIntArray(n);

        //排序 O(NlogN)
        Arrays.sort(a);

        //Debug... 测试效率(计算数组访问次数)
        int debug = 0;

        //上一个匹配成功的i
        int lastI = -1;
        //上一次匹配成功的j
        int lastJ = -1;
        //上一匹配成功的i, j匹配到的数字个数
        int lastCnt = 0;

        //累积匹配到的整数对的数量
        int cnt = 0;

        //相对于a[a.length]的偏移量
        int offset = 0;

        //左边从开头开始, 遍历a中的所有负数, 试图在所有正数中找到匹配的
        int i;

        int j;

        //遍历两个负数的情况, 去正数区寻找匹配的数
        for (i = 0; i < a.length && a[i] < 0; i++, offset = 0)
            for (j = i + 1; j < a.length && a[j] < 0; j++) {
                debug++;

                //跳过比|a[i]+a[j]|大的
                while(a[a.length - 1 - offset] > -a[i]-a[j]) {
                    debug++;
                    offset++;
                }

                if (-a[i]-a[j] == a[a.length - 1 - offset]) {
                    cnt++;
                    lastI = i;
                    lastJ = j;
                    //不管前面有没有记录过别的数对的记录, 都还原成1
                    lastCnt = 1;
                    //看看a[i]和a[j]能不能在正数区匹配到更多的数
                    while(a[a.length - 1 - (++offset)] == -a[i]-a[j]) {
                        cnt++;
                        lastCnt++;
                        debug++;
                    }
                } else if (lastI > -1 && lastJ > -1 && a[i] == a[lastI] && a[j] == a[lastJ]) {
                    //-a[i] > a[a.length - 1 - offset]
                    //如果a[i+1]还是和a[i]一样的值, 而且原来a[i]匹配成功过, 就直接给这一个也算上之前匹配的对数
                    cnt += lastCnt;
                }
            }

        //记数0的个数
        //最差情况: 全是0: 时间消耗:1/2N^2
        int cntZero = 0;

        while (i < a.length && a[i] <= 0)
            if (a[i++] == 0) {
                debug++;
                cntZero++;
            }

        //cntZero取3的组合
        cnt += cntZero * (cntZero - 1) * (cntZero - 2) / 6;

        //遍历两个正数的情况, 去负数区寻找匹配的数
        //清零偏移量, 现在的偏移量是相对于a[0]的
        offset = 0;

        //重置lastI和lastJ
        lastI = -1;
        lastJ = -1;

        for (i = a.length - 1; i >= 0 && a[i] > 0; i--, offset = 0)
            for (j = i - 1; j >= 0 && a[j] > 0; j--) {
                debug++;

                //跳过比-|a[i]+a[j]|小的
                while(a[offset] < -a[i]-a[j]) {
                    debug++;
                    offset++;
                }

                if (-a[i]-a[j] == a[offset]) {
                    cnt++;
                    lastI = i;
                    lastJ = j;
                    lastCnt++;
                    //看看a[i]和a[j]能不能在负数区匹配到更多的数
                    while(a[++offset] == -a[i]-a[j]) {
                        cnt++;
                        lastCnt = 1;
                        debug++;
                    }
                } else if (lastI > -1 && lastJ > -1 && a[i] == a[lastI] && a[j] == a[lastJ]) {
                    //-a[i]-a[j] > a[a.length - 1 - offset]
                    //如果a[i+1]还是和a[i]一样的值, 而且原来a[i]匹配成功过(同理a[j]), 就直接给这一对i,j也算上之前匹配的对数
                    cnt += lastCnt;
                }
            }

        //debug...
        o("数组访问次数: " + debug);

        return cnt;
    }

    /**
     * Ex 1.4.17
     * Farthest Pair
     * O(N)
     * @param n
     *		数组的元素个数
     */
    private static void farthestPair(int n) {
        //创建并打乱数组
        double[] a = new double[n];

        for (int i = 0; i < n; i++)
            a[i] = i;

        StdRandom.shuffle(a);

        double min = a[0];
        double max = a[0];

        for (int i = 0; i < n; i++) {
            //找出最小的数
            min = Math.min(a[i], min);
            max = Math.max(a[i], max);
        }

        o("相差最大的两个数分别是: " + min + ", "+ max);

    }

    /**
     * Exercise 1.4.20 1.4.41
     * 非递归实现, 在数组中由参数指定的范围查找key的index.
     * O(logN)
     * @param key 要找的那个数值
     * @param a 已经由小到大排序好的int数组
     * @param lo 查找范围的下限index
     * @param hi 查找范围的上线offset
     * @return 找不到就返回-1, 找得到就返回key在a中的index
     */
    public static int rank(int key, int[] a, int lo, int hi) {
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            if (key < a[mid])
                hi = mid - 1;
            else if (key > a[mid])
                lo = mid + 1;
            else
                return mid;
        }

        return -1;
    }

    /**
     * Exercise 1.4.20 1.4.41
     * 在数组中由参数指定的范围查找key的index.
     * O(logN)
     * @param key 要找的那个数值
     * @param a 已经由大到小排序好的int数组
     * @param lo 查找范围的下限index
     * @param hi 查找范围的上线offset
     * @return 找不到就返回-1, 找得到就返回key在a中的index
     */
    public static int reverseRank(int key, int[] a, int lo, int hi) {
        if (lo > hi)
            return -1;

        int mid = lo + (hi - lo) / 2;

        if (key < a[mid])
            return rank(key, a, mid + 1, hi);
        else if (key > a[mid])
            return rank(key, a, lo, mid - 1);
        else
            return mid;

    }

    /**
     * Ex 1.4.20
     * Find index of Largest
     * O(logN)
     * @param bitonicArray
     *			双调数组
     * @param low 要寻找的范围的下界
     * @param high 要寻找的范围的上界
     * @return 双调数组中最大的数的index
     */
    private static int indexOfLargest(int[] bitonicArray, int low, int high) {
        //处理几种极端情况
        if (bitonicArray.length == 1)
            return bitonicArray[0];
        else if (bitonicArray[0] > bitonicArray[1])
            return bitonicArray[0];
        else if (bitonicArray[bitonicArray.length - 1] > bitonicArray[bitonicArray.length - 2])
            return bitonicArray[bitonicArray.length - 1];


        if (low > high)
            return -1;

        int mid = low + (high - low) / 2;
        int index;

        if (mid - 1 >= 0 && mid + 1 < bitonicArray.length && bitonicArray[mid] > bitonicArray[mid - 1]
                && bitonicArray[mid] < bitonicArray[mid + 1])
        {
            return indexOfLargest(bitonicArray, mid + 1, high);
        } else  if (mid - 1 >= 0 && mid + 1 < bitonicArray.length && bitonicArray[mid] < bitonicArray[mid - 1]
                && bitonicArray[mid] > bitonicArray[mid + 1])
        {
            return indexOfLargest(bitonicArray, low, mid - 1);
        } else {
            return mid;
        }
    }

    /**
     * Ex 1.4.20
     * Bitonic Search
     * use ~ 3logN compares in worst case
     * Omega(2logN) compares in worst case
     * @param bitonicArray
     *			双调数组(就是元素大小先递增后递减的数组), 程序不会检查数组是否符合要求
     * @param key 要在bitonicArray中查找的值
     * @return 如果key在bitonicArray中, 就返回true
     */
    private static boolean bitonicSearch(int[] bitonicArray, int key) {
        int indexOfLargest = indexOfLargest(bitonicArray, 0, bitonicArray.length - 1);

        if (rank(key, bitonicArray, 0, indexOfLargest) > -1
                || reverseRank(key, bitonicArray, indexOfLargest, bitonicArray.length - 1) > -1)
            return true;
        else
            return false;
    }

    /**
     * Ex 1.4.22
     * 仅使用加减法实现的二分查找(Mihai Patrascu法)
     * 即使用	斐波那契数来代替2的幂, 毕竟斐波那契数也很爆炸.
     * O(log N)
     * @param key 要找的数
     * @param a
     *			要处理的数组
     * @return key在a中的index
     */
    private static int fibonacciSearch(int key, int[] a) {
        Arrays.sort(a);

        //构造出三个相邻的斐波那契数 f1 < f2 < f3, 且f3 >= a.length
        int f1 = 0;
        int f2 = 1;

        int f3 = f1 + f2;

        while (f3 < a.length) {
            f1 = f2;
            f2 = f3;
            f3 = f1 + f2;
        }

		/*
		* 先设置offset(也就是相对于a[0]的偏移量)为0, 也就是在[0, f3]范围内搜索, 匹配a[offset+f1]比key相比较,
		* 1) 如果小于key, 就将范围缩小到[offset + 0, offset + f1](也就是把f3向后递减两位, 使f3=f1, f2, f1也重新确定,
		* 保持原来的顺序, i.e., 三个相邻的斐波那契数 f1 < f2 < f3), 然后再匹配a[offset+f1](其中的f1为新的f1).
		* 2) 如果大于key, 就将范围缩小到[offset + f1, offset + f1 + f2](也就是新的偏移量为offset + f1, 加大了偏移量),
		* 同时为了保持搜索范围的上限和上次一样(本来这个上限都已经超过a的有边界了), 所需需要把f3向后递推一位, 也就是f3的
		* 值变为f2, 新的f2, f1也重新确定, 保持原来的顺序, i.e., 三个相邻的斐波那契数 f1 < f2 < f3.
		*
		* 这样递推直至f3向后递推到正好等与2, 此时要么搜索范围变成[f3 - 1, f3](这里的f3为最开始那个大于等于a.length的初始值),
		* f3 - 1就是此时的offset, 然后a[offset + f1] = a[f3], 这是唯一(如果能)能够匹配的最后一个值; 或者搜索范围变成
		* [0, 2](也就是[0, f3], offset还是0), 此时会比较a[f1]与key.
		* 不过这里需要注意offset+1是否超过了a.length - 1这一下标, 因为有可能最后一次(或者倒数几次)判断的时候, 因为最初的f3
		* 肯定是要超过数组下标的(f3 >= a.length), 所以需要判断一下(用Math.min())
		*
		* 最后我发现两种极端情况要么是比较a[1]要么是比较a[f3], 不如更改最初的offset为-1, 使index都往后移动一位.
		*/

        int offset = -1;

        while (f3 > 1) {
            //防止a[i]下标上溢.
            int i = Math.min(offset + f1, a.length - 1);

            if (a[i] > key) {
                //更改范围为[offset + 0, offset + f1], 也就是三个斐波那契数都往后移动两位.
                f2 = f2 - f1;
                f1 = f1 - f2;
                f3 = f1 + f2;
            } else if (a[i] < key) {
                //更改范围为[offset + f1, offset + f1 + f2], 也就是扩大offset(范围下限), 保持范围上限不变
                offset += f1;
                f1 = f2 - f1;
                f2 = f3 - f2;
                f3 = f1 + f2;
            } else {
                //匹配成功
                return i;
            }
        }

        //匹配失败
        return -1;
    }

    /**
     * Ex 1.4.23
     * 有点难, Google了才找到时间 ~ logN, 时间最差为O(N), 空间O(1)的解法
     * Using Farey Sequence:
     * In mathematics, the Farey sequence of order n is the sequence of completely reduced
     * fractions between 0 and 1 which when in lowest terms have denominators less than or
     * equal to n, arranged in order of increasing size.
     * if p/q has neighbours a/b and c/d in some Farey sequence, with a/b < p/q < c/d, then
     * p/q = (a + b)/(c + d)
     * @param N 0 < p < q < N
     * @param x 要找到与x最接近或等于x的有理数, x必须是[0, 1]
     */
    private static void fareyFractionSearch(int N, double x) {
        //最终的答案
        int p = 0, q = 0;

        //A, B为搜索的范围, num表示分子, denom表示分母
        int numA = 0, denomA = 1;
        int numB = 1, denomB = 1;

        //A, B中间的Farey数
        double mid;

        while (denomA < N && denomB < N) {
            //令mid最A和B中间的Farey数
            mid = (1.0 * numA + numB) / (denomA + denomB);

            //这里用到了一个公理(易证): 分母都小于N的两分数的差不小于1/(n^2)
            //由此可得只要|x - mid|的差小于等于1/(n^2), 那么x和mid之间一定不含有一个满足分子分母都小于N的分数.
            //然而, 书上给的这个hints就是坑啊, 这只是一个必要条件而已, 就是有可能: 两个分子分母都小于N的相距最近
            //的分数, 可能他们的差为2/(N^2), 然而x与较小的那个分数的差却有1.5/(N^2), 所以下面的if句并不能判断是
            //x的floor的分数还是x的ceil的分数... 甚至都可能找不到...
            if (Math.abs(x - mid) <= 1.0 / ((1.0 * N) * N) ) {
                if (denomA + denomB < N) {
                    //如果这个答案的分子分母小于N就直接得出答案
                    p = numA + numB;
                    q = denomA + denomB;
                    break;
                } else if (denomA > denomB) {
                    //找上一次递推的farey数
                    //A就是上一轮递推的farey数
                    p = numA;
                    q = denomA;
                    break;
                } else {
                    p = numB;
                    q = denomB;
                    break;
                }

            } else if (x < mid) {
                numB = numA + numB;
                denomB = denomA + denomB;
            } else if (x > mid){
                //x > mid
                numA = numA + numB;
                denomA = denomA + denomB;
            }
        }

        if (x == 0.0d) {
            p = 0;
            q = 1;
        } else if (x == 1.0d) {
            p = 1;
            q = 1;
        }

        //o("分子分母都小于" + N + "的有理数中最接近" + x + "并且小于" + x + "的有理数是: " + p + "/" + q);
        //似乎跟题目的要求还是有点相悖...
        o("分子分母都小于" + N + "的有理数中最接近" + x + "的有理数是: " + p + "/" + q);
    }


    /**
    * Ex 1.4.24
    * EggsDrop问题
    * ~logN方法
    * @param N
    *		楼层高度
    * @param F
    *		鸡蛋恰好摔碎的楼层
    * @return 程序猜到的楼层数
    */
    private static int eggsDrop(int N, int F) {
    	//debug
    	int debugCnt = 0;
    	int debugEggs = 0;

    	//默认在一楼, 这样所有的鸡蛋抛下去都会摔碎
    	int f = 1;

    	int lo = 1;
    	int hi = N;

    	while (lo <= hi) {
    		int mid = lo + (hi - lo) / 2;

    		if (mid < F && mid + 1 < F) {
    			lo = mid + 1;
    			debugCnt += 2;
    		} else if (mid >= F) {
    			hi = mid - 1;
    			debugCnt += 2;
    			debugEggs += 1;
    		} else if (mid < F && mid + 1 >= F) {
    			debugCnt += 2;
    			debugEggs++;
    			f = mid + 1;
    			break;
    		}
    	}

    	o("tosses: " + debugCnt + ", eggs: " + debugEggs);

    	return f;
    }

    /**
    * Ex 1.4.24
    * EggsDrop问题
    * ~2logF投掷, ~logF鸡蛋
    * @param N
    *		楼层高度
    * @param F
    *		鸡蛋恰好摔碎的楼层
    * @return 程序猜到的楼层数
    */
    private static int eggsDrop2(int N, int F) {
    	//debug
    	int debugCnt = 0;
    	int debugEggs = 0;

    	//默认在一楼, 这样所有的鸡蛋抛下去都会摔碎
    	int f = 1;

    	int lastSqrt = N;

    	//找到那个事鸡蛋不会碎的sqrt(n)^k
    	while ( (int)Math.sqrt(lastSqrt) >= F )  {
    		lastSqrt = (int)Math.sqrt(lastSqrt);
    		debugCnt++;
    		debugEggs++;
    	}

    	//在(int)Math.sqrt(lastSqrt)到lastSqrt之间二分搜索
    	int lo = (int)Math.sqrt(lastSqrt);
    	int hi = lastSqrt;

    	while (lo <= hi) {
    		int mid = lo + (hi - lo) / 2;

    		if (mid < F && mid + 1 < F) {
    			lo = mid + 1;
    			debugCnt += 2;
    		} else if (mid >= F) {
    			hi = mid - 1;
    			debugCnt += 1;
    			debugEggs += 1;
    		} else if (mid < F && mid + 1 >= F) {
    			debugCnt += 2;
    			debugEggs++;
    			f = mid + 1;
    			break;
    		}
    	}

    	o("tosses: " + debugCnt + ", eggs: " + debugEggs);

    	return f;
    }

    /**
    * Ex 1.4.24
    * EggsDrop问题
    * ~2logF投掷, ~logF鸡蛋
    * @param N
    *		楼层高度
    * @param F
    *		鸡蛋恰好摔碎的楼层
    * @return 程序猜到的楼层数
    */
    private static int eggsDrop3(int N, int F) {
    	//debug...
    	int debugCnt = 0;
    	int debugEggs = 0;

    	//区块的左端点相对于一楼的offset
    	int leftOffset = 0;
    	//区块的长度
    	int length = 1;

    	//result
    	int f = 0;

    	//初始第一个区块的左右offset都是0, 因为在第一个区块只有一层楼
    	while (leftOffset + length <= N) {
    		//leftOffset + legth表示当前区块的右端点
    		if (leftOffset + length >= F) {
    			debugEggs++;
    			debugCnt++;
    			//从区块左端点开始遍历
    			int last = leftOffset + 1;
    			while (last++ < F)
    				debugCnt++;
    			debugEggs++;
    			f = last - 1;
    			break;
    		} 

    		debugCnt++;
    		leftOffset += length;
    		length++;
    	}

    	o("tosses: " + debugCnt + ", eggs: " + debugEggs);

    	return f;
    }

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
            if (rank(-a[i], a, 0, a.length - 1) > i)
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
                if (rank(-a[i]-a[j], a, 0, a.length - 1) > j)
                    count++;


        return count;
    }

    /**
     * 归并排序+二分搜索算法ThreeSum
     * 时间增长倍数: O(N^3)
     * @param n
     *			要处理的元素个数
     * @return
     *			花的时间
     */
    private static double threeSumFast(int n) {
        int[] a  = generateIntArray(n);


        long start = System.currentTimeMillis();

        int count = 0;

        //时间增长倍数: O(logN)
        //先归并排序
        Arrays.sort(a);

        //时间增长倍数: O(N^2*logN)
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (rank(-a[i]-a[j], a, 0, a.length - 1) > j)
                    count++;


        return (System.currentTimeMillis() - start) / 1000.0;
    }




    /**
     * Ex 1.4.41
     * DoublingTest
     * @throws RuntimeException
     *				读取文件的时候如果出问题就抛出RuntimeException
     */
    private static void doublingTest() throws RuntimeException {
        String[] testFiles = {"1Kints.txt", "2Kints.txt", "4Kints.txt", "8Kints.txt"};

        //做4轮测试, 为了节省时间, 还是把原来的4调成2算了
        for (int i = 0; i < 2; i++) {
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

    /**
     * 从8Kints读取指定的ints到数组
     * @param n
     *          要生成的数组的大小
     * @throws RuntimeException 出现问题就直接抛RuntimeException
     */
    private static int[] generateIntArray(int n) throws RuntimeException {
        int[] a = new int[n];

        if (n > 8000)
            throw new RuntimeException("n不能大于8000");

        //从文件读取ints到ints数组
        try {
            File intsFile = new File(AnalysisOfAlgorithms.class.getResource("8Kints.txt").toURI());

            if (intsFile.exists() && intsFile.canRead()) {
                Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(intsFile)), "UTF-8");

                //以回车符, 空白符分隔
                sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

                int index = 0;
                while ( sc.hasNext() && index < n ) {
                    a[index++] = Integer.parseInt(sc.next());
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

        return a;
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
        //Ex 1.4.1
		/*
		* 法一)
		* 数学归纳法:
		* 1. 归纳奠基: 当n=3的时候显然成立
		* 2. 假设当n=k的时候亦成立, 即有组合数为k(k-1)(k-2)/6
		* 又第k+1个数与前l个数中任取两个(Ck取2, i.e., (k^2 - 2)/2), 然后k(k-1)(k-2)/6+(k^2 - 2)/2)=k(k-1)(k+1)/6
		* 证毕.
		* 法二)
		* 直接Cn取3就行了
		*/

        //1.4.3
        //画出ThreeSum的时间消耗与数据规模的关系图
        StdDraw.setXscale(0, 10_000);
        StdDraw.setYscale(0, 45);

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.005);

        //为了控制时间, 把原来的8000改成了200
        for (int i = 100; i <= 200; i += 50) {
            //Debug...
            o(i);

            StdDraw.point(i, threeSumFast(i));
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(1000, 1, "1K");
        StdDraw.text(2000, 1, "2K");
        StdDraw.text(4000, 1, "4K");
        StdDraw.text(8000, 1, "8K");
        StdDraw.text(200, 5, "5");
        StdDraw.text(200, 10, "10");
        StdDraw.text(200, 15, "15");
        StdDraw.text(200, 20, "20");
        StdDraw.text(200, 25, "25");
        StdDraw.text(200, 30, "30");
        StdDraw.text(200, 35, "35");

        //Ex 1.4.5
        //g那一小题不知道写

        //Ex 1.4.6
        //2) 2^log(2N)

        //Ex 1.4.9
        title("Ex 1.4.9");

        doublingRatio(2);

        //Ex 1.4.11
		/* 首先花O(logN)的时间找到任意一个key所在的offset, 然后分0~anfKeyOffset和anfKeyOffset~array.legth之间开始二分搜索,
		* 搜索到的新的offset, 在按心得offfset与0和array.length开始二分搜索, 自到返回-1就能将这两个offset相减确定元素个数,
		* 最坏的情况也应该是O(logN)
		*/


        //Ex 1.4.15
        //有点难
        title("Ex 1.4.15");

        //元素个数  匹配对数
        //11 55
        int[] a = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        //9 0
        int[] b = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
        //13 11
        int[] c = {-100, -100, -100, -100, -100, -100, -100, -100, 0, 0, 0, 1, 100};
        //13 10
        int[] d = {-10, -1, 0, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};

        o(twoSumFaster(a));
        o(twoSumFaster(b));
        o(twoSumFaster(c));
        o(twoSumFaster(d));

        int n = 2000;
        int[] ints = generateIntArray(n);

        o(countTwoSumFast(ints));

        o(twoSumFaster(n));

        title("");

        o(countThreeSum(ints));

        o(countThreeSumFast(ints));

        o(threeSumFaster(n));

        //Ex 1.4.16
        //先用O(N*logN)的Arrays.sort()排序, 然后O(N)遍历, 找出a[i+1]-a[i]的最小值

        //Ex 1.4.17
        title("Ex 1.4.17");

        farthestPair(100);

        //Ex 1.4.19
        //类似于Ex 1.4.18, 是不过方法需要把有两个参数来限定for-loop范围
        //总的来说是基于BinarySearch的方法, 时间复杂度为O(logN)

        //Ex 1.4.20
        title("Ex 1.4.20");

        o(bitonicSearch(new int[]{1, 2, 3, 4, 6, 8, 8, 6, 4, 5, 2, 2, 1}, 5));

        //Ex 1.4.22
        //有点难, 参考了别人的解法才写出来的
        title("Ex 1.4.22");

        o(fibonacciSearch(1, new  int[]{1, 2 , 4, 6, 8, 8, 6}));
        o(fibonacciSearch(5, new int[]{1, 2, 3, 4, 6, 8, 8, 6, 4, 5}));
        o(fibonacciSearch(7, new int[]{1, 4, 3, 7, 4, 7, 34, 34, 8, 9, 10}));

        //Ex 1.4.23
        title("Ex 1.4.23");

        fareyFractionSearch(15, 0.853);
        fareyFractionSearch(15, 0.0);
        fareyFractionSearch(15, 1.0);
        //1658137/7693090(小于x)
        fareyFractionSearch(9434654, 0.21553589);
        //155313/208324
        fareyFractionSearch(446454, 0.7455358);

        //Ex 1.4.24 & Ex 1.4.25
        //有点难度
        //REference: http://datagenetics.com/blog/july22012/index.html+
        title("Ex 1.4.24 & 1.4.25");

        /*
        * 要用 ~ logN个鸡蛋尝试 ~ logN次投掷: 直接用二分法就行
        */
        o(eggsDrop(10, 10));
        o(eggsDrop(10, 2));
        o(eggsDrop(100000, 56461));

        title("");

        /*
        * 更进一步的, 要使投掷次数为 ~ 2logF, 鸡蛋 ~ logF, 第一次在floor(sqrt(N))(不超过根号N的最大整数)处投掷, 
        * 1) 如果碎了, 则说明floor(sqrt(N)) > F, 继续对floor(sqrt(N))根号取整(i.e., floor(sqrt(floor(sqrt(N)))) ), 如此往复,
        * 直至鸡蛋没碎(这里假设尝试了k次, 下面的公式中的^k后缀表示k轮递推, e.g. sqrt(N)^k表示N开k次根)
        * 2) 如果鸡蛋没碎, 则说明floor(sqrt(N))^k < sqrt(N)^k < F, => N < F^2^k, 两边取对数=>log(N) < 2log(F) <= 2^klog(F), 
        * 在[floor(sqrt(N))^k, floor(sqrt(N))^(k-1)], 开始二分搜索
        * 综上, 在最坏情况下(F=1), 需要尝试 ~ logN次, 花掉 ~ logN个鸡蛋.
        * 一般情况下, 例如第一次(floor(sqrt(N)))就成功的话, 在[sqrt(N), N]之间二分搜索, 需要O(log(N - sqrt(N)))的复杂度, 
        * 又log(N - sqrt(N)) = 1/2logN + log(sqrt(N) - 1) < logN < 2logF, 所以投掷次数 ~ 2logF, 鸡蛋使用次数(也许)是~logF
        */
        o(eggsDrop2(10, 10));
        o(eggsDrop2(10, 2));
        o(eggsDrop2(100000, 56461));

        /*
        * 继续这个问题, 如果我们只有2个鸡蛋, 要做到 ~ 2sqrt(N)的投掷次数, 我们可以先拿一个鸡蛋, 以一个鸡蛋按照floor(sqrt(N))为
        * 间隔将N分割成floor(sqrt(N))块来投掷(每次都往那一区块的最大楼层投掷), 直到这个鸡蛋摔碎了, 那么就继续从这一区块的最小楼层
        * 往上投掷, 知道摔碎
        * 这样就算是最差的情况, 也能保持最多tosses 2sqrt(N)次
        */

        /*
        * 一个更好的解法, 上述解法在最糟糕的情况下是2sqrt(N)次的投掷, 不过还有另外一个更好的(可以达到O(sqrt(2N))):
        * 上述方法, 遍历经过的区块越多, 花在确定区块的投掷次数就会越来越多, 而每一个区块中的楼层个数又是一样的, 如果把区块变成逐渐
        * 递减的区块, 就能够弥补经过区块越多浪费在这上面的投掷次数就会多的劣势, 这里用到的公差为1的等差数列, 解方程x(x-1)/2=N
        * 求的x的ceil整数, 以这样的区块来递推, 可以保证不管目标楼层在哪, 在最糟糕的情况(也就是目标楼层在目标区块的倒数第二层)需要
        * 的时间都是ceil(x)
        */

        /*
        * 继续, 如果只有两个鸡蛋, 且需要做到 ~ c * sqrt(T) (c为常数)的投掷次数
        * 答案类似上面的方法反过来, 同样的, 分区块, 不过第一个区块只有一个元素, 然后依次累加(每次+1, i.e., 第二个区块有2层楼, 
        * 第三个区块有3层楼, 第t个区块有t层楼), 假设T在第t个区块中, 所以1 + 2 + ... + t ~= 1/2t(t-1), 而T <= 1/2t(t-1),
        * => t ~ sqrt(2T), 然后在最坏情况下, 在这一个区块还需要遍历t次(sqrt(2T)), 所以总共需要花 ~ 2sqrt(2T)次tosses.
        */

        title("");
        o(eggsDrop3(10, 10));
        o(eggsDrop3(10, 2));
        o(eggsDrop3(100000, 56461));

        //拓展: 要求的n层楼t个鸡蛋的最少尝试次数, 需要考虑到二叉树和DP(Dynamic Programming), 一个经典的Google面试题
        //Reference: http://www.geeksforgeeks.org/dynamic-programming-set-11-egg-dropping-puzzle/



        //Ex 1.4.32
		/*
		* Proof sketch:
		* 分情况进行分析:
		*
		* 1) 当Stack中的元素从来没有达到MAXSIZE / 4, 这样数组的访问次数为M+MAXSIZE(100为数组初始化的开销)
		* 2) 当Stack中的元素达到过MAXSIZE / 4但是从来没有达到过MAXSIZE, 即触发过n次resize(MAXSIZE / 2),
		* 这样数组的访问次数为M+MAXSIZE(数组第一次初始化)+3/4(MAXSIZE+1/2MAXSIZE+1/4MAXSIZE+...+(1/2)^n-1MAXSIZE)
		* (其中3/4表示创建原来MAXSIZE大小的1/2的新数组, 以及原来MAXSIZE的1/4大小次的移动数组操作).
		* 3) 当数组元素一直增加, 不断resize(MAXSIZE*2), 那么数组的访问次数就是 M+MAXSIZE(数组第一次初始化)+3/4MAXSIZE
		* (元素达到1/4MAXSIZE触发的一次resize(MAXSIZE/2))+3/2MAXSIZE+3MAXSIZE+6MAXSIZE+....(这是一直resize(MAXSIZE*2))
		* 的访问次数, 每一项都是前一项的2倍次数.
		* 4) 综上, 每resize(MAXSIZE/2), 都会增加3/4MAXSIZE(MAXSIZE为当前操作之前的MAXSIZE)次数操作, 每resieze(MAXSIZE*2)
		* 都会增加3MAXSIZE(MAXSIZE为当前操作之前的MAXSIZE)次数组访问.
		*/

        //Ex 1.4.41
        title("Ex 1.4.41");

        doublingTest();


    }

}///~