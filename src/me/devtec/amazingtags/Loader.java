package me.devtec.amazingtags;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.configapi.Config;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;
	static String prefix;
	
	public void onEnable() {
		plugin=this;
		
		Configs.load();
		prefix=config.getString("Options.Prefix");
		TheAPI.createAndRegisterCommand(config.getString("Options.Command.Name"),config.getString("Options.Command.Permission"), new AmazingTagsCommand(), config.getStringList("Options.Command.Aliases"));
		
		/*if(Placeholders.isEnabledPlaceholderAPI())
			new PAPISupport().load();*/ //TODO

	}
	
	public void onDisable() {
		
	}
	
	

	public static void reload(CommandSender ss) {
		Loader.config.reload();
		Loader.gui.reload();
		Loader.tags.reload();
		TheAPI.msg(prefix+" Configurations reloaded.", ss);
	}
	
	public static boolean has(CommandSender s, String permission) {
		if(s.hasPermission(permission)) return true;
		TheAPI.msg(config.getString("Translation.noPerms").replace("%permission%", permission), s);
		return false;
	}
}
