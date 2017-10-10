package tk.dcmmc.fundamentals.Algorithms;

/**用数学归纳法证明欧几里德算法能够求出两个非负数的最大公约数
* 首先证明引理 gcd(a, b) = gcd(b, a mod b)
* 假设 a = bq + r, 且a, b的最大公约数是d
* 1) d|a, d|b, 所以d|(a - bq), 即d|r, 所以d也是r的约数, 所以d是b, r的最大公约数.
* 2) 假设d是b和r的最大公约数, 即d|b, d|r, 所以d|(bq + r), 即d|a, 所以d是a, b的最大公约数.
* 证毕
* 
* 由上面的引理不难递归出 gcd(a, b) = gcd(b, r1) = gcd(r1, r2) = ... = gcd(rn-1, rn) = gcd(rn, 0);
* 且b > r1 > r2 > ... > rn > 0 = rn+1, 以及rn-1能够被rn整除, 所以gcd(a, b) = gcd(rn-1, rn) = rn;
*/


/**
* 欧几里德算法(aka 辗转相除法)
* Description: 求两个非零整数的最大公约数
* @Author DCMMC
* Created by DCMMC on 2017/7/20
*/
public class EuclidGCD {
	/**
	* Ex 1.1.25 
	* 欧几里德辗转相除求最大公约数
	* @param p nonzero int num.
	* @param q nonzero int num. 且|p| >= |q|
	* @return gcd of p and q.
	* @throws IllegalArgumentException p, q非0.
	*/
	public static int gcd(int p, int q) throws IllegalArgumentException {
		//取绝对值
		p = Math.abs(p);
		q = Math.abs(q);

		if (p == 0 || q == 0)
			throw new IllegalArgumentException("要求: p, q非0\n但是, 参数p = " + p + ", q = " + q);

		//必须是|p| >= |q|, 否则交换
		if (p < q) {
			int tmp = p;
			p = q;
			q = tmp;
		}


		if(p % q == 0)
			return q;
		else 
			return gcd(q, p % q);
	}

	/**
	* gcd()的测试程序
	* @param args command-line args
	*/
	public static void main(String[] args) {
		final int p = 364, q = 250;

		System.out.println(p + "和" + q + "的最大公约数是" + gcd(p, q));
	}
}///~