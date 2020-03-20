import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.datatransfer.*;
import java.awt.event.*;

/**
 * JavaPuzzle class - Main flow of the puzzle game
 * @author Neo Yi Siang
 */
public class JavaPuzzle {
	
	private JFrame jf = null;
	private JFrame oriImageFrame = null;
	private BufferedImage puzzleImage = null;
	private PuzzlePanel puzzlePane;
	private JTextArea displayArea;
	
	public static void main(String[] args) {
		JavaPuzzle jPuzz = new JavaPuzzle();
		jPuzz.start();
	}

	/**
	 * setup the main user interface of the puzzle game
	 */
	public void setup() {
		jf = new JFrame("Puzzle Image");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		oriImageFrame = new JFrame();
		
		puzzlePane = new PuzzlePanel(puzzleImage, new PuzzlePieceTransferHandler());
		puzzleImage = puzzlePane.getImage();
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridBagLayout());
		topPane.add(puzzlePane);
		jf.getContentPane().add(BorderLayout.CENTER ,topPane);
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());
		displayArea = new JTextArea(5, 30);
		displayArea.setEditable(false);
		displayArea.setLineWrap(true);
		displayArea.append("Game started!\n");
		JScrollPane scroller = new JScrollPane(displayArea);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
//		JScrollBar bar = scroller.getVerticalScrollBar();
//	    bar.setValue(bar.getMaximum());
		bottomPane.add(BorderLayout.NORTH, scroller);
		
		JButton loadImageBtn = new JButton("Load Another Image");
		JButton showOriImageBtn = new JButton("Show Original Image");
		JButton exitBtn = new JButton("Exit");
		loadImageBtn.addActionListener(new loadListener());
		showOriImageBtn.addActionListener(new showOriImageListener());
		exitBtn.addActionListener(new exitListener());
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout());
		buttonPane.add(loadImageBtn);
		buttonPane.add(showOriImageBtn);
		buttonPane.add(exitBtn);
		bottomPane.add(BorderLayout.SOUTH, buttonPane);
		jf.getContentPane().add(BorderLayout.SOUTH ,bottomPane);
		
		jf.setVisible(true);
		jf.pack();
	}
	
	/**
	 * start the puzzle game
	 */
	public void start() {
		oriImageFrame = new JFrame();
		if (loadImage()) {
			if (jf != null)
				jf.dispose();
			if (oriImageFrame != null)
				oriImageFrame.dispose();
			setup();
		}
	}
	
	/**
	 * prompt a file chooser and load the image chosen by the user
	 * if the image cannot be loaded, an alert message will be shown
	 * @return true if the image is successfully loaded, false otherwise
	 */
	public boolean loadImage() {
		String pathName = FilePicker.getFilePath();
		if (pathName.equals(""))
			return false;
		try {
			puzzleImage = ImageIO.read(new File(pathName));
			System.out.println(pathName);
		} catch (IOException e) {System.out.println("no");}
		
		while (puzzleImage == null) {
			if (pathName.equals(""))
				return false;
			JOptionPane.showMessageDialog(null, "Image cannot be loaded, please choose another image.");
			pathName = FilePicker.getFilePath();
			try {
				puzzleImage = ImageIO.read(new File(pathName));
				System.out.println("successful laod");
			} catch (IOException e) {System.out.println("exception laod");}
		}
		return true;
	}
	
	/**
	 * action listener for exit button
	 * @author Neo Yi Siang
	 */
	class exitListener implements ActionListener {
		
		/**
		 * close the main window and exit the program
		 */
		public void actionPerformed(ActionEvent e) {
			jf.dispose();
			System.exit(0);
		}
	}
	
	/**
	 * action listener for load another image button
	 * @author Neo Yi Siang
	 */
	class loadListener implements ActionListener {
		
		/**
		 * prompt file chooser and update the new puzzle image upon successful loading
		 */
		public void actionPerformed(ActionEvent e) {
			if (loadImage()) {
				if (jf != null)
					jf.dispose();
				if (oriImageFrame != null)
					oriImageFrame.dispose();
				setup();
			}
		}
	}
	
	/**
	 * action listener for show original image button
	 * @author Neo Yi Siang
	 */
	class showOriImageListener implements ActionListener {
		
		/**
		 * display the original image on the new window
		 */
		public void actionPerformed(ActionEvent e) {
			oriImageFrame.getContentPane().add(new JPanel() {
				public Dimension getPreferredSize() {
					if (puzzleImage == null)
						return new Dimension();
					else
						return new Dimension(puzzleImage.getWidth(), puzzleImage.getHeight());
				}
				
				public void paintComponent (Graphics g) {
					g.drawImage(puzzleImage, 0, 0, null);
				}
			});
			oriImageFrame.setVisible(true);
			oriImageFrame.pack();
		}
	}
	
	/**
	 * transfer handler for the puzzle piece
	 * @author Neo Yi Siang
	 */
	class PuzzlePieceTransferHandler extends TransferHandler {
	    private DataFlavor flavor = DataFlavor.imageFlavor;
	    private PuzzlePiecePanel src;

	    /**
	     * image switching between the source and target
	     */
	    public boolean importData(JComponent c, Transferable t) {
	        PuzzlePiece puzzle;
	        if (canImport(c, t.getTransferDataFlavors())) {
	        	PuzzlePiecePanel dst = (PuzzlePiecePanel) c; 
	        	if (src.checkMatch() || dst.checkMatch()) {
	        		displayArea.append("Image(s) is/are in the correct position!\n");
	        		return true;
	        	}
	            if (src == dst) {
	                return true;
	            }
	            try {
	                puzzle = (PuzzlePiece)t.getTransferData(flavor);
	                src.setImage(dst.getImage());
	                if (src.checkMatch())
	                	displayArea.append("Image block in correct position!\n");
	                dst.setImage(puzzle);
	                if (dst.checkMatch())
	                	displayArea.append("Image block in correct position!\n");
	                if (puzzlePane.checkCompletePuzzle())
	                	JOptionPane.showMessageDialog(null, "You win!!!.");
	                return true;
	            } catch (UnsupportedFlavorException ufe) {
	                System.out.println("importData: unsupported data flavor");
	            } catch (IOException ioe) {
	                System.out.println("importData: I/O exception");
	            }
	        }
	        return false;
	    }

	    /**
	     * create transferable for puzzle pieces
	     */
	    protected Transferable createTransferable(JComponent c) {
	        src = (PuzzlePiecePanel)c;
	        return new PuzzlePieceTransferable(src);
	    }

	    /**
	     * return the source action of the transfer handler
	     */
	    public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }

	    /**
	     * action done after the transfer of data
	     */
	    protected void exportDone(JComponent c, Transferable data, int action) {
	        src = null;
	    }

	    /**
	     * return true if the transferable can be imported to the destination target
	     */
	    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	        for (int i = 0; i < flavors.length; i++) {
	            if (flavor.equals(flavors[i])) {
	                return true;
	            }
	        }
	        return false;
	    }

	    /**
	     * transferable for puzzle piece
	     * @author Neo Yi Siang
	     */
	    class PuzzlePieceTransferable implements Transferable {
	        private PuzzlePiece puzzle;

	        /**
	         * constructor for the transferable
	         * @param c puzzle piece panel
	         */
	        PuzzlePieceTransferable(PuzzlePiecePanel c) {
	            puzzle = c.getImage();
	        }

	        /**
	         * return the transfer data
	         */
	        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
	            if (!isDataFlavorSupported(flavor)) {
	                throw new UnsupportedFlavorException(flavor);
	            }
	            return puzzle;
	        }

	        /**
	         * return the list of type of data can be transferred
	         */
	        public DataFlavor[] getTransferDataFlavors() {
	            return new DataFlavor[] { flavor };
	        }

	        /**
	         * return true if the type of data is supported by this transferable
	         */
	        public boolean isDataFlavorSupported(DataFlavor f) {
	            return flavor.equals(f);
	        }
	    }
	}
}
