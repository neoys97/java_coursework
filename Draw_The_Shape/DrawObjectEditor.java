import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 * Public class pf DrawObjectEditor controlling the main flow of the program
 * 
 * @author Neo Yi Siang
 *
 */
public class DrawObjectEditor extends JFrame{
	
	private DrawPanel drawPane;
	private JButton Line;
	private JButton Circle;
	private JButton Select;
	private JButton Triangle;
	private JButton Move;
	private JButton Delete;
	private JButton Copy;
	private JButton Save;
	private JButton Load;
	
	public static void main(String[] args) {
		DrawObjectEditor oe = new DrawObjectEditor();
		oe.setup();
	}
	
	/**
	 * Setup the GUI of the DrawObjectEditor program
	 */
	public void setup() {
		setTitle("Draw Object Editor");
		JPanel bigPane = new JPanel();
		bigPane.setLayout(new BoxLayout(bigPane, BoxLayout.Y_AXIS));
		
		drawPane = new DrawPanel();
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new GridLayout(3,3));
		Line = new JButton("Line");
		Line.addActionListener(new lineListener());
		Circle = new JButton("Circle");
		Circle.addActionListener(new circleListener());
		Triangle = new JButton("Triangle");
		Triangle.addActionListener(new triangleListener());
		Select = new JButton("Select");
		Select.addActionListener(new selectListener());
		Move = new JButton("Move");
		disableButton(Move);
		Move.addActionListener(new moveListener());
		Delete = new JButton("Delete");
		disableButton(Delete);
		Delete.addActionListener(new deleteListener());
		Copy = new JButton("Copy");
		disableButton(Copy);
		Copy.addActionListener(new copyListener());
		Save = new JButton("Save");
		Save.addActionListener(new saveListener());
		Load = new JButton("Load");
		Load.addActionListener(new loadListener());
		bottomPane.add(Line);
		bottomPane.add(Circle);
		bottomPane.add(Triangle);
		bottomPane.add(Select);
		bottomPane.add(Move);
		bottomPane.add(Delete);
		bottomPane.add(Copy);
		bottomPane.add(Save);
		bottomPane.add(Load);
		
		bigPane.add(drawPane);
		bigPane.add(bottomPane);
		getContentPane().add(bigPane);
		setVisible(true);
		pack();
	}
	
	/**
	 * Disable a JButton component and change the background to gray colour
	 * @param btn JButton to be disabled
	 */
	public void disableButton (JButton btn) {
		btn.setEnabled(false);
		btn.setBackground(Color.gray);
	}
	
	/**
	 * Enable a JButton component and change the background to default colour
	 * @param btn JButton to be enabled
	 */
	public void enableButton (JButton btn) {
		btn.setEnabled(true);
		btn.setBackground(null);
	}

	
	/**
	 * Class lineListener handling the event listener of Line Button 
	 * @author Neo Yi Siang
	 */
	class lineListener implements ActionListener {

		/**
		 * Start to draw line when Line Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Line);
			drawPane.setMode(0, Line);
		}
	}
	
	/**
	 * Class circleListener handling the event listener of Circle Button
	 * @author Neo Yi Siang
	 */
	class circleListener implements ActionListener {

		/**
		 * Start to circle line when Circle Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Circle);
			drawPane.setMode(1, Circle);
		}
	}
	
	/**
	 * Class triangleListener handling the event listener of Triangle Button
	 * @author Neo Yi Siang
	 */
	class triangleListener implements ActionListener {

		/**
		 * Start to triangle line when Triangle Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Triangle);
			drawPane.setMode(2, Triangle);
		}	
	}
	
	/**
	 * Class selectListener handling the event listener of Select Button
	 * @author Neo Yi Siang
	 */
	class selectListener implements ActionListener {

		/**
		 * Start to select shape when Select Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Select);
			drawPane.setMode(3, Move, Delete, Copy, Select);
		}
		
	}
	
	/**
	 * Class moveListener handling the event listener of  Move Button
	 * @author Neo Yi Siang
	 */
	class moveListener implements ActionListener {

		/**
		 * Start to move selected shape when Move Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Move);
			disableButton(Delete);
			disableButton(Copy);
			drawPane.setMode(4, Select);
		}
	}
	
	/**
	 * Class deleteListener handling the event listener of Delete Button
	 * @author Neo Yi Siang
	 */
	class deleteListener implements ActionListener {

		/**
		 * Start to delete selected shape when Delete Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Move);
			disableButton(Delete);
			disableButton(Copy);
			drawPane.deleteSelected();
			enableButton(Select);
		}
	}
	
	/**
	 * Class copyListener handling the event listener of Copy Button
	 * @author Neo Yi Siang
	 */
	class copyListener implements ActionListener {

		/**
		 * Start to copy selected shape when Copy Button is clicked
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			disableButton(Move);
			disableButton(Delete);
			disableButton(Copy);
			drawPane.copy();
			enableButton(Select);
		}
	}
	
	/**
	 * Class saveListener handling the event listener of Save Button
	 * @author Neo Yi Siang
	 */
	class saveListener implements ActionListener {

		/**
		 * Prompt a file picker to let user choose where to save the shape object
		 * Save the shape object into the selected file
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			String filePath = "";
			int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				filePath = selectedFile.getAbsolutePath().toString();
			}
			if (! filePath.equals(""))
				drawPane.save(filePath);
		}
	}
	
	/**
	 * Class loadListener handling the event listener of Load Button
	 * @author Neo Yi Siang
	 */
	class loadListener implements ActionListener {

		/**
		 * Prompt a file picker to let user choose where to load the shape object
		 * Load the shape object from the selected file
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			String filePath = "";
			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				filePath = selectedFile.getAbsolutePath().toString();
			}
			if (! filePath.equals(""))
				drawPane.load(filePath);
		}
	}

}
