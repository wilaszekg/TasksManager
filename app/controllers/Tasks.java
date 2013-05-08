package controllers;

import java.util.Date;
import java.util.List;

import models.Contributor;
import models.HistoryEvent;
import models.MileStone;
import models.Project;
import models.Task;
import models.TaskStatus;
import models.User;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import be.objectify.deadbolt.java.actions.Restrict;

@Security.Authenticated(Secured.class)
@Restrict("CONTRIBUTOR")
public class Tasks extends Controller {

	private static final String JTABLE_RECORDS = "Records";
	private static final String JTABLE_RECORD = "Record";
	private static final String JTABLE_STATUS = "OK";
	private static final String JTABLE_ERROR = "ERROR";
	private static final String JTABLE_MESSAGE = "Message";
	private static final String JTABLE_RESULT = "Result";
	private static final String JTABLE_OPTIONS = "Options";

	static Form<Task> form = Form.form(Task.class);

	public static Result tasks(long projectId) {
		return ok(views.html.tasks.render(Project.findById(projectId)));
	}

	public static Result list(long projectId) {
		DynamicForm form = Form.form().bindFromRequest();

		List<Task> list = Project.findById(projectId).allTasks(
				form.get("mileStoneId"), form.get("statusId"),
				form.get("contributorId"), form.get("priorityId"),
				form.get("taskTypeId"), form.get("creatorId"),
				form.get("jtSorting"));
		ObjectNode result = getJsonResultOK();
		ArrayNode records = result.putArray(JTABLE_RECORDS);
		for (Task task : list) {
			records.add(task.toJsonObject());
		}
		return ok(result);
	}

	private static ObjectNode getJsonResultOK() {
		ObjectNode result = Json.newObject();
		result.put(JTABLE_RESULT, JTABLE_STATUS);
		return result;
	}

	public static ObjectNode getJsonResultERROR(String message) {
		ObjectNode result = Json.newObject();
		result.put(JTABLE_RESULT, JTABLE_ERROR);
		result.put(JTABLE_MESSAGE, message);
		return result;
	}

	public static Result create(long projectId) {
		Form<Task> filledForm = form.bindFromRequest();
		if (filledForm.hasErrors()) {
			ObjectNode result = null;
			if (filledForm.errors().containsKey("user")) {
				result = getJsonResultERROR("No such user in the system.");
			} else {
				result = getJsonResultERROR("Unexpected error.");
			}
			return ok(result);
		}
		Task newTask = filledForm.get();

		Project project = Project.findById(projectId);

		newTask.project = project;
		newTask.creationDate = new Date();
		newTask.creator = User.findByLogin(session("user"));
		newTask.taskStatus = TaskStatus.OPENED;
		newTask.prepereTaskNumber();

		if (newTask.dueDate == null) {
			if (newTask.mileStone != null) {
				newTask.dueDate = newTask.mileStone.dueDate;
			}
		}

		newTask.save();
		ObjectNode result = getJsonResultOK();
		result.put(JTABLE_RECORD, newTask.toJsonObject());

		return ok(result);

	}

	private static Task getTaskFromForm() {
		Form<Task> filledForm = form.bindFromRequest();
		Task task = filledForm.get();

		return task;
	}

	public static Result update(long projectId) {
		Task updatedTask = getTaskFromForm();
		updatedTask.update();
		return ok(getJsonResultOK());
	}

	public static Result delete(long projectId) {
		Task deletedTask = getTaskFromForm();
		long id = deletedTask.id;
		Task.remove(id);
		return ok(getJsonResultOK());

	}

	public static Result mileStonesOptions(long projectId) {
		ObjectNode result = getJsonResultOK();
		ArrayNode options = result.putArray(JTABLE_OPTIONS);

		ObjectNode nodeUnassigned = Json.newObject();
		nodeUnassigned.put("Value", "");
		nodeUnassigned.put("DisplayText", "");

		options.add(nodeUnassigned);

		for (MileStone type : Project.findById(projectId).mileStones) {
			options.add(type.asJsonOption());
		}
		return ok(result);
	}

	public static Result contributorsOptions(long projectId) {
		ObjectNode result = getJsonResultOK();
		ArrayNode options = result.putArray(JTABLE_OPTIONS);

		ObjectNode nodeUnassigned = Json.newObject();
		nodeUnassigned.put("Value", "");
		nodeUnassigned.put("DisplayText", "");

		options.add(nodeUnassigned);

		for (Contributor contributor : Project.findById(projectId).contributors) {
			options.add(contributor.user.asJsonOption());
		}
		return ok(result);
	}

	public static Result taskSite(long projectId, long taskId) {
		Task task = Task.findByTaskNumber(projectId, taskId);
		if(task.project.id != projectId) {
			return forbidden("This task does not belong to this project.");
		}
		if (task == null) {
			return badRequest("Page not found");
		}
		return ok(views.html.taskSite.render(task));
	}

	@Transactional
	public static Result closeTask(long projectId, long taskId) {
		Task task = Task.findByTaskNumber(projectId, taskId);
		if(task.project.id != projectId) {
			return forbidden("This task does not belong to this project.");
		}
		if(task.taskStatus == TaskStatus.CLOSED) {
			return ok(views.html.taskSite.render(task));
		}
		HistoryEvent historyEvent = newHistoryEvent(task);
		historyEvent.changeTo = TaskStatus.CLOSED;
		
		historyEvent.save();
		
		task.taskStatus = TaskStatus.CLOSED;
		task.update();
		
		return ok(views.html.taskSite.render(task));
	}

	@Transactional
	public static Result reopenTask(long projectId, long taskId) {
		Task task = Task.findByTaskNumber(projectId, taskId);
		if(task.project.id != projectId) {
			return forbidden("This task does not belong to this project.");
		}
		if(task.taskStatus == TaskStatus.OPENED) {
			return ok(views.html.taskSite.render(task));
		}
		HistoryEvent historyEvent = newHistoryEvent(task);
		historyEvent.changeTo = TaskStatus.OPENED;
		
		historyEvent.save();
		
		task.taskStatus = TaskStatus.OPENED;
		task.update();
		
		return ok(views.html.taskSite.render(task));
	}

	public static Result commentTask(long projectId, long taskId) {
		Task task = Task.findByTaskNumber(projectId, taskId);
		DynamicForm commentForm = Form.form().bindFromRequest();
		String comment = commentForm.get("comment");
		if(comment == null || comment.trim().equals("")) {
			// TODO: intorm about reject
			return badRequest(views.html.taskSite.render(task));
		}
		if(task.project.id != projectId) {
			return forbidden("This task does not belong to this project.");
		}
		HistoryEvent historyEvent = newHistoryEvent(task);
		historyEvent.comment = comment;
		
		historyEvent.save();
		
		return ok(views.html.taskSite.render(task));
	}
	
	private static HistoryEvent newHistoryEvent(Task task) {
		HistoryEvent historyEvent = new HistoryEvent();
		historyEvent.user = User.findByLogin(session("user"));
		historyEvent.date = new Date();
		historyEvent.task = task;
		
		return historyEvent;
	}
}
