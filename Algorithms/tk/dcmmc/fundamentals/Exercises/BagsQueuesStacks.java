package tk.dcmmc.fundamentals.Exercises;

import tk.dcmmc.fundamentals.Algorithms.Stack;
import tk.dcmmc.fundamentals.Algorithms.Queue;
import tk.dcmmc.fundamentals.Algorithms.DoubleLinkedList;
import java.util.Scanner;
import java.util.LinkedHashMap;

/**
 * 本文件包括Exercise 1.3中部分习题的解答
 * Created by DCMMC on 2017/7/24
 * Finished on 2017/7/
 * @author DCMMC
 * @since 1.5
 */
public class BagsQueuesStacks {
    /**
     * 用于Ex 1.3.30 和 Ex 1.3.37的嵌套类
     */
    private static class Node<Item> {
        Item item;
        Node<Item> next;
        Node (Item item) {
            this.item = item;
        }
    }

    /**************************************
     * 习题要用到的方法                     *
     **************************************/
    /**
     * Ex 1.3.9
     * 为一个缺少所有左括号的算术表达式添加左括号
     * @param expression
     *           要添加左括号的表达式
     * @return result
     */
    private static String addLeftParentheses(String expression) {
        Scanner sc = new Scanner(expression);

        //记录装有输入解析出来的所有token的Stack
        DoubleLinkedList<String> tokens = new DoubleLinkedList<>();

        //暂存输入的单个token
        String inputToken;

        //暂时只处理小括号
        String rightParentheses = ")";
        String leftParentheses = "(";

        //先输入所有的tokens
        while (sc.hasNext() && (inputToken = sc.next()) != null) {
            tokens.addLast(inputToken);
            if (inputToken.equals(rightParentheses)) {
                //添加一个"("

                //如果解析到了")", 就开始在tokens中开始查找"("
                //暂存弹出来的这些tokens
                Stack<String> tmpStoreTokens = new Stack<>();

                //当前token
                String tmpToken;

                //记录需要查找的算子个数
                int elementCnt = 0;

                while ( (tmpToken = tokens.popLast()) != null) {
                    if (tmpToken.equals(rightParentheses)) {
                        //放入暂存Stack, 并且要找的算子element又要加三个
                        tmpStoreTokens.push(tmpToken);
                        elementCnt += 3;
                    } else {
                        //那就只剩下左括号, 运算符和数值了
                        //遇到左括号, 这个左括号包括起来的算子被找到, 所以elementCnt要减一
                        if (--elementCnt == 0) {
                            //如果正好知道这个算子就所有要找的算子都被找到了, 也就是要在这个算子左边加上一个左括号了
                            //先往tokens加一个"(", 然后把暂存Stack中的所有tokens推回原Stack
                            tokens.addLast(leftParentheses);
                            //把当前tmpToken(也就是"(")也推回去
                            tokens.addLast(tmpToken);
                            for (String t : tmpStoreTokens)
                                tokens.addLast(t);
                            break;
                        } else {
                            //否则继续放在暂存Stack中
                            tmpStoreTokens.push(tmpToken);
                        }
                    }
                }

            }
        }


        //准备输出
        String resultExpression = "";

        for (String t : tokens)
            resultExpression += t + " ";

        return resultExpression;
    }

    /**
     * Ex 1.3.10
     * 把中序算术表达式变成后序(没有括号的)
     * e.g. ( 1 + 2 ) * 6 =>  1 2 + 6 *
     * 不做算术表达式正确性检查
     * @param exp
     *           原中序算术表达式
     * @return result.
     */
    private static String infixToPostfix(String exp) {
        Stack<String> ops = new Stack<>();

        //String exp = "1 + ( 2 + 3 ) / 88 - ( ( 10 * 3 ) / ( 2 - 3 ) )";

        //exp可以是没有省略任何括号的表达式, 也可以是有优先级和括号相混的情况

        //存储几个运算符的优先级
        LinkedHashMap<String, Integer> priority = new LinkedHashMap<>();

        //"("优先级最低
        priority.put("(", 1);
        //+ - 优先级为2
        priority.put("+", 2);
        priority.put("-", 2);
        priority.put("*", 3);
        priority.put("/", 3);


        Scanner sc = new Scanner(exp);

        String postfixExp = "";

        while (sc.hasNext()) {
            String s = sc.next();
            switch (s) {
                case "(":
                    ops.push(s);
                    break;
                case ")":
                    postfixExp += ops.pop() + " ";
                    //去掉ops中对应的那个"("
                    ops.pop();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    String lastOperator = ops.pop();
                    //如果ops中还有一个待配对或者多个的运算符, 而且那个运算符的优先级还大于等于当前运算符,
                    //就先把原来的那那些运算符抛出, e.g., 1 + 2 * 3 + 6这种情况
                    while (lastOperator != null && priority.get(s) <= priority.get(lastOperator)) {
                        postfixExp += lastOperator + " ";
                        lastOperator = ops.pop();
                    }

                    //如果这个lastOperator非空且优先级小于当前运算符, 还得把这个运算符放回ops
                    if (lastOperator != null)
                        ops.push(lastOperator);

                    //把当前运算符加入到ops中
                    ops.push(s);
                    break;
                default:
                    //如果是数值
                    postfixExp += s + " ";
            }
        }

        //如果还有运算符元素在ops里面, 比如exp是"1 + 2 * 3 + 4"的情况
        String remainsOp;
        while ( (remainsOp = ops.pop()) != null)
            postfixExp += remainsOp + " ";

        return postfixExp;
    }

