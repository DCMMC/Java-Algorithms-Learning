package tk.dcmmc.fundamentals.Algorithms;

import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;

/**
 * class comment : Generic Type Double Linked List(DLLists)
 * @author DCMMC
 * Created by DCMMC on 2017/7/24.
 */
public class DoubleLinkedList<Item> implements Iterable<Item> {
    /**************************************
     * Fields                             *
     **************************************/
    //当前List中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node first;

    //尾节点也就是最晚加入的节点, 初始化为null
    private Node last;

    //为ReverseArrayIterator迭代器记录对List的操作次数, 防止在迭代的过程中List被更改
    private int opsCnt;



    /**************************************
     * Constructors                       *
     **************************************/
    /**
     * 默认构造器
     */
    public DoubleLinkedList() {

    }

    /**
     * 从数组创建LinkedList
     * 基本类型数组并不能向上转型为Item[](Object[]), 不过(基本类型)数组都可以向上转型为Object, 然后再利用
     * java.lang.reflect.Array类获取index和get元素. 在强制向下转型为Item
     * @param array
     *           Item数组
     */
    @SuppressWarnings("unchecked")
    public DoubleLinkedList(Object array) {
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++)
            addLast( (Item)Array.get(array, i));
    }

    /**
     * 从可变参数中创建LinkedList
     * @param arrayElements
     *           可变长参数
     */
    @SuppressWarnings("unchecked")
    public DoubleLinkedList(Item... arrayElements) {
        for (Item i : arrayElements)
            addLast(i);
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

        //上一个节点
        Node previous;

        //构造器
        Node(Item item) {
            this.item = item;
        }
    }

    /**
     * 成员内部类
     * 用于遍历这个DoubleLinkedList
     */
    private class ReverseArrayIterator implements Iterator<Item> {
        //为当前opsCnt创建副本
        private final int opsCntCopy = opsCnt;

        private Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return List中上个被遍历的元素后面还有元素就返回true
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public boolean hasNext() throws ConcurrentModificationException {
            if (opsCntCopy != opsCnt)
                throw new ConcurrentModificationException();

            return current != null;
        }

        /**
         * 继续遍历List后面的所有元素
         * @return 下一个元素的值
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public Item next() throws ConcurrentModificationException {
            if (opsCntCopy != opsCnt)
                throw new ConcurrentModificationException();

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
     * Methods                            *
     **************************************/

    /**
    * 返回由DoubleLinkedList表示的数组(FIFO)
    */
    @SuppressWarnings("unchecked")
    public Item[] toArray() {
        if (first.item == null)
            return null;

        Item[] array = (Item[])java.lang.reflect.Array.newInstance(first.item.getClass(), getSize());
        //不能像下面这样做, 因为实质性的array还是Object[], 而上面的虽然转换成了Object(Item[]), 但是通过RTTI可以知道其是一个Item所属的
        //引用类型的数组
        //Item[] array = (Item[])new Object[getSize()];

        ReverseArrayIterator iter = new ReverseArrayIterator();

        int index = 0;

        while (iter.hasNext())
            array[index++] = iter.next();

        return array;
    }

    /**
     * 从DoubleLinkedList中的前端添加新的元素(模拟LIFO)
     * @param item 新元素
     */
    public void addFirst(Item item) {
        //如果LinkedList里面还没有任何元素
        if (first == null) {
            last = first = new Node(item);
            size++;
            opsCnt++;
        } else {
            Node tmpFirst = new Node(item);
            tmpFirst.next = this.first;
            this.first.previous = tmpFirst;
            first = tmpFirst;
            size++;
            opsCnt++;
        }
    }

    /**
     * 从DoubleLinkedList中的后端添加新的元素(模拟FIFO)
     * @param item 新元素
     */
    public void addLast(Item item) {
        //如果LinkedList里面还没有任何元素
        if (last == null) {
            last = first = new Node(item);
            size++;
            opsCnt++;
        } else {
            Node tmpLast = new Node(item);
            last.next = tmpLast;
            tmpLast.previous = this.last;
            last = tmpLast;
            size++;
            opsCnt++;
        }
    }

    /**
     * 从给定的offset后面插入指定的值
     * @param offset
     *           在offset后插入新的节点
     * @param item
     *           新的这个节点中Item的值
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public void add(int offset, Item item) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                //找到该offset所在的Node
                Node newNode = new Node(item);
                newNode.previous = current;
                newNode.next = current.next;
                current.next = newNode;
                size++;
                opsCnt++;
            }

            //继续向后遍历
            current = current.next;
        }

        //那就可能是last那个Node
        if (index == offset) {
            //找到该offset所在的Node
            Node newNode = new Node(item);
            newNode.previous = current;
            newNode.next = current.next;
            current.next = newNode;
            last = newNode;
            size++;
            opsCnt++;
        }

    }

    /**
     * 从任意的offset中获取item, 并把这个item所在的Node从List中删除
     * @param offset
     *           要获取的元素的offset, 0 <= offset <= getSize() - 1
     * @return 要获取的元素
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public Item pop(int offset) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                //如果获取到该offset所在的Node
                //如果是first
                if (current.previous == null) {
                    //如果List只有一个元素
                    if (first.next == null)
                        first = last = null;
                    else
                        first = first.next;
                } else {
                    current.previous.next = current.next;
                }
                size--;
                opsCnt++;
                return current.item;
            }

            //继续向后遍历
            current = current.next;
        }


        //如果是last
        if (index == offset) {
            //如果只有一个元素
            if (getSize() == 1) {
                first = last = null;
            } else {
                last = last.previous;
                last.next = null;
            }
            size--;
            opsCnt++;
            return current.item;
        }

        return null;
    }


    /**
     * 返回List前端的元素, 并把该元素从List中删除.(模拟LIFO)
     * @throws NoSuchElementException
     * if the client attempts to remove an item from an empty list
     * @return List前端第一个元素
     */
    public Item popFirst() {
        if (getSize() == 0)
            throw new NoSuchElementException("This Deque is empty!");

        return pop(0);
    }

    /**
     * 返回List后端的元素, 并把该元素从List中删除.(模拟FIFO)
    * @throws NoSuchElementException
    * if the client attempts to remove an item from an empty list
     * @return List后端最后一个元素
     */
    @SuppressWarnings("unchecked")
    public Item popLast() {
        if (getSize() == 0)
            throw new NoSuchElementException("This Deque is empty!");

        Item lastItem = last.item;

        //如果只有一个元素
        if (getSize() == 1) {
            first = last = null;
        } else {
            last = last.previous;
            last.next = null;
        }
        size--;
        opsCnt++;
        return lastItem;
    }

    /**
     * 用List中删除指定offset的元素
     * @offset   要删除的元素的序号
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public void remove(int offset) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        pop(offset);
    }


    /**
     * 返回List后端的元素, 并且不会删除这个元素
     * @return List前端第一个元素
     */
    public Item getFirst() {
        //如果List为空就返回null
        if (first == null)
            return null;

        return first.item;
    }

    /**
     * 返回List后端的元素, 并且不会删除这个元素
     * @return List后端最后一个元素
     */
    public Item getLast() {
        //如果List为空就返回null
        if (last == null)
            return null;

        return last.item;
    }

    /**
     * 从任意的offset中获取item, 并且不会删除这个元素
     * @param offset
     *           要获取的元素的offset, 0 <= offset <= getSize() - 1
     * @return 要获取的元素
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public Item get(int offset) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset)
                return current.item;

            //继续向后遍历
            current = current.next;
        }

        //如果是last
        if (index == offset)
            return current.item;

        return null;
    }


    /**
     * 将指定offset的元素中的内容更换
     * @param offset
     *           元素的序列
     * @param item
     *           该Node上的新item
     * @return 该Node上的旧值
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    public Item modify(int offset, Item item) throws IndexOutOfBoundsException {
        outOfBoundsCheck(offset);

        int index = 0;
        Node current = first;
        while (current.next != null) {
            if (index++ == offset) {
                Item oldItem = current.item;
                current.item = item;
                return oldItem;
            }


            //继续向后遍历
            current = current.next;
        }

        //可能是last
        if (index == offset) {
            Item oldItem = current.item;
            current.item = item;
            return oldItem;
        }

        return null;
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
     * 更加易读的toString信息
     * @return 关于该List中所有元素的打印
     */
    @Override
    public String toString() {
        if (getSize() == 0)
            return "没有任何元素.";
        
        Iterator<Item> itr = iterator();

        String info = "[ ";

        while (itr.hasNext()) {
            info += (itr.next() + ", ");
        }

        info = info.substring(0, info.length() - 2);

        info += ("] (总共有" + getSize() + "个"
                + first.item.getClass().toString().replaceFirst("class ", "")
                + "型元素.)");

        return info;
    }


    /**
     * 检查offset是否合法
     * @param offset
     *           要检查的offset
     * @throws IndexOutOfBoundsException 如果offset不存在就抛出异常
     */
    private void outOfBoundsCheck(int offset) throws IndexOutOfBoundsException {
        if ( offset < 0 || offset >= getSize() )
            throw new IndexOutOfBoundsException("序号" + offset
                    + "在本List中不存在, 请输入0 ~ " + getSize() + "的数");
    }

    /**************************************
     * 我的一些方法和client测试方法         *
     **************************************/

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * @param obj 要输出的String.
     * @throws IllegalArgumentException 参数不能为空
     */
    private static void o(Object obj) throws IllegalArgumentException {
        if (obj == null)
            throw new IllegalArgumentException("参数不能为空!");

        System.out.println(obj);
    }

    /**
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * 重载的一个版本, 不接受任何参数, 就是为了输出一个回车.
     */
    private static void o() {
        System.out.println();
    }



    /**
     * Test Client.
     * @param args
     *          command-line arguments.
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        DoubleLinkedList<Integer> dllist = new DoubleLinkedList<>();

        //一系列操作, 操作完的结果应该是:
        dllist.addFirst(5);
        dllist.addFirst(6);
        dllist.addLast(2);
        dllist.addLast(7);
        dllist.add(3, 6);
        dllist.add(0, 8);
        dllist.add(dllist.getSize() - 1, 9);


        o("getFirst(): " + dllist.getFirst() + ", getLast(): " + dllist.getLast()
                + ", get(2): " + dllist.get(2));

        //Pop Test
        dllist.popFirst();
        dllist.popLast();
        dllist.pop(2);

        //Test Modify
        dllist.modify(1, 7);

        //Test toString and Iterator, 结果应该是8, 7, 7, 6
        o(dllist);

        //array to Linked List
        //Primitive type array
        int[] array = {1, 3, 5, 7};
        o(new DoubleLinkedList<Integer>(array));

        //test toArray
        o("test toArray()");
        for (Integer i : new DoubleLinkedList<Integer>(array).toArray())
            System.out.print(" " + i);
        o();

        //Reference type array
        Integer[] integerArray = {1, 3, 5, 7};
        //为了抑制java对于数组造成的潜在的varargs参数混淆的警告, 先强制转化为Object而不是Object[]
        o(new DoubleLinkedList((Object)integerArray));

        //varargs test
        o(new DoubleLinkedList(1, 5, 9, 11));

    }
}///:~

