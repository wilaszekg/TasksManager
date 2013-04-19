package controllers;

import models.Project;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import views.html.projects;

@Security.Authenticated(Secured.class)
public class Projects extends Controller {
	
	static Form<Project> projectForm = Form.form(Project.class);

	public static Result myProjects(){
		User user = User.findByLogin(session("user"));
		return ok(projects.render(Form.form(Project.class), user.ownedProjects));
	}
	
	public static Result addProject(){
		Form<Project> filledForm = projectForm.bindFromRequest();
		filledForm.data().put("owner", User.findByLogin(session("user")).login);

		User user = User.findByLogin(session("user"));
		
		if(filledForm.hasErrors()){
			filledForm.reject("The project creating form was not correct.");
			return badRequest(projects.render(filledForm, user.ownedProjects));
		}
		Project project = filledForm.get();
		
		if(Project.findByName(project.name, user) != null) {
			filledForm.reject("You already have a project with name " + project.name);
			return badRequest(projects.render(filledForm, user.ownedProjects));
		}
		
		project.owner = user;
		project.save();
		flash("success", "You created a new project: " + project.name);
		return ok(projects.render(Form.form(Project.class), user.ownedProjects));
		
	}
}
