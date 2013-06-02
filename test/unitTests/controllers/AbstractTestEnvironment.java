package unitTests.controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;
import models.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.FakeApplication;

public abstract class AbstractTestEnvironment {
	protected static String testLogin = "testLogin";
	protected static String testPasswd = "testLogin";
	protected static String testLoginNotExist = "non-existing";
	protected static String testNewLogin = "testNewLogin";
	protected static String testNewPasswd = "testNewLogin";
	
	protected static FakeApplication fakeApplication;
	
	@BeforeClass
	public static void setFakeApplication() {
		fakeApplication = fakeApplication(inMemoryDatabase());
		start(fakeApplication);
		User user = new User();
		user.login = testLogin;
		user.password = testPasswd;
		user.save();
	}
	
	@AfterClass
	public static void desposeApplication() {
		stop(fakeApplication);
	}
}
