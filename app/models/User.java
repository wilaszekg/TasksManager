package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;

@Entity
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String login;

	public String password;

	@OneToMany(mappedBy = "owner")
	public List<Project> ownedProjects;

	@OneToMany(mappedBy = "user")
	public List<Contributor> contributions;

	@OneToMany
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
	
	public List<Project> contributedProjects() {
		return Project.find.fetch("contributors").where().eq("contributors.user", this).findList();
	}

	public boolean contributesAt(Project project) {
		List<Contributor> list = contributions(project);
		if (list.size() > 1) {
			Logger.error("User: " + this.login + " contributes at project: "
					+ project.id + " (" + project.name + ") " + list.size()
					+ " times.");
		}
		if(list.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public List<Contributor> contributions(Project project) {
		return Contributor.find.where().eq("user", this)
				.eq("project", project).findList();
	}
	
	public Contributor uniqueContribution(Project project) {
		List<Contributor> list = contributions(project);
		if (list.size() > 1) {
			Logger.error("User: " + this.login + " contributes at project: "
					+ project.id + " (" + project.name + ") " + list.size()
					+ " times.");
		}
		if(list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	public ObjectNode asJsonOption(){
        ObjectNode node = Json.newObject();
        node.put("Value", login);
        // TODO: add extra info in DisplayText
        node.put("DisplayText", login);
        return node;
    }

}
