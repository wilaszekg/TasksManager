import models.User;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application application) {
		/*
		 * This is for final application version:
		 */

		/*
		 * This is for development stage:
		 */
		if (User.findByLogin("user") == null) {
			User u1 = new User();
			u1.login = "user";
			u1.password = "user";
			u1.save();
		}

	}

}
