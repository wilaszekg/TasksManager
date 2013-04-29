import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import models.MileStone;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formats;
import play.data.format.Formatters;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application application) {
		/*
		 * This is for final application version:
		 */

		Formatters.register(User.class, new Formatters.SimpleFormatter<User>() {

			@Override
			public User parse(String arg0, Locale arg1) throws ParseException {
				User user = User.findByLogin(arg0);
				if(user == null) {
					throw new java.text.ParseException("No sych user: " + arg0, 0);
				}
				return user;
			}

			@Override
			public String print(User arg0, Locale arg1) {
				return arg0.toString();
			}

		});
		
		Formatters.register(MileStone.class, new Formatters.SimpleFormatter<MileStone>() {

			@Override
			public MileStone parse(String arg0, Locale arg1) throws ParseException {
				MileStone user = MileStone.findById(Long.parseLong(arg0));
				if(user == null) {
					throw new java.text.ParseException("No such MileStone: " + arg0, 0);
				}
				return user;
			}

			@Override
			public String print(MileStone arg0, Locale arg1) {
				return arg0.name;
			}

		});

		Formatters.register(Date.class, new Formats.DateFormatter(
				MileStone.dateFormat));

		/*
		 * This is for development stage:
		 */
		if (User.findByLogin("user") == null) {
			User u1 = new User();
			u1.login = "user";
			u1.password = "user";
			u1.save();
		}
		
		if (User.findByLogin("jan") == null) {
			User u1 = new User();
			u1.login = "jan";
			u1.password = "jan";
			u1.save();
		}
		
		if (User.findByLogin("stas") == null) {
			User u1 = new User();
			u1.login = "stas";
			u1.password = "stas";
			u1.save();
		}
		
		if (User.findByLogin("ala") == null) {
			User u1 = new User();
			u1.login = "ala";
			u1.password = "ala";
			u1.save();
		}
		
		if (User.findByLogin("julian") == null) {
			User u1 = new User();
			u1.login = "julian";
			u1.password = "julian";
			u1.save();
		}

	}

}
