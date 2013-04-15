package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String login;

	public String password;

	public static Model.Finder<String, User> find = new Model.Finder<String, User>(
			String.class, User.class);

	/**
	 * Checks if there exist a user with given credentials. Probably it will be
	 * moved to outside authentication service.
	 * 
	 * @param login
	 * @param password
	 * @return true when there exist a user with login & password given
	 */
	public static boolean authenticate(String login, String password) {
		User user = findByLogin(login);
		if (user == null)
			return false;

		if (login.equals(user.login) && password.equals(user.password))
			return true;

		return false;
	}

	public static List<User> all() {
		return find.all();
	}

	public static void create(User user) {
		user.save();
	}

	public static void remove(String login) {
		find.ref(login).delete();
	}

	public static User findByLogin(String uLogin) {
		return find.byId(uLogin);
	}

}