    /**
     * Ex 1.3.11
     * 由给出的后序表达式, 计算结果
     * @param postfixExpression, 不对该表达式的格式正确性做判断
     * @return 该后序表达式的值
     */
    private static Double evaluatePostfix(String postfixExpression) {
        Scanner sc = new Scanner(postfixExpression);

        String token;
        Stack<String> vals = new Stack<>();

        while (sc.hasNext() && (token = sc.next()) != null) {
            switch (token) {
                case "+" :
                    vals.push("" + (Double.parseDouble(vals.pop()) + Double.parseDouble(vals.pop())) );
                    break;
                case "-" :
                    Double val2 = Double.parseDouble(vals.pop());
                    Double val1 = Double.parseDouble(vals.pop());
                    vals.push( "" + (val1 - val2) );
                    break;
                case "*" :
                    vals.push("" + (Double.parseDouble(vals.pop()) * Double.parseDouble(vals.pop())) );
                    break;
                case "/" :
                    val2 = Double.parseDouble(vals.pop());
                    val1 = Double.parseDouble(vals.pop());
                    vals.push( "" + (val1 / val2) );
                    break;
                //如果是数值
                default  : vals.push(token);
                    break;
            }
        }


        return Double.parseDouble(vals.pop());
    }

    /**
     * Ex 1.3.30
     * 将链表反转并返回链表的首个结点
     * 使用(中)递归的方法处理, 要好好的慢慢的理解这个递归的原理.
     * @param first
     *           要反转的链表的首结点.
     * @return 反转之后的链表的首结点
     */
    @SuppressWarnings("unchecked")
    private Node reverse(Node first) {
        //如果链表是空的, 否则, 递归开始, 假设该链表共有N个Node
        if (first == null)
            return null;

        //这是递归到了最后一个结点的时候(只有在第N次调用reverse才会使用这个), 也就是reverse(lastNode) == lastNode.
        if (first.next == null)
            return first;

        //second就是当前参数表示的结点在原来的链表中的下一个结点.
        //first在第几次调用reverse()就是在原链表中的第几个结点
        Node second = first.next;
        //到下面这条语句最后一次解析的时候(也就是在N-1次调用reverse()的时候), reset就是second就是lastNode,
        //first就是lastNode的上一个Node
        Node reset = reverse(second);
        //第一次访问下列语句的时候, 也就是第N-1次调用reverse()的时候, 正在小心的将first(Node(N-1))和second(Node(N))互换
        //第二次访问下列语句的时候, 也就是第N-2次调用reverse()的时候, 正在将first(Node(N-2))和second(Node(N-1))互换,
        //这时候Node(N)->Node(N-1)->Node(N-2)->null, 且同时有Node(1)->Node(2)->...->Node(N-2)
        //然后一直到第N-1次访问下列语句, 也就是第1次调用reverse()的时候, 链表状态就是Node(N)->...->Node(1)了.
        second.next = first;
        first.next = null;

        //reset一直都是Node(N), 也就是最后反序之后的第一个结点.
        return reset;
    }

