package me.devtec.amazingtags.utils;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.bukkit.game.ItemMaker;

public class Category {
	
	private String name; // Category Name
	public Category(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @return All content of category
	 */
	public List<String> getContent(){
		return Loader.tags.getStringList("categories."+getName()+".content");
	}
	
	//SEE permission  - if player can SEE category in GUI (like some admin category or something :D)
	public static String getSeePermission(String category) {
		return Loader.tags.getString("categories."+category+".see_permission");
	}
	
	public static boolean canSee(Player p, String category) {
		if(getSeePermission(category) != null && !getSeePermission(category).isEmpty())
			return p.hasPermission(getSeePermission(category));
		return true;
	}
	
	//permission - if player can OPEN category
	public String getPermission() {
		return Loader.tags.getString("categories."+getName()+".permission");
	}
	
	public boolean hasPermission(Player p) {
		if(getPermission() != null && !getPermission().isEmpty())
			return p.hasPermission(getPermission());
		return true;
	}
	
	public int getSlot() {
		if(Loader.tags.exists("categories."+name+".item.slot"))
			return Loader.tags.getInt("categories."+name+".item.slot");
		else
			return -1;
	}

	public String getSpecial() {
		if(Loader.tags.exists("categories."+name+".special"))
			return Loader.tags.getString("categories."+name+".special");
		else
			return "NONE";
	}
	public ItemStack getItem() {
		return ItemMaker.loadFromConfig(Loader.tags, "categories."+getName()+".item");
	}
}
