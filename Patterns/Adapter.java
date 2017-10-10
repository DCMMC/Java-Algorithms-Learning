//: DCMMC/ReadableInterface.java
import java.util.*;//导入java.util类库中的所有类
import static net.mindview.util.Print.*;//导入Thinking in Java给的Print类中的静态方法(输出方法的简写版)(Java SE5新特性)

interface DBSocketInerface { //德标接口
	void powerWithTwoRound();
}
class DBSocket implements DBSocketInerface { //德国插座
	@Override
	public void powerWithTwoRound() {
		print("使用两个圆头的插孔供电");
	}
}
class Hotel { //德国旅馆
	private DBSocketInerface dbSocket;
	public void setSocket(DBSocketInerface dbSocket) {
		this.dbSocket = dbSocket;
	}
	public void charge() {//旅馆中的充电功能
		dbSocket.powerWithTwoRound();//只能使用德标插口充电
	}
}
interface GBSocketInerface {
	void powerWithThreeFlat();
}
class GBSocket implements GBSocketInerface {
	@Override
	public void powerWithThreeFlat() {
		print("使用三角扁头插孔供电");
	}
}
class SocketAdapter implements DBSocketInerface {//实现旧接口
	private GBSocketInerface gbSocket;
	public SocketAdapter(GBSocketInerface gbSocket) {
		this.gbSocket = gbSocket;
	}
	@Override
	public void powerWithTwoRound() {
		gbSocket.powerWithThreeFlat();
	}
}
/**class comment:适配器设计模式
*@author DCMMC
*@version 1.0
*/
public class Adapter {
	/** main method
	@param args arrays of string arguments
	@since Java SE5
	*/
	public static void main(String[] args) {
		GBSocketInerface gbSocket = new GBSocket();
		Hotel hotel = new Hotel();
		SocketAdapter socketAdapter = new SocketAdapter(gbSocket);
		hotel.setSocket(socketAdapter);
		hotel.charge();
	}
}///:~