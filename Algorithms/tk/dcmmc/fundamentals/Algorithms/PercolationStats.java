//package com.DCMMC.Algorithms;


//import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * class comment :
 * Created by DCMMC on 2017/4/8.
 */
public class PercolationStats {
    private double[] results;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
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

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(results.length));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(results.length));
    }

    // test client (described below)
    public static void main(String[] args) {
        Stopwatch watch = new Stopwatch();
        Integer n = new Integer(args[0]);
        Integer T = new Integer(args[1]);

        //Integer n = StdIn.readInt();
        //Integer T = StdIn.readInt();

        PercolationStats ps = new PercolationStats(n, T);

        System.out.println("mean                    = "+String.format("%-20.19f", ps.mean()) + "\n"
                         + "stddev                  = "+String.format("%-20.19f", ps.stddev()) + "\n"
                         + "95% confidence interval = ["+String.format("%-20.19f, %-20.19f", ps.confidenceLo(), ps.confidenceHi())+"]\n");
        System.out.println("Total time : " + watch.elapsedTime() + " seconds.");
    }
}
