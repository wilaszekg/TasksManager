package controllers;

import models.User;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	public static class Login {
		@Required
		public String login;
		@Required
		public String password;

	}

	public static class Register {
		@Required
		public String login;
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

	static Form<Login> loginForm = Form.form(Login.class);

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result login() {
		session().clear();
		return ok(views.html.login.render(Form.form(Login.class)));
	}

	public static Result authenticate() {
		Form<Login> filledLoginForm = loginForm.bindFromRequest();
		if (filledLoginForm.hasErrors()) {
			filledLoginForm.reject("Login form has errors.");
			return badRequest(views.html.login.render(filledLoginForm));
		}
		Login login = filledLoginForm.get();
		if (!User.authenticate(login.login, login.password)) {
			filledLoginForm.reject("Wrong login or password."); // setting a
																	// global
																	// error
			return badRequest(views.html.login.render(filledLoginForm));
		} else {
			session("user", login.login);
			// session("role", user.role.name());

			return ok("SUCCESS");

		}
	}
}
