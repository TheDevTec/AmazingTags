package me.devtec.amazingtags;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.MessageUtils;
import me.devtec.amazingtags.utils.MessageUtils.Placeholders;
import me.devtec.amazingtags.utils.Metrics;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.amazingtags.utils.sql.MySQL;
import me.devtec.amazingtags.utils.sql.SQL;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, //Config.yml file
							gui, //GUI.yml file
							tags; // Tags.yml file
	public static MySQL sql;

	protected static PlaceholderExpansion placeholders;
	
	protected static Metrics metrics;
	
	public void onEnable() {
		plugin=this;
		//Loading files and next+prev button
		Configs.load();
		//Loading command
		PluginCommand cmd = BukkitCommandManager.createCommand(config.getString("command.name"),this);
		cmd.setPermission(config.getString("command.permission"));
		AmazingTagsCommand amf = new AmazingTagsCommand();
		cmd.setExecutor(amf);
		cmd.setAliases(config.getStringList("command.aliases"));
		//cmd.setTabCompleter(amf);
		BukkitCommandManager.registerCommand(cmd);

        //Loading bStats
        metrics = new Metrics(this, 19647);
		reloadtagCountMetric();
		
		//Checking if MyQL options is enabled and loading database connection
		if(SQL.isEnabled()) {
			sql = new MySQL().prepare();
		}
		//Loading placeholders
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null)
			loadPlaceholders();

		plugin=this;
	}
	
	public void onDisable() {
		//Disabling placeholders
		if(placeholders!=null)
			Loader.placeholders.unregister();
		//Closing SQL connection
		if(sql!=null)
			sql.close();
	}
	
	public static void reload(CommandSender ss) {
		//Reloading configs
		Loader.config.reload();
		Loader.gui.reload();
		Loader.tags.reload();
		reloadtagCountMetric();
		ss.sendMessage(ColorUtils.colorize(MessageUtils.getPrefix()+" Configurations reloaded."));
	}
	
	//If player have permission, if not sending noPerms message
	public static boolean has(CommandSender s, String permission) {
		if(s.hasPermission(permission)) return true;
		MessageUtils.message(s, "translation.noPerms", Placeholders.c().replace("%permission%", permission));
		//s.sendMessage(ColorUtils.colorize(config.getString("Translation.noPerms").replace("%permission%", permission).replace("%prefix%", Loader.config.getString("Prefix"))));
		return false;
	}
	
	public void loadPlaceholders() {
		/* Placeholder
		 * %amazingtags_...%
		 * 
		 * tag/tag_format - Tag's format from Tags.yml
		 * info/tag_info - Tag's information text
		 * name/tag_name - Tag's name, used path name or special 'name' setting
		 * status/tag_status - Tag's status
		 */
		
		Loader.placeholders = new PlaceholderExpansion("amazingtags") {
			@Override
			public String apply(String identifier, UUID player) {
				if(player == null || Bukkit.getPlayer(player) == null){
					return null;
			    }
				if(identifier.equalsIgnoreCase("tag") || identifier.equalsIgnoreCase("tag_format")) {
					return Tags.getTagFormat(API.getSelectedTag(Bukkit.getPlayer(player)));
				}
				if(identifier.equalsIgnoreCase("info") || identifier.equalsIgnoreCase("tag_info")) {
					return Tags.getTagInfo(API.getSelectedTag(Bukkit.getPlayer(player)));
				}
				if(identifier.equalsIgnoreCase("name") || identifier.equalsIgnoreCase("tag_name")) {
					return Tags.getTagInfo(API.getSelectedTag(Bukkit.getPlayer(player)));
				}
				if(identifier.equalsIgnoreCase("status") || identifier.equalsIgnoreCase("tag_status")) {
					return Tags.getTagInfo(API.getSelectedTag(Bukkit.getPlayer(player)));
				}
				return null;
			}
		};
		Loader.placeholders.register();
	}
	
	private static void reloadtagCountMetric() {
	    metrics.addCustomChart(new Metrics.SingleLineChart("tag_count", new Callable<Integer>() {
	        @Override
	        public Integer call() throws Exception {
	            // (This is useless as there is already a player chart by default.)
	        	int i = 0;
	        	for(String tag: Loader.tags.getKeys("tags")) {
	        		if(Tags.isEnabled(tag))
	        			i++;
	        	}
	            return i;
	        }
	    }));
	}
}
