package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.Model;
import play.libs.Json;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Subject;

@Entity
public class Contributor extends Model implements Subject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long id;
	@ManyToOne
	public User user;
	@ManyToOne
	public Project project;
	@Enumerated
	public Role role;
	
	
	@Transient
	public static final String CONTRIB_ID = "id";
	@Transient
	public static final String CONTRIB_USER = "user";
	@Transient
	public static final String CONTRIB_PROJECT = "project";
	@Transient
	public static final String CONTRIB_ROLE = "role";
	
	public static Finder<Long, Contributor> find = new Finder<Long, Contributor>(
			Long.class, Contributor.class);

	public static List<Contributor> all() {
		return find.all();
	}
	
	public static void create(Contributor contributor) {
		contributor.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static Contributor findById(Long id) {
		return find.byId(id);
	}
	
	public ObjectNode toJsonObject() {
		ObjectNode node = Json.newObject();
		node.put(CONTRIB_ID, id);
		node.put(CONTRIB_USER, user.login);
		node.put(CONTRIB_PROJECT, project.id);
		node.put(CONTRIB_ROLE, role.name());
		return node;
	}

	@Override
	public String getIdentifier() {
		return Long.toString(id);
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return null;
	}

	@Override
	public List<? extends be.objectify.deadbolt.core.models.Role> getRoles() {
		List<Role> list = new ArrayList<Role>();
        list.add(role);
        /*
         * ADMIN is also a CONTRIBUTOR
         */
        if(role == Role.ADMIN) {
        	list.add(Role.CONTRIBUTOR);
        }
        return list;
	}
	
}
