import java.awt.*;
import java.awt.image.*;

/**
 * PuzzlePiece class that inherits BufferedImage
 * @author Neo Yi Siang
 */
public class PuzzlePiece extends BufferedImage {
	
	private int sequence;
	
	/**
	 * PuzzlePiece constructor 
	 * @param puzzle BufferedImage of the puzzle piece
	 * @param seq sequence of the puzzle piece
	 */
	public PuzzlePiece (BufferedImage puzzle, int seq) {
		super(puzzle.getWidth(), puzzle.getHeight(), puzzle.getType());
		Graphics g = super.createGraphics();
		g.drawImage(puzzle, 0, 0, puzzle.getWidth(), puzzle.getHeight(), null);
		g.dispose();
		sequence = seq;
	}
	
	/**
	 * return the sequence of the puzzle piece
	 * @return sequence of the puzzle piece
	 */
	public int getSequence() {
		return sequence;
	}
	
}
