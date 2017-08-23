package tk.dcmmc.sorting.Exercises;

import edu.princeton.cs.algs4.StdRandom;
import tk.dcmmc.sorting.Algorithms.QuickSort;
import tk.dcmmc.sorting.Algorithms.Sort;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import tk.dcmmc.fundamentals.Algorithms.Queue;
import java.util.Arrays;

/**
* Quicksort算法练习
* Ex 2.3
* Create on 2017/8/16
* Finish on 2017/8/
* @author DCMMC
* @since 1.5
*/
public class QuicksortEx extends Sort {
    /**
    * Ex 2.3.15
    * 用于给nutsAndBolts提供方法参数
    */
    @FunctionalInterface
    private interface CompareTwo{
        int compare(Comparable a, Comparable b);
    }

    /* Ex 2.3.24 */
    //process的个数, 也是bucket的个数
    private static final int PROCESS_NUM = 25;


    /**
     * partition Thread
     */
    private static class PartitionThread extends Thread {
        /* Fields */
        private Comparable[] a;
        private int lo;
        private int hi;
        private Comparable[] splitters;
        //存储每一个bucket的上下界
        private int[][] bucketsBounds = new int[PROCESS_NUM][2];

        //can not be instanced by default constructor
        private PartitionThread() { }

        /**
         * 对a的指定部分(已排序)按照splitters中的值为界限将指定部分划分为splitters.length个部分
         * @param a 目标数组
         * @param lo 要处理的部分的第一个元素的下标
         * @param hi 要处理的部分的最后一个元素的下标
         * @param splitters splitters
         */
        PartitionThread(Comparable[] a, int lo, int hi, Comparable[] splitters) {
            this.a = a;
            this.lo = lo;
            this.hi = hi;
            this.splitters = splitters;
        }

        /**
         * O(N)
         * 对a的指定部分(已经自然排序好的)按照splitters中的值为界限将指定部分划分为splitters.length个部分
         * @param a 目标数组
         * @param lo 要处理的部分的第一个元素的下标
         * @param hi 要处理的部分的最后一个元素的下标
         * @param splitters splitters
         */
        @SuppressWarnings("unchecked")
        private void partition(Comparable[] a, int lo, int hi, Comparable[] splitters) {
            //splitters的index
            int index = 0;

            /* 垃圾代码 很多情况没考虑到...
            //当前bucket的范围的下界, 如果一个符合当前bucket的元素都还没遇到就是-1
            int lowerIndex = -1;

            for (int i = lo; i <= hi; ) {
                if (lowerIndex < 0) {
                    if (a[i].compareTo(splitters[index]) < 0 || index == splitters.length) {
                        //如果一个符合当前bucket的元素都还没遇到但是遇到了一个小于当前splitter的元素, 那这个元素就是
                        //当前bucket的下界
                        //或者是到了最后一个桶了, 后面都没有splitter了
                        lowerIndex = i++;
                    } else
                        //一个符合当前bucket的元素都还没遇到却遇到了一个大于等于当前splitter的元素, 那么说明在a[lo...hi]
                        //中没有符合当前bucket范围的元素, -1表示没有元素, 然后bucket顺推到下一个, 继续判断a[i]是否在下一个
                        //bucket的范围中
                        bucketsBounds[index++] = new int[]{-1, -1};
                } else if (index < splitters.length && a[i].compareTo(splitters[index]) >= 0) {
                    //当前元素已经超出了当前bucket的范围, 就开始把元素放入下一个桶里面去
                    bucketsBounds[index++] = new int[]{lowerIndex, i - 1};
                    //重置lowerIndex
                    lowerIndex = -1;
                } else
                    ++i;
            }

            if (lowerIndex > -1 && index < PROCESS_NUM) {
                //如果lowIndex有而bucket的个数没够, 那说明剩下的所有的元素都应该在这个bucket里面
                bucketsBounds[index] = new int[]{lowerIndex, hi};
            }
            */

            //binary search partitioning
            //当前二分搜索的起点
            int start = lo;

            //由binarySearchPartition()返回的结果
            int end;
            for (Comparable splitter : splitters) {
                if (start > hi)
                    bucketsBounds[index++] = new int[]{-1, -1};
                else {
                    end = binarySearchPartition(a, splitter, start, hi);
                    if (end == -1)
                        bucketsBounds[index++] = new int[]{-1, -1};
                    else {
                        bucketsBounds[index++] = new int[] {start, end};
                        start = end + 1;
                    }
                }
            }

            //最后一个桶的边界
            bucketsBounds[index] = new int[]{start <= hi ? start : -1, hi};
        }

        @SuppressWarnings("unchecked")
        int binarySearchPartition(Comparable[] a, Comparable splitter, int lo, int hi) {
            //debug...
            //o("lo hi " + lo + " " + hi);

            if (lo == hi)
                if (a[lo].compareTo(splitter) < 0)
                    return lo;
                else
                    return -1;

            int mid = lo + (hi - lo) / 2;

            if (a[mid].compareTo(splitter) < 0 )
                if (a[mid + 1].compareTo(splitter) >= 0)
                    return mid;
                else
                    return binarySearchPartition(a, splitter, mid + 1, hi);
            else
                return binarySearchPartition(a, splitter, lo, mid);
        }

