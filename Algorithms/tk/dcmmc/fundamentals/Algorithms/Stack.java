package tk.dcmmc.fundamentals.Algorithms;

import java.util.Iterator;
import java.util.Scanner;

/**
 * class comment : Generic Type Stack(LIFO)
 * 使用(可变长度resizing-capacity)数组实现.
 * 但是更换数组长度的时候会产生时间消耗(不过至少比fixed-capacity array好一点)
 * 一个更好的实现就是LinkedList(SLL/DLL).
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
public class Stack<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前Stack中的元素个数
    private int size = 0;
    //默认最大容量
    private int MAXSIZE = 1000;
    /* reseizing-capacity stack using array implement */
    private Item[] elements;

    /**************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (Item[]) new Object[MAXSIZE];
    }

    /**
     * 重载的构造器, 指定Stack容量
     * @param capacity
     *			Stack的容量
     */
    @SuppressWarnings("unchecked")
    public Stack(int capacity) {
        elements = (Item[]) new Object[capacity];
        this.MAXSIZE = capacity;
    }

    /**************************************
     * Inner Class                        *
     **************************************/
    /**
     * 成员内部类
     * 用于遍历这个Stack
     */
    private class ReverseArrayIterator implements Iterator<Item> {
    	//用于遍历的时候存储当前遍历的序列, 还没有遍历过的时候默认值为-1
    	private int iterateoffset = -1;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return Stack中上个被遍历的元素下面还有元素就返回true
         */
        @Override
        public boolean hasNext() {
            //第一次调用, 把iterateOffset设为当前Stack中的元素数量
            if (iterateoffset == -1) {
                iterateoffset = size;
            }

            return iterateoffset > 0;
        }

        /**
         * 遍历iterateOffset下面的所有元素
         * @return 下一个元素的值
         */
        @Override
        public Item next() {
            if (hasNext())
                return elements[--iterateoffset];
            else
                return null;
        }

    }

    /**************************************
     * Methods     		                  *
     **************************************/
    /**
    * 向Stack中添加新的元素
    * @param item 新元素
    */
    public void push(Item item) {
    	//如果Stack小于MAXSIZE * 0.8, 就正常添加, 否则就resize到MAXSIZE * 2.
        //原书是到了MAXSIZE才加倍到MAXSIZE * 2
        if(getSize() >= MAXSIZE * 4 / 5)
        	resize(MAXSIZE * 2);

        elements[size++] = item;
    }

    /**
    * 从Stack中取出最后一个添加到Stack的元素, 并把这个元素从Stack中删除, 这里会把元素强制向下转型
    * @return 最后一个添加到Stack的元素
    */
    @SuppressWarnings("unchecked")
    public Item pop() {
    	//如果size等于Stack的1/4就resize到MAXSIZE / 2
        if (isEmpty())
            return null;
        else if (size > 0 && size == MAXSIZE / 4)
        	resize(MAXSIZE / 2);

        Object tmp = elements[size - 1];
        elements[--size] = null;
        return (Item)tmp;
    }

    /**
    * 从Stack中取出最后一个添加到Stack的元素, 并且不会把这个元素从Stack中删除, 这里会把元素强制向下转型
    * @return 最后一个添加到Stack的元素
    */
    @SuppressWarnings("unchecked")
    public Item peek() {
        //如果size等于Stack的1/4就resize到MAXSIZE / 2
        if (isEmpty())
            return null;
        else if (size > 0 && size == MAXSIZE / 4)
            resize(MAXSIZE / 2);

        return (Item)elements[size - 1];
    }

    /**
    * 获得当前Stack存储了多少个元素
    * @return 当前Stack存储的多少个元素
    */
    public int getSize() {
        return size;
    }

    /**
    * 判断Stack是否是空的
    * @return 判断Stack是否是空的
    */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
    * 判断Stack是否已满, resizing-capacity array的实现方案直接都返回true
    * @return 判断Stack是否已满
    */
    public boolean isFull() {
    	//这是原来fixed-capacity array实现的stack的方案
        //return getSize() >= MAXSIZE;

        //新的resizing-capacity array实现的stack, 直接返回false.
    	return false;
    }



    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        return this.new ReverseArrayIterator();
    }

    /**
    * 更新array的大小, 使用原子操作, 防止在更换的时候, stack出现异常
    * @param size 新的array的大小
    * @return 原来stack的MAXSIZE
    */
    @SuppressWarnings("unchecked")
    private int resize(int newCapacity) {
    	//注意: 这里不会检查size是否大于newCapacity
    	synchronized (elements) {
    		Item[] newElements = (Item[]) new Object[newCapacity];
    		int cnt = 0;
    		for (Item i : elements)
    			newElements[cnt++] = i;

    		//把elements交接到新的array对象
    		elements = newElements;

    		//更新MAXSIZE大小
    		int oldMAXSIZE = this.MAXSIZE;
    		this.MAXSIZE = newCapacity;
    		return oldMAXSIZE;
    	}
    }

    /**
     * Test Client.
     * @param args
     *			command-line arguments.
     */
    public static void main(String[] args) {
        //foreach遍历测试
        System.out.println("foreach遍历测试");
        Stack<Integer> stack = new Stack<>(4);
        stack.push(5);
        stack.push(2);
        stack.push(1);
        stack.push(7);

        //遍历
        for (int i : stack) {
            System.out.println(i);
        }


        /* Dijkstra双栈算术表达式求值算法 */
        System.out.println("输入一行算术表达式, 空行回车或者Ctrl+Z结束本程序.");

        Stack<String> ops = new Stack<>();
        Stack<Double> vals = new Stack<>();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            //nextLine会读取回车, 但是回车的内容不会成为返回的字符串的一部分.
            String line = sc.nextLine();

            if (line.equals("")) {
                System.out.println("End of Reading.");
                return;
            } else {
                //从这一行中读取
                Scanner scInLine = new Scanner(line);

		        /* 算术表达式示例: ( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) ) */
                while (scInLine.hasNext()) {
                    //读取字符串
                    String str = scInLine.next();

                    switch (str) {
                        //忽略左括号
                        case "(" 	:  break;
                        case "+" 	:
                        case "-" 	:
                        case "*" 	:
                        case "/" 	:
                        case "sqrt" : ops.push(str);
                            break;
                        //如果是右括号的话, 就执行操作
                        case ")" 	:
                            String operator = ops.pop();
                            Double val = vals.pop();

                            //计算并将结果压回Stack
                            switch (operator) {
                                case "+" : vals.push(vals.pop() + val);
                                    break;
                                case "-" : vals.push(vals.pop() - val);
                                    break;
                                case "*" : vals.push(vals.pop() * val);
                                    break;
                                case "/" : vals.push(vals.pop() / val);
                                    break;
                                case "sqrt" : vals.push(Math.sqrt(val));
                                    break;
                            }
                            break;
                        //如果既不是运算符也不是括号, 就是操作数了
                        default : vals.push(Double.parseDouble(str));
                    }
                   

                }

                //输出结果
                System.out.println("上述算术表达式的值为" + vals.pop());
                System.out.println("输入一行算术表达式, 空行回车或者Ctrl+Z结束本程序.");
            }
        }

    }
}///:~

