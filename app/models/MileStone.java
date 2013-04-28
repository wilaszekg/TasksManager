package models;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.node.ObjectNode;

import play.data.format.Formats;
import play.db.ebean.Model;
import play.libs.Json;

@Entity
public class MileStone extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String dateFormat = "yyyy-MM-dd";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;
	public String name;
	public Date creationDate;
	public Date dueDate;
	@ManyToOne
	public Project project;
	
	
	@Transient
	public static final String MSTONE_ID = "id";
	@Transient
	public static final String MSTONE_NAME = "name";
	@Transient
	public static final String MSTONE_CREATED = "creationDate";
	@Transient
	public static final String MSTONE_DUE = "dueDate";
	@Transient
	public static final String MSTONE_PROJECT = "project";

	public static Finder<Long, MileStone> find = new Finder<Long, MileStone>(
			Long.class, MileStone.class);

	public static List<MileStone> all() {
		return find.all();
	}

	public static void create(MileStone mileStone) {
		mileStone.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static MileStone findById(Long id) {
		return find.byId(id);
	}

	public static List<MileStone> all(Project project) {
		return find.where().eq("project", project).findList();
	}

	public ObjectNode toJsonObject() {
		Formats.DateFormatter dateFormatter = new Formats.DateFormatter(dateFormat);
		
		// TODO: it would be better to use default formatter
		// Maybe it would be enough: Formatters.print(creationDate)
		
		ObjectNode node = Json.newObject();
		node.put(MSTONE_ID, id);
		node.put(MSTONE_NAME, name);
		node.put(MSTONE_CREATED, dateFormatter.print(creationDate, new Locale("en")));
		if (dueDate == null) {
			node.put(MSTONE_DUE, "");
		} else {
			node.put(MSTONE_DUE, dateFormatter.print(dueDate, new Locale("en")));
		}
		node.put(MSTONE_PROJECT, project.id);
		return node;
	}
}
