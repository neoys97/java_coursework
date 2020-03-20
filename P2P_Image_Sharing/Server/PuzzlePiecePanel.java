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
public class PuzzlePiecePanel extends JPanel implements MouseListener, MouseMotionListener {
	
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
		addMouseListener(this);
		addMouseMotionListener(this);
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
		addMouseListener(this);
		addMouseMotionListener(this);
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
	 * return the sequence of this panel
	 * @return sequence of the panel
	 */
	public int getSequence() {
		return sequence;
	}
	
	/**
	 * Pack the ImageIcon and sequence into a custom object DataPacket
	 * and send it to the output stream
	 * 
	 * @param os ObjectOutputStream for object to be sent out
	 */
	public boolean sendClientImage(ObjectOutputStream os) {
		if (os != null) {
			try {
				os.writeObject(new DataPacket(puzzle, sequence));
				os.flush();
				return true;
			} 
			catch (Exception e) {
				return false;
			}
		}
		return false;
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

	/**
	 * create mouse puzzle piece panel transfer handler upon being dragged
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
        if (firstMouseEvent != null) {
        	JComponent c = (JComponent)e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, firstMouseEvent, TransferHandler.MOVE);
            firstMouseEvent = null;
        }
    }
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	/**
	 * set the first mouse event upon mouse pressed
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		firstMouseEvent = e;
	}

	/**
	 * set the first mouse event to null upon mouse released
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		firstMouseEvent = null;
	}
}
