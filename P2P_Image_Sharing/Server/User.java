import java.time.LocalDate;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

/**
 * 
 * Public User class that implements Hash Interface
 * 
 * @author Neo Yi Siang
 *
 */
public class User implements Hash{
	
	/** public static array list to store the current users **/
	static public ArrayList<User> userList = new ArrayList<User>();
	
	private String username;
	private String hashedPassword;
	private String fullName;
	private String emailAddress;
	private String phoneNumber;
	private int failedLoginCount = 0;
	private LocalDate lastLogin;
	private boolean accountLocked = false;
	private boolean loggedIn = false;
	
	/** public static String to specify the hashing algorithm **/
	public static String algorithm = "SHA-256";
	private static boolean adminLoggedIn = false;
	
	/**
	 * public static function to read user information from a textfile in JSON format
	 */
	public static void readJSON() {
		String jsonData = "";
		
	    try {
	    	File file = new File("User.txt");

	    	if (!file.exists())
	    		file.createNewFile();

		    FileReader fr = new FileReader(file); 
		    char [] a = new char[(int)file.length()];
		    fr.read(a);
		    
		    jsonData = new String(a);
		    	    
		    fr.close();
	    }
	    catch (IOException  exception) {
	        System.err.println("Error when reading the file");
	    }
	    if (jsonData.length() > 0) {
		    try {
		    	JSONObject jsonObject = new JSONObject();
		    	JSONArray jsonArray = new JSONArray();
		    	JSONParser parser = new JSONParser();
		    	jsonObject = (JSONObject)parser.parse(jsonData);
		    	jsonArray = (JSONArray)jsonObject.get("user_array");
		    	for (Object data : jsonArray) {
		    		jsonObject = (JSONObject)data;
		    		String json_user = (String)jsonObject.get("username");
		    		if (json_user.equals("administrator"))
		    			new AdminUser(jsonObject);
		    		else
		    			new User(jsonObject);
		    	}
		    } 
		    catch (ParseException  exception) {
		        System.err.println("JSON parse error");
		    }
	    }
	}
	
