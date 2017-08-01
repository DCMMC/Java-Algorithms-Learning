package tk.dcmmc.fundamentals.Algorithms;

import java.util.Random;

/**
 * @author DCMMC
 * terms : 1.connected components 连通分量 2.Union found 联合查找
 * using : Eager Approach 贪心算法
 */
public class UnionFind {
    //Only when the index of array has the same value,
    //they are connected(i.e. them are in A Connected Components.
    private int[] id;
    //To use quickUnionWeighted method.
    //记录每个site下面的子site的个数
    private int[] size;

    //number of components
    private int count;

    //for Ex 1.5.16
    //单次操作的数组访问
    int cost = 0;
    //总共的数组访问
    int total = 0;


    /**
    * 不允许通过默认构造器创建对象
    */
    private UnionFind() {

    }


    /**
    * Initializes an empty union–find data structure with {@code n} sites
    * {@code 0} through {@code n-1}. Each site is initially in its own 
    * component.
    * O(N)
    *
    * @param  n the number of sites
    * @throws IllegalArgumentException if {@code n < 0}
    */
    public UnionFind(int n) throws IllegalArgumentException {
        if (n < 0)
            throw new IllegalArgumentException("参数n(" + n + ")必须是大于0的整数!");

        count = n;

        id = new int[n];
        size = new int[n];

        //初始化, 每个位的id都不一样, 代表都没有连接
        for(int i = 0;i < n;i++)  {
            id[i] = i;
            size[i] = 1;
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int getCount() {
        return count;
    }

    /**
    * Connect p to q
    * A Slow implementation, 就是如果p, q相连, 则与p, q相连的所有site都要变成一样的id
    * O(N)
    * @param p 
    *       与q相连
    * @param q
    *       与p相连
    */
    public void unionSlow(int p, int q) {
        int pid = id[p];
        int qid = id[q];
        //遍历整个数组，把所有的值为pid的项的值给为qid
        //e.g.  0 1 2 3 4 5 6
        //id[i] 0 1 1 3 4 5 6
        // union(1,3) ==> id[i] 0 3 3 3 4 5 6
        for(int i = 0; i < id.length; i++)
            if(id[i] == pid)
                id[i] = qid;

        count--;
    }

    /**
    * O(1)
    * @param p 要判断是否相连的一个site
    * @param q 要判断是否相连的另一个site
    * @return p, q如果是相连的就返回true
    */
    public boolean isConnectedSlow(int p, int q) {
        return id[p] == id[q];
    }

    /**************************************
     * 比暴力算法更好的实现(quickUnion)     *
     **************************************/

    /**
    * 用偷懒策略来实现一个效率高一点点的算法
    * 就是把p这个位中的内容指向q的index
    * Quick Union
    * O(1)
    * @param p 
    *       把p指向q
    * @param q
    *       把p指向q
    */
    public void quickUnion(int p, int q) {
        id[p] = q;
        count--;
    }

    /**
    * O(log N) base 2 logarithm
    * 找到index的那个site的root的id
    * @param index 
    *           要找到其rootId的位的index
    * @return index指向的根位的id
    */
    public int rootId(int index) {
        while(index != id[index])
            index = id[index];
        return index;
    }

    /**
    * But when the tree is Tall enough,its also spend much time to backtrack to Root Id.
    * O(log N)
    * @param p 
    *           判断p位和q位是否相连(即他们的rootId是否一样)
    * @param q
    *           判断p位和q位是否相连(即他们的rootId是否一样)
    * @return 如果p, q相连就返回true
    */
    public boolean isConnected(int p, int q) {
        return rootId(p) == rootId(q);
    }

    /**************************************
     * 比quickUnion更好的实现(WQUPC)       *
     **************************************/


    /**
    * A better rootId Method. Decrease the Depth of the tree when get the rootId.
    * That called Path Compression.(路径压缩)
    * Also O(log N) but better.
    * @param index
    *           要找到其rootId的位的index
    * @return index指向的根位的id
    */
    public int compressedRootId(int index) {
        while(index != id[index]) {
            //This addition line to set the value of indexfrom its parent to its grandparent.
            //This will Decrease the Depth to Depth - 1
            id[index] = id[id[index]];
            index = id[index];

            //for Ex 1.5.16
            cost += 3;
        }

        return index;
    }

    /**
    * 判断p, q是否相连
    * still O(log N) but better than isConnectedFast.
    * @param p 
    *           判断p位和q位是否相连(即他们的rootId是否一样)
    * @param q
    *           判断p位和q位是否相连(即他们的rootId是否一样)
    * @return 如果p, q相连就返回true
    */
    public boolean isConnectedFaster(int p, int q) {
        return compressedRootId(p) == compressedRootId(q);
    }

    /**
    * To Avoid create a Tall tree. We ues Weighted Tree to make sure A Smaller Tree is always linked 
    * to the bottom of the Taller Tree.
    * Connect the root of smaller tree to the root of the larger tree. Let Tree Shorted and wider. 
    * Improve the efficiency of union method.
    * O(log N) But Much less than the O(log N) of quickUnion Method. Because the Depth is more less.
    * @param p 
    *         p与q相连
    * @param q
    *         p与q相连
    */
    public void quickUnionWeighted(int p, int q) {
        //for Ex 1.5.16
        cost = 0;
        
        int pRootId = compressedRootId(p);
        int qRootId = compressedRootId(q);

        if(pRootId == qRootId)
            return;
        if(size[pRootId] < size[qRootId]) {
            id[pRootId] = qRootId;
            size[qRootId] += size[pRootId];

            //for Ex 1.5.16
            cost += 2;
        } else {
            id[qRootId] = pRootId;
            size[pRootId] += size[qRootId];

            //for Ex 1.5.16
            cost += 2;
        }

        count--;


        //for Ex 1.5.16
        total += cost;
    }

    /**
    * For Ex 1.5.16
    * getCost
    * @return cost
    */
    public int getCost() {
        return cost;
    }

    /**
    * For Ex 1.5.16
    * getTotal
    * @return total
    */
    public int getTotal() {
        return total;
    }

    /**
    * Driver for UnionFind
    * @param args 
    *           command-line arguments
    */
    public static void main(String[] args) {
        final int SIZE = 1_000_000;

        UnionFind demo = new UnionFind(SIZE);
        Random rand = new Random();

        for(int i = 0; i < SIZE / 2; i++)
            demo.quickUnionWeighted(rand.nextInt(SIZE),rand.nextInt(SIZE));
        demo.quickUnionWeighted(787_897, 7_777);

        System.out.println("555888 and 9729 : " + demo.isConnectedFaster(555888, 9729));
        System.out.println("3 and 6 : " + demo.isConnectedFaster(3, 6));
        System.out.println("3 and 3 : " + demo.isConnectedFaster(3, 3));
        System.out.println("787897 and 7777: " + demo.isConnectedFaster(787_897,7_777));

        //SO , the MOST Efficiency Method is WQUPC (Weighted Quick Union with Path Compression)
        //Any sequence of M union-find ops(abbr. operations) on N objects makes <= c( M + lg* N ) array access
        //P.S. lg* N is iterate logarithm base 2, it means the number of time to make the lg of N to 1.
        //e.g. lg* (2^65535) = 5) What a AMAZING function!
        //But it is impossible to make the Complexity(时间复杂度) to O(N) (i.e.linear线性) as it was be proved by theory.

        //Applications:
        //1. Probability(概率) Of A Percolation(渗滤) Model
        //   Monte Carlo simulation(蒙特卡洛仿真) to Test
        // if any bottom sites is connected to any top sites in a N-by-N grid(表格).


        /**
         * Interview Question 1 :
         * Social network connectivity.
         * Given a social network containing n members and a log file containing m timestamps at which
         * times pairs of members formed friendships, design an algorithm to determine the earliest time
         * at which all members are connected (i.e., every member is a friend of a friend of a friend ...
         * of a friend). Assume that the log file is sorted by timestamp and that friendship is an
         * equivalence relation. The running time of your algorithm should be mlogn or better and use
         * extra space proportional to n.
         *
         * Note: these interview questions are ungraded and purely for your own enrichment. To get a hint,
         * submit a solution.
         *
         *
         * My Answer:
         * Create a int TreeCount to store the tree counts.And its initialization value is n,i.e.All the
         * member of id[n] is a single tree.In each union operation,check if the pair of elements is connected
         * and if their are not connected then TreeCount minus 1.At the last of union operation,judge that if
         * the value of TreeCount is 1,and the earliest time when all members are connected is the time when
         * it became 1.
         */

        /**
         * Interview Question 2 :
         * Union-find with specific canonical element. Add a method find() to the union-find data type so
         * that find(i) returns the largest element in the connected component containing i. The operations,
         * union(), connected(), and find() should all take logarithmic time or better.
         *
         * For example, if one of the connected components is {1,2,6,9}, then the find() method should return
         * 9 for each of the four elements in the connected components.
         *
         *
         * My Answer:
         * Create a array maxElement[n] with 1 as every array element's initialization value to store each
         * connectivity component's max element.The each element of array reflect to the id[n]'s element.
         * We will update the maxElement when we use union operation. The operation's detail is compare the
         * two maxElements reflect to the pair elements' rootId, and update all the two maxElements to the
         * sum of the original values. To implement find() method, we can simply return maxElement[root(i)].
         */

        /**
         *
         * Interview Question 3 :
         *
         * Successor with delete. Given a set of N integers S={0,1,...,n−1} and a sequence of requests of
         * the following form:
         *
         * Remove x from S
         * Find the successor of x: the smallest y in S such that y≥x.
         *
         * design a data type so that all operations (except construction) take logarithmic time or better
         * in the worst case.
         *
         *
         * My Answer :
         * The challenge of this question is how to relate the solution to Union-Find approach at the first
         * place without any hints about Union-Find.
         * Use a Non-Weighted Quick-Union with Path Compression. Remove(int x) approach is union(x,root(x)),
         * and successor(int x) is return root(x + 1). So the remain of numbers is all the connectivity
         * components' root element.
         */
    }
}///:~