package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.TheAPI.SudoType;
import me.devtec.theapi.utils.datakeeper.User;

public class API {

	public static HashMap<Player, String> players = new HashMap<>();
	
	/*
	 * Selecting new tag
	 */
	public static void selectNewTag(Player player, String tag) {
		if(players.containsKey(player))
			players.remove(player);
		
		players.put(player, tag);
		User u = TheAPI.getUser(player);
		u.set("amazingtags.selected", tag);
		u.save();
		
		process(player, tag);
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
			User u = TheAPI.getUser(player);
			String tag = null;
			if(u.exist("amazingtags.selected"))
				tag = u.getString("amazingtags.selected");
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
	
	
	/*
	 * Other
	 */
	private static void process(Player player, String tag) {
		List<String> cmds = new ArrayList<>();
		List<String> msg = new ArrayList<>();
		
		if(Loader.tags.exists("Tags."+tag+".Cmds"))
			cmds = Loader.tags.getStringList("Tags."+tag+".Cmds");
		else
			cmds = Loader.config.getStringList("Options.Tags.Commands");
		
		if(Loader.tags.exists("Tags."+tag+".Messages"))
			msg = Loader.tags.getStringList("Tags."+tag+".Messages");
		else
			msg = Loader.config.getStringList("Options.Tags.Messages");
		
		for(String command: cmds) {
			TheAPI.sudoConsole(SudoType.COMMAND, command.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) );
		}
		for(String message: msg) {
			TheAPI.msg(message.replace("%player%", player.getName()).replace("%tagname%", tag).replace("%tag%", getTagFormat(tag)) , player);
		}
	}
}