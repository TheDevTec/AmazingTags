package me.devtec.amazingtags.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.apis.ItemCreatorAPI;

public class Category {
	
	private String name; // Category Name
	public Category(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return Loader.tags.getString("categories."+name+".name");
	}
	public List<String> getDescription(){
		return Loader.tags.getStringList("categories."+name+".description");
	}
	public List<String> getContent(){ //obsah - Achievementy, Questy atd...
		return Loader.tags.getStringList("categories."+name+".content");
	}
	public boolean hasPermission(Player p) {
		if(Loader.tags.exists("categories."+name+".permission"))
			return p.hasPermission(Loader.tags.getString("categories."+name+".permission"));
		return true;
	}
	public int getSlot() {
		if(Loader.tags.exists("categories."+name+".slot"))
			return Loader.tags.getInt("categories."+name+".slot");
		else
			return -1;
	}
	public String getSpecial() {
		if(Loader.tags.exists("categories."+name+"special"))
			return Loader.tags.getString("categories."+name+".slot");
		else
			return null;
	}
	
	public ItemStack getIcon() {
		if(Loader.tags.exists("categories."+name+".head"))
			return createHead(getHead());
		else
			return new ItemStack(Material.valueOf(Loader.tags.getString("categories."+name+".icon") ));
	}
	public ItemStack getItem() {
		ItemCreatorAPI item = new ItemCreatorAPI(getIcon());
		item.setDisplayName(getDisplayName());
		item.setLore(getDescription());
		
		if(Loader.tags.exists("categories."+name+".head"))
			return fixHead(item, getHead()).create();
		return item.create();
	}
	
	public String getHead() {
		return Loader.tags.getString("categories."+name+".head");
	}
	
	private static ItemStack createHead(String head) {
		if(head.toLowerCase().startsWith("hdb:"))
			return new ItemCreatorAPI( HDBSupport.parse(head)).create();
		else
		if(head.startsWith("https://")||head.startsWith("http://"))
			return ItemCreatorAPI.createHeadByWeb(1, "&7Head from website", head);
		else
		if(head.length()>16) { 
			return ItemCreatorAPI.createHeadByValues(1, "&7Head from values", head);
		}else
			return ItemCreatorAPI.createHead(1, "&7" + head + "'s Head", head);
	}

	
	private static ItemCreatorAPI fixHead(ItemCreatorAPI item, String head) {
		if(head.length()>16)
			item.setOwnerFromValues(head);
		return item;
	}
}
