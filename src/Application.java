import javax.swing.JApplet;
import lotus.domino.*;

public class Application extends JApplet {
	private Controller controller;

	public void init() {
		controller = new Controller(this);
	}

	public void start() {
		controller.resize();
	}

	public void loadXML(String xml) {
		controller.loadXML(xml);
		//controller.resize();
	}
	
}
