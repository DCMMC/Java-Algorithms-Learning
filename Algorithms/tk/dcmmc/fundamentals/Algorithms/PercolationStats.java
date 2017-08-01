import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * class comment :
 * Created by DCMMC on 2017/4/8.
 */
public class PercolationStats {
    private double[] results;
    private double mean;
    private double stddev;

    /**
    * perform trials independent experiments on an n-by-n grid
    * @param n
    *          grid的大小
    * @param trials
    *          尝试trials次试验
    */
    public PercolationStats(int n, int trials) {
        //检验参数合法性
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("参数不能小于等于0!");

        results = new double[trials];

        for(int i = 0;i < trials;i++) {
            Percolation percolationTest = new Percolation(n);

            int rndRow, rndCol;
            while (!percolationTest.percolates()) {
                if(percolationTest.isOpen(rndRow = (StdRandom.uniform(n) + 1), rndCol = (StdRandom.uniform(n) + 1)))
                    continue;
                else
                    percolationTest.open(rndRow, rndCol);
            }
            results[i] = (percolationTest.numberOfOpenSites() * 1.0) / (n * n);
        }

        mean = StdStats.mean(results);
        stddev = StdStats.stddev(results);
    }

    // sample mean of percolation threshold
    public double mean() {
        return this.mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return this.stddev;
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - (1.96 * stddev / Math.sqrt(results.length));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + (1.96 * stddev / Math.sqrt(results.length));
    }

    /**
    * test client (described below)
    * @param args 
    *           commandline arguments
    */
    public static void main(String[] args) {
        Stopwatch watch = new Stopwatch();
        //从commanline读取n和t
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        //Debug
        //int n = 800;
        //int t = 100;

        PercolationStats ps = new PercolationStats(n, t);

        System.out.println("mean                    = " + String.format("%-20.19f", ps.mean()) + "\n"
                         + "stddev                  = " + String.format("%-20.19f", ps.stddev()) + "\n"
                         + "95% confidence interval = [" + String.format("%-20.19f, %-20.19f", ps.confidenceLo(), ps.confidenceHi())+"]\n");
        
        System.out.println("Total time : " + watch.elapsedTime() + " seconds.");
    }
}
