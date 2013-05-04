package controllers;

import be.objectify.deadbolt.java.actions.Restrict;
import models.Contributor;
import models.Project;
import models.Role;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import views.html.milestones;
import views.html.projects;

@Security.Authenticated(Secured.class)
public class Projects extends Controller {
	
	static Form<Project> projectForm = Form.form(Project.class);

	public static Result myProjects(){
		User user = User.findByLogin(session("user"));
		return ok(projects.render(Form.form(Project.class), user.ownedProjects, user.contributedProjects()));
	}
	
	@Restrict("CONTRIBUTOR")
	public static Result mileStones(long projectId) {
		return ok(milestones.render(Project.findById(projectId)));
	}
	
	public static Result addProject(){
		Form<Project> filledForm = projectForm.bindFromRequest();
		filledForm.data().put("owner", User.findByLogin(session("user")).login);

		User user = User.findByLogin(session("user"));
		
		if(filledForm.hasErrors()){
			filledForm.reject("The project creating form was not correct.");
			return badRequest(projects.render(filledForm, user.ownedProjects, user.contributedProjects()));
		}
		Project project = filledForm.get();
		
		if(Project.findByName(project.name, user) != null) {
			filledForm.reject("You already have a project with name " + project.name);
			return badRequest(projects.render(filledForm, user.ownedProjects, user.contributedProjects()));
		}
		
		project.owner = user;
		project.save();
		
		Contributor contributor = new Contributor();
		contributor.role = Role.ADMIN;
		contributor.user = user;
		contributor.project = project;
		contributor.save();
		
		
		flash("success", "You created a new project: " + project.name);
		return ok(projects.render(Form.form(Project.class), user.ownedProjects, user.contributedProjects()));
		
	}
	
	@Restrict("CONTRIBUTOR")
	public static Result projectMain(long id) {
		return redirect(routes.Tasks.tasks(id));
	}
}
