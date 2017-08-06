import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


/**
* Programming Assignment 2: Deques
* @author DCMMC
* @since 1.5
*/
public class Permutation {
	/**
	*  client program
	*  takes a command-line integer k; reads in a sequence of strings from standard input using StdIn.
	* readString(); and prints exactly k of them, uniformly at random. Print each item from the sequence at most once.
	*/
	public static void main(String[] args) {
		int k = Integer.parseInt(args[0]);

		RandomizedQueue<String> queue = new RandomizedQueue<>();
		
		while (!StdIn.isEmpty()) {
			queue.enqueue(StdIn.readString());
		}

		int cnt = -1;

		for (String s : queue) {
			if (++cnt == k)
				break;

			StdOut.println(s);
		}
	}
}///~