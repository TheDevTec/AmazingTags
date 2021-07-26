package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.TheAPI.SudoType;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.utils.datakeeper.User;

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
			User u = TheAPI.getUser(player);
			u.set("amazingtags.selected", tag);
			u.save();
			
			if(SQL.isEnabled() && Loader.connection!=null) {
				SQL.selectTag(player, tag);
			}
			
			process(player, tag);
		}
		else {
			if(players.containsKey(player))
				players.remove(player);
			
			if(SQL.isEnabled() && Loader.connection!=null) {
				SQL.selectTag(player, null);
			}
			
			User u = TheAPI.getUser(player);
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
			} else {
				User u = TheAPI.getUser(player);
				if(u.exist("amazingtags.selected"))
					tag = u.getString("amazingtags.selected");
			}
			players.put(player, tag);
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
		
		if(Loader.tags.exists("Tags."+tag+".Select.Messages"))
			msg = Loader.tags.getStringList("Tags."+tag+".Select.Messages");
		else
			msg = Loader.config.getStringList("Options.Tags.Select.Messages");
		
		for(String command: cmds) {
			TheAPI.sudoConsole(SudoType.COMMAND, PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) ));
			//TheAPI.sudoConsole(SudoType.COMMAND, command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) );
		}
		for(String message: msg) {
			TheAPI.msg( PlaceholderAPI.setPlaceholders(player, message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag))), player) ;
			//TheAPI.msg(message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) , player);
		}
	}
}
