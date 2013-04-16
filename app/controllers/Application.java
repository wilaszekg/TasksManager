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
			if (User.findByLogin(login) != null) {
				return "Login already in use.";
			}
			if (!password.equals(confPassword)) {
				return "Password not confirmed";
			}
			return null;
		}

	}

	static Form<Login> loginForm = Form.form(Login.class);
	static Form<Register> registerForm = Form.form(Register.class);

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

	public static Result registerWindow() {
		session().clear();
		return ok(views.html.register.render(Form.form(Register.class)));
	}

	public static Result register() {
		Form<Register> filledLoginForm = registerForm.bindFromRequest();
		if (filledLoginForm.hasErrors()) {
			return badRequest(views.html.register.render(filledLoginForm));
		}
		Register registered = filledLoginForm.get();
		User user = new User();
		user.login = registered.login;
		user.password = registered.password;
		user.save();
		
		session("user", user.login);
		
		return ok("SUCCESS");
	}
	
	public static Result logout() {
        session().clear();
        flash("success", "You have signed out.");
        return redirect(
                routes.Application.login()
        );
    }
}
