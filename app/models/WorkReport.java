package models;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class WorkReport extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;
	@JsonIgnore
	@ManyToOne
	public Contributor contributor;
	@JsonIgnore
	@ManyToOne
	public Task task;
	@JsonIgnore
	public Date date;
	public int hoursCount;
	
	public static Finder<Long, WorkReport> find = new Finder<Long, WorkReport>(Long.class,
			WorkReport.class);

	public static List<WorkReport> all() {
		return find.all();
	}

	public static void create(WorkReport workReport) {
		workReport.save();
	}

	public static void remove(Long id) {
		find.ref(id).delete();
	}

	public static WorkReport findById(Long id) {
		return find.byId(id);
	}
	
	@JsonProperty("contributor")
	public String jsonGetContributor() {
		return contributor.user.login;
	}
	
	@JsonProperty("date")
	public String jsonGetDate() {
		Formats.DateFormatter dateFormatter = new Formats.DateFormatter(
				MileStone.dateFormat);
		return dateFormatter.print(date, new Locale("en"));
	}
	
	

}
