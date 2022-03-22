package me.devtec.amazingtags;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.ItemCreatorAPI;
import me.devtec.amazingtags.utils.SQL;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.shared.Ref;
import me.devtec.shared.database.DatabaseHandler;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.utility.StringUtils;

public class Loader extends JavaPlugin{
	
	public static Loader plugin;
	public static Config config, gui, tags;
	static String prefix;
	public static DatabaseHandler connection;
	private static final Constructor<?> constructor = Ref.constructor(PluginCommand.class, String.class, Plugin.class);
	
	public static ItemStack next = ItemCreatorAPI.createHeadByValues(1, "&cNext", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmNTVmMWIzMmMzNDM1YWMxYWIzZTVlNTM1YzUwYjUyNzI4NWRhNzE2ZTU0ZmU3MDFjOWI1OTM1MmFmYzFjIn19fQ=="), 
			prev = ItemCreatorAPI.createHeadByValues(1, "&cPrevious", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc2OGVkYzI4ODUzYzQyNDRkYmM2ZWViNjNiZDQ5ZWQ1NjhjYTIyYTg1MmEwYTU3OGIyZjJmOWZhYmU3MCJ9fX0=");

	public static void createAndRegisterCommand(String commandName, String permission, CommandExecutor commandExecutor,
			List<String> aliases) {
		PluginCommand cmd = createCommand(commandName.toLowerCase(), plugin);
		if (permission != null)
			Ref.set(cmd, "permission", permission.toLowerCase());
		cmd.setPermissionMessage("");
		List<String> lowerCase = new ArrayList<>();
		if (aliases != null)
			for (String s : aliases)
				lowerCase.add(s.toLowerCase());
		cmd.setAliases(lowerCase);
		cmd.setUsage("");
		Ref.set(cmd, "executor", commandExecutor);
		registerCommand(cmd);
	}
	
	public static PluginCommand createCommand(String name, Plugin plugin) {
		return (PluginCommand) Ref.newInstance(constructor, name, plugin);
	}

	public static CommandMap cmdMap = (CommandMap)Ref.get(Bukkit.getPluginManager(), "commandMap");
	@SuppressWarnings("unchecked")
	public static Map<String, Command> knownCommands = (Map<String, Command>) Ref.get(cmdMap, "knownCommands");

	public static void registerCommand(PluginCommand command) {
		String label = command.getName().toLowerCase(Locale.ENGLISH).trim();
		String sd = command.getPlugin().getName().toLowerCase(Locale.ENGLISH).trim();
		command.setLabel(sd + ":" + label);
		command.register(cmdMap);
		if (command.getTabCompleter() == null) {
			if (command.getExecutor() instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter) command.getExecutor());
			} else
				command.setTabCompleter(new TabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
						return null;
					}
				});
		}
		if (command.getExecutor() == null) {
			if (command.getTabCompleter() instanceof CommandExecutor) {
				command.setExecutor((CommandExecutor) command.getTabCompleter());
			} else
				return; // exectutor can't be null
		}
		List<String> low = new ArrayList<>();
		for (String s : command.getAliases()) {
			s = s.toLowerCase(Locale.ENGLISH).trim();
			low.add(s);
		}
		command.setAliases(low);
		command.setPermission("");
		if (!low.contains(label))
			low.add(label);
		for (String s : low)
			knownCommands.put(s, command);
	}
	
	protected static PlaceholderExpansion placeholders;
	
	public void onEnable() {
		plugin=this;
		
		Configs.load();
		prefix=config.getString("Options.Prefix");
		createAndRegisterCommand(config.getString("Options.Command.Name"),config.getString("Options.Command.Permission"), new AmazingTagsCommand(), config.getStringList("Options.Command.Aliases"));
		
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
