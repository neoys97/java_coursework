import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.util.concurrent.TimeUnit;
import java.util.*;


/**
 * 
 * Public class ImagePeer
 * 
 * @author Neo Yi Siang
 *
 */
public class ImagePeer {
	
	private ServerSocket p2pSocket = null;
	private ArrayList<ObjectInputStream> listOfInputStreams = new ArrayList<ObjectInputStream>();
	private ArrayList<ObjectOutputStream> listOfOutputStreams = new ArrayList<ObjectOutputStream>();
	private ObjectOutputStream toServerStream;
	private ObjectInputStream fromServerStream;
	private String serverIP;
	private int serverPort = 9000;
	private int port = 0;
	private PeerNode currentNode = null;
	private ArrayList<PeerNode> listOfPeers = new ArrayList<PeerNode>();
	private ArrayList <PuzzlePiecePanel> puzzlePiecePanelList = new ArrayList<PuzzlePiecePanel>();
	private ArrayList <DataPacket> DataPacketList = new ArrayList<DataPacket>();
	private JFrame jf = null;
	private boolean blocked = false;
	private boolean tempLocked = false;
	
	public static void main(String[] args) {
		ImagePeer ip = new ImagePeer();
		System.out.println("client");
		ip.go();
	}
	
	/**
	 * start point of the program
	 */
	public void go() {
		setupServer();
		
		serverIP = JOptionPane.showInputDialog("Connect to server:");
		String username = JOptionPane.showInputDialog("Username");
		String password = JOptionPane.showInputDialog("Password");

		currentNode = new PeerNode(username, password, port);

		boolean success;

		try {
			Socket toServerSocket = new Socket(serverIP, serverPort);
			toServerStream = new ObjectOutputStream(toServerSocket.getOutputStream());
			fromServerStream = new ObjectInputStream(toServerSocket.getInputStream());
			
			toServerStream.writeObject(currentNode);

			success = (boolean)fromServerStream.readObject();
			if (success) {
				System.out.println("Login Success");
				listOfPeers = (ArrayList<PeerNode>)fromServerStream.readObject();
				setupGUI();
				connectSocket();
				toServerStream.writeObject(true);
				serverImageReceive();
			}
			else {
				JOptionPane.showMessageDialog(null, "Login fail");
				System.exit(0);
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid IP");
			System.exit(0);
		}
	}
	
	/**
	 * Setup the server socket for other peers to connect
	 */
	public void setupServer() {
		try {
			p2pSocket = new ServerSocket(0);
			Thread p2pThread = new Thread(new p2pSocketThread());
			p2pThread.start();
			port = p2pSocket.getLocalPort();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Setup the graphic user interface for image display
	 */
	public void setupGUI() {
		jf = new JFrame("Image Peer #" + (listOfPeers.size() + 1));
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PuzzlePanel puzzlePane = new PuzzlePanel();
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridBagLayout());
		topPane.add(puzzlePane);
		jf.getContentPane().add(BorderLayout.CENTER ,topPane);
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());

		jf.setVisible(true);
		jf.pack();
	}
	
	/**
	 * waiting for the input from the server
	 * upon receiving data, update the pictures accordingly
	 */
	public void serverImageReceive() {
		try {
			Object tmp;
			DataPacket data;
			while ((tmp = fromServerStream.readObject())!=null) {
				if ((tmp instanceof DataPacket)){
					if (!blocked) {
						data = (DataPacket) tmp;
						int i = data.getSeq();
						puzzlePiecePanelList.get(i).setImage(data.getImg());
						for (ObjectOutputStream os : listOfOutputStreams) { 
							try {
								os.writeObject(data);
							}
							catch (Exception e) {
								listOfOutputStreams.set(listOfOutputStreams.indexOf(os), null);
							}
						}
					}
				}
				else {
					if ((boolean)tmp) {
						tempLocked = true;
						blocked = false;
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to update the picture?","Image updated on the server.",dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION) {
							tempLocked = false;
							blocked = false;
							updatePictures();
						}
						else {
							tempLocked = false;
							blocked = true;
							DataPacketList.clear();
						}
					}
					else {
						blocked = false;
						tempLocked = false;
					}
				}
			}
			for (ObjectOutputStream os : listOfOutputStreams) { 
				if (os == null) {
					listOfOutputStreams.remove(os);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * connect to other peers in the list of peers
	 */
	public void connectSocket() {
		for (PeerNode node : listOfPeers) {
			try {
				Socket newSoc = new Socket(node.getIP(), node.getPort());
				ObjectOutputStream os = new ObjectOutputStream(newSoc.getOutputStream());
				os.flush();
				InputStream inputs = newSoc.getInputStream();
				ObjectInputStream is = new ObjectInputStream(inputs);
				boolean proceed = (boolean)is.readObject();
				if (proceed) {
					addPeer(os, is);
					Thread serverImageThread = new Thread(new ImageReceiveThread(is));
					serverImageThread.start();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * add custom object DataPacket to the list
	 * 
	 * @param dp DataPacket to be added
	 */
	public synchronized void addData (DataPacket dp) {
		DataPacketList.add(dp);
	}
	
	/**
	 * update the pictures with the data from custom object DataPacket
	 */
	public void updatePictures() {
		for (DataPacket data : DataPacketList) {
			int i = data.getSeq();
			puzzlePiecePanelList.get(i).setImage(data.getImg());
		}
		DataPacketList.clear();
	}
	
	/**
	 * add new object output and input stream to their respective list
	 * 
	 * @param o ObjectOutputStream to be added
	 * @param i ObjectInputStream to be added
	 */
	public synchronized void addPeer(ObjectOutputStream o, ObjectInputStream i) {
		listOfOutputStreams.add(o);
		listOfInputStreams.add(i);
	}
	
	/**
	 * delete the object output stream from the list
	 * 
	 * @param o ObjectOutputStream to be deleted
	 */
	public synchronized void deleteOutputStream (ObjectOutputStream o) {
		listOfOutputStreams.remove(o);
	}
	
	/**
	 * delete the object input stream from the list
	 * 
	 * @param i ObjectInputStream to be deleted
	 */
	public synchronized void deleteInputStream (ObjectInputStream i) {
		listOfInputStreams.remove(i);
	}
	
	/**
	 * PuzzlePanel class
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class PuzzlePanel extends JPanel{

		private Dimension d;
		private int puzzleSize = 700;
		private int gridNumber = 10;
			
		/**
		 * Constructor for the puzzle panel
		 * @param image image for the puzzle
		 * @param th transfer handler for te puzzle piece
		 */
		public PuzzlePanel () {
			d = new Dimension(puzzleSize, puzzleSize);
			this.setLayout(new GridLayout(gridNumber, gridNumber));
		    populatePuzzlePiecePanel();
		}
		
		/**
		 * add the puzzle piece image into puzzle piece panel and add the panel to this current panel
		 */
		public void populatePuzzlePiecePanel() {
			for (int i = 0; i < (gridNumber*gridNumber); i++) {
				puzzlePiecePanelList.add(new PuzzlePiecePanel(puzzleSize/gridNumber, puzzleSize/gridNumber,i));
				add(puzzlePiecePanelList.get(i));
			}
		}
		
		/**
		 * return the preferred dimension of the component
		 */
		public Dimension getPreferredSize() {
			return d;
		}
	}
	
	/**
	 * p2pSocketThread class to handle the thread waiting for the connection of other peers 
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class p2pSocketThread implements Runnable {
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			try {
				while (true) {
					Socket s = p2pSocket.accept();
					ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
					ObjectInputStream is = new ObjectInputStream(s.getInputStream());
					addPeer(os, is);
					Thread serverImageThread = new Thread(new ImageReceiveThread(is));
					serverImageThread.start();
					os.writeObject(true);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * ImageReceiveThread class to handle input from input stream in the list
	 * 
	 * @author Neo Yi Siang
	 *
	 */
	public class ImageReceiveThread implements Runnable {
		private ObjectInputStream is;
		
		/**
		 * ImageReceiveThread constructor
		 * 
		 * @param i ObjectInputStream for this thread
		 */
		public ImageReceiveThread(ObjectInputStream i) {
			is = i;
		}
		
		
		/**
		 * starting point of the thread
		 */
		public void run() {
			try {
				Object tmp = null;
				DataPacket data;
				while ((tmp = is.readObject())!=null) {
					data = (DataPacket) tmp;
					if (blocked) {
						continue;
					}
					if (tempLocked) {
						addData(data);
					} 
					else {
						int i = data.getSeq();
						puzzlePiecePanelList.get(i).setImage(data.getImg());
					}
				}
			}
			catch (Exception e) {
				deleteInputStream(is);
			}
		}
	}
}
