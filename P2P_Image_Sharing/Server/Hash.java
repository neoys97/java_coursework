
/**
 * 
 * public Hash interface
 * 
 * @author Neo Yi Siang
 *
 */
public interface Hash {
	/**
	 * hash the input with the specified hashing algorithm
	 * @param input : input to be hashed
	 * @param algo : hashing algorithm used
	 * @return hashed result
	 */
	public String hash (String input, String algo);
}
