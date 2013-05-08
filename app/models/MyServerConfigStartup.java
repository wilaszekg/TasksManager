package models;
import play.Logger;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;


public class MyServerConfigStartup implements ServerConfigStartup {

	@Override
	public void onStart(ServerConfig serverConfig) {
		Logger.debug("setting ebean server configuration");
		serverConfig.setEncryptKeyManager(new BasicEncryptKeyManager());
	}

}
