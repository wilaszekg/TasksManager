package selenium;

import models.User;
import play.test.TestBrowser;
import static org.fluentlenium.core.filter.FilterConstructor.*;

public abstract class AbstractSeleniumTest {
	protected String testLogin = "test";
	protected String testPasswd = "test";
	
	public void login(TestBrowser browser, String login, String password) {
		browser.$("#login").text(login);
		browser.$("#password").text(password);
		browser.$(".btn.btn-large.btn-primary").click();
	}
	
	public void logout(TestBrowser browser) {
		browser.$("a", withText("Logout")).click();
	}
	
	public void register(TestBrowser browser, String login, String password, String confPassword) {
		browser.$("#login").text(login);
		browser.$("#password").text(password);
		browser.$("#confPassword").text(confPassword);
		browser.$(".btn.btn-large.btn-primary").click();
	}
	
	public void createProject(TestBrowser browser, String projectName, String description) {
		browser.$(".btn.btn-primary", withText("Add new project")).click();
		browser.$("#name").text(projectName);
		browser.$("#description").text(description);
		browser.$("button", withText("Create project"));
	}
	
	protected void createTestUser() {
		User user = new User();
		user.login = testLogin;
		user.password = testPasswd;
		user.save();
	}
}
