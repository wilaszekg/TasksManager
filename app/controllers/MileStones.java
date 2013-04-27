package controllers;

import java.util.Date;
import java.util.List;

import models.MileStone;
import models.Project;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.data.format.Formats;
import play.data.format.Formatters;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class MileStones extends Controller{
	

    private static final String JTABLE_RECORDS = "Records";
    private static final String JTABLE_RECORD = "Record";
    private static final String JTABLE_STATUS = "OK";
    private static final String JTABLE_RESULT = "Result";
    private static final String JTABLE_OPTIONS = "Options";
    
    static Form<MileStone> form = Form.form(MileStone.class);
    
    static {
    	Formatters.register(Date.class, new Formats.DateFormatter(MileStone.dateFormat));
    }
    
    public static Result list(long projectId) {
    	Logger.debug("id: " + projectId);
    	Logger.debug("project: " + Project.findById(projectId).name);
    	
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
        MileStone dbServer = filledForm.get();

        return dbServer;
    }
	
	public static Result update(long projectId) {
		MileStone updatedServer = getMileStoneFromForm();
        updatedServer.update();
        return ok(getJsonResultOK());
    }
	
	public static Result delete(long projectId) {
		MileStone deletedServer = getMileStoneFromForm();
        long id = deletedServer.id;
        MileStone.remove(id);
        return ok(getJsonResultOK());

    }
}
