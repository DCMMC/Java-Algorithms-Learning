package com.DCMMC.Algorithms;

import java.util.Random;

/**
 * @author DCMMC
 * term : 1.connected components 连通分量 2.Union found 联合查找
 * using : Eager Approach 贪心算法
 */
class UnionFound {
    //Only when the index of array has the same value,
    //them are connected(i.e. them are in A Connected Components.
    private int[] id;
    private int[] size;//To use quickUnionWeighted method.

    //O(N)
    UnionFound(int n) {
        id = new int[n];
        size = new int[n];

        for(int i = 0;i < n;i++)  {
            id[i] = i;
            size[i] = 1;
        }
    }

    //Connect p to q
    //A Slow method
    //O(N)
    void unionSlow(int p,int q) {
        int pid = id[p];
        int qid = id[q];
        //遍历整个数组，把所有的值为pid的项的值给为qid
        //e.g.  0 1 2 3 4 5 6
        //id[i] 0 1 1 3 4 5 6
        // union(1,3) ==> id[i] 0 3 3 3 4 5 6
        for(int i = 0;i < id.length;i++)
            if(id[i] == pid)
                id[i] = qid;
    }

    //O(1)
    boolean isConnectedSlow(int p,int q) {
        return id[p] == id[q];
    }

    //用偷懒策略来实现一个效率高一点点的算法
    //Quick Union
    //O(1)
    void quickUnion(int p, int q) {
        id[p] = q;
    }

    //O(log N) base 2 logarithm
    int rootId(int index) {
        while(index != id[index])
            index = id[index];
        return index;
    }

    //A better rootId Method.Decrease the Depth of the tree when get the rootId.
    //That called Path Compression.(路径压缩)
    //Also O(log N) but better.
    int compressedRootId(int index) {
        while(index != id[index]) {
            id[index] = id[id[index]];//This addition line to set the value of index
            // from its parent to its grandparent.This will Decrease the Depth to Depth - 1
            index = id[index];
        }
        return index;
    }

    //But when the tree is Tall enough,its also spend much time to backtrack to Root Id.
    //O(log N)
    boolean isConnectedFaster(int p,int q) {
        return rootId(p) == rootId(q);
    }

    //To Avoid create a Tall tree.
    // We ues Weighted Tree to make sure A Smaller Tree is always linked to the bottom of the Taller Tree.

    //Connect the root of smaller tree to the root of the larger tree.Let Tree Shorted and wider.Improve the efficiency of union method.
    //O(log N) But Much less than the O(lg N) of quickUnion Method.Because the Depth is more less.
    void quickUnionWeighted(int p,int q) {
        int pRootId = compressedRootId(p);
        int qRootId = compressedRootId(q);

        if(pRootId == qRootId)
            return;
        if(size[pRootId] < size[qRootId]) {
            id[pRootId] = qRootId;
            size[qRootId] += size[pRootId];
        } else {
            id[qRootId] = pRootId;
            size[pRootId] += size[qRootId];
        }
    }
}
/**
 * @author DCMMC
 * @since 1.5
 * class comment : A driver to test DynamicConnectivity
 * Created by DCMMC on 2017/3/16.
 */
public class DynamicConnectivity {
    public static void main(String[] args) {
        final int SIZE = 1_000_000;

        UnionFound demo = new UnionFound(SIZE);
        Random rand = new Random();

        for(int i = 0;i < SIZE / 2;i++)
            demo.quickUnionWeighted(rand.nextInt(SIZE),rand.nextInt(SIZE));
        demo.quickUnionWeighted(787_897,7_777);

        System.out.println("555888 and 9729 : "+demo.isConnectedFaster(555888,9729));
        System.out.println("3 and 6 : "+demo.isConnectedFaster(3,6));
        System.out.println("3 and 3 : "+demo.isConnectedFaster(3,3));
        System.out.print("787897 and 7777"+demo.isConnectedFaster(787_897,7_777));

        //SO , the MOST Efficiency Method is WQUPC (Weighted Quick Union with Path Compression)
        //Any sequence of M union-find ops(abbr. operations) on N objects makes <= c( M + lg* N ) array access
        //P.S. lg* N is iterate logarithm base 2,it means the number of time to make the lg of N to 1.
        //e.g. lg* (2^65535) = 5) What a AMAZING function!
        //But it is impossible to make the Complexity(时间复杂度) to O(N) (i.e.linear线性) as it was be proved by theory.

        //Applications:
        //1. Probability(概率) Of A Percolation(渗滤) Model
        //   Monte Carlo simulation(蒙特卡洛仿真) to Test
        // if any bottom sites is connected to any top sites in a N-by-N grid(表格).


        /**
         * Practice1:
         * Social network connectivity.
         * Given a social network containing n members and a log file containing m timestamps at which
         * times pairs of members formed friendships, design an algorithm to determine the earliest time
         * at which all members are connected (i.e., every member is a friend of a friend of a friend ...
         * of a friend). Assume that the log file is sorted by timestamp and that friendship is an
         * equivalence relation. The running time of your algorithm should be mlogn or better and use
         * extra space proportional to n.
         *
         * Note: these interview questions are ungraded and purely for your own enrichment. To get a hint,
         submit a solution.
         *
         *
         * My Answer:
         * Create a int TreeCount to store the tree counts.And its initialization value is n,i.e.All the
         * member of id[n] is a single tree.In each union action,check if the pair of elements is connected
         * and if their are not connected then TreeCount minus 1.At the last of union action,judge that if
         * the value of TreeCount is 1,and the earliest time when all members are connected is the time when
         * it became 1.
         */
    }
}///:~