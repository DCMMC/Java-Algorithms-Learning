package tk.dcmmc.fundamentals.Algorithms;


import java.util.Iterator;

/**
 * class comment : Generic Type Queue
 * 使用SLL(Single Linked List)实现的Queue(FIFO).
 * 总的效率: LinkedList = fixed-capacity array > resizing-capacity array
 * 扩展性: LinkedList > resizing-capacity array > fixed-capacity
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
public class Queue<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前Queue中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node first;

    //尾节点也就是最晚加入的节点, 初始化为null
    private Node last;



    /**************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    public Queue() {
        
    }

    /**************************************
     * Inner Class                        *
     **************************************/
    /**
    * Linked List节点
    */
    private class Node {
        //节点中保存的元素, 初始化为null
        Item item;

        //下一个节点, 初始化为null
        Node next;

        //构造器
        Node(Item item) {
            this.item = item;
        }
    }

    /**
     * 成员内部类
     * 用于遍历这个Queue
     */
    private class ReverseArrayIterator implements Iterator<Item> {
        private Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return Queue中上个被遍历的元素后面还有元素就返回true
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * 继续遍历Queue后面的所有元素
         * @return 下一个元素的值
         */
        @Override
        public Item next() {
            if (hasNext()) {
                Item item = current.item;
                current = current.next;
                return item;
            }
            else
                return null;
        }

    }

    /**************************************
     * Methods     		                  *
     **************************************/
    /**
    * 向Queue中添加新的元素
    * @param item 新元素
    */
    public void enqueue(Item item) {
        //如果LinkedList里面还没有任何元素
    	if (last == null) {
            last = first = new Node(item);
        } else {
            Node tmpLast = new Node(item);
            this.last.next = tmpLast;
            this.last = tmpLast;
            size++;
        }
    }

    /**
    * 返回第一个添加到Queue的元素, 并从Queue从删除这个元素
    * @return 第一个添加到Queue的元素
    */
    public Item dequeue() {
        //如果Queue为空就返回null
    	if (first == null)
            return null;

        //更新first
        Item item = first.item;
        this.first = first.next;

        //如果first变成了null(也就是这个Queue最后一个元素都被dequeue出来了), 那么last也要是null
        if (first == null)
            last = null;

        return item;
    }

    /**
    * 获得当前Queue存储了多少个元素
    * @return 当前Queue存储的多少个元素
    */
    public int getSize() {
        return size;
    }

    /**
    * 判断Queue是否是空的
    * @return 判断Queue是否是空的
    */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
    * 判断Queue是否已满, resizing-capacity array的实现方案直接都返回true
    * @return 判断Queue是否已满
    */
    public boolean isFull() {
    	//这是原来fixed-capacity array实现的Queue的方案
        //return getSize() >= MAXSIZE;

        //新的resizing-capacity array实现的Queue, 直接返回false.
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
     * Test Client.
     * @param args
     *			command-line arguments.
     */
    public static void main(String[] args) {
        //foreach遍历测试
        System.out.println("foreach遍历测试");

        Queue<Integer> queue = new Queue<>();
        queue.enqueue(5);
        queue.enqueue(2);
        queue.enqueue(1);
        queue.enqueue(7);

        //遍历
        for (int i : queue) {
            System.out.println(i);
        }

    }
}///:~

