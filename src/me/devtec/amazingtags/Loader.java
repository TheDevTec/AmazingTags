package me.devtec.amazingtags;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.MessageUtils;
import me.devtec.amazingtags.utils.MessageUtils.Placeholders;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.amazingtags.utils.sql.MySQL;
import me.devtec.amazingtags.utils.sql.SQL;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;
import me.devtec.theapi.bukkit.game.ItemMaker;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;
	static String prefix;
	public static MySQL sql;
	
	public static ItemStack next = ItemMaker.loadFromConfig(gui, "GUI.Items.Next"), 
			prev = ItemMaker.loadFromConfig(gui, "GUI.Items.Previous");

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
			sql = new MySQL().prepare();
		}
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null)
			loadPlaceholders();

		plugin=this;
	}
	
	public void onDisable() {
		if(placeholders!=null)
			Loader.placeholders.unregister();
		if(sql!=null)
			sql.close();
	}
	
	public static void reload(CommandSender ss) {
		Loader.config.reload();
		Loader.gui.reload();
		Loader.tags.reload();
		prefix=config.getString("Options.Prefix");
		ss.sendMessage(ColorUtils.colorize(prefix+" Configurations reloaded."));
	}
	
	public static boolean has(CommandSender s, String permission) {
		if(s.hasPermission(permission)) return true;
		MessageUtils.message(s, "Translation.noPerms", Placeholders.c().replace("%permission%", permission));
		//s.sendMessage(ColorUtils.colorize(config.getString("Translation.noPerms").replace("%permission%", permission).replace("%prefix%", Loader.config.getString("Options.Prefix"))));
		return false;
	}
	
	public static void msg(String msg, CommandSender s) {
		s.sendMessage(ColorUtils.colorize(msg.replace("%prefix%", Loader.config.getString("Options.Prefix"))));
	}
	
	public void loadPlaceholders() {
		Loader.placeholders = new PlaceholderExpansion("amazingtags") {

			@Override
			public String apply(String identifier, UUID player) {
			       if(player == null || Bukkit.getPlayer(player) == null){
			           return null;
			       }
				   	if(identifier.equalsIgnoreCase("tag")) {
				   		return Tags.getTagFormat(API.getSelectedTag(Bukkit.getPlayer(player)));
				   	}
			       return null;
			}
		};
		Loader.placeholders.register();
	}
}
