package selenium;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static play.test.Helpers.FIREFOX;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class ProjectsSeleniumTest extends AbstractSeleniumTest {
	private String testProject = "test project";
	private String testDescription = "description of test project";
	
	@Test
	public void createNewProject() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						createTestUser();
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						login(browser, testLogin, testPasswd);
						
						createProject(browser, testProject, testDescription);
						
						assertThat(browser.pageSource().contains("You created a new project"));
						assertThat(browser.pageSource().contains(testProject));
						assertThat(browser.pageSource().contains(testDescription));

						assertThat(browser.pageSource().contains("Tasks"));
						assertThat(browser.pageSource().contains("Mile Stones"));
					}
				});
	}
	
	@Test
	public void duplicateProjectName() {
		running(testServer(9009, fakeApplication(inMemoryDatabase())), FIREFOX,
				new Callback<TestBrowser>() {
					public void invoke(TestBrowser browser) {
						createTestUser();
						browser.goTo("http://localhost:9009");
						
						assertThat(browser.pageSource()).contains("Sign in");
						login(browser, testLogin, testPasswd);
						
						createProject(browser, testProject, testDescription);
						createProject(browser, testProject, testDescription);
						
						assertThat(browser.pageSource().contains("You already have a project with name"));
					}
				});
	}
}
