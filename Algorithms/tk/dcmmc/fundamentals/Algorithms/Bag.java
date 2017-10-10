package tk.dcmmc.fundamentals.Algorithms;


import java.util.Iterator;

/**
 * class comment : Generic Type Bag
 * 使用SLL(Single Linked List)实现的Bag.
 * Bag: 一种只能添加元素, 不能删除元素, 可以用foreach遍历的一种ADT.
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
public class Bag<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前Bag中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node first;



    /**************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    public Bag() {
        
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
     * 用于遍历这个Bag
     */
    private class BagIterator implements Iterator<Item> {
        private Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return Bag中上个被遍历的元素后面还有元素就返回true
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * 继续遍历Bag后面的所有元素
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
    * 向Bag中添加新的元素
    * @param item 新元素
    */
    public void add(Item item) {
        //如果LinkedList里面还没有任何元素
    	if (first == null) {
            first = new Node(item);
        } else {
            Node tmpFirst = new Node(item);
            tmpFirst.next = first;
            first = tmpFirst;
            size++;
        }
    }

 
    /**
    * 获得当前Bag存储了多少个元素
    * @return 当前Bag存储的多少个元素
    */
    public int getSize() {
        return size;
    }

    /**
    * 判断Bag是否是空的
    * @return 判断Bag是否是空的
    */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
    * 判断Bag是否已满, SLL的实现方案直接都返回true
    * @return 判断Bag是否已满
    */
    public boolean isFull() {
    	return false;
    }



    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        return this.new BagIterator();
    }

    /**
     * Test Client.
     * @param args
     *			command-line arguments.
     */
    public static void main(String[] args) {
        //foreach遍历测试
        System.out.println("foreach遍历测试");

        Bag<Integer> bag = new Bag<>();
        bag.add(5);
        bag.add(2);
        bag.add(1);
        bag.add(7);

        //遍历
        for (int i : bag) {
            System.out.println(i);
        }

    }
}///:~

