package me.devtec.amazingtags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.amazingtags.utils.Tags;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.placeholderapi.PlaceholderRegister;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;
	static String prefix;
	public static ItemStack next = ItemCreatorAPI.createHeadByValues(1, "&cNext", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmNTVmMWIzMmMzNDM1YWMxYWIzZTVlNTM1YzUwYjUyNzI4NWRhNzE2ZTU0ZmU3MDFjOWI1OTM1MmFmYzFjIn19fQ=="), 
			prev = ItemCreatorAPI.createHeadByValues(1, "&cPrevious", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc2OGVkYzI4ODUzYzQyNDRkYmM2ZWViNjNiZDQ5ZWQ1NjhjYTIyYTg1MmEwYTU3OGIyZjJmOWZhYmU3MCJ9fX0=");

	protected static PlaceholderRegister palceholders;
	
	public void onEnable() {
		plugin=this;
		
		Configs.load();
		prefix=config.getString("Options.Prefix");
		TheAPI.createAndRegisterCommand(config.getString("Options.Command.Name"),config.getString("Options.Command.Permission"), new AmazingTagsCommand(), config.getStringList("Options.Command.Aliases"));
		
		if(PluginManagerAPI.getPlugin("PlaceholderAPI")!=null)
			loadPlaceholders();

		plugin=this;
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
	public void loadPlaceholders() {
		Loader.palceholders=new PlaceholderRegister("amazingtags", "DevTec", Loader.plugin.getDescription().getVersion()) {
			
			public String onRequest(Player player, String identifier) {
		   	
		       /*
		       Check if the player is online,
		       You should do this before doing anything regarding players
		        */
		       if(player == null){
		           return null;
		       }
			   	if(identifier.startsWith("tag")) {
			   		return Tags.getTagFormat(Tags.getSelected(player));
			   	}
		       return null;
			}
		};
		Loader.palceholders.register();
	}
}
