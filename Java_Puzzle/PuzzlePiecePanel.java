import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.event.*;

/**
 * PuzzlePiecePanel class that inherits JPanel
 * @author Neo Yi Siang
 */
public class PuzzlePiecePanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private int sequence;
	private MouseEvent firstMouseEvent = null;
	private PuzzlePiece puzzle = null;
	
	/**
	 * Constructor for PuzzlePiecePanel 
	 * @param p puzzle piece to be assigned to this panel
	 * @param seq sequence of the panel in the grid layout
	 */
	public PuzzlePiecePanel (PuzzlePiece p ,int seq) {
		puzzle = p;
		sequence = seq;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * set the puzzle piece to the panel and repaint the element
	 * @param p puzzle piece
	 */
	public void setImage(PuzzlePiece p) {
		puzzle = p;
		this.repaint();
	}
	
	/**
	 * return the puzzle piece of the panel
	 * @return puzzle piece of the panel
	 */
	public PuzzlePiece getImage () {
		return puzzle;
	}
	
	/**
	 * check if the puzzle piece is in the same puzzle piece panel
	 * @return true if the puzzle piece is in the right panel, false otherwise
	 */
	public boolean checkMatch() {
		return (sequence == puzzle.getSequence());
	}
	
	/**
	 * return the dimension of the panel
	 */
	public Dimension getPreferredSize() {
		if (puzzle == null)
			return new Dimension();
		else
			return new Dimension(puzzle.getWidth(), puzzle.getHeight());
	}
	
	/**
	 * paint the puzzle piece onto the panel
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.drawImage(puzzle, 0, 0, null);
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
