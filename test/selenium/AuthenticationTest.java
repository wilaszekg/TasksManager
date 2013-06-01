package selenium;
import models.User;

import org.junit.*;

import play.Logger;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class AuthenticationTest extends AbstractSeleniumTest {
	/**
	 * add your integration test here in this example we just check if the
	 * welcome page is being shown
	 */
	@Test
	public void loginSuccess() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						createTestUser();
						
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						
						login(browser, testLogin, testPasswd);
						
						assertThat(browser.pageSource().contains("Logged in as"));
						
						logout(browser);
						
						assertThat(browser.pageSource().contains("You have signed out"));
					}
				});
	}
	
	@Test
	public void loginInvalidCredentials() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						
						login(browser, "non-exist", "non-exist");
						
						assertThat(browser.pageSource().contains("Wrong login or password"));
					}
				});
	}
	
	@Test
	public void loginMissingCredentials() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						
						login(browser, "", "");
						
						assertThat(browser.pageSource().contains("Login form has errors"));
					}
				});
	}
	
	@Test
	public void registerSuccess() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						browser.$("a", withText("Register")).click();
						
						register(browser, "new-user", "new-user", "new-user");
						
						assertThat(browser.pageSource().contains("Logged in as"));
					}
				});
	}
	
	@Test
	public void registerLoginInUse() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						createTestUser();
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						browser.$("a", withText("Register")).click();
						
						register(browser, testLogin, testPasswd, testPasswd);
						
						assertThat(browser.pageSource().contains("Login already in use"));
					}
				});
	}

}