        /**
         * get bucketBounds
         * @return 当前process的block中的元素按照splitters划分之后的各个分区的上下界的下标
         */
        int[][] getBucketsBounds() {
            //debug
            /*
            title("");
            o("Partition Thread with range [" + lo + ", " + hi + "]");
            for (int i = lo; i <= hi; i++)
                of(a[i] + " ");
            o();
            o("Splitters: " + new DoubleLinkedList<>(splitters));
            o("Buckets: ");
            int bucketCnt = 1;
            for (int[] b : bucketsBounds)
                o("Bucket " + (bucketCnt++) + ": " + b[0] + " ~ " + b[1]);
            */

            return bucketsBounds;
        }

        /**
         * run method
         */
        @Override
        public void run() {
            partition(a, lo, hi, splitters);
        }
    }

    /**
     * MergeSort Thread
     */
    private static class MergeThread extends Thread {
        /* Fields */
        private Comparable[] src;
        private Comparable[] dst;
        private int lower;
        private Integer[] blocksBounds;

        //cannot instanced by default thread
        private MergeThread() { }

        /**
         * 根据blocksBounds中给出的边界将n个blocks合并到一个桶里面去
         * @param src unsorted array
         * @param dst 把blocksBounds给出的那些blocks归并好并放入dst数组
         * @param lower 桶的下界
         * @param blocksBounds 记录每一个blocks的边界信息(这些blocks都是排序好的了)
         */
        MergeThread(Comparable[] src, Comparable[] dst, int lower, Integer ... blocksBounds) {
            this.src = src;
            this.dst = dst;
            this.lower = lower;
            this.blocksBounds = blocksBounds;
        }

        /**
         * bottom-up MergeSort
         * @param src unsorted array
         * @param dst 把blocksBounds给出的那些blocks归并好并放入dst数组
         * @param lower 桶的下界
         * @param blocksBounds 记录每一个blocks的边界信息(这些blocks都是排序好的了)
         */
        private void mergeSort(Comparable[] src, Comparable[] dst, int lower, Integer[] blocksBounds) {
            //先复制到src中当前桶指定的范围内
            //记录桶中每一块已经排序的block的边界
            int[] blocks = new int[blocksBounds.length];
            blocks[0] = lower;

            //当前复制的位置
            int index = lower;
            for (int i = 0; i < blocksBounds.length - 1; ) {
                try {
                    System.arraycopy(src, blocksBounds[i], dst, index, blocksBounds[i + 1] - blocksBounds[i] + 1);
                } catch (ArrayIndexOutOfBoundsException e) {
                    o("Fuck Index Out of Bounds! srcPos = " + blocksBounds[i] +
                        ", destPos = " + index + ", length = " + blocksBounds[i + 1] + " - " + blocksBounds[i]
                            + " + " + 1 + "\nblocksBounds: " + new DoubleLinkedList<>(blocksBounds)
                            + "\nblocks in dst: " + new DoubleLinkedList<>(blocks));
                }

                index += blocksBounds[i + 1] - blocksBounds[i] + 1;
                blocks[i + 1] = index - 1;
                i += 2;
                //下一块block的lower
                if (i < blocks.length)
                    blocks[i] = index;
            }

            //bottom-up merge debug traces
            /*
            String mergeTraces = "all elements in process (range [" + blocks[0] + ", " + blocks[blocks.length - 1] +
                    "]): \n";
            for (int i = lower; i <= blocks[blocks.length - 1]; i++)
                mergeTraces += " " + dst[i];
            mergeTraces += "\n";
            */

            //bottom-up merge
            Comparable[] aux = new Comparable[blocks[blocks.length - 1] + 1];

            for (int sz = 1; sz < blocks.length; sz = sz * 2 + 1)
                for (int lo = 0; lo + sz + 1 < blocks.length; lo += sz * 2 + 2) {
                    merge(aux, dst, blocks[lo], blocks[lo + sz], blocks[Math.min(lo + sz * 2 + 1, blocks.length - 1)]);
                    //debug
                    //mergeTraces += "merge (blocks[" + lo + "], blocks[" + (lo + sz) + "], blocks["
                    //        + Math.min(lo + sz * 2 + 1, blocks.length - 1) + "]\n";
                }


            //debug...
            /*
            String debug = "";
            for (int i = lower; i <= blocks[blocks.length - 1]; i++)
                debug += " " + dst[i];
            //debug
            o("-----------------------------------------------------------------------\n"
                    + mergeTraces
                    + "\nblocksBounds: " + new DoubleLinkedList<>(blocksBounds)
                    + "\nblocks in dst: " + new DoubleLinkedList<>(blocks)
                    + "\nsorted sub-blocks in current bucket: \n"
                    + debug);
            */

        }

