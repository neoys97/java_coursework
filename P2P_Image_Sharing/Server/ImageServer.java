import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 * Public class ImageServer
 * 
 * @author Neo Yi Siang
 *
 */
public class ImageServer {
	
	private ServerSocket serverSocket;
	private ArrayList<PeerNode> listOfPeers = new ArrayList<PeerNode>();
	private ArrayList<ObjectInputStream> listOfInputStreams = new ArrayList<ObjectInputStream>();
	private ArrayList<ObjectOutputStream> listOfOutputStreams = new ArrayList<ObjectOutputStream>();
	private ArrayList <ImageIcon> puzzlePieceList = new ArrayList<ImageIcon>();
	private ArrayList <PuzzlePiecePanel> puzzlePiecePanelList = new ArrayList<PuzzlePiecePanel>();
	private BufferedImage puzzleImage = null;
	private JFrame jf = null;
	private JPanel topPane;
	private int numberOfBlocks = 100;
	
	public static void main(String[] args) {
		ImageServer is = new ImageServer();
		System.out.println("server");
		is.go();
	}

	/**
	 * Starting point of the program
	 */
	public void go() {
		
		if(loadImage()) {
			setupGUI();
		}
		else {
			System.exit(0);
		}
		setupConnection();
		setupLoginData();
		Thread loginListenerThread = new Thread(new loginThread());
		loginListenerThread.start();
	}
	
	/**
	 * Setup the graphic user interface for image display
	 */
	public void setupGUI() {
		jf = new JFrame("Image Server");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PuzzlePanel puzzlePane = new PuzzlePanel();
		puzzleImage = puzzlePane.getImage();
		topPane = new JPanel();
		topPane.setLayout(new GridBagLayout());
		topPane.add(puzzlePane);
		jf.getContentPane().add(BorderLayout.CENTER ,topPane);
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());
		
		JButton loadImageBtn = new JButton("Load Another Image");
		loadImageBtn.addActionListener(new loadListener());
		bottomPane.add(loadImageBtn);
		jf.getContentPane().add(BorderLayout.SOUTH ,bottomPane);
		
