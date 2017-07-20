//package com.DCMMC.Algorithms;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;


import java.util.BitSet;



/**
 * class comment :
 * Created by DCMMC on 2017/4/8.
 */
public class Percolation {
    private WeightedQuickUnionUF wquModel;
    private final int n;
    //记录所有位是block还是open
    private BitSet sites;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        validNum(n);
        this.n = n;

        //额外添加两个Virtual Site位于最顶端和最底部
        wquModel = new WeightedQuickUnionUF(n * n + 2);

        //创建BitSet并且把除了顶部和底部的Virtual Site之外所有位都设置为false(0),那两个Virtual Site设置为true(1).
        sites = new BitSet(n * n + 2);
        sites.set(0, true);
        sites.set(n * n + 1, true);
    }

    //判断n是否合法(大于0)
    private void validNum(int n) throws IllegalArgumentException {
        if(n < 1)
            throw new IllegalArgumentException("Argument " + n + " is invalid.");
    }

    private int getN(int row, int col) {
        validNum(row);
        validNum(col);

        //加上最顶端添加的Virtual Node, offset从0开始(0是那个Virtual Site)
        return (row - 1) * n + col;
    }

    // open site (row, col) if it is not open already
    public    void open(int row, int col) {
        validNum(row);
        validNum(col);

        int offset = getN(row, col);

        sites.set(offset, true);
        //如果是第一排或者最后一排的Site open了, 就把这个Site连接到Top Virtual Site或Bottom Virtual Site
        if(row == 1)
            wquModel.union(offset, 0);
        if(row == n)
            wquModel.union(offset, getN(n, n) + 1);

        //依次跟连接up down left right的Open Site连接
        //up
        if(row != 1 && isOpen(row - 1, col))
            wquModel.union(offset, getN(row - 1, col));
        //down
        if(row != n && isOpen(row + 1, col))
            wquModel.union(offset, getN(row + 1, col));
        //left
        if(col != 1 && isOpen(row, col - 1))
            wquModel.union(offset, getN(row, col - 1));
        //right
        if(col != n && isOpen(row, col + 1))
            wquModel.union(offset, getN(row, col + 1));
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        validNum(row);
        validNum(col);


        return sites.get(getN(row, col));
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        validNum(row);
        validNum(col);

        return wquModel.connected(getN(row, col), 0);
    }

    // number of open sites
    public     int numberOfOpenSites() {
        //这里要减去两个一直都是Open的Virtual Site
        return sites.cardinality() - 2;
    }

    // does the system percolate?
    public boolean percolates() {
        return wquModel.connected(0, getN(n, n) + 1);
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
    }
}///~
