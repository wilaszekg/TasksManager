package controllers;

import controllers.Application.Login;
import models.User;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;

@Security.Authenticated(Secured.class)
public class UserPanel extends Controller {
	
	public static class ResetPassword {
		@Required
		public String password;
		@Required
		public String confPassword;
		
		public String validate() {
			if (!password.equals(confPassword)) {
				return "Password not confirmed";
			}
			return null;
		}
	}
	
	static Form<ResetPassword> passwordForm = Form.form(ResetPassword.class);
	
	public static Result panelMain() {
		User user = User.findByLogin(session("user"));
		return ok(views.html.userPanel.render(user, passwordForm));
	}
	
	public static Result changePassword() {
		Form<ResetPassword> form = passwordForm.bindFromRequest();
		User user = User.findByLogin(session("user"));
		if(form.hasErrors()) {
			return badRequest(views.html.userPanel.render(user, form));
		}
		
		user.password = form.get().password;
		user.update();
		
		flash("success", "Password changed");
		
		return ok(views.html.userPanel.render(user, passwordForm));
	}

}
