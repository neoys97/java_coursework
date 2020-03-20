import java.io.*;

/**
 * PeerNode class that implements Serializable
 * 
 * @author Neo Yi Siang
 *
 */
public class PeerNode implements Serializable{
	
	private String username;
	private String password;
	private String ip;
	private int port;
	
	/**
	 * Constructor of PerrNode
	 * 
	 * @param un username of the peer
	 * @param pw password of the peer
	 * @param p port number of the peer
	 */
	public PeerNode (String un, String pw, int p) {
		username = un;
		password = pw;
		port = p;
	}
	
	/**
	 * return the username
	 * @return username 
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * return the password
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * return the IP address
	 * @return IP address
	 */
	public String getIP() {
		return ip;
	}
	
	/**
	 * return the port number
	 * 
	 * @return port number
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set the IP address
	 * 
	 * @param i IP address
	 */
	public void setIP(String i) {
		ip = i;
	}
	
}
