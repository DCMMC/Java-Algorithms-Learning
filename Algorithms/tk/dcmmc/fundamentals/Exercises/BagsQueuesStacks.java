package tk.dcmmc.fundamentals.Exercises;

import tk.dcmmc.fundamentals.Algorithms.Stack;
import java.util.Scanner;

/**
* 本文件包括Exercise 1.3中部分习题的解答
* Created by DCMMC on 2017/7/23
* Finished on 2017/7/
* @Author DCMMC
* @since 1.5
*/
public class BagsQueuesStacks {
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
        final int LEN = 40;
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
	* 			command-line arguments.
	*/
	public static void main(String[] args) {
		/* Dijkstra双栈算术表达式求值算法 */
		title("Dijkstra双栈算术表达式求值算");

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

        //

	}
}///~