        /**
         * merge checked with local auxiliary array
         * @param auxLocal 暂存数组
         * @param dst unsorted array
         * @param lo 第一块block的第一个元素的下标
         * @param mid 第一块block的最后一个元素的下标
         * @param hi 第二块block的最后一个元素的下标
         */
        @SuppressWarnings("unchecked")
        private void merge(Comparable[] auxLocal, Comparable[] dst, int lo, int mid, int hi) {
            //checked
            if (dst[mid].compareTo(dst[mid + 1]) <= 0)
                return;

            //先将数据暂存在辅助数组中
            System.arraycopy(dst, lo, auxLocal, lo, hi - lo + 1);

            //i, j分别为两部分的第一个元素的下标
            int i = lo;
            int j = mid + 1;
            //归并
            for (int k = lo; k <= hi; k++) {
                if (i > mid)
                    dst[k] = auxLocal[j++];
                else if (j > hi)
                    dst[k] = auxLocal[i++];
                else if (auxLocal[j].compareTo(auxLocal[i]) < 0)
                    dst[k] = auxLocal[j++];
                else
                    dst[k] = auxLocal[i++];
            }
        }

        /**
         * run method
         */
        @Override
        public void run() {
            //merge
            mergeSort(src, dst, lower, blocksBounds);
        }
    }

	/**************************************
     * Methods                            *
     **************************************/
    /**
    * Ex 2.3.6
    * 一般情况下Quicksort的比较次数
    * @param a
    *       要排序的数组
    */
    public static int quickSortCompareCount(Comparable[] a) {
        //把a打乱
        StdRandom.shuffle(a);

        return quickSortCompareCount(a, 0, a.length - 1);
    }

    /**
    * Ex 2.3.6
    */
    @SuppressWarnings("unchecked")
    private static int quickSortCompareCount(Comparable[] a, int lo, int hi) {
        if (lo >= hi)
            return 0;

        //分区的分界位置
        int i = lo, j = hi + 1;

        //统计比较次数
        int cnt = 0;

        while (true) {
            while (++cnt > 0 && a[++i].compareTo(a[lo]) < 0)
                if (i == hi)
                    break;

            while (++cnt > 0 && a[lo].compareTo(a[--j]) < 0)
                if (j == lo)
                    break;

            if (i >= j)
                break;

            exch(a, i, j);
        }

        exch(a, lo, j);

        return cnt + quickSortCompareCount(a, lo, j - 1) + quickSortCompareCount(a, j + 1, hi);
    }
    /**
    * Ex 2.3.15
    * Nuts and Bolts
    * 排序nuts使得nuts中每一个元素都能与bolts中相对应的bolts拧在一起
    * 采用Quicksort的思路
    * @param nuts
    *       所有螺母的型号
    * @param bolts
    *       所有的螺帽的型号
    * @param fitting
    *       拧和器
    */
    private static void nutsAndBolts(Integer[] nuts, Integer[] bolts, CompareTwo fitting) {
        //校验nuts和bolts是否有相同数量的元素
        if (nuts.length != bolts.length) {
            o("nuts和bolts必须有相同数量的元素.");
            return;
        }

        //先把nuts打乱, 避免worst-case
        StdRandom.shuffle(nuts);

        nutsAndBolts(nuts, bolts, 0, nuts.length - 1, QuicksortEx::fitting);
    }

    /**
    * Ex 2.3.15
    * 判断nut和bolt是否能拧在一起
    * 拧和器
    * @param nut
    *       螺母
    * @param bolt
    *       螺帽
    * @return 
    *       如果nut大了就返回+1, 刚好合适就返回0, nut小了就返回-1
    */
    @SuppressWarnings("unchecked")
    private static int fitting(Comparable nut, Comparable bolt) {
        int cmp = nut.compareTo(bolt);

        if (cmp > 0)
            return +1;
        else if (cmp < 0) 
            return -1;
        else
            return 0;
    }

    /**
    * Ex 2.3.15
    * Nuts and Bolts
    * 排序给定范围的nuts使得nuts中每一个元素都能与bolts中相对应的bolts拧在一起
    * 采用Quicksort的思路
    * @param nuts
    *       所有螺母的型号
    * @param bolts
    *       所有的螺帽的型号
    * @param lo
    *       nuts和bolts的排序范围的下界
    * @param hi
    *       nuts和bolts的排序范围的上界
    * @param fitting
    *       拧和器
    */
    private static void nutsAndBolts(Integer[] nuts, Integer[] bolts, int lo, int hi, CompareTwo fitting) {
        //partitioning
        

    }

    /**
    * Ex 2.3.16
    * produce a best-case array
    * @param size
    *       要生成的数组的大小(>=1)
    * @return
    *       返回一个能够使Quicksort生成完全平衡树的best-case array
    */
    private static Comparable[] generateBestCaseArr(int size) {
        //先生成一个已排序数组
        Comparable[] sorted = new Integer[size];
        for (int i = 0; i < sorted.length; i++) 
            sorted[i] = i;

        //把这个已排序数组调整顺序成best-case array
        generateBestCaseArr(sorted, 0, size - 1);

        return sorted;
    }

