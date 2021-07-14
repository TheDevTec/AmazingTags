package me.devtec.amazingtags;

import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;

public class Configs {
	static List<String> datas = Arrays.asList("Config.yml","GUI.yml","Tags.yml");
	
	public static void load() {
		Data data = new Data();
    	boolean change = false;
		for(String s : datas) {
			data.reset();
			Config c = null;
	    	switch(s) {
	    	case "Config.yml":
	    		c=Loader.config;
	    		break;
	    	case "Tags.yml":
	    		c=Loader.tags;
	    		break;
	    	case "GUI.yml":
	    		c=Loader.gui;
	    		break;
	    	}
	    	if(c!=null) {
	    		c.reload();
	    	}else c=new Config("AmazingTags/"+s);
    		try {
    		URLConnection u = Loader.plugin.getClass().getClassLoader().getResource("Configs/"+s).openConnection();
    		u.setUseCaches(false);
    		data.reload(StreamUtils.fromStream(u.getInputStream()));
    		}catch(Exception e) {e.printStackTrace();}
	    	change = c.getData().merge(data, true, true);
	    	if(change)
	    	c.save();
	    	switch(s) {
	    	case "Config.yml":
	    		Loader.config=c;
	    		break;
	    	case "Tags.yml":
	    		Loader.tags=c;
	    		break;
	    	case "GUI.yml":
	    		Loader.gui=c;
	    		break;
	    	}
		}
		data.reset();
		convertTags();
	}
	
	private static void convertTags() { //TODO - dokončit
		
	}
}
