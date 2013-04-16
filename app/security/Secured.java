package security;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import controllers.routes;

public class Secured extends Security.Authenticator {
	/**
	 * Returns a login of current user.
	 * 
	 * @param ctx
	 * @return
	 */
	@Override
	public String getUsername(Context ctx) {
		return ctx.session().get("user");
	}

	/**
	 * Redirects to login site when user wasn't authorised.
	 * 
	 * @param ctx
	 * @return
	 */
	@Override
	public Result onUnauthorized(Context ctx) {
		return Results.redirect(routes.Application.login());
	}

}
