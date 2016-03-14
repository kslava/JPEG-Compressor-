import java.awt.Checkbox;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;


public class Layout extends GroupLayout {
	// init, max, min value for quality slider
	private int QT_MAX = 100;
	private int QT_MIN = 1;
	private int QT_INIT = 50;
	// layout components
	private JLabel lbImportDir, lbExportDir, lbQuality;
	private JTextField tfImportDir, tfExportDir;
	private JButton btImportDir, btConvert;
	private Checkbox cbColor, cbGray;
	private JSlider sdQuality;
	// image compressor engine
	Compressor compressor;
	StartConvert stConvert;
	
	public Layout(Container host) {
		super(host);
		// create Compressor object
		compressor = new Compressor();
		// JPanel property set
		this.setAutoCreateGaps(true);
		this.setAutoCreateContainerGaps(true);
		// element initialize
		lbImportDir = new JLabel("Import Directory: ");
		lbExportDir = new JLabel("Export Directory: ");
		lbQuality = new JLabel("JPEG Quality: ");
		tfImportDir = new JTextField("Import Directory...", 50);
		tfExportDir = new JTextField("Export Directory...", 50);
		sdQuality = new JSlider(JSlider.HORIZONTAL, QT_MIN, QT_MAX, QT_INIT);
		
	    btImportDir = new JButton("File Select");
	    btConvert = new JButton("Start Converting");
	    
	    cbColor = new Checkbox("Color");
	    cbGray = new Checkbox("Gray");

	    // Vertex Group
	    GroupLayout.SequentialGroup leftToRight = this.createSequentialGroup();

	    GroupLayout.ParallelGroup columnLeft = this.createParallelGroup();
		columnLeft.addComponent(lbImportDir);
		columnLeft.addComponent(lbExportDir);
		columnLeft.addComponent(lbQuality);
		leftToRight.addGroup(columnLeft);
	    
		GroupLayout.ParallelGroup columnCenter = this.createParallelGroup();
		columnCenter.addComponent(tfImportDir);
		columnCenter.addComponent(tfExportDir);
		columnCenter.addComponent(sdQuality);
		columnCenter.addComponent(btConvert);
		
		leftToRight.addGroup(columnCenter);
		
		GroupLayout.ParallelGroup columnRight = this.createParallelGroup();
		columnRight.addComponent(btImportDir);
		columnRight.addComponent(cbColor);
		columnRight.addComponent(cbGray);
		
		leftToRight.addGroup(columnRight);
		
		// Horizontal Group
		GroupLayout.SequentialGroup topToBottom = this.createSequentialGroup();
		GroupLayout.ParallelGroup rowTop = this.createParallelGroup();
		rowTop.addComponent(lbImportDir);
		rowTop.addComponent(tfImportDir);
		rowTop.addComponent(btImportDir);
		topToBottom.addGroup(rowTop);
		
		GroupLayout.ParallelGroup rowMiddle = this.createParallelGroup();
		rowMiddle.addComponent(lbExportDir);
		rowMiddle.addComponent(tfExportDir);
		rowMiddle.addComponent(cbColor);
		topToBottom.addGroup(rowMiddle);
		
		GroupLayout.ParallelGroup rowBottom = this.createParallelGroup();
		rowBottom.addComponent(lbQuality);
		rowBottom.addComponent(sdQuality);
		rowBottom.addComponent(cbGray);
		topToBottom.addGroup(rowBottom);
		
		GroupLayout.ParallelGroup rowBottom2 = this.createParallelGroup();
		rowBottom2.addComponent(btConvert);
		topToBottom.addGroup(rowBottom2);
		
		this.setHorizontalGroup(leftToRight);
		this.setVerticalGroup(topToBottom);
		
		btImportDir.addActionListener(new FileOpen());
		cbColor.addItemListener(new CheckColor());
		cbColor.setState(true);
		cbGray.addItemListener(new CheckGray());
		stConvert = new StartConvert();
		
	}
	
	/**
	 * Event listener for checking color mode
	 *
	 */
	private class CheckColor implements ItemListener{
		public void itemStateChanged(ItemEvent arg0) {
			cbColor.setState(true);
			cbGray.setState(false);
		}
	}
	
	/**
	 * Event listener for checking gray scale mode
	 *
	 */
	private class CheckGray implements ItemListener{
		public void itemStateChanged(ItemEvent arg0) {
			cbColor.setState(false);
			cbGray.setState(true);
		}
	}
	
	/**
	 * File open browser class
	 *
	 */
	private class FileOpen implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int rsVal = fileChooser.showOpenDialog(btImportDir);
			if (rsVal == JFileChooser.APPROVE_OPTION) {
				String fileDir = fileChooser.getCurrentDirectory().toString() + "\\" + fileChooser.getSelectedFile().getName();
				tfImportDir.setText(fileDir);
				
				String outFileDir = fileDir.substring(0, fileDir.lastIndexOf(".")) + ".jpg";
				File outFile = new File(outFileDir);
				int i = 1;
				while (outFile.exists()) {
					outFileDir = fileDir.substring(0, fileDir.lastIndexOf(".")) + "_" + (i++) + ".jpg";
					outFile = new File(outFileDir);
				}
				tfExportDir.setText(outFileDir);
				btConvert.removeActionListener(stConvert);
				btConvert.addActionListener(stConvert);
				stConvert.setDir(fileDir, outFileDir);
			}
		}
	}
	
	/**
	 * Event listener for clicking 'Start Converting' button
	 *
	 */
	private class StartConvert implements ActionListener{
		private String fileDir, outFileDir;
		public void setDir(String _fileDir, String _outFileDir){
			fileDir = _fileDir;
			outFileDir = _outFileDir;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			compressor.setFileDir(fileDir);
			compressor.setOutFileDir(outFileDir);
			compressor.setQuality((int) sdQuality.getValue());
			compressor.setMode(cbColor.getState());
			compressor.execute();
		}
		
	}
}
