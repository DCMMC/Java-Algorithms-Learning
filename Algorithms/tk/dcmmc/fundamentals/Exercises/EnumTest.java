//: DCMMC/EnumTest.java
import java.util.*;//导入java.util类库中的所有类
//import static net.mindview.util.Print.*;//导入Thinking in Java给的输出语句的简写版(Java SE5新特性)

/**class comment:测试enum枚举类型
*@author DCMMC
*@version 1.0
*/
enum Demo {
	red,black,white
}
public class EnumTest {
	/** main method
	@param args arrays of string arguments
	@since Java SE5
	*/
	public static void main(String... args) {
		Demo test = Demo.white;
		System.out.println(test);//enum支持toString()方法
		for(Demo s : Demo.values())//values()方法按照enum声明的顺序产生由这些enum常量构成的数组
			System.out.println(s+",ordinal "+s.ordinal());//ordinal()返回该enum常量声明的位置（从0开始）
	
		System.out.println(EnumTest.class.getPackage());
	}
} ///:~