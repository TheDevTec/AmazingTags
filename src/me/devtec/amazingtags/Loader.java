package me.devtec.amazingtags;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.ItemCreatorAPI;
import me.devtec.amazingtags.utils.SQL;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.shared.database.DatabaseHandler;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;
	static String prefix;
	public static DatabaseHandler connection;
	
	public static ItemStack next = ItemCreatorAPI.createHeadByValues(1, "&cNext", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmNTVmMWIzMmMzNDM1YWMxYWIzZTVlNTM1YzUwYjUyNzI4NWRhNzE2ZTU0ZmU3MDFjOWI1OTM1MmFmYzFjIn19fQ=="), 
			prev = ItemCreatorAPI.createHeadByValues(1, "&cPrevious", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc2OGVkYzI4ODUzYzQyNDRkYmM2ZWViNjNiZDQ5ZWQ1NjhjYTIyYTg1MmEwYTU3OGIyZjJmOWZhYmU3MCJ9fX0=");

	protected static PlaceholderExpansion placeholders;
	
	public void onEnable() {
		plugin=this;
		
		Configs.load();
		prefix=config.getString("Options.Prefix");
		PluginCommand cmd = BukkitCommandManager.createCommand(config.getString("Options.Command.Name"),this);
		cmd.setPermission(config.getString("Options.Command.Permission"));
		AmazingTagsCommand amf = new AmazingTagsCommand();
		cmd.setExecutor(amf);
		cmd.setAliases(config.getStringList("Options.Command.Aliases"));
		//cmd.setTabCompleter(amf);
		BukkitCommandManager.registerCommand(cmd);
		
		if(SQL.isEnabled()) {
			connection = SQL.connect();
			SQL.createTable();
		}
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null)
			loadPlaceholders();

		plugin=this;
	}
	
	public void onDisable() {
		if(placeholders!=null)
			Loader.placeholders.unregister();
		if(connection!=null)
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public static void reload(CommandSender ss) {
		Loader.config.reload();
		Loader.gui.reload();
		Loader.tags.reload();
		prefix=config.getString("Options.Prefix");
		ss.sendMessage(StringUtils.colorize(prefix+" Configurations reloaded."));
	}
	
	public static boolean has(CommandSender s, String permission) {
		if(s.hasPermission(permission)) return true;
		s.sendMessage(StringUtils.colorize(config.getString("Translation.noPerms").replace("%permission%", permission).replace("%prefix%", Loader.config.getString("Options.Prefix"))));
		return false;
	}
	
	public static void msg(String msg, CommandSender s) {
		s.sendMessage(StringUtils.colorize(msg.replace("%prefix%", Loader.config.getString("Options.Prefix"))));
	}
	
	public void loadPlaceholders() {
		Loader.placeholders=new PlaceholderExpansion("amazingtags") {

			@Override
			public String apply(String identifier, UUID player) {
			       if(player == null || Bukkit.getPlayer(player) == null){
			           return null;
			       }
				   	if(identifier.equalsIgnoreCase("tag")) {
				   		return Tags.getTagFormat(API.getSelected(Bukkit.getPlayer(player)));
				   	}
			       return null;
			}
		};
		Loader.placeholders.register();
	}
}
