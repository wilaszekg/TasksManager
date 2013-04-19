import java.text.ParseException;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import models.Project;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application application) {
		/*
		 * This is for final application version:
		 */
		
		Formatters.register(User.class, new Formatters.SimpleFormatter<User>(){

			@Override
			public User parse(String arg0, Locale arg1)
					throws ParseException {
				return User.findByLogin(arg0);
			}

			@Override
			public String print(User arg0, Locale arg1) {
				return arg0.toString();
			}
			
		});

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