		jf.setVisible(true);
		jf.pack();
	}
	

	/**
	 * prompt a file chooser and return the path to the file chosen
	 * 
	 * @return path to the file, empty if no file or invalid file chosen
	 */
	public String getFilePath() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			return (selectedFile.getAbsolutePath().toString());
		}
		
		return ("");
	}
	
	/**
	 * load the image from the path
	 * 
	 * @return true if image successfully loaded, false otherwise
	 */
	public boolean loadImage() {
		String pathName = getFilePath();
		if (pathName.equals(""))
			return false;
		try {
			puzzleImage = ImageIO.read(new File(pathName));;
		} 
		catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * read the user data from txt file
	 */
	public void setupLoginData() {
		User.readJSON();
	}
	
	/**
	 * login to the account
	 * 
	 * @param un username
	 * @param pw password
	 * @return true if successfully logged in, false otherwise
	 */
	public boolean accountLogin(String un, String pw) {
		User currentUser = null;
		
		for (User element : User.userList) {
			if (element.getUsername().equals(un)) {
				currentUser = User.userList.get(User.userList.indexOf(element));
			}
		}
		
		if (currentUser == null) {
			return false;
		}
		if (currentUser.acctLocked()) {
			return false;
		}
		if (currentUser.login(pw)) {
			return true;
		}
		return false;
	}
	
	/**
	 * setup server socket
	 */
	public void setupConnection() {
		try {
			serverSocket = new ServerSocket(9000);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * add the information of the new client into the list
	 * 
	 * @param n custom object PeerNode to be added
	 * @param o ObjectOutputStream to be added
	 * @param i ObjectInputStream to be added
	 */
	public synchronized void addPeer(PeerNode n, ObjectOutputStream o, ObjectInputStream i) {
		listOfPeers.add(n);
		listOfOutputStreams.add(o);
		listOfInputStreams.add(i);
	}
	
	/**
	 * delete the information of a client
	 * 
	 * @param o ObjectOutputStream of the clien
	 */
	public synchronized void deletePeer (ObjectOutputStream o) {
		int index = listOfOutputStreams.indexOf(o);
		listOfPeers.remove(index);
		listOfInputStreams.remove(index);
		listOfOutputStreams.remove(o);
	}
	
	/**
	 * delete the information of a client
	 * 
	 * @param o ObjectInputStream of the clien
	 */
	public synchronized void deletePeer (ObjectInputStream i) {
		int index = listOfInputStreams.indexOf(i);
		listOfPeers.remove(index);
		listOfOutputStreams.remove(index);
		listOfInputStreams.remove(i);
	}
	
	/**
	 * 
	 * PuzzlePanel class
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class PuzzlePanel extends JPanel{

		private Dimension d;
		private int puzzleSize = 700;
		private int gridNumber = 10;
		private TransferHandler puzzlePieceTransferHandler;
		
		/**
		 * Constructor for the puzzle panel
		 * @param image image for the puzzle
		 * @param th transfer handler for te puzzle piece
		 */
		public PuzzlePanel () {
			puzzlePieceTransferHandler = new PuzzlePieceTransferHandler();
			d = new Dimension(puzzleSize, puzzleSize);
			this.setLayout(new GridLayout(gridNumber, gridNumber));
		    resize (puzzleSize, puzzleSize);
		    splitPuzzle();
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
			puzzlePieceList.clear();
			int size = puzzleSize / gridNumber;
			for (int i = 0; i < gridNumber; i++) {
				for (int j = 0; j < gridNumber; j++) {
					BufferedImage puzzle = puzzleImage.getSubimage(j*size, i*size, size, size);
					ImageIcon tmp = new ImageIcon(puzzle);
					puzzlePieceList.add(tmp);
				}
			}
		}

		/**
		 * add the puzzle piece image into puzzle piece panel and add the panel to this current panel
		 */
		public void populatePuzzlePiecePanel() {
			puzzlePiecePanelList.clear();
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
		 * return the preferred dimension of the component
		 */
		public Dimension getPreferredSize() {
			return d;
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
	        Image puzzle;
	        if (canImport(c, t.getTransferDataFlavors())) {
	        	PuzzlePiecePanel dst = (PuzzlePiecePanel) c; 
	            if (src == dst) {
	                return true;
	            }
	            try {
	                puzzle = (Image)t.getTransferData(flavor);
	                ImageIcon srcI = new ImageIcon(dst.getImage());
	                ImageIcon dstI = new ImageIcon(puzzle);
	                src.setImage(dst.getImage());
                    dst.setImage(puzzle);
                    for (ObjectOutputStream os : listOfOutputStreams) {
                    	try {
                    		os.writeObject(new DataPacket(srcI, src.getSequence()));
                    		os.writeObject(new DataPacket(dstI, dst.getSequence()));
                    	}
                    	catch (Exception e) {
                    		listOfOutputStreams.set(listOfOutputStreams.indexOf(os), null);
                    	}
                    }
                    for (ObjectOutputStream os : listOfOutputStreams) {
                    	if (os == null) {
                    		listOfOutputStreams.remove(os);
                    	}
                    }
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
	        private Image puzzle;

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
	
	/**
	 * action listener for load another image button
	 * @author Neo Yi Siang
	 */
	public class loadListener implements ActionListener {
		
		/**
		 * prompt file chooser and update the new puzzle image upon successful loading
		 */
		public void actionPerformed(ActionEvent e) {
			if (loadImage()) {
				for (ObjectOutputStream os : listOfOutputStreams) {
					if (os == null)
						deletePeer(os);
				}
				topPane.removeAll();
				PuzzlePanel puzzlePane = new PuzzlePanel();
				puzzleImage = puzzlePane.getImage();
				topPane.setLayout(new GridBagLayout());
				topPane.add(puzzlePane);
				jf.revalidate();
				for (ObjectOutputStream os : listOfOutputStreams) {
					Thread sendImageThread = new Thread(new sendImageThread(os, listOfOutputStreams.indexOf(os), true));
					sendImageThread.start();
				}
			}
		}
	}
	
	/**
	 * 
	 * loginThread class to receive login from the client
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class loginThread implements Runnable {
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					Thread loginSetup = new Thread(new loginSetupThread(socket));
					loginSetup.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
		
	/**
	 * 
	 * loginSetupThread class to handle login from client
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class loginSetupThread implements Runnable {
		
		private Socket socket = null;
		private ObjectInputStream is = null;
		private ObjectOutputStream os = null;
		
		/**
		 * loginSetupThread constructor
		 * @param s Socket to be connected
		 */
		public loginSetupThread (Socket s) {
			socket = s;
		}
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			try {	
				os = new ObjectOutputStream(socket.getOutputStream());
				is = new ObjectInputStream(socket.getInputStream());
								
				PeerNode incomingNode = (PeerNode)is.readObject();
				
				String ip = "";
				SocketAddress socketAddress = socket.getRemoteSocketAddress();
				
				if (socketAddress instanceof InetSocketAddress) {
				    InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
				    ip = inetAddress.toString();
				    ip = ip.substring(ip.indexOf('/')+1);
				}
				
				if(accountLogin(incomingNode.getUsername(), incomingNode.getPassword())) {
					incomingNode.setIP(ip);
					os.writeObject(true);
					os.writeObject(listOfPeers);
					addPeer(incomingNode, os, is);
					
					System.out.println(incomingNode.getUsername() + " login success");
					boolean proceed = (boolean)is.readObject();
					if (proceed) {
						sendImage();
						Thread connectionCheck = new Thread(new CheckConnectionThread(is));
						connectionCheck.start();
					}
				} 
				else {
					os.writeObject(false);
					System.out.println(incomingNode.getUsername() + " login fail");
					os.close();
					is.close();
				}
				os.flush();		
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * start send image thread for all the ObjectOutputStream in the list
		 */
		public synchronized void sendImage () {
			for (int i = 0; i < listOfOutputStreams.size(); i++) {
				Thread sendImageThread = new Thread(new sendImageThread(listOfOutputStreams.get(i), i, false));
				sendImageThread.start();
			}
		}
		
	}
	
	/**
	 * 
	 * sendImageThread class to handle the sending of image to the client
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class sendImageThread implements Runnable {

		private ObjectOutputStream os;
		private int index;
		private boolean isNew;
		
		/**
		 * sendImageThread constructor
		 * @param o ObjectOutputStream for this thread
		 * @param i index of the ObjectOutputStream in the list
		 * @param in true if the image is from the load button listener, false otherwise
		 */
		public sendImageThread(ObjectOutputStream o, int i, boolean in) {
			os = o;
			index = i;
			isNew = in;
		}
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			int n = (int)Math.ceil((double)numberOfBlocks/listOfPeers.size());
			int start = n*index;
			try {
				os.writeObject(isNew);
			}
			catch (Exception e) {
				listOfOutputStreams.set(index, null);
			}
			for (int i = 0; i < n; i ++) {
				if (start < puzzlePiecePanelList.size()) {
					if (!(puzzlePiecePanelList.get(start).sendClientImage(os))) {
						listOfOutputStreams.set(index, null);
					}
				}
				else
					break;
				start += 1;
			}
		}
	}
	
	/**
	 * 
	 * CheckConnectionThread class to handle the active or disconnected connection
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class CheckConnectionThread implements Runnable {
		
		private ObjectInputStream is;

		/**
		 * CheckConnectionThread constructor
		 * @param i ObjectInputStream to be checked
		 */
		public CheckConnectionThread(ObjectInputStream i) {
			is = i; 
		}
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			try {
				while (is.readObject()!=null) {}
			}
			catch (Exception e) {
				deletePeer(is);
			}
		}
	}
}
