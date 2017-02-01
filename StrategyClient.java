//: DCMMC/ReadableInterface.java
import java.util.*;//导入java.util类库中的所有类
import static net.mindview.util.Print.*;//导入Thinking in Java给的Print类中的静态方法(输出方法的简写版)(Java SE5新特性)

interface Strategy {
	void doSomething();
}
class ConcreteStrategy1 implements Strategy {
	public void doSomething() {
		print("具体策略1");
	}
}
class ConcreteStrategy2 implements Strategy {
	public void doSomething() {
		print("具体策略2");
	}
}
class Context { //封装类：也叫上下文，对策略进行二次封装，目的是避免高层模块对策略的直接调用。
	private Strategy strategy;

	public Context(Strategy strategy) {
		this.strategy = strategy;
	}

	public void execute() {
		strategy.doSomething();
	}
}
/**class comment:策略（strategy）设计模式
*@author DCMMC
*@version 1.0
*/
public class StrategyClient {
	/** main method
	@param args arrays of string arguments
	@since Java SE5
	*/
	public static void main(String[] args) {
		Context context;
		print("------执行策略1------");
		context = new Context(new ConcreteStrategy1());
		context.execute();

		print("------执行策略2------");
		context = new Context(new ConcreteStrategy2());
		context.execute();
	}
}///:~