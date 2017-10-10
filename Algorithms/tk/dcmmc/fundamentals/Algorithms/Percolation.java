//package tk.dcmmc.fundamentals.Algorithms;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

/**
 * class comment :
 * Created by DCMMC on 2017/4/8.
 */
public class Percolation {
    //有一个Top Virtual Site的带路径压缩并且加权的UF
    private WeightedQuickUnionUF wquTopModel;
    //有一个Bottom Virtual Site的带路径压缩并且加权的UF
    private WeightedQuickUnionUF wquBottomModel;
    
    //n-by-n grid的大小
    private final int n;
    //记录所有位是block还是open
    private final boolean[] sites;
    //记录所有位中open的个数(不包括顶部和底部的Virtual Site)
    private int opens;

    //记录系统是否已经percolate
    private boolean percolate = false;

    /**
    * create n-by-n grid, with all sites blocked
    * @param n 
    *           grid的大小
    */
    public Percolation(int n) {
        if (n < 1)
            throw new IllegalArgumentException("Argument " + n + " is invalid.");

        this.n = n;

        //额外添加两个Virtual Site(分别为最顶端和最底部)
        wquTopModel = new WeightedQuickUnionUF(n * n + 1);
        wquBottomModel = new WeightedQuickUnionUF(n * n + 1);

        sites = new boolean[n * n];
    }

    /**
    * 判断n是否合法(n为[1, n]的整数)
    * @param num
    *           要检验的num
    */
    private void validNum(int num) {
        if(num < 1 || num > n)
            throw new IllegalArgumentException("Argument " + num + " is invalid.");
    }

    /**
    * 由提供的row和col得出对应的site在WeightQuickUnionUF中的index
    * @param row
    *           行
    * @param col
    *           列
    * @return 对应的index([1, n * n])
    */
    private int getIndex(int row, int col) {
        validNum(row);
        validNum(col);

        //加上最顶端添加的Virtual Node, offset从0开始(0是那个Virtual Site)
        return (row - 1) * n + col;
    }

    // open site (row, col) if it is not open already
    public    void open(int row, int col) {
        validNum(row);
        validNum(col);

        int offset = getIndex(row, col);

        //防止多次重复open
        if (sites[offset - 1])
            return;

        sites[offset - 1] = true;

        opens++;

        //如果是第一排或者最后一排的Site open了, 就把这个Site连接到Top Virtual Site或Bottom Virtual Site
        if (row == 1)
            wquTopModel.union(offset, 0);
        if (row == n)
            wquBottomModel.union(offset, 0);

        //依次跟连接up down left right的Open Site连接
        //up
        if (row != 1 && isOpen(row - 1, col)) {
            wquTopModel.union(offset, getIndex(row - 1, col));
            wquBottomModel.union(offset, getIndex(row - 1, col));
        }
        //down
        if (row != n && isOpen(row + 1, col)){
             wquTopModel.union(offset, getIndex(row + 1, col));
            wquBottomModel.union(offset, getIndex(row + 1, col));
        }
        //left
        if (col != 1 && isOpen(row, col - 1)) {
            wquTopModel.union(offset, getIndex(row, col - 1));
            wquBottomModel.union(offset, getIndex(row, col - 1));
        }
        //right
        if (col != n && isOpen(row, col + 1)) {
            wquTopModel.union(offset, getIndex(row, col + 1));
            wquBottomModel.union(offset, getIndex(row, col + 1));
        }

        //如果offset在两个UF中都是以Virtual Site为root的话, 就说明系统已经完全渗透了
        if (wquTopModel.connected(offset, 0) && wquBottomModel.connected(offset, 0))
            percolate = true;
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        validNum(row);
        validNum(col);


        return sites[getIndex(row, col) - 1];
    }

    /**
    * is site (row, col) full?
    * 判断从Top Stite是否能够渗透到该site
    * @param row
    *           行
    * @param col
    *           列
    * @return 是否能够从Top Stite是否能够渗透到该site
    */
    public boolean isFull(int row, int col) {
        validNum(row);
        validNum(col);

        int offset = getIndex(row, col);

        //如果这个site同时是以top和bottom Virtual Site的为root的, 那就说明是连通的
        return wquTopModel.connected(offset, 0);
    }

    // number of open sites
    public     int numberOfOpenSites() {
        //这里要减去两个一直都是Open的Virtual Site
        return opens;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.percolate;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation test = new Percolation(20);

        test.open(1, 1);
        test.open(1, 2);
        test.open(2, 2);    

        System.out.println("(2 , 2) isFull ? " + test.isFull(2, 2));
        System.out.println("is percolates ? "+ test.percolates());
        System.out.println("number of Open Sites : " + test.numberOfOpenSites());

        
        
        //从文件读取ints到DoubleLinkedList数组
        try {
            File intsFile = new File(Percolation.class.getResource("sedgewick60.txt").toURI());

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

                Percolation perc = new Percolation(n);

                int cnt = 0;

                for (int i : a) {
                    if (last == 0) {
                        last = i;
                    } else {
                        perc.open(last, i);
                        last = 0;

                        if (++cnt == 1)
                            System.out.println( perc.isFull(1, 6) );
                    }
                }

                System.out.println(perc.numberOfOpenSites());

                System.out.println(intsFile.getName() + ": " + perc.percolates());
            } else {
                //Debug...
                System.out.println("File not found....");
            }
        } catch (Exception e) {
            //Exception
            throw new RuntimeException(e);
        }

        
    }
}///~
