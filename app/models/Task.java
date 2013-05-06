package models;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.validation.NotNull;

import play.data.format.Formats;
import play.db.ebean.Model;
import play.libs.Json;

@Entity
public class Task extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;
	public long taskNumber;
	public String name;
	public String description;
	@Enumerated
	public Priority priority;
	@Enumerated
	public TaskKind taskKind;
	public Date creationDate;
	public Date dueDate;
	@ManyToOne
	public User creator;
	@ManyToOne
	public User assignee;
	@ManyToOne
	public MileStone mileStone;
	@ManyToOne
	public Project project;
	@Enumerated
	@NotNull
	public TaskStatus taskStatus;

	@Transient
	public static final String TASK_ID = "id";
	@Transient
	public static final String TASK_NAME = "name";
	@Transient
	public static final String TASK_DESCR = "description";
	@Transient
	public static final String TASK_PRIORITY = "priority";
	@Transient
	public static final String TASK_KIND = "taskKind";
	@Transient
	public static final String TASK_CREATED = "creationDate";
	@Transient
	public static final String TASK_DUE = "dueDate";
	@Transient
	public static final String TASK_MSTONE = "mileStone";
	@Transient
	public static final String TASK_CREATOR = "creator";
	@Transient
	public static final String TASK_ASSIGNEE = "assignee";
	@Transient
	public static final String TASK_PROJECT = "project";
	@Transient
	public static final String TASK_STATUS = "taskStatus";
	@Transient
	public static final String TASK_NUMBER= "taskNumber";

	public static Finder<Long, Task> find = new Finder<Long, Task>(Long.class,
			Task.class);

	public static List<Task> all() {
		return find.all();
	}

	public static void create(Task task) {
		task.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static Task findById(Long id) {
		return find.byId(id);
	}
	
	public static Task findByTaskNumber(long projectId, long taskNumber) {
		return find.where().eq("project.id", projectId).eq("taskNumber", taskNumber).findUnique();
	}
	
	public void prepereTaskNumber() {
		List<Task> tasks = find.where().setOrderBy("taskNumber DESC").findList();
		if(tasks.size() > 0) {
			taskNumber = tasks.get(0).taskNumber + 1;
		} else {
			taskNumber = 1;
		}
	}

	public ObjectNode toJsonObject() {
		Formats.DateFormatter dateFormatter = new Formats.DateFormatter(
				MileStone.dateFormat);
		ObjectNode node = Json.newObject();
		node.put(TASK_ID, id);
		node.put(TASK_NAME, name);
		node.put(TASK_DESCR, description);
		node.put(TASK_PRIORITY, priority.name());
		node.put(TASK_KIND, taskKind.name());
		
		node.put(TASK_CREATED, creationDate == null ? "" : dateFormatter.print(creationDate, new Locale("en")));
		node.put(TASK_DUE, dueDate == null ? "" : dateFormatter.print(dueDate, new Locale("en")));
		
		if (mileStone == null) {
			node.put(TASK_MSTONE, "");
		} else {
			node.put(TASK_MSTONE, mileStone.id);
		}
		
		node.put(TASK_CREATOR, creator == null ? "" : creator.login);
		node.put(TASK_ASSIGNEE, assignee == null ? "" : assignee.login);
		node.put(TASK_STATUS, taskStatus.name());
		node.put(TASK_NUMBER, taskNumber);
		
		return node;
	}
}
