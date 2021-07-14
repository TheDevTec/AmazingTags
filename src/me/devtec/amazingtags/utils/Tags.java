package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.utils.datakeeper.User;

public class Tags {

	
	public static boolean isTag(String tag) {
		if(Loader.tags.exists("Tags."+tag+".Tag"))
			return true;
		else {
			Bukkit.getLogger().severe("[MISTAKE] Missing Tags format!!");
			Bukkit.getLogger().severe("[MISTAKE] Tags."+tag+".Tag");
			Bukkit.getLogger().severe("[MISTAKE] In file Tags.yml");
			Bukkit.getLogger().severe("[BUG] This is not plugin bug!");
			return false;
		}
	}
	

	public static String getTagFormat(String tag) {
		if(tag==null) return "";
		if(Loader.tags.exists("Tags."+tag+".Tag"))
			return Loader.tags.getString("Tags."+tag+".Tag");
		else
			return null;
	}
	public static String getTagInfo(String tag) {
		if(tag==null) return "";
		if(Loader.tags.exists("Tags."+tag+".Info"))
			return Loader.tags.getString("Tags."+tag+".Info");
		else
			return "";
	}
	public static String getTagName(String tag) {
		if(tag==null) return "";
		if(Loader.tags.exists("Tags."+tag+".Name"))
			return Loader.tags.getString("Tags."+tag+".Name");
		else
			return tag;
	}
	public static String getTagPermission(String tag) {
		if(Loader.tags.exists("Tags."+tag+".Permission"))
			return Loader.tags.getString("Tags."+tag+".Permission").replace("%tagname%", tag);
		else
			return null;
	}
	
	
	
	
	public static Material getType(String tag) {
		if(Loader.tags.exists("Tags."+tag+".Type"))
			return Material.valueOf(Loader.tags.getString("Tags."+tag+".Type"));
		else
			return Material.valueOf(Loader.config.getString("Options.Tags.Default.Material"));
	}
	
	public static List<String> getLore(String tag, Player p) {
		List<String> lore = new ArrayList<>();
		List<String> list = new ArrayList<>();
		if(Loader.tags.exists("Tags."+tag+".Lore")) {
			list = Loader.tags.getStringList("Tags."+tag+".Lore");
		} else {
			list =  Loader.config.getStringList("Options.Tags.Default.Lore");
		}
		
		for(String line: list) {
			lore.add(line.replace("%info%", getTagInfo(tag) ).replace("%tag%", getTagFormat(tag)!=null?getTagFormat(tag):"" ).replace("%tagname%", getTagName(tag))
					.replace("%player%", p!=null?p.getName():""));
		}
		return lore;
	}
	
	public static String getName(String tag) {
		if(Loader.tags.exists("Tags."+tag+".Name"))
			return Loader.tags.getString("Tags."+tag+".Name");
		else
			return Loader.config.getString("Options.Tags.Default.Name").replace("%tagname%", tag);
	}
	public static String getDefaultPermission(String tag) {
		return Loader.config.getString("Options.Tags.Default.Permission").replace("%tagname%", tag);
	}
	
	public static boolean canSee(Player p, String tag) {
		if(Loader.config.getBoolean("Options.Tags.Settings.seeAll")) {
			return true;
		}
		if(getTagPermission(tag)!=null && !getTagPermission(tag).isEmpty()) {
			if(p.hasPermission(getTagPermission(tag)))
				return true;
			else return false;
		}
		if(p.hasPermission(getDefaultPermission(tag)))
			return true;
		return false;
	}
	public static boolean hasPermission(Player p, String tag) {
		if(getTagPermission(tag)!=null && !getTagPermission(tag).isEmpty()) {
			if(p.hasPermission(getTagPermission(tag)))
				return true;
			else return false;
		}
		if(p.hasPermission(getDefaultPermission(tag)))
			return true;
		return false;
	}
	
	
	public static HashMap<Player, String> players = new HashMap<>();
	
	public static void select(Player player, String tag) {
		if(players.containsKey(player))
			players.remove(player);
		
		players.put(player, tag);
		User u = TheAPI.getUser(player);
		u.set("amazingtags.selected", tag);
		u.save();
	}
	public static String getSelected(Player player) {
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
	
	
	
	
	/*
	 *     PREVIEW ITEM
	 */
	private static String getPreviewItemName(Player p) {
		return Loader.gui.getString("GUI.Items.Preview.Name").replace( "%tag%", getTagFormat(getSelected(p)) ).replace("%tagname%", getSelected(p)!=null?getSelected(p):"" );
	}
	private static List<String> getPreviewItemLore(Player p) {
		List<String> lore = new ArrayList<>();
		for(String line : Loader.gui.getStringList("GUI.Items.Preview.Lore")) {
			lore.add(line.replace("%player%", p.getName()).replace( "%tag%", getTagFormat(getSelected(p)) ).replace("%tagname%", getSelected(p)!=null?getSelected(p):"") );
		}
		return lore;
	}
	private static Material getPreviewType() {
		return Material.valueOf(Loader.gui.getString("GUI.Items.Preview.Type") );
	}
	
	public static ItemStack getPreviewItem(Player p) {
		if(Loader.gui.getBoolean("GUI.Items.Preview.PlayerHead")) {
			return ItemCreatorAPI.createHead(1, getPreviewItemName(p), p.getName(), getPreviewItemLore(p));
		}
		else {
			return ItemCreatorAPI.create(getPreviewType(), 1, getPreviewItemName(p), getPreviewItemLore(p));
		}
	}
	
	
	
	
}
