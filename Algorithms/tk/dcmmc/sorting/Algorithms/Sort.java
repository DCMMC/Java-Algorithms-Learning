package tk.dcmmc.sorting.Algorithms;

/**
* 基类, 扩展出所有的Sort子类
*/
class Sort {
	/**
	* 判断v是否小于w
	* @param v
	*			要比较的两个对象中较小的那一个
	* @param w
	*			要比较的两个对象中较大的那一个
	* @return 如果v由其中的compareTo实现得出小于w, 就返回true
	*/
	@SuppressWarnings("unchecked")
	protected static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;
	}

	/**
	* 交换数组a中i, j对应的index的元素的值, 这里不做i, j合法性检验
	* @param a
	*			目标数组
	* @param i
	*			要交换的元素的index
	* @param j
	*			要交换的元素的index
	*/
	protected static void exch(Comparable[] a, int i, int j) {
		Comparable tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

}