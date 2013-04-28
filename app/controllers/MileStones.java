package controllers;

import java.util.Date;
import java.util.List;

import models.MileStone;
import models.Project;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import be.objectify.deadbolt.java.actions.Restrict;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;

@Security.Authenticated(Secured.class)
@Restrict("CONTRIBUTOR")
public class MileStones extends Controller{
	

    private static final String JTABLE_RECORDS = "Records";
    private static final String JTABLE_RECORD = "Record";
    private static final String JTABLE_STATUS = "OK";
    private static final String JTABLE_RESULT = "Result";
    private static final String JTABLE_OPTIONS = "Options";
    
    static Form<MileStone> form = Form.form(MileStone.class);
    
    
    public static Result list(long projectId) {
    	
        //List<MileStone> list = MileStone.all(Project.findById(projectId));
    	List<MileStone> list = Project.findById(projectId).mileStones;
        ObjectNode result = getJsonResultOK();
        ArrayNode records = result.putArray(JTABLE_RECORDS);
        for (MileStone mileStone : list) {
            records.add(mileStone.toJsonObject());
        }
        return ok(result);
    }
    
	private static ObjectNode getJsonResultOK() {
        ObjectNode result = Json.newObject();
        result.put(JTABLE_RESULT, JTABLE_STATUS);
        return result;
    }
	
	public static Result create(long projectId) {
		MileStone newMileStone = getMileStoneFromForm();
        
        newMileStone.creationDate = new Date();
        
        Project project = Project.findById(projectId);
        newMileStone.project = project;
        
        newMileStone.save();
        ObjectNode result = getJsonResultOK();
        result.put(JTABLE_RECORD, newMileStone.toJsonObject());
        
        return ok(result);

    }
	
	private static MileStone getMileStoneFromForm() {
        Form<MileStone> filledForm = form.bindFromRequest();
        MileStone mileStone = filledForm.get();

        return mileStone;
    }
	
	public static Result update(long projectId) {
		MileStone updatedMileStone = getMileStoneFromForm();
        updatedMileStone.update();
        return ok(getJsonResultOK());
    }
	
	public static Result delete(long projectId) {
		MileStone deletedMileSton = getMileStoneFromForm();
        long id = deletedMileSton.id;
        MileStone.remove(id);
        return ok(getJsonResultOK());

    }
}