    /**
     * Ex 1.3.37
     * Josephus问题
     * 使用环形链表处理该问题
     * @param persons
     *           总人数
     * @param kill
     *           要被杀死的那个人的报数, 为正整数
     */
    @SuppressWarnings("unchecked")
    private static void josephus(int persons, int kill) {
        //首结点
        Node<Integer> first = new Node<>(0);

        Node<Integer> current = first;

        int cnt = 0;

        //第2到第persons个Node
        while (++cnt < persons) {
            current.next = new Node<>(cnt);
            current = current.next;
        }

        //最后一个Node接到first
        current.next = first;

        //人们被杀死的顺序(编号)
        String order = "";

        Node<Integer> previous = current;

        //移动到首个节点
        current = current.next;

        int number = 0;

        while (first != null) {
            if (++number == kill) {
                //如果只剩下一个结点了
                if (previous == current) {
                    //记录下来
                    order += current.item + " ";
                    first = previous = current = null;
                } else {
                    //把当前结点从链表中删除
                    previous.next = current.next;
                    //记录下来
                    order += current.item + " ";
                    //如果当前结点就是first, 那就把first变成下一个结点(因为原来first指向的结点要被用链表中删除)
                    if (current == first)
                        first = first.next;
                    current = current.next;
                }
                //报数清零
                number = 0;
            } else {
                //顺序往下移动
                previous = previous.next;
                current = current.next;
            }

        }

        o(order);
    }

    /**
     * Ex 1.3.37
     * Josephus问题
     * 使用Queue处理该问题, 比使用环形链表的方案更加简单
     * @param n
     *           总人数
     * @param m
     *           要被杀死的那个人的报数, 为正整数
     */
    private static void josephusQueue(int n, int m) {
        Queue<Integer> queue = new Queue<>();

        String order = "";

        //创建队列
        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
        }

        int cnt = 0;

        while (!queue.isEmpty()) {
            //取出
            int x = queue.dequeue();

            //如果报数到了m, 就放进order, 如果没有就重新放回去(而且因为是队列, 所以就放回到了队列最后面)
            if (++cnt % m == 0)
                order += x + " ";
            else
                queue.enqueue(x);
        }

        o(order);
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
     * 那个控制台输出的语句太长啦, 搞个方便一点的.
     * 格式化输出.
     * @param format
     *        a format string.
     * @param args
     *        由format中格式说明符指定的内容
     * @throws
     *        NullPointerException format不能为null
     */
    private static void of(String format, Object... args) {
        if (format == null)
            throw new NullPointerException("第一个参数不允许为空");

        System.out.printf(format, args);
    }


    /**
     * 为每道题的输出前面加一行Title, 这样看起来舒服一点
     * @param exName 题目名称
     */
    private static void title(String exName) {
        final int LEN = 80;
        String titleStr = "";

        int prefixLen = (LEN - exName.length()) / 2;
        int suffixLen =  LEN - prefixLen - exName.length();

        for (int i = 0; i < prefixLen; i++)
            titleStr += '#';
        titleStr += exName;
        for (int i = 0; i < suffixLen; i++)
            titleStr += '#';

        o("\n" + titleStr + "\n");
    }
    /**
     * Test Client
     * @param args
     *          command-line arguments.
     */
    public static void main(String[] args) {
        //Ex 1.3.3
        //自己手动模拟一遍就能知道答案
        //答案: b(应该是...910而不是...901) 就看了下a, b, 其他就不去看了, 反正原理差不多

        //Ex 1.3.4
        //解法: 把所有的左括号放在Stack left中, 当遇到右括号的时候, 从left弹出来, 对比是否正确

        //Ex 1.3.9
        title("Ex 1.3.9");

        //Demo Input 必须确保输入格式正确
        String demoIn = "1 + 2 ) * 3 - 4 ) * 5 - 6 ) ) )";

        o(addLeftParentheses(demoIn));

        //Ex 1.3.10
        //InfixToPostfix
        title("Ex 1.3.10");

        o(infixToPostfix("1 + 2 * 3"));
        //输出应该是 1 2 3 + 88 / + 10 3 * 2 3 - / -
        o(infixToPostfix("1 + ( 2 + 3 ) / 88 - ( ( 10 * 3 ) / ( 2 - 3 ) )"));
        //A B + C D + *
        o(infixToPostfix(" A + B ) * ( C + D )"));
        //10 3 5 * 16 4 - / +
        o(infixToPostfix("10 + 3 * 5 / ( 16 - 4 )"));

        //Ex 1.3.11
        title("Ex 1.3.11");

        o(infixToPostfix("10 + 3 * 5 / ( 16 - 4 )") + "的结果是"
                + evaluatePostfix(infixToPostfix("10 + 3 * 5 / ( 16 - 4 )")));

        //Ex 1.3.31 & Ex 1.3.32 & Ex 1.3.33 原理和思路和DoubleLinkedList.java类似

        //Ex 1.3.37
        //Josephus问题
        title("Ex 1.3.37");

        josephus(7, 2);

        josephusQueue(7, 2);

        //Ex 1.3.49 
        //听说很难
        title("Ex 1.3.49");

        

        //Ex 1.3.50 
        //这道题的内容我已经写在了DoubleLinkedList里面了

    }
}///~

