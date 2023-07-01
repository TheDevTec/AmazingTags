package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.amazingtags.utils.sql.SQL;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;

public class API {

	public static ConcurrentHashMap<Player, String> players = new ConcurrentHashMap<>();
	
	/** Selecting new tag
	 * @param player - online player on server
	 * @param tag - some existing tag from Tags.yml
	 */
	public static void selectNewTag(Player player, String tag) {
		if(tag!=null) {
			if(players.containsKey(player))
				players.remove(player);
			
			players.put(player, tag);
			
			if(SQL.isEnabled() && Loader.sql.connection!=null) {
				SQL.selectTag(player, tag);
			}else {
				Config u = me.devtec.shared.API.getUser(player.getUniqueId());
				u.set("amazingtags.selected", tag);
				u.save();
			}
			
			process(player, tag);
		}
		else {
			if(players.containsKey(player))
				players.remove(player);
			
			if(SQL.isEnabled() && Loader.sql.connection!=null) {
				SQL.selectTag(player, null);
			}
			
			Config u = me.devtec.shared.API.getUser(player.getUniqueId());
			u.remove("amazingtags.selected");
			u.save();
		}
	}
	/** Selecting tag
	 * @param player - online player on server
	 * @param tag - some existing tag from Tags.yml
	 */
	public static void selectTag(Player player, String tag) {
		selectNewTag(player, tag);
	}
	
	/** Getting player's selected tag
	 * @param player - online player on server
	 * @return Selected tag from Tags.yml
	 */
	public static String getSelectedTag(Player player) {
		if(players.containsKey(player))
			return players.get(player);
		else {
			String tag = Loader.config.getString("options.tags.default_Tag");
			
			if(SQL.isEnabled()) {
				String s = SQL.getTag(player);
				tag = s!=null?s:tag;
				//Bukkit.broadcastMessage("načten tag z databáze: "+tag);
			} else {
				Config u = me.devtec.shared.API.getUser(player.getUniqueId());
				if(u.exists("amazingtags.selected"))
					tag = u.getString("amazingtags.selected");
			}
			players.put(player, tag);
			//Bukkit.broadcastMessage("načten tag: "+tag);
			return tag;
		}
	}
	/** Getting player's tag format
	 * @param player - online player on server
	 * @return Selected tag format from Tags.yml
	 */
	public static String getSelectedTagFormat(Player player) {
		return getTagFormat(getSelectedTag(player));
	}
	
	/** Getting tag's format
	 * @param player - online player on server
	 * @return Selected tag from Tags.yml
	 */
	public static String getTagFormat(String tag) {
		if(tag==null) return "";
		return Tags.getTagFormat(tag);
	}
	
	/** Create new tag
	 * @param tagName - used as identificator
	 * @param tagFormat - colors, symbols, etc...
	 */
	public static void createTag(String tagName, String tagFormat) {
		Loader.tags.set("tags."+tagName+".tag", tagFormat);
		Loader.tags.save();
	}
	
	/*
	 * Other
	 */
	@SuppressWarnings("deprecation")
	private static void process(Player player, String tag) {
		List<String> cmds = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		
		if(Loader.tags.exists("tags."+tag+".select.commands"))
			cmds = Loader.tags.getStringList("tags."+tag+".select.commands");
		else
			cmds = Loader.config.getStringList("options.tags.select.commands");
		
		if(Loader.tags.exists("tags."+tag+".select.messages")) {
			msg = Loader.tags.getStringList("tags."+tag+".select.messages");
		}
		else {
			msg = Loader.config.getStringList("options.tags.select.messages");
		}

		for(String command: cmds) {
			
			BukkitLoader.getNmsProvider().postToMainThread(() -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.apply(command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)), player.getUniqueId() ));
			});
		}
		for(String message: msg) {
			player.sendMessage(StringUtils.colorize(PlaceholderAPI.apply(message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)), player.getUniqueId())));
		}
	}
}
