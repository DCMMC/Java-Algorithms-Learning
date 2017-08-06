import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* Programming Assignment 2: Deques
* @author DCMMC
* @since 1.5
*/
public class Deque<Item> implements Iterable<Item> {
	/**************************************
     * Fields                             *
     **************************************/
    //当前List中的元素个数
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
	* construct an empty deque
	*/
	public Deque() {

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
        private Node current = first;

        /**
         * 返回当前遍历是否还有下一个元素
         * @return List中上个被遍历的元素后面还有元素就返回true
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * 继续遍历List后面的所有元素
         * @return 下一个元素的值
         * @throws ConcurrentModificationException
         *         如果在迭代期间, List被修改, 就抛出异常
         */
        @Override
        public Item next() {
            if (hasNext()) {
                Item item = current.item;
                current = current.next;
                return item;
            } else
                throw new NoSuchElementException(" there are no more items to return.");
        }

        /**
        * 从父类继承到的方法, 在deque中不允许执行, 直接抛出异常
        * @throws UnsupportedOperationException 
        */
        @Override
        public void remove() {
        	throw new UnsupportedOperationException ("remove() cannot be called in this iterator.");
        }

    }

    /**************************************
     * Methods                            *
     **************************************/

	/**
	* is the deque empty?
	* 判断Deque是否是空的
     * @return 判断Stack是否是空的
	*/
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	* return the number of items on the deque
    * @return 当前Deque存储的多少个元素
	*/
	public int size() {
		return size;
	}

	/**
	* add the item to the front
	* @param item 新元素
	* @throws  IllegalArgumentException
	* if the client attempts to add a null item
	*/
	public void addFirst(Item item) {
		if (item == null)
			throw new IllegalArgumentException("item can not be null!");

		//如果LinkedList里面还没有任何元素
        if (first == null) {
            last = first = new Node(item);
            size++;
        } else {
            Node tmpFirst = new Node(item);
            tmpFirst.next = this.first;
            this.first.previous = tmpFirst;
            first = tmpFirst;
            size++;
        }
	}

	/**
	* add the item to the end
	* @param item 新元素
	* @throws  IllegalArgumentException
	* if the client attempts to add a null item
	*/
	public void addLast(Item item) {
		if (item == null)
			throw new IllegalArgumentException("item can not be null!");
		
		//如果LinkedList里面还没有任何元素
        if (last == null) {
            last = first = new Node(item);
            size++;
        } else {
            Node tmpLast = new Node(item);
            last.next = tmpLast;
            tmpLast.previous = this.last;
            last = tmpLast;
            size++;
        }
	}

	/**
	* remove and return the item from the front
	* @throws NoSuchElementException
	* if the client attempts to remove an item from an empty deque
	*/
	public Item removeFirst() {
		if (size() == 0)
			throw new NoSuchElementException("This Deque is empty!");

		Item firstItem = first.item;

		if (size() == 1) {
			first = last = null;
		} else {
			first = first.next;
			first.previous = null;
		}
		size--;
		return firstItem;
	}

	/**
	* remove and return the item from the end
	* @throws NoSuchElementException
	* if the client attempts to remove an item from an empty deque
	* @return List后端最后一个元素
	*/
	public Item removeLast() {
		if (size() == 0)
			throw new NoSuchElementException("This Deque is empty!");

		Item lastItem = last.item;

        //如果只有一个元素
        if (size() == 1) {
            first = last = null;
        } else {
            last = last.previous;
            last.next = null;
        }
        size--;
        return lastItem;
	}

    /**
     * Returns an iterator over elements of type {@code T} in order from front to end
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        return this.new ReverseArrayIterator();
    }

	/**
	* unit testing (optional)
	*/
	public static void main(String[] args) {
		Deque<Integer> demo = new Deque<>();

		demo.addFirst(1);
		demo.addFirst(2);
		demo.addLast(5);
		demo.addLast(6);
		System.out.println(demo.removeFirst());
		System.out.println(demo.removeLast());

		for (int i : demo) {
			System.out.print(i + " ");
		}

		//throw exception
		demo.removeFirst();
		demo.removeFirst();
		//demo.removeFirst();

		System.out.println("\n" + demo.size());
	}
}///~