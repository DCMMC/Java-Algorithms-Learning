package tk.dcmmc.fundamentals.Exercises;

import edu.princeton.cs.algs4.StdDraw;
import tk.dcmmc.fundamentals.Algorithms.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.awt.Font;

/**
 * 本文件包括Exercise 1.5中部分习题的解答
 * Created by DCMMC on 2017/7/31
 * Finished on 2017/
 * @author DCMMC
 * @since 1.5
 */
class UnionFindEx {
    /**
    * ErdosRenyi
    * @param n
    *       生成的UF的大小, 以及产生的随机数也是在[0, n-1]
    * @return 生成的连接总数
    */
    private static int erdosRenyiCount(int n) {
        UnionFind uf = new UnionFind(n);

        //随机生成的连接对数
        int count = 0;

        Random rand = new Random();

        while (uf.getCount() > 1) {
            count++;

            int p = rand.nextInt(n);
            int q = rand.nextInt(n);

            if (!uf.isConnectedFaster(p, q))
                uf.quickUnionWeighted(p, q);
        }

        return count;
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
     * Driver to test my solutions to this Exercises
     * @param args
     *			command-line arguments
     */
    public static void main(String[] args) {
        //Ex 1.5.12 & 1.5.13
        //见Algorithms--UnionFind

        //Ex 1.5.16
        //从文件读取ints到DoubleLinkedList数组
        try {
            File intsFile = new File(UnionFindEx.class.getResource("mediumUF.txt").toURI());

            if (intsFile.exists() && intsFile.canRead()) {
                Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(intsFile)), "UTF-8");

                //以回车符, 空白符分隔
                sc.useDelimiter(Pattern.compile("[\\s\r\n]+"));

                int n = sc.nextInt();

                DoubleLinkedList<Integer> a = new DoubleLinkedList<>();

                int index = 0;

                while ( sc.hasNext() ) {
                    a.addLast(sc.nextInt());
                }

                int last = 0;

                int cnt = 0;

                UnionFind uf = new UnionFind(n);

                StdDraw.setXscale(0.0d, 650.0d);
                StdDraw.setYscale(0.0d, 100.0d);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
                StdDraw.text(13, 20, "20");
                StdDraw.text(13, 98, "100");
                StdDraw.text(625, 2, "625");
                StdDraw.setPenRadius(0.004);

                for (int i : a) {
                    if (last == 0) {
                        last = i;
                    } else {
                        uf.quickUnionWeighted(last, i);
                        last = 0;
                        StdDraw.point(++cnt, uf.getCost());
                        StdDraw.setPenColor(StdDraw.RED);
                        StdDraw.point(cnt, uf.getTotal() * 1.0 / cnt);
                        StdDraw.setPenColor(StdDraw.BLACK);
                    }
                }

            } else {
                //Debug...
                System.out.println("File not found....");
            }
        } catch (Exception e) {
            //Exception
            throw new RuntimeException(e);
        }

        //Ex 1.5.17
        o("大小为50的UF在所有sites都连接的时候需要花" + erdosRenyiCount(50) + "次随机连接");
    }
}///~