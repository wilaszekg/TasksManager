package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class HistoryEvent extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long id;
	@Column(columnDefinition="TEXT")
	public String comment;
	public TaskStatus changeTo;
	@ManyToOne
	public User user;
	@ManyToOne
	public Task task;
	public Date date;
	
	public static Finder<Long, HistoryEvent> find = new Finder<Long, HistoryEvent>(Long.class,
			HistoryEvent.class);

	public static List<HistoryEvent> all() {
		return find.all();
	}

	public static void create(HistoryEvent historyEvent) {
		historyEvent.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static HistoryEvent findById(Long id) {
		return find.byId(id);
	}
}
