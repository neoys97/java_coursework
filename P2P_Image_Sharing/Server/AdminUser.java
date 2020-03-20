
import org.json.simple.JSONObject;

public class AdminUser extends User{
	
	/**
	 * AdminUser constructor
	 * @param data : JSON Object containing the data of the administrator
	 */
	public AdminUser (JSONObject data) {
		super(data);
	}
	
	/**
	 * AdminUser constructor
	 * @param pw : password of the administrator account
	 */
	public AdminUser (String pw) {
		super("administrator", pw, "Administrator", "admin@admin.com", "0");
	}
	
}