	/**
	 * public static function to write the current user information into a text file in JSON format
	 */
	public static void writeToJSON() {
		File file=new File("User.txt");
		FileOutputStream fileOutputStream=null;
		PrintStream printStream=null;
		String toBeWritten = "{\"user_array\":[";
		for (User p : userList) {
			toBeWritten += "{";
			toBeWritten += "\"username\":\"" + p.username + "\",";
			toBeWritten += "\"hash_password\":\"" + p.hashedPassword + "\",";
			toBeWritten += "\"Full Name\":\"" + p.fullName + "\",";
			toBeWritten += "\"Email\":\"" + p.emailAddress + "\",";
			toBeWritten += "\"Phone number\":" + p.phoneNumber + ",";
			toBeWritten += "\"Fail count\":" + p.failedLoginCount + ",";
			toBeWritten += "\"Last Login Date\":\"" + p.lastLogin + "\",";
			toBeWritten += "\"Account locked\":" + p.accountLocked + "},";
		}
		toBeWritten = toBeWritten.substring(0, toBeWritten.length() - 1) + "]}";
		
		try {
			fileOutputStream=new FileOutputStream(file);
			printStream=new PrintStream(fileOutputStream);
			
			printStream.print(toBeWritten);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * validate the password if it matches the criteria
	 * @param pw : input password
	 * @return : error message, empty string if the password is valid
	 */
	public static String validPassword(String pw) {
		boolean minChar = true;
		boolean hasUpper = false;
		boolean hasLower = false;
		boolean hasNumber = false;
		
		String message = "";
		
		if (pw.length() < 6) {
			minChar = false;
		}
			
		for(int i = 0;i < pw.length();i++) {
	        char ch = pw.charAt(i);
	        if( Character.isDigit(ch)) {
	        	hasNumber = true;
	        }
	        else if (Character.isUpperCase(ch)) {
	        	hasUpper = true;
	        } 
	        else if (Character.isLowerCase(ch)) {
	        	hasLower = true;
	        }
	        
	        if (hasUpper && hasLower && hasNumber) {
	        	break;
	        }
		}
		
		if (!(minChar && hasLower && hasUpper && hasNumber)) {
			
			message = "Your password has to fulfill: at least ";
			
			if (minChar)
				message += "1 small letter, 1 capital letter, 1 digit!";
			else
				message += "6 characters!";
		}
		return message;
	}
	
	/**
	 * set the hashing algorithm
	 * @param algo : algorithm
	 */
	public static void setHashAlgorithm(String algo) {
		algorithm=algo;
	}
	
	/**
	 * User constructor
	 * @param un : username
	 * @param pw : password
	 * @param fn : full name
	 * @param ea : email address
	 * @param pn : phone number
	 */
	public User(String un, String pw, String fn, String ea, String pn) {
		username = un;
		hashedPassword = hash(pw, algorithm);
		fullName = fn;
		emailAddress = ea;
		phoneNumber = pn;
		lastLogin = LocalDate.now();
		userList.add(this);
	}
	
	/**
	 * User constructor
	 * @param data : JSON Object containing the information of user
	 */
	public User(JSONObject data) {
		username = (String)data.get("username");
		hashedPassword = (String)data.get("hash_password");
		fullName = (String)data.get("Full Name");
		emailAddress = (String)data.get("Email");
		phoneNumber = Long.toString((Long)data.get("Phone number"));
		Long temp = (Long)data.get("Fail count");
		failedLoginCount = temp.intValue();
		accountLocked = (boolean)data.get("Account locked");
		lastLogin = LocalDate.parse((String)data.get("Last Login Date"));
		userList.add(this);
	}
	
	/**
	 * log in into the user account
	 * @param pw : input password
	 * @return true if successfully logged in, false other wise
	 */
	public boolean login(String pw) {
		String hashInputPw = hash(pw, algorithm);
		if (hashInputPw.equals(hashedPassword)) {
			successfulLogin();
			return true;
		}
		else {
			failLogin();
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see Hash#hash(java.lang.String, java.lang.String)
	 */
	public String hash (String input, String algo) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance(algo);
			byte[] digest = md.digest(input.getBytes());      
		     
		    StringBuffer hexString = new StringBuffer();
		      
		    for (int i = 0;i<digest.length;i++) {
		       hexString.append(Integer.toHexString(0xFF & digest[i]));
		    }
		    
		    result = hexString.toString();
	    }
	    catch (NoSuchAlgorithmException exception) {
	        System.err.println("Not a valid message digest algorithm");
	    }
		return result;
	}
	
	private void successfulLogin() {
		if (username.equals("administrator")) {
			adminLoggedIn = true;
		}
		failedLoginCount = 0;
		loggedIn = true;
		lastLogin = LocalDate.now();
	}
	
	private void failLogin() {
		failedLoginCount += 1;
		if (failedLoginCount >= 3)
			accountLocked = true;
	}
	
	/**
	 * log out from the current user
	 */
	public void logout() {
		if (username.equals("administrator"))
			adminLoggedIn = false;
		loggedIn = false;
	}
	
	/**
	 * update the user information
	 * @param pw : password
	 * @param fn : full name
	 * @param ea : email address
	 */
	public void updateDetails (String pw, String fn, String ea) {
		if (loggedIn) {
			fullName = fn;
			emailAddress = ea;
			hashedPassword = hash(pw, algorithm);
		}
		else {
			System.out.println("The account is not logged in.");
		}
	}
	
	/**
	 * change the password of the user if administrator account is logged in
	 * @param pw : new password
	 */
	public void changePassword(String pw) {
		if (adminLoggedIn) {
			hashedPassword = hash(pw, algorithm);
			failedLoginCount = 0;
			accountLocked = false;
		}
		else {
			System.out.println ("Cannot change the password. Administrator is not logged in.");
		}
	}
	
	/**
	 * return if the account is locked
	 * @return : true if account is locked, false otherwise
	 */
	public boolean acctLocked() {
		return accountLocked;
	}
	
	/**
	 * get the full name of the accoutn
	 * @return : full name
	 */
	public String getFullName() {
		if (loggedIn)
			return fullName;
		else
			System.out.println("The account is not logged in.");
			return "";
	}
	
	/**
	 * get the email address of the accoutn
	 * @return : email address
	 */
	public String getEmailAddress() {
		if (loggedIn)
			return emailAddress;
		else
			System.out.println("The account is not logged in.");
			return "";
	}
	
	/**
	 * get the phone number of the accoutn
	 * @return : phone number
	 */
	public String getPhoneNumber() {
		if (loggedIn)
			return phoneNumber;
		else
			System.out.println("The account is not logged in.");
			return "";
	}
	
	/**
	 * get the username of the accoutn
	 * @return : username
	 */
	public String getUsername() {
		return username;
	}
}
