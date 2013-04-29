package controllers;

import java.util.List;

import models.Contributor;
import models.Project;
import models.Role;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import be.objectify.deadbolt.java.actions.Restrict;

@Security.Authenticated(Secured.class)
public class Contributors extends Controller {


	private static final String JTABLE_RECORDS = "Records";
	private static final String JTABLE_RECORD = "Record";
	private static final String JTABLE_STATUS = "OK";
	private static final String JTABLE_ERROR = "ERROR";
	private static final String JTABLE_MESSAGE = "Message";
	private static final String JTABLE_RESULT = "Result";
	private static final String JTABLE_OPTIONS = "Options";

	static Form<Contributor> form = Form.form(Contributor.class);

	@Restrict("CONTRIBUTOR")
	public static Result contributors(long projectId) {
		return ok(views.html.contributors.render(Project.findById(projectId)));
	}

	@Restrict("CONTRIBUTOR")
	public static Result list(long projectId) {
		List<Contributor> list = Project.findById(projectId).contributors;
		ObjectNode result = getJsonResultOK();
		ArrayNode records = result.putArray(JTABLE_RECORDS);
		for (Contributor contributor : list) {
			records.add(contributor.toJsonObject());
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

	@Restrict("ADMIN")
	public static Result create(long projectId) {
		Form<Contributor> filledForm = form.bindFromRequest();
		if(filledForm.hasErrors()) {
			ObjectNode result = null;
			if(filledForm.errors().containsKey("user")) {
				result = getJsonResultERROR("No such user in the system.");
			} else {
				result = getJsonResultERROR("Unexpected error.");
			}
			return ok(result);
		}
		Contributor newContributor = filledForm.get();
		
		Project project = Project.findById(projectId);
		if(newContributor.user.contributesAt(project)) {
			return ok(getJsonResultERROR("This user already contributes to this project"));
		}
		newContributor.project = project;

		newContributor.save();
		ObjectNode result = getJsonResultOK();
		result.put(JTABLE_RECORD, newContributor.toJsonObject());

		return ok(result);

		

	}

	private static Contributor getContributorFromForm() {
		Form<Contributor> filledForm = form.bindFromRequest();
		Contributor contributor = filledForm.get();

		return contributor;
	}

	@Restrict("ADMIN")
	public static Result update(long projectId) {
		Contributor updatedContributor = getContributorFromForm();
		Contributor persistedContributor = Contributor.findById(updatedContributor.id);
		if(persistedContributor.project.owner == persistedContributor.user && updatedContributor.role != Role.ADMIN) {
			return ok(getJsonResultERROR("This is the owner of this project and will be forever."));
		}
		updatedContributor.update();
		return ok(getJsonResultOK());
	}

	@Restrict("ADMIN")
	public static Result delete(long projectId) {
		Contributor deletedContributor = getContributorFromForm();
		Contributor persistedContributor = Contributor.findById(deletedContributor.id);
		if(persistedContributor.project.owner == persistedContributor.user) {
			return ok(getJsonResultERROR("This is the owner of this project and will be forever."));
		}
		long id = deletedContributor.id;
		Contributor.remove(id);
		return ok(getJsonResultOK());

	}
}
