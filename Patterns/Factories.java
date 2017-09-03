//: DCMMC/Factories.java
import java.util.*;//导入java.util类库中的所有类
import static net.mindview.util.Print.*;//导入Thinking in Java给的Print类中的静态方法(输出方法的简写版)(Java SE5新特性)

interface Service {
	void method1();
	void method2();
}
interface ServiceFactory {
	Service getService();
}
class Implemention1 implements Service {
	Implemention1() {}
	public void method1() {
		print("Implemention1 method1");
	}
	public void method2() {
		print("Implemention1 method2");
	}
}
class Implemention1Factory implements ServiceFactory {
	public Service getService() {
		return new Implemention1();
	}
}
class Implemention2 implements Service {
	Implemention2() {}
	public void method1() {
		print("Implemention2 method1");
	}
	public void method2() {
		print("Implemention2 method2");
	}
}
class Implemention2Factory implements ServiceFactory {
	public Service getService() {
		return new Implemention2();
	}
}

/**class comment:工厂设计模式
*@author DCMMC
*@version 1.0
*/
class Factories {
	public static void serviceConsumer(ServiceFactory fact) {
		Service s = fact.getService();
		s.method1();
		s.method2();
	}
	/** main method
	@param args arrays of string arguments
	@since Java SE5
	*/
	public static void main(String[] args) {
		serviceConsumer(new Implemention1Factory());
		serviceConsumer(new Implemention2Factory());
	}

}
///:~