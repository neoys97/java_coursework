import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

/**
 * PuzzlePiecePanel class that inherits JPanel
 * @author Neo Yi Siang
 */
public class PuzzlePiecePanel extends JPanel {
	
	private int sequence;
	private MouseEvent firstMouseEvent = null;
	private ImageIcon puzzle;
	private Dimension d;
	
	/**
	 * Constructor for PuzzlePiecePanel
	 * 
	 * @param p ImageIcon for this panel
	 * @param seq sequence of this panel
	 */
	public PuzzlePiecePanel (ImageIcon p ,int seq) {
		puzzle = p;
		d = new Dimension(puzzle.getIconWidth(), puzzle.getIconHeight());
		sequence = seq;
	}
	
	/**
	 * Constructor for PuzzlePiecePanel
	 * 
	 * @param w width of this panel
	 * @param h height of this panel
	 * @param seq sequence of this panel
	 */
	public PuzzlePiecePanel (int w, int h, int seq) {
		d = new Dimension(w, h);
		puzzle = new ImageIcon();
		sequence = seq;
	}
	
	/**
	 * set the image of the ImageIcon in this panel
	 * 
	 * @param p Image for the ImageIcon
	 */
	public void setImage(Image p) {
		puzzle.setImage(p);
		d = new Dimension(puzzle.getIconWidth(), puzzle.getIconHeight());
		this.repaint();
	}
	
	/**
	 * return the Image of the ImageIcon
	 * 
	 * @return Image of the ImageIcon
	 */
	public Image getImage () {
		return puzzle.getImage();
	}
	
	/**
	 * Pack the ImageIcon and sequence into a custom object DataPacket
	 * and send it to the output stream
	 * 
	 * @param os ObjectOutputStream for object to be sent out
	 */
	public void sendClientImage(ObjectOutputStream os) {
		if (os != null) {
			try {
				os.writeObject(new DataPacket(puzzle, sequence));
				os.flush();
			} 
			catch (Exception e) {
				e.printStackTrace();
				os = null;
			}
		}
	}
	
	/**
	 * return the dimension of the panel
	 */
	public Dimension getPreferredSize() {
		return d;
	}
	
	/**
	 * paint the puzzle piece onto the panel
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.drawImage(puzzle.getImage(), 0, 0, null);
	}
}
