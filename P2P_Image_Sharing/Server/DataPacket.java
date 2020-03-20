import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * 
 * DataPacket class that implements Serializable
 * 
 * @author Neo Yi Siang
 *
 */
public class DataPacket implements Serializable{
	private int seq;
	private ImageIcon img = null;
	
	/**
	 * DataPacket constructor 
	 * @param i ImageIcon
	 * @param se sequence
	 */
	public DataPacket (ImageIcon i, int se) {
		img = i;
		seq = se;
	}
	
	/**
	 * return the sequence
	 * @return sequence
	 */
	public int getSeq() {
		return seq;
	}
	
	/**
	 * return the Image of the ImageIcon
	 * @return Image of the ImageIcon
	 */
	public Image getImg() {
		return img.getImage();
	}

}
