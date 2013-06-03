package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.MileStone;
import models.Task;
import models.WorkReport;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.format.Formats;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

@Security.Authenticated(Secured.class)
@Restrict(@Group("CONTRIBUTOR"))
public class TaskContributionCharts extends Controller {

	public static Result contributionDates(long projectId, long taskId) {
		Task task = Task.findByTaskNumber(projectId, taskId);
		if (task.project.id != projectId) {
			return forbidden("This task does not belong to this project.");
		}

		List<WorkReport> workReports = task.workReports;
		Formats.DateFormatter dateFormatter = new Formats.DateFormatter(
				MileStone.dateFormat);

		List<Date> dates = new ArrayList<>();
		for (WorkReport workReport : workReports) {
			dates.add(workReport.date);
		}
		Collections.sort(dates);

		List<String> dateStrings = new ArrayList<>();
		for (Date date : dates) {
			String dateString = dateFormatter.print(date, Locale.ENGLISH);
			if (!dateStrings.contains(dateString)) {
				dateStrings.add(dateString);
			}
		}

		Map<String, Integer[]> series = new HashMap<String, Integer[]>();

		for (WorkReport workReport : workReports) {
			String login = workReport.contributor.user.login;
			if (!series.keySet().contains(login)) {
				Integer[] values = new Integer[dateStrings.size()];
				for (int i = 0; i < values.length; i++) {
					values[i] = 0;
				}
				series.put(login, values);
			}
			String dateString = dateFormatter.print(workReport.date, Locale.ENGLISH);
			int index = dateStrings.indexOf(dateString);
			series.get(login)[index] += workReport.hoursCount;
		}
		
		ObjectNode data = Json.newObject();
		data.put("categories", Json.toJson(dateStrings));
		ArrayNode records = data.putArray("series");
		for(String login : series.keySet()) {
			ObjectNode contribution = Json.newObject();
			contribution.put("name", login);
			contribution.put("data", Json.toJson(series.get(login)));
			records.add(contribution);
		}

		return ok(data);
	}
}
