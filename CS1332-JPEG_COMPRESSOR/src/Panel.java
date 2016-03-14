import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	Layout mainLayout;
	
	public Panel(JFrame frame){
		mainLayout = new Layout(this);
		this.setLayout(mainLayout);
	}
}
