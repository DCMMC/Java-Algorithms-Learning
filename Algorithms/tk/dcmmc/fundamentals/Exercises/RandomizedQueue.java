import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdRandom;

/**
* Programming Assignment 2: Deques
* @author DCMMC
* @since 1.5
*/
public class RandomizedQueue<Item> implements Iterable<Item> {
	/**************************************
     * Fields                             *
     **************************************/
    //当前List中的元素个数
    private int size = 0;

    //头节点也就是最早加入的节点, 初始为null
    private Node last;

	/**************************************
     * Constructors                       *
     **************************************/
    /**
    * 默认构造器
	* construct an empty deque
	*/
	public RandomizedQueue() {

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
    	private RandomizedQueue<Item> randQueue = new RandomizedQueue<>();
    	private Node current = null;

    	ReverseArrayIterator() {
    		//generate random array storing the indexs of randomed queue elements.
    		int[] indexs = new int[size];

    		for (int i = 0; i < size; i++)
    			indexs[i] = i;

    		StdRandom.shuffle(indexs);

    		current = last;

    		Item[] items = (Item[])new Object[size];
    		int index = 0;

    		while (current != null) {
    			items[index++] = current.item;
    			current = current.previous;
    		}

    		for (int i : indexs)
    			randQueue.enqueue(items[i]);

    		current = randQueue.last;
    	}


        /**
         * 返回当前遍历是否还有下一个元素
         * @return List中上个被遍历的元素后面还有元素就返回true
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
                current = current.previous;
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
	* is the queue empty?
	*/
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	* return the number of items on the queue
	*/
    public int size() {
    	return size;
    } 

    /**
    * add the item
    * @param item 新元素
    * @throws  IllegalArgumentException
	* if the client attempts to add a null item
    */
    public void enqueue(Item item) {
    	if (item == null)
			throw new IllegalArgumentException("item can not be null!");

		//如果queue里面还没有任何元素
        if (last == null) {
            last = new Node(item);
            size++;
        } else {
            Node tmp = new Node(item);
            tmp.previous = last;
            last = tmp;
            size++;
        }
    }

    /**
    * remove and return a random item
    */
    public Item dequeue() {
    	if (size() == 0)
    		throw new NoSuchElementException("");

    	//generate the random index that will be dequeued.
    	int outIndex = StdRandom.uniform(size);

    	Node tmp = last;
    	Node tmpNext = null;

    	for (int i = 0; i < outIndex; i++) {
    		tmpNext = tmp;
    		tmp = tmp.previous;
    	}

    	Item item = tmp.item;

    	if (tmp.previous != null && tmpNext != null)
    		tmpNext.previous = tmp.previous;
    	else if (tmp.previous == null && tmpNext != null)
    		tmpNext.previous = null;
    	else if (tmp.previous != null && tmpNext == null)
    		last = tmp.previous;

    	size--;

    	return item;
    }

    /**
    * return (but do not remove) a random item
    */
    public Item sample() {
    	if (size() == 0)
    		throw new NoSuchElementException("");

    	//generate the random index that will be dequeued.
    	int outIndex = StdRandom.uniform(size);

    	Node tmp = last;
    	Node tmpNext = null;

    	for (int i = 0; i < outIndex; i++) {
    		tmpNext = tmp;
    		tmp = tmp.previous;
    	}

    	return tmp.item;
    }

    /**
    * return an independent iterator over items in random order
    */
    public Iterator<Item> iterator() {
    	return this.new ReverseArrayIterator();
    }

    /**
    * unit testing (optional)
    */
    public static void main(String[] args) {
    	RandomizedQueue<Integer> demo = new RandomizedQueue<>();

		demo.enqueue(1);
		demo.enqueue(2);
		demo.enqueue(5);
		demo.enqueue(6);

		for (int i : demo) {
			System.out.print(" " + i);
		}
    }
}///~