package models;

import com.avaje.ebean.config.EncryptKey;
import com.avaje.ebean.config.EncryptKeyManager;

public class BasicEncryptKeyManager implements EncryptKeyManager {

	@Override
	public EncryptKey getEncryptKey(String tableName, String columnName) {
		// TODO Auto-generated method stub
		return new CustomEncryptKey(tableName, columnName);
	}

	@Override
	public void initialise() {
		// TODO Auto-generated method stub
		
	}
	
	class CustomEncryptKey implements EncryptKey{

	    private String tableName;

	    private String columnName;

	    public CustomEncryptKey(String tableName, String columnName){
	        this.tableName = tableName;
	        this.columnName = columnName;
	    }

	    @Override
	    public String getStringValue() {
	        //This is just an example and may not be the most secure or efficient way to do this
	        //return play.Configuration.root().getString("application.secret") + "::" + this.tableName + "::" + this.columnName;
	    	return play.Configuration.root().getString("application.secret");
	    }

	}

}
