package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Project extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public long id;
	@Required
	public String name;
	public String description;
	@ManyToOne
	public User owner;
	@ManyToMany(mappedBy = "projects")
	public List<User> users;
	@OneToMany
	public List<MileStone> mileStones;

	public static Finder<Long, Project> find = new Finder<Long, Project>(
			Long.class, Project.class);

	public static List<Project> all() {
		return find.all();
	}

	public static void create(Project project) {
		project.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static Project findById(Long id) {
		return find.byId(id);
	}

	public static Project findByName(String name, User user) {
		
		List<Project> userProjects = find.where().eq("name", name).where()
				.eq("owner", user).findList();

		if (userProjects == null || userProjects.size() == 0) {
			return null;
		} else if (userProjects.size() == 1) {
			return userProjects.get(0);
		} else {
			Logger.debug("Invalid number of projects with name: " + name
					+ " for user: " + user.login);
			return null;
		}
	}

}
