package unitTests.controllers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.mvc.Result;
import play.test.FakeRequest;


public class ApplicationTest extends AbstractTestEnvironment {
	
	
	
	@Test
	public void authenticateSuccess() {
		
		
		final Map<String, String> data = new HashMap<String, String>();
        data.put("login", testLogin);
        data.put("password", testPasswd);
		FakeRequest fakeRequest = new FakeRequest().withFormUrlEncodedBody(data);
		
		Result result = callAction(controllers.routes.ref.Application.authenticate(), fakeRequest);
		assertEquals(SEE_OTHER, status(result));
	}
	
	@Test
	public void authenticateWrongPassword() {
		final Map<String, String> data = new HashMap<String, String>();
        data.put("login", testLogin);
        data.put("password", testPasswd + "garbage");
		FakeRequest fakeRequest = new FakeRequest().withFormUrlEncodedBody(data);
		
		Result result = callAction(controllers.routes.ref.Application.authenticate(), fakeRequest);
		assertEquals(BAD_REQUEST, status(result));
	}
	
	@Test
	public void authenticateMissingPassword() {
		final Map<String, String> data = new HashMap<String, String>();
        data.put("login", testLogin);
		FakeRequest fakeRequest = new FakeRequest().withFormUrlEncodedBody(data);
		
		Result result = callAction(controllers.routes.ref.Application.authenticate(), fakeRequest);
		assertEquals(BAD_REQUEST, status(result));
	}
	
	@Test
	public void registerSuccess() {
		final Map<String, String> data = new HashMap<String, String>();
        data.put("login", testNewLogin);
        data.put("password", testNewPasswd);
        data.put("confPassword", testNewPasswd);
		FakeRequest fakeRequest = new FakeRequest().withFormUrlEncodedBody(data);
		
		Result result = callAction(controllers.routes.ref.Application.register(), fakeRequest);
		assertEquals(SEE_OTHER, status(result));
	}
	
	@Test
	public void registerFail() {
		final Map<String, String> data = new HashMap<String, String>();
        data.put("login", testLoginNotExist);
        data.put("password", testNewPasswd);
		FakeRequest fakeRequest = new FakeRequest().withFormUrlEncodedBody(data);
		
		Result result = callAction(controllers.routes.ref.Application.register(), fakeRequest);
		assertEquals(BAD_REQUEST, status(result));
	}
}