    /**
    * Ex 2.3.16
    * produce a best-case array in given range
    * @param a
    *       要处理的数组
    * @param lo
    *       要处理的部分的下界
    * @param hi
    *       要处理的部分的上界
    * @return
    *       返回一个能够使Quicksort生成完全平衡树的best-case array
    */
    private static void generateBestCaseArr(Comparable[] a, int lo, int hi) {
        //递归边界
        if (lo + 1 >= hi)
            return;

        //中间元素的下标
        int mid = lo + (hi - lo) / 2;

        //把中间的元素与第一个元素(也就是Quicksort的时候的partition item)交换
        generateBestCaseArr(a, lo, mid);
        generateBestCaseArr(a, mid + 1, hi);

        //与Quicksort相反的是, 生成best-case array是先二分再交换, 也就是后序遍历交换
        //把mid与partition item交换
        exch(a, lo, mid);
    }


    /**
    * Ex 2.3.17
    * Sentinels
    * 使用哨兵减少冗余的if判断
    * @param a
    *       要排序的数组
    */
    @SuppressWarnings("unchecked")
    private static void quickSortWithSentinels(Comparable[] a) {
        //把a打乱
        StdRandom.shuffle(a);

        //找到最大的item并且交换到a[length - 1]
        //sentinel2
        int indexOfMax = 0;
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[indexOfMax]) > 0)
                indexOfMax = i;
        exch(a, indexOfMax, a.length - 1);

        quickSortWithSentinels(a, 0, a.length - 1);
    }

    /**
    * Ex 2.3.17
    * 快速排序指定部分
    * @param a
    *       要排序的数组
    * @param lo
    *       要排序的范围的下界
    * @param hi
    *       要排序的范围的上界
    */
    private static void quickSortWithSentinels(Comparable[] a, int lo, int hi) {
        if (lo >= hi)
            return;

        //分区的分界位置
        int j = partitionWithSentinels(a, lo, hi);

        quickSortWithSentinels(a, lo, j - 1);
        quickSortWithSentinels(a, j + 1, hi);
    }

    /**
    * Ex 2.3.17
    * 原地分区(In-place Partition) with sentinels
    * 使用哨兵减少冗余的if判断
    * @param a
    *       要分区的数组
    * @param lo
    *       分区范围的下界
    * @param hi
    *       分区范围的上界
    */
    @SuppressWarnings("unchecked")
    private static int partitionWithSentinels(Comparable[] a, int lo, int hi) {
        //i, j的初始值都要分别比第一个要处理的小一位和大一位
        //左边从lo+1开始遍历, 右边从hi开始遍历, a[lo]为partition item
        int i = lo, j = hi + 1;

        while (true) {
            //i向后推进, 直到遇到大于等于a[lo]或者遍历完所有的值的时候就退出循环
            //条件一定不能是<=
            //当a[i]遍历完的时候, 也就是遍历到了最后一个元素, 最后这一个元素按照void quickSortWithSentinels(Comparable[] a)
            //所处理的那样, 是整个数组中最大的数字, 所以a[length - 1]可以作为所有与a[length - 1]相关联的subarray是的哨兵
            //对于中间的那些subarrays, 也就是subarray中不包含a[length - 1]的那些interior subarrays, 分区的时候, subarray中的
            //右边那个分区的最左边的那一个元素就是哨兵
            while (a[++i].compareTo(a[lo]) < 0)
                continue;

            //j向前推进, 直到遇到小于等于a[lo]或者遍历完所有的值的时候就退出循环
            //当a[j]遍历完所有值的时候, 也就是j == lo, 那肯定要跳出这个inner while loop
            //a[lo] 就是senltinel2
            while (a[lo].compareTo(a[--j]) < 0)
                continue;

            //判断此时i, j是否交叉, 如果交叉就说明找到了a[lo]的最终位置了
            if (i >= j)
                break;

            //i, j没有交叉说明还未分区完, 交换不符合分区结果的这两个值(因为此时的a[i] >= a[lo], a[j] <= a[lo])
            exch(a, i, j);
        }

        //此时的a[j] <= a[lo]且a[j + 1] > a[lo](或者没有a[j + 1]这个元素)
        exch(a, lo, j);

        //返回a[lo]的最终位置
        return j;
    } 

    /**
    * Ex 2.2.18
    * QuickSort with Median-of-three partitioning
    * 并且中位数作为哨兵减少了冗余的if判断
    */
    private static void quickSortMedianOfThree(Comparable[] a) {
        //把a打乱
        StdRandom.shuffle(a);

        quickSortMedianOfThree(a, 0 , a.length - 1);
    }

    /**
    * Ex 2.2.18
    * QuickSort with Median-of-three partitioning
    * 并且中位数作为哨兵减少了冗余的if判断
    */
    @SuppressWarnings("unchecked")
    private static void quickSortMedianOfThree(Comparable[] a, final int lo, final int hi) {
        //递归边界
        if (lo >= hi)
            return;

        //如果只有两个元素, 连中位数都不需要找了
        if (lo + 1 == hi) {
            if(a[lo].compareTo(a[hi]) > 0)
                exch(a, lo, hi);

            return;
        }

        //排序并找出subarray中前三个数的中位数作为partition item
        if (a[lo].compareTo(a[lo + 2]) > 0)
            exch(a, lo, lo + 2);
        if (a[lo + 1].compareTo(a[lo]) < 0)
            exch(a, lo + 1, lo);
        if (a[lo + 1].compareTo(a[lo + 2]) > 0)
            exch(a, lo + 1, lo + 2);

        //把中位数交换到hi, 作为哨兵和partition item
        exch(a, lo + 1, hi);

        //左边从lo+1开始遍历, 右边从hi - 1开始遍历, a[hi]为partition item
        int i = lo, j = hi;

        while (true) {
            //i向后推进, 直到遇到大于等于a[lo]或者遍历完所有的值的时候就退出循环
            //条件一定不能是<=
            //当a[i]遍历完的时候, 也就是遍历到了最后一个元素, 最后这一个元素就是partition item(作为哨兵)
            while (a[++i].compareTo(a[hi]) < 0)
                continue;

            //j向前推进, 直到遇到小于等于a[lo]或者遍历完所有的值的时候就退出循环
            //当a[j]遍历完所有值的时候, 也就是j == lo, 而a[lo] <= partition item, 那肯定要跳出这个inner while loop
            //a[lo] 就是senltinel2
            while (a[hi].compareTo(a[--j]) < 0)
                continue;

            //判断此时i, j是否交叉, 如果交叉就说明找到了a[lo]的最终位置了
            if (i >= j)
                break;

            //i, j没有交叉说明还未分区完, 交换不符合分区结果的这两个值(因为此时的a[i] >= a[hi], a[j] <= a[hi])
            exch(a, i, j);
        }

        //此时的a[i] >= a[hi]且a[i - 1] < a[hi]
        exch(a, hi, i);

        quickSortMedianOfThree(a, lo, i - 1);
        quickSortMedianOfThree(a, i + 1, hi);
    }

    /**
    * Extra Credit in Ex 2.3.19 
    * Median of five use fewer than 7 comparisons ()
    * 不检验输入的合法性
    * @param a
    *       有五个元素的数组
    * @return 数组的中位数
    */
    @SuppressWarnings("unchecked")
    private static Integer medianOfFive(Integer[] a) {
        //令a[1] <= a[3], 1次比较
        if (a[1].compareTo(a[3]) > 0)
            exch(a, 1, 3);

        //引理: 假设x_1, x_2, x_3中x_2为三个数的中位数, 则一定有min(abs(x_i - \bar{x})) = abs(x_2 - \bar{x}) 其中\bar{x}
        //表示平均数
        Integer bar = (a[1] + a[2] + a[3]) / 3;

        //因为a[1] <= a[3], 1..3中的中位数一定是a[2]或a[3]中的一个, 需要一次比较, 这样还可以顺便把1..3排序好了
        if (Math.abs(bar - a[2]) > Math.abs(bar - a[3])) {
            exch(a, 2, 3);
        } 

        //记录median的值
        Integer median = a[2];


        //假设这时候的排序为0 1 2 3 4 (0, 4待定)
        

        //在把a[0]和a[2]进行一次比较, 然后第二层再按照情况与a[1]或a[3]进行一次比较
        if (a[0].compareTo(a[2]) > 0) {
            if (a[0].compareTo(a[3]) > 0) {
                //排序应该为1 2 3 0 4 (4待定)
                
                if (a[4].compareTo(a[3]) < 0) {
                    if (a[4].compareTo(a[2]) > 0)
                        //1 2 4 3 0 共6次比较
                        median = a[4];
                    else 
                        //(1 | 4) 2 3 0 共6次比较
                        median = a[2];
                } else {
                    //如果a[4] >= a[3]的话就可以肯定median为a[3]了
                    //排序1 2 3 (0 | 4) 共5次比较
                    median = a[3];
                }
            } else {
                //1 2 0 3 4 (4待定) 共4次比较
                median = a[0];
            }
        } else {
            //(0 | 1) 2 3 4 (4待定) 共三次比较
            median = a[2];
        }

        return median;
    }

    /**
    * Ex 2.3.20
    * 我觉得书上的思路(把subarrays存储在stack中, 然后要用的时候再pop出来)比较浪费空间, 所以我实现这个的时候只是存储了树
    * 的每一层上的nodes的lo和hi的信息.
    * Nonrecursive Quicksort
    * @param a 要排序的数组
    */
    private static void nonrecursiveQuickSort(Comparable[] a) {
        //把a打乱
        StdRandom.shuffle(a);

        //找到最大的item并且交换到a[length - 1]
        //sentinel2
        int indexOfMax = 0;
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[indexOfMax]) > 0)
                indexOfMax = i;
        exch(a, indexOfMax, a.length - 1);

        //存储二叉树当前level的所有lo和hi的值
        DoubleLinkedList<Integer[]> currentLevel = new DoubleLinkedList<>(), temp = new DoubleLinkedList<>();

        currentLevel.addLast(new Integer[]{0, a.length - 1});

        int lo, hi;

        while (true) {
            Integer[] range = currentLevel.popFirst();

            lo = range[0];
            hi = range[1];

            //debug...
            //o("debug lo hi " + lo + " " + hi);
            
            int j = partitionWithSentinels(a, lo, hi);

            //debug...
            //o("j: " + j);
            //o(new DoubleLinkedList<>(a));

            //把子树的上下界加进List
            //只剩下一个元素的时候不会加入记录partition的二叉树的lo和hi的信息的List里面去, 算是partition的边界
            if (j - 1 > lo)
                temp.addLast(new Integer[]{lo, j - 1});
            if (j + 1 < hi)
                temp.addLast(new Integer[]{j + 1, hi});

            if (currentLevel.getSize() == 0) {
                currentLevel = temp;
                temp = new DoubleLinkedList<Integer[]>();
                //debug...
                //o("Size: " + currentLevel.getSize());
            }

            //循环边界
            if (currentLevel.getSize() == 0)
                break;
        }
    }

    /**
    * Ex 2.3.24
    * Parallelizing Sample Sort
    * @param a 要排序的数组
    * @return 排序后的数组
    */
    @SuppressWarnings("unchecked")
    private static Comparable[] sampleSort(Comparable[] a) {
        //首先数组的元素肯定要大于等于PROCESS_NUM * PROCESS_NUM个
        if (a.length < PROCESS_NUM * PROCESS_NUM) {
            Arrays.sort(a);
            return a;
        }


        //step 1.
        //把a大致均分PROCESS_NUM块blocks, 并且对每一块block进行QuickSort
        //用来记录每一块block第一个元素和的下标
        int[] firstIndexOfBlocks = new int[PROCESS_NUM];
        for (int i = 0; i < PROCESS_NUM; i++) {
            firstIndexOfBlocks[i] = i * a.length / PROCESS_NUM;
            QuickSort.quickSortFaster(a, firstIndexOfBlocks[i], (i + 1) * a.length / PROCESS_NUM);
            //debug
            //if (!isSorted(a, firstIndexOfBlocks[i], (i + 1) * a.length / PROCESS_NUM - 1))
            //    o("Fuck wrong QuickSort write by DCMMC...");

            //Arrays.sort(a, firstIndexOfBlocks[i], (i + 1) * a.length / PROCESS_NUM);

            //debug...
            //o("PROCESS: ");
            //for (int j = firstIndexOfBlocks[i]; j < (i + 1) * a.length / PROCESS_NUM; j++)
            //    of(a[j] + " ");
            //o();
        }
        firstIndexOfBlocks[PROCESS_NUM - 1] = (PROCESS_NUM - 1) * a.length / PROCESS_NUM;

        //debug
        //o("firstIndexOfBlocks: " + new DoubleLinkedList<>(firstIndexOfBlocks));


        //step 2.
        //from each sorted block it chooses m - 1 evenly spaced elements, these m(m - 1) elements(aka sample)
        // used to determine the buckets
        Integer[] samplesIndex = new Integer[PROCESS_NUM * (PROCESS_NUM - 1)];
        int index = 0;

        /* P0 Process */
        //
        for (int i : firstIndexOfBlocks) {
            //sample中每一个元素的间隔
            int spaced = (a.length / PROCESS_NUM - 1) / PROCESS_NUM;

            //相对于firstIndexOfBlocks[i]的间隔
            int offset = 0;

            for (int j = 0; j < PROCESS_NUM - 1; j++) {
                samplesIndex[index++] = i + offset + spaced;
                offset += spaced + 1;
            }
        }

        //debug
        //o("samples: ");
        //for (int i : samplesIndex)
        //    of(" " + a[i]);
        //o();

        //step 2.
        //排序sample并且从sample中均匀的选取出splitters
        //sort sample
        Arrays.sort(samplesIndex, (i1, i2) -> a[i1].compareTo(a[i2]));

        //Splitters
        Comparable[] splitters = new Comparable[PROCESS_NUM - 1];
        //从sample中均匀的选出PROCESS_NUM - 1个splitters
        //思路: 先把sample按顺序等分为PROCESS_NUM - 1块, 每块有PROCESS_NUM个元素, 然后从每一块的相同的位置(一般是中间)上
        //取一个元素, 组合成splitters
        int position = PROCESS_NUM / 2;
        for (int i = 0; i < PROCESS_NUM - 1; i++)
            splitters[i] = a[samplesIndex[i * PROCESS_NUM + position]];

        //debug
        //o("splitters: \n" + new DoubleLinkedList<>(splitters));

        //step 3.
        //多线程分区, 把每一个process都分成PROCESS_NUM个blocks, splitter为分区之间的界限, 如果有与splitter相等的值的话
        //这个splitter就是分区的第一个元素
        PartitionThread[] partitionThreads = new PartitionThread[PROCESS_NUM];
        for (int i = 0; i < firstIndexOfBlocks.length - 1; i++)
            partitionThreads[i]  = new PartitionThread(a, firstIndexOfBlocks[i], firstIndexOfBlocks[i + 1] - 1, splitters);
        partitionThreads[PROCESS_NUM - 1] = new PartitionThread(a, firstIndexOfBlocks[PROCESS_NUM - 1], a.length - 1, splitters);

        //Run Thread
        for (PartitionThread p : partitionThreads)
            p.start();
        try {
            //Join堵塞等待
            for (PartitionThread p : partitionThreads)
                p.join();
        } catch (Exception e) {
            //...
            throw new RuntimeException("Error when partitioning!", e);
        }

        //step 4.
        //多线程归并每个桶中的blocks, 然后所有线程都结束的时候就可以得到结果了
        //排序好的数组
        //该死的不能创建Comparable[], 不然不能向下转型到a真正的类型去, 只能用反射了
        Comparable[] result = (Comparable[])java.lang.reflect.Array.newInstance(a[0].getClass(), a.length);

        //归并队列
        MergeThread[] mergeThreads = new MergeThread[PROCESS_NUM];

        //记录每一个桶的lower边界的值
        int[] lowers = new int[PROCESS_NUM];
        lowers[0] = 0;

        //获取partition之后每个process中的桶的边界信息
        int[][][] bucketsBounds = new int[PROCESS_NUM][PROCESS_NUM][2];
        for (int i = 0; i < partitionThreads.length; i++)
            bucketsBounds[i] = partitionThreads[i].getBucketsBounds();

        //入桶
        for (int i = 0; i < PROCESS_NUM; i++) {
            //记录当前桶中的元素的个数
            int elementsCnt = 0;

            DoubleLinkedList<Integer> buckets = new DoubleLinkedList<>();

            for (int j = 0; j < PROCESS_NUM; j++)
                if (bucketsBounds[j][i][0] != -1) {
                    buckets.addLast(bucketsBounds[j][i][0]);
                    buckets.addLast(bucketsBounds[j][i][1]);
                    elementsCnt += bucketsBounds[j][i][1] - bucketsBounds[j][i][0] + 1;
                }

            //记录下一个桶的lower
            if (i + 1 < PROCESS_NUM)
                lowers[i + 1] = lowers[i] + elementsCnt;

            //放入归并队列
            mergeThreads[i] = new MergeThread(a, result, lowers[i], buckets.toArray());
        }

        //debug..
        //o("lower in each bucket: " + new DoubleLinkedList<>(lowers));


        //开始归并
        for (MergeThread m : mergeThreads)
            m.start();

        try {
            //Wait for all the threads to exit.  This suspends the calling thread.
            for (MergeThread m : mergeThreads)
                m.join();
        } catch (Exception e) {
            throw new RuntimeException("Error when merge!", e);
        }


        return result;
    }

	/**************************************
     * 我的一些方法和client测试方法         *
     **************************************/
    /**
    * 判断数组已经按照由小到大顺序将a排序好了
    * @param a
    *           要判断的数组
    * @return 如果已经按照由小到大顺序将a排序好了就返回true
    */
    @SuppressWarnings("unchecked")
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    /**
    * 判断数组指定部分已经按照由小到大顺序将a排序好了
    * @param a
    *           要判断的数组
    * @param lo 要判断的部分的下界
    * @param hi 要判断的部分的上界
    * @return 如果已经按照由小到大顺序将a排序好了就返回true
    * @throws IllegalArgumentException if a is null or lo or hi is not in range of a.
    */
    @SuppressWarnings("unchecked")
    private static boolean isSorted(Comparable[] a, int lo, int hi) throws IllegalArgumentException {
        if (a == null) 
            throw new IllegalArgumentException("argument array is null");
        if (lo > hi || lo < 0 || hi < 0 || lo >= a.length || hi >= a.length)
            throw new IllegalArgumentException("argument lo = " + lo + " or hi = " + hi + " is not in range of a");

        if (lo == hi)
            return true;

        for (int i = lo + 1; i <= hi; i++) 
            if (a[i].compareTo(a[i - 1]) < 0)
                return false;

        return true;
    }

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
        //Ex 2.3.6
        title("Ex 2.3.6");

        Integer[][] samples = new Integer[][]{new Integer[100], new Integer[1000], new Integer[10_000]};
        for (Integer[] s : samples)
            for (int i = 0; i < s.length; i++)
                s[i] = StdRandom.uniform(s.length);

        o("   N  compares C_N(2NlnN)");
        for (Integer[] s : samples) {
            of("%6d %8d %8d", s.length, quickSortCompareCount(s), 
                (int)Math.ceil(2 * s.length * Math.log(s.length)));
            o();
        }

        //Ex 2.3.8
        //原始版本的Quicksort就算是所有元素都相等, 还是需要比较~ 2NlnN次
        //3-way partitioning 优化过之后可以达到~ 2ln2*N*H次比较

        //Ex 2.3.10
        //TODO
        //不知道怎么用Chebyshev不等式, 因为那个不等式需要用到离散随机变量的标准差, 可是我不知道
        //不过我在Johns Hopkins University的CS 600.664 Randomized and Big Data Algorithms这门课看到了使用tail inequality
        //来确定Quicksort的比较次数的下界(也就是 ~ 2ln2N*H)的方法=,=

		//Ex 2.3.11
		//见SortExample.java源码
        //如果把QuickSort.java中的partition()中的两个inner loop中的条件改成<=, 用倍率测试直接爆栈了... 而且差不多是N^2
        //的时间复杂度

        //Ex 2.3.13
        //worst case: 极不平衡的二叉树: depth N
        //best case: 完全平衡的二叉树: depth logN
        //average case: 由Quicksort平均情况下的比较次数为2ln2*N*H可以得到: depth 2ln2*H (H为Shannon entropy)


        //Ex 2.3.14
        //TODO
        //连证明都不知道怎么证


        //Ex 2.3.15
        //TODO
        //没想出来, 反正就是一个Quicksort思路的变形

        //Ex 2.3.16
        title("Ex 2.3.16");

        o(new DoubleLinkedList<>(generateBestCaseArr(15)));

        //Ex 2.3.17
        title("Ex 2.3.17");

        //test
        StdRandom.shuffle(samples[2]);
        quickSortWithSentinels(samples[2]);
        o(isSorted(samples[2]));

        //Ex 2.3.18
        title("Ex 2.3.18");

        StdRandom.shuffle(samples[2]);
        quickSortMedianOfThree(samples[2]);
        o(isSorted(samples[2]));

        //Ex 2.3.19 Extra credit
        title("Ex 2.3.19 Extra credit");

        Integer[] fiveInts = new Integer[]{3, 2, 8, 3, 5};

        o("median : " + medianOfFive(fiveInts) + " in " + new DoubleLinkedList<>(fiveInts));

        //Ex 2.3.20
        title("Ex 2.3.20");

        StdRandom.shuffle(samples[2]);
        nonrecursiveQuickSort(samples[2]);
        o(isSorted(samples[2]));

        //Ex 2.3.21
        //见我的笔记里面相关证明

        //Quicksort Improvements
        //3-way partitioning and CUTOFF
        title("Quicksort Improvements");

        final int SIZE = 10_000;

        Integer[] testArr = new Integer[SIZE];
        for (int i = 0; i < SIZE; i++)
            testArr[i] = StdRandom.uniform(SIZE / 5);
        StdRandom.shuffle(testArr);

        QuickSort.quickSortImprove(testArr);

        o(isSorted(testArr));

        //Ex 2.3.22 Ex 2.3.23
        title("Ex 2.3.22 Ex 2.3.23");

        StdRandom.shuffle(testArr);

        QuickSort.quickSortImprove(testArr);

        o(isSorted(testArr));
        

        //Ex 2.3.24
        //samplesort
        //没怎么理解题目的意思=,=
        //花了两天去实现了这个鬼并行samplesort(基于桶排序), 结果效率感人...连quicksortFaster的一般效率都没有... 而且内存占用惊人
        //不搞了
        title("Ex 2.3.24");

        StdRandom.shuffle(testArr);
        //debug
        testArr = new Integer[]{22, 7, 13, 18, 2, 17, 1, 14,
                                20, 6, 10, 24, 15, 9, 21, 3,
                                16, 19, 23, 4, 11, 12, 5, 8};
        //Integer[] testArr = new Integer[]{22, 7, 13, 18, 2, 17, 1, 14, 20};

        testArr = (Integer[])sampleSort(testArr);

        //test PartitionThread
        //PROCESS_NUM = 5
        //PartitionThread pt = new PartitionThread(new Integer[]{0, 1, 1, 1, 1, 1, 1}
        //                  , 0, 6, new Integer[]{1, 7, 9, 13});
        //pt.start();
        //pt.getBucketsBounds();

        //radio test with quickSortFaster
        of("  N  samplesort quickSortFaster radio\n");
        for (int i = 5; i < 7; i++) {
            //每一个N有5次实验
            for (int t = 0; t < 5; t++) {
                of(" 10^%d ", i);

                //generate array with size 10^N
                Integer[] arr = new Integer[(int)Math.pow(10, i)];
                for (int j = 0; j < arr.length; j++)
                    arr[j] = StdRandom.uniform((int)Math.pow(10, i) * 10);

                long start = System.currentTimeMillis();
                arr = (Integer[])sampleSort(arr);
                double time1 = (System.currentTimeMillis() - start) / 1000.0;
                of(" %8.6f ", time1);

                if (!isSorted(arr)) {
                    of("Fuck the samplesort\n");
                }

                StdRandom.shuffle(arr);

                start = System.currentTimeMillis();
                QuickSort.quickSortFaster(arr);
                double time2 = (System.currentTimeMillis() - start) / 1000.0;
                of(" %8.6f %4.2f\n", time2, time2 / time1);

                if (!isSorted(arr)) {
                    of("Fuck the quickSortFaster\n");
                }
            }
        }

        //debug..
        //title(("testArr"));
        //o(new DoubleLinkedList<>(testArr));

        o(isSorted(testArr));

        /* Experiments */

        //Ex 2.3.25
        title("Ex 2.3.25");

        int[] sizes = new int[]{1_000, 10_000, 100_000};
        Double[][] dt = new Double[][]{new Double[sizes[0]], new Double[sizes[1]], new Double[sizes[2]]};
        for (Double[] d : dt) {
            for (int i = 0; i < d.length; i++)
                d[i] = StdRandom.uniform(d.length) * 1.0;
        }

        o("m(CUTOFF)    1K   10K   100K");
        long start;
        //m为要测试的CUTOFF值
        for (int m = 0; m <= 30; m++) {
            of("%4d     ", m);

            for (Double[] d : dt) {
                start = System.currentTimeMillis();
                QuickSort.quickSortImprove(d, 0, d.length - 1, m);
                of(" %5.3f  ", (System.currentTimeMillis() - start) / 1000.0);
            }
            
            o("");
        }

	}
}///~