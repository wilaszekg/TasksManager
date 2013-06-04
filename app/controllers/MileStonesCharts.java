package controllers;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.HistoryEvent;
import models.MileStone;
import models.Task;
import models.TaskStatus;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.format.Formats;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class MileStonesCharts extends Controller {

	final static long milisPerDay = 1000 * 60 * 60 * 24l;

	@SuppressWarnings("deprecation")
	public static Result mileStoneBurndown(long projectId, long mileStoneId) throws ParseException {
		MileStone mileStone = MileStone.findById(mileStoneId);
		if (mileStone.project.id != projectId) {
			return forbidden("This mile stone is not a part of of this project.");
		}

		Formats.DateFormatter dateFormatter = new Formats.DateFormatter(
				MileStone.dateFormat);

		Calendar start = Calendar.getInstance();
		start.setTime(mileStone.creationDate);
		
		Calendar end = Calendar.getInstance();
		end.setTime(mileStone.dueDate);
		
		ObjectNode data = Json.newObject();
		
		ObjectNode downLine = data.putObject("downLine");
		ObjectNode downStart = downLine.putObject("start");
		ObjectNode downEnd = downLine.putObject("end");
		
		// looking for the oldest close event
		for (Task task : mileStone.tasks) {
			for (HistoryEvent historyEvent : task.historyEvents) {
				if (historyEvent.changeTo == TaskStatus.CLOSED
						&& historyEvent.date.compareTo(end.getTime()) > 0) {
					end.setTime(historyEvent.date);
				}
			}
		}
		
		downStart.put("year", start.get(Calendar.YEAR));
		downStart.put("month", start.get(Calendar.MONTH));
		downStart.put("day", start.get(Calendar.DAY_OF_MONTH));
		downStart.put("count", mileStone.tasks.size());
		
		downEnd.put("year", end.get(Calendar.YEAR));
		downEnd.put("month", end.get(Calendar.MONTH));
		downEnd.put("day", end.get(Calendar.DAY_OF_MONTH));

		Map<String, Integer> tasksClosedPerDay = new HashMap<String, Integer>();
		// add first day:
		tasksClosedPerDay.put(dateFormatter.print(mileStone.creationDate, Locale.ENGLISH), 0);
		for(Task task : mileStone.tasks) {
			if(task.taskStatus == TaskStatus.CLOSED) {
				List<Date> closeEvents = new LinkedList<>();
				for(HistoryEvent historyEvent : task.historyEvents) {
					if(historyEvent.changeTo == TaskStatus.CLOSED) {
						closeEvents.add(historyEvent.date);
					}
				}
				Collections.sort(closeEvents);
				Date lastDate = closeEvents.get(closeEvents.size() - 1);
				String dateString = dateFormatter.print(lastDate, Locale.ENGLISH);
				if(!tasksClosedPerDay.containsKey(dateString)) {
					tasksClosedPerDay.put(dateString, 0);
				}
				int newamount = tasksClosedPerDay.get(dateString) + 1;
				tasksClosedPerDay.put(dateString, newamount); 
			}
		}
		
		List<Date> tasksClosedDates = new LinkedList<>();
		for(String key : tasksClosedPerDay.keySet()) {
			tasksClosedDates.add(dateFormatter.parse(key, Locale.ENGLISH));
		}
		Collections.sort(tasksClosedDates);
		
		int previousTasksCount = mileStone.tasks.size(); 
		ArrayNode arrayProgress = data.putArray("progress");
		
		for(Date date : tasksClosedDates) {
			String key = dateFormatter.print(date, Locale.ENGLISH);
			int count = tasksClosedPerDay.get(key);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			ObjectNode node = arrayProgress.addObject();
			node.put("year", calendar.get(Calendar.YEAR));
			node.put("month", calendar.get(Calendar.MONTH));
			node.put("day", calendar.get(Calendar.DAY_OF_MONTH));
			node.put("count", previousTasksCount - count);
			previousTasksCount -= count;
		}
		/*
		// making array of all days
		List<String> timeArray = new LinkedList<>();
		Date tmp = start;
		while (tmp.compareTo(end) < 0) {
			timeArray.add(dateFormatter.print(tmp, Locale.ENGLISH));
			long nextDay = tmp.getTime() + milisPerDay;
			tmp.setTime(nextDay);
		}
		
		// the last day could be not added:
		String lastDay = dateFormatter.print(tmp, Locale.ENGLISH);
		if(!timeArray.contains(lastDay)) {
			timeArray.add(lastDay);
		}*/
		
		
		return ok(data);
	}

}
