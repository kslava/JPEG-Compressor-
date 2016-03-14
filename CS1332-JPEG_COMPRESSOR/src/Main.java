import javax.swing.JFrame;

/**
 * Main method for JPEG compressor
 *
 */
public class Main {
	
	public static void main(String[] args) {
		JFrame jf = new JFrame("JPEG Compressor");
		jf.add(new Panel(jf));
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.pack();
	}
}