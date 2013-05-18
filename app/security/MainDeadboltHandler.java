package security;

import models.Project;
import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import controllers.Contributors;

public class MainDeadboltHandler extends Controller implements DeadboltHandler {

	@Override
	public Result beforeAuthCheck(Http.Context context) {
		return null; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public Subject getSubject(Http.Context context) {
		if (context.session().containsKey("user")) {

			String[] subPaths = context.request().path().split("/");

			// * subPaths should look like: ["", "projects", "id", ... ]

			if (subPaths.length < 3 || !subPaths[1].equals("projects")) {
				return null;
			}
			long projectId = Integer.parseInt(subPaths[2]);
			Project project = Project.findById(projectId);
			User user = User.findByLogin(context.session().get("user"));

			return user.uniqueContribution(project);

		}
		return null;
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(Http.Context context) {
		return null; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public Result onAuthFailure(Context arg0, String arg1) {
		return ok(Contributors
				.getJsonResultERROR("You are not allowed to do this."));
	}
}