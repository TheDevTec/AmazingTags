package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.utility.StringUtils;

public class API {

	public static HashMap<Player, String> players = new HashMap<>();
	
	/*
	 * Selecting new tag
	 */
	public static void selectNewTag(Player player, String tag) {
		if(tag!=null) {
			if(players.containsKey(player))
				players.remove(player);
			
			players.put(player, tag);
			
			if(SQL.isEnabled() && Loader.connection!=null) {
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
			
			if(SQL.isEnabled() && Loader.connection!=null) {
				SQL.selectTag(player, null);
			}
			
			Config u = me.devtec.shared.API.getUser(player.getUniqueId());
			u.remove("amazingtags.selected");
			u.save();
		}
	}
	public static void select(Player player, String tag) {
		selectNewTag(player, tag);
	}
	
	
	/*
	 * Getting tag
	 */
	public static String getSelectedTag(Player player) {
		if(players.containsKey(player))
			return players.get(player);
		else {
			String tag = Loader.config.getString("Options.Tags.Default_Tag");
			
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
	public static String getSelected(Player player) {
		return getSelectedTag(player);
	}
	public static String getSelectedTagFormat(Player player) {
		return getTagFormat(getSelectedTag(player));
	}
	
	/*
	 * TAG
	 */
	public static String getTagFormat(String tag) {
		if(tag==null) return "";
		return Tags.getTagFormat(tag);
	}
	
	public static void createTag(String tagName, String tagFormat) {
		Loader.tags.set("Tags."+tagName+".Tag", tagFormat);
		Loader.tags.save();
	}
	
	/*
	 * Other
	 */
	private static void process(Player player, String tag) {
		List<String> cmds = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		
		if(Loader.tags.exists("Tags."+tag+".Select.Commands"))
			cmds = Loader.tags.getStringList("Tags."+tag+".Select.Commands");
		else
			cmds = Loader.config.getStringList("Options.Tags.Select.Commands");
		
		if(Loader.tags.exists("Tags."+tag+".Select.Messages")) {
			msg = Loader.tags.getStringList("Tags."+tag+".Select.Messages");
		}
		else {
			msg = Loader.config.getStringList("Options.Tags.Select.Messages");
		}

		for(String command: cmds) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.apply(command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)), player.getUniqueId() ));
			//TheAPI.sudoConsole(SudoType.COMMAND, command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) );
		}
		for(String message: msg) {
			player.sendMessage(StringUtils.colorize(PlaceholderAPI.apply(message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)), player.getUniqueId())));
			//TheAPI.msg(message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) , player);
		}
	}
}
