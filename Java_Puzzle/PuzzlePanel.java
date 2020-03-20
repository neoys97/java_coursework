import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

/**
 * PuzzlePanel class that inherits JPanel
 * @author Neo Yi Siang
 */
public class PuzzlePanel extends JPanel{
	
	private BufferedImage puzzleImage = null;
	private Dimension d;
	private int puzzleSize = 800;
	private int gridNumber = 10;
	private ArrayList <PuzzlePiece> puzzlePieceList = new ArrayList<PuzzlePiece>();
	private ArrayList <PuzzlePiecePanel> puzzlePiecePanelList = new ArrayList<PuzzlePiecePanel>();
	private TransferHandler puzzlePieceTransferHandler;
	
	/**
	 * Constructor for the puzzle panel
	 * @param image image for the puzzle
	 * @param th transfer handler for te puzzle piece
	 */
	public PuzzlePanel (BufferedImage image, TransferHandler th) {
		puzzleImage = image;
		puzzlePieceTransferHandler = th;
		d = new Dimension(puzzleSize, puzzleSize);
		this.setLayout(new GridLayout(gridNumber, gridNumber));
	    resize (puzzleSize, puzzleSize);
	    splitPuzzle();
	    shufflePuzzleList();
	    populatePuzzlePiecePanel();
	}
	
	/**
	 * resize the image to desired dimension
	 */
	public void resize (int newW, int newH) {
	    BufferedImage dimg = new BufferedImage(newW, newH, puzzleImage.getType());  
	    Graphics2D g = dimg.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(puzzleImage, 0, 0, newW, newH, null);  
	    g.dispose();  
	    puzzleImage = dimg; 
	}  
	
	/**
	 * split the puzzle into grid x grid small images with all having the same width and length
	 */
	public void splitPuzzle () {
		int size = puzzleSize / gridNumber;
		for (int i = 0; i < gridNumber; i++) {
			for (int j = 0; j < gridNumber; j++) {
				BufferedImage puzzle = puzzleImage.getSubimage(j*size, i*size, size, size);
				puzzlePieceList.add(new PuzzlePiece(puzzle, i*gridNumber + j));
			}
		}
	}
	
	/**
	 * shuffle the puzzle piece image
	 */
	public void shufflePuzzleList() {
		Collections.shuffle(puzzlePieceList);
	}
	
	/**
	 * add the puzzle piece image into puzzle piece panel and add the panel to this current panel
	 */
	public void populatePuzzlePiecePanel() {
		for (int i = 0; i < puzzlePieceList.size(); i++) {
			puzzlePiecePanelList.add(new PuzzlePiecePanel(puzzlePieceList.get(i), i));
			puzzlePiecePanelList.get(i).setTransferHandler(puzzlePieceTransferHandler);
			add(puzzlePiecePanelList.get(i));
		}
	}
	
	/**
	 * return the image of the puzzle
	 * @return image of the puzzle
	 */
	public BufferedImage getImage() {
		return puzzleImage;
	}
	
	/**
	 * check if the puzzle is being solved
	 * @return true if the puzzle is solved, false otherwise
	 */
	public boolean checkCompletePuzzle() {
		for (PuzzlePiecePanel element : puzzlePiecePanelList) {
			if (! element.checkMatch())
				return false;
		}
		return true;
	}
	
	/**
	 * return the preferred dimension of the component
	 */
	public Dimension getPreferredSize() {
		return d;
	}
	
}
