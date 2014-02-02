import javax.swing.JApplet;

public class Application extends JApplet {
	private Controller controller;

	public void init() {
		controller = new Controller(this);
	}

	public Controller getController() {
		return controller;
	}
	
}
