package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;

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
	@OneToMany(mappedBy = "project")
	public List<Contributor> contributors;
	@OneToMany(mappedBy = "project")
	public List<MileStone> mileStones;
	@OneToMany(mappedBy = "project")
	public List<Task> tasks;

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

	public List<Contributor> activeContributors() {
		return Contributor.find.where().eq("project", this)
				.in("role", Role.ADMIN, Role.CONTRIBUTOR).findList();
	}

	public Contributor contributorByUser(User user) {
		return Contributor.find.where().eq("project", this).eq("user", user).findUnique();
	}

	public List<Task> allTasks(String mileStoneId, String statusId,
			String contributorId, String priorityId, String taskTypeId,
			String creatorId, String sorting) {

		/*
		 * TODO: It could be replaced by setting ID's of <select> components in
		 * the view to names of Task's attributes. Then automatic
		 * bindFromRequest would set not-null values (but what with eg. user set
		 * to "" ? ) At the end addind to the filter would be done in a loop, no
		 * like this:
		 */
		ExpressionList<Task> filter = Task.find.where().eq("project", this);
		if (!mileStoneId.equals("") && mileStoneId != null) {
			try {
				long id = Long.parseLong(mileStoneId);
				MileStone mileStone = MileStone.findById(id);
				if (mileStone == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("mileStone", mileStone));
			} catch (Exception e) {
				// also NumberFormatException
				Logger.error("Invalid format of mile stone id: " + mileStoneId);
			}
		}
		if (!statusId.equals("") && statusId != null) {
			try {
				TaskStatus taskStatus = TaskStatus.valueOf(statusId);
				if (taskStatus == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("taskStatus", taskStatus));
			} catch (Exception e) {
				Logger.error("Invalid format of status id: " + statusId);
			}
		}
		if (!contributorId.equals("") && contributorId != null) {
			try {
				User user = User.findByLogin(contributorId);
				if (user == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("assignee", user));
			} catch (Exception e) {
				Logger.error("Invalid format of assignee id: " + contributorId);
			}
		}
		if (!priorityId.equals("") && priorityId != null) {
			try {
				Priority priority = Priority.valueOf(priorityId);
				if (priority == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("priority", priority));
			} catch (Exception e) {
				Logger.error("Invalid format of priority id: " + contributorId);
			}
		}
		if (!taskTypeId.equals("") && taskTypeId != null) {
			try {
				TaskKind taskKind = TaskKind.valueOf(taskTypeId);
				if (taskKind == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("taskKind", taskKind));
			} catch (Exception e) {
				Logger.error("Invalid format of taskKind id: " + contributorId);
			}
		}
		if (!creatorId.equals("") && creatorId != null) {
			try {
				User user = User.findByLogin(creatorId);
				if (user == null) {
					throw new Exception();
				}
				filter.add(Expr.eq("creator", user));
			} catch (Exception e) {
				Logger.error("Invalid format of creator id: " + contributorId);
			}
		}
		if (!sorting.equals("") && sorting != null) {
			filter.setOrderBy(sorting);
		}
		return filter.findList();
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
