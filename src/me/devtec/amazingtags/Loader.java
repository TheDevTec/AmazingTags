package me.devtec.amazingtags;

import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.configapi.Config;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;

	public void onEnable() {
		plugin=this;
		
	}
	
	public void onDisable() {
		
	}
	
	
}
