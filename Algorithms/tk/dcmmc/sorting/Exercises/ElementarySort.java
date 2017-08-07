package tk.dcmmc.sorting.Exercises;

import edu.princeton.cs.algs4.StdDraw;
import java.awt.Font;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdRandom;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;

/**
* 初级排序算法
* Ex 2.1
* Create on 2017/8/7
* @author DCMMC
* @since 1.5
*/
class ElementarySort {
    /**
    * 用于存储排序前数组中元素的index
    */
    @SuppressWarnings("unchecked")
    private static class ItemOriginIndex implements Comparable<ItemOriginIndex> {
        Comparable a;
        int index;

        @Override
        public int compareTo(ItemOriginIndex w) {
            return this.a.compareTo(w.a);
        }
    }

	/**************************************
     * Methods                            *
     **************************************/
    /**
    * 可视化的排序
    */
    @SuppressWarnings("unchecked")
    private static void insertionSortTrace(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.textLeft(0, a.length + 2 - i, i + "");

            //找到a[i]前面的序列(前面的序列都是由大到小排序好了的)中, 在a[i]应该所在的位置插入(这里通过一个一个交换来实现)
            int j;
            for (j = i; j > 0 && a[j].compareTo(a[j - 1]) < 0; j--) {
                Comparable tmp = a[j];
                a[j] = a[j - 1];
                a[j - 1] = tmp;
            }

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.textLeft(1, a.length + 2 - i, j + "");

            StdDraw.setPenColor(StdDraw.GRAY);
            for (int k = 0; k < j; k++)
                StdDraw.textLeft(2 + k, a.length + 2 - i, a[k] + "");

            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.textLeft(2 + j, a.length + 2 - i, a[j] + "");

            StdDraw.setPenColor(StdDraw.BLACK);
            for (int k = ++j; k <= i; k++)
                StdDraw.textLeft(2 + k, a.length + 2 - i, a[k] + "");

            StdDraw.setPenColor(StdDraw.GRAY);
            for (int k = i + 1; k < a.length; k++) 
                StdDraw.textLeft(2 + k, a.length + 2 - i, a[k] + "");
        }
    }

    /**
     * 可视化的ShellSort
     * @param a
     *          要排序的数组
     */
    private static void showTraceOfInsertionSort(Comparable[] a) {
        StdDraw.setCanvasSize(800, 800);

        StdDraw.setXscale(0, a.length + 3);
        StdDraw.setYscale(0, a.length + 4);

        StdDraw.setFont(new Font("msyh", Font.BOLD, 19));

        //draw head line
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(0, a.length + 3, " i");
        StdDraw.textLeft(1, a.length + 3, "j");
        for (int i = 0; i < a.length; i++) {
            StdDraw.textLeft(i + 2, a.length + 3, i + "");
            StdDraw.textLeft(i + 2, a.length + 2, a[i] + "");
        }
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.line(0, a.length + 2.7, a.length + 3, a.length + 2.7);

        insertionSortTrace(a);

    }

    public static void shellSortAnimation(Comparable[] a) {
        ItemOriginIndex[] copy = new ItemOriginIndex[a.length];
        for (int i = 0; i < a.length; i++) {
            copy[i] = new ItemOriginIndex();
            copy[i].a = a[i];
            copy[i].index = i;
        }
        Arrays.sort(copy);
        int[] heights = new int[a.length];
        int height = 1;

        for (ItemOriginIndex i : copy) {
            heights[i.index] = height++;
        }

        StdDraw.clear();
        StdDraw.setCanvasSize(3 * (a.length + 2) * 10, (4 + a.length + 28) * 10);
        StdDraw.setYscale(0, 4 + a.length + 28);
        StdDraw.setXscale(0, 3 * (a.length + 2));
        StdDraw.setFont(new Font("msyh", Font.BOLD, 18));


        //先确定一个不大于a.length/3的一个数作为h序列的最大项
        int h = 1;

        while (h < a.length/3)
            h = h*3 + 1;//by Knuth

        int lastCircleX = 0;
        
        while (h >= 1) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle(4, 4 + a.length + 24, 5, 2.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(4, 4 + a.length + 24, h + "-sorting");

            StdDraw.setPenColor(StdDraw.GRAY);
            for (int i = 0; i < heights.length; i++) {
                StdDraw.filledRectangle(2 + i*2 + 1 + (i - 1), 4 + heights[i] / 2.0, 1, heights[i] / 2.0);
            }

            int lastI = 0;
            
            //以当前h做h-sorting
            for (int i = h; i < a.length; i++) {
                StdDraw.setPenColor(StdDraw.GRAY);
                if (lastI != 0 && h != 1) {
                    for (int j = lastI; j >= 0; j -= h) 
                        StdDraw.filledRectangle(2 + j*2 + 1 + (j - 1), 4 + heights[j] / 2.0, 1, heights[j] / 2.0);
                }
                lastI = i;

                StdDraw.setPenColor(StdDraw.BLACK);
                for (int j = i; j >= 0; j -= h) 
                    StdDraw.filledRectangle(2 + j*2 + 1 + (j - 1), 4 + heights[j] / 2.0, 1, heights[j] / 2.0);

                for (int j = i; j >= h && a[j].compareTo(a[j - h]) < 0; j -= h) {

                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledCircle(lastCircleX, 2, 1.7);
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.filledCircle(2 + j*2 + 1 + (j - 1), 2, 1.5);
                    lastCircleX = 2 + j*2 + 1 + (j - 1);

                    StdDraw.filledRectangle(2 + (j - h)*2 + 1 + (j - h - 1), 4 + heights[j - h] / 2.0, 1, heights[j - h] / 2.0);

                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledRectangle(2 + j*2 + 1 + (j - 1), 4 + heights[j] / 2.0, 1, heights[j] / 2.0);
                    StdDraw.filledRectangle(2 + (j - h)*2 + 1 + (j - h - 1), 4 + heights[j - h] / 2.0, 1, heights[j - h] / 2.0);

                    Comparable tmp = a[j];
                    a[j] = a[j - h];
                    a[j - h] = tmp;

                    int tmpHeight = heights[j];
                    heights[j] = heights[j - h];
                    heights[j - h] = tmpHeight;

                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledRectangle(2 + j * 2 + 1 + (j - 1), 4 + heights[j] / 2.0, 1, heights[j] / 2.0);
                    StdDraw.filledRectangle(2 + (j - h)*2 + 1 + (j - h - 1), 4 + heights[j - h] / 2.0, 1, heights[j - h] / 2.0);
                }
            }

            h /= 3;
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
	* Test Client
	* @param args
	*		command-line arguments
	*/
	public static void main(String[] args) {
		//Ex 2.1.3
		//完全逆序的情况

        //Ex 2.1.4
        title("Ex 2.1.4");

        //showTraceOfInsertionSort(new Character[]{'E', 'A', 'S', 'Y', 'Q', 'U', 'E', 'S', 'T', 'I', 'I', 'N'});
        showTraceOfInsertionSort(new Character[]{'S', 'O', 'R', 'T', 'E', 'X', 'A', 'M', 'P', 'L', 'E'});

        //Ex 2.1.11
        //见ShellSort源码

        //Ex 2.1.13
        //Insertion Sort

        //Ex 2.1.14
        //不是很懂

        //Ex 2.1.15
        //Selection Sort

        //Ex 2.1.16
        //将原来的数组中的所有的元素的hashCode记录在一个数组中, 比较排序后数组的元素的hashCode顺序是否改变

        //原理类似于Ex 2.1.17 & 2.1.18
        title("shellSortAnimation");

        int size = 60;
        Integer[] array = new Integer[size];
        for (int i = 0; i < array.length; i++)
            array[i] = i;
        StdRandom.shuffle(array);

        shellSortAnimation(array);

        //Ex 2.1.20
        //希尔排序的最好情况就是数组本身就是排序好的情况, 这种情况下exch是0, compare取决于increment sequence

        //Ex 2.1.24
        //先遍历找到min值(O(N)的复杂度), 然后把j > 0替换成a[j] > min
        //这样就不需要进行边界判断了, 貌似在这里也没什么好处

        //Ex 2.1.25
        //就是先把最开始的a[j]保存在tmp中, 把交换替代为直接a[j - 1] = a[j], 在最后再把tmp放在替换在目标元素上
        //这样做应该比exch要快一些, 毕竟每次exch都要有一个tmp(如果是纯数字的话可以不需要tmp来实现exch)

        //Ex 2.1.26
        //这个直接用我实现的DoubleLinkedList就可以快速实现了

        //Ex 2.1.29
        //k从0开始, 计算两公式得出的值, 存储在HashSet中(在这种容器中不会有重复的值, 自动按照从小到大的顺序排列),
        //知道数值大于3/N的时候停止计算

         
    }
}///~