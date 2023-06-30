package me.devtec.amazingtags.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.amazingtags.Loader;
import me.devtec.amazingtags.utils.MessageUtils.Placeholders;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.game.ItemMaker.HeadItemMaker;

public class Tags {

	/* Tags.yml paths
	 * path:
	 *   <tag>:
	 *     Tag - tag formát
	 *     Info - info o tagu
	 *     Enabled - jestli je tag zapnutý
	 *     Permission - speciální permise
	 *     Name - Speciální jméno tagu (kdyby mělo být jméno jiné od formátu tagu)
	 *     item:... ItemMaker thingies
	 */
	
	/* Placeholders
	 * %amazingtags_...%
	 * 
	 * tag/tag_format - tagformát z Tags.yml
	 * info/tag_info - informace o tagu
	 * name/tag_name - jméno tagu, použité buď v path nebo speciální setting
	 * status/tag_status - status tagu
	 */
	
	/** If Tag exist
	 * @param tag - The name used in the config to access the tag data
	 * @return True if tag exists
	 */
	public static boolean isTag(String tag) {
		if(Loader.tags.exists("tags."+tag+".tag"))
			return true;
		else {
			Bukkit.getLogger().severe("[MISTAKE] Missing Tags format!!");
			Bukkit.getLogger().severe("[MISTAKE] tags."+tag+".tag");
			Bukkit.getLogger().severe("[MISTAKE] In file Tags.yml");
			Bukkit.getLogger().severe("[BUG] This is not plugin bug!");
			return false;
		}
	}
	
	/** If Tag exist
	 * @param tag - The name used in the config to access the tag data
	 * @return True if tag exists
	 */
    public static boolean exist(String tag) {
        return Loader.tags.exists("tags." + tag + ".tag");
    }
    /** Gets Tag format from Tags.yml
     * @param tag - The name used in the config to access the tag data
     * @return Returns tags format
     */
	public static String getTagFormat(String tag) {
		if(tag==null) return "";
		if(Loader.tags.exists("tags."+tag+".tag"))
			return Loader.tags.getString("tags."+tag+".tag");
		else
			return "";
	}
	/** Gets Tag info from Tags.yml if exists
	 * @param tag - The name used in the config to access the tag data
	 * @return Returns "" if tag info does not exists in Tags.yml
	 */
	public static String getTagInfo(String tag) {
		if(tag==null) return "";
		if(Loader.tags.exists("tags."+tag+".info"))
			return Loader.tags.getString("tags."+tag+".info");
		else
			return "";
	}
	/** Gets Tag name from Tags.yml if exists. Is used instead of path name.
	 * Example: official name is superTag05, but you do not want player to see this, you use this.
	 * @param tag - The name used in the file to access the tag data
	 * @return Returns input parameter if is not used
	 */
	public static String getTagName(String tag) {
		if(tag==null || tag.isEmpty()) return "";
		if(Loader.tags.exists("tags."+tag+".name"))
			return Loader.tags.getString("tags."+tag+".name");
		else
			return tag;
	}
	/** Gets Tags permission.
	 * @param tag - The name used in the file to access the tag data
	 * @return Returns special tag permission from Tags.yml. If not used, returns default permission from Config.yml
	 */
	public static String getTagPermission(String tag) { //Get special or default tag permission
		if(Loader.tags.exists("tags."+tag+".permission"))
			return Loader.tags.getString("tags."+tag+".permission").replace("%tagname%", tag);
		else
			return null;
	}
	/** Is used to check if player can see tag in GUI menu.
	 * @param player - Player
	 * @param tag - The name used in the file to access the tag data
	 * @return True if player can see tag in menu
	 */
	public static boolean canSee(Player player, String tag) {
		if(!isEnabled(tag)) { //If tag is enabled in Tags.yml
			return false;
		}
		//If ALL players can see ALL tags in menu regardless of permission
		if(Loader.config.getBoolean("options.tags.settings.seeAll")) {
			return true;
		}
		return hasPermission(player, tag); //If permission... :D
	}
	/** If tag is enabled
	 * @param tag - The name used in the file to access the tag data
	 * @return true/false - all tags enabled in default
	 */
	public static boolean isEnabled(String tag) {
		if(Loader.tags.exists("tags."+tag+".enabled")) { //If tag is enabled in Tags.yml
			return Loader.tags.getBoolean("tags."+tag+".enabled");
		}
		return true;
	}
	/** If player can use or see tag in gui
	 * @param player Player
	 * @param tag - The name used in the file to access the tag data
	 * @return a
	 */
	public static boolean hasPermission(Player player, String tag) {
		if(getTagPermission(tag)!=null && !getTagPermission(tag).isEmpty()) {
			if(player.hasPermission(getTagPermission(tag)))
				return true;
			else return false;
		}
		if(player.hasPermission(getDefaultPermission(tag)))
			return true;
		return false;
	}
	//Default permission of all tags
	//Default permission value is amazingtags.tag.%tagname%
	public static String getDefaultPermission(String tag) {
		return Loader.config.getString("options.tags.default.permission").replace("%tagname%", tag);
	}
	/** Gets status of tag. If player can use tag, is using tag or does not have permission to use tag.
	 * @param tag - The name used in the file to access the tag data
	 * @param player - Player
	 * @return Returns status in form of String. Active - tag is selected. Available - if tag can be selected. NoPerm - you can't use this tag.
	 * You can change these Strings in Config.yml 
	 */
	private static String getStatus(String tag, Player player) {
		if(hasPermission(player, tag)) {
			if(API.getSelectedTag(player).equals(tag))
				return Loader.config.getString("options.status.active");
			else
				return Loader.config.getString("options.status.available");
		}
		return Loader.config.getString("options.status.noPerm");
	}
	

	/** Returns tag item. Admins can edit items in Tags.yml or the default one in GUI.yml
	 * @param player The player opening menu
	 * @param tag The tag path name from Tags.yml
	 * @return {@link ItemStack}
	 */
	public static ItemStack getTagItem(Player player, String tag) {
		ItemMaker item = ItemMaker.of(ItemMaker.loadFromConfig(Loader.tags, "tags."+tag+".item"));

		/*
		 * %amazingtags_...%
		 * 
		 * tag/tag_format - tagformát z Tags.yml
		 * info/tag_info - informace o tagu
		 * name/tag_name - jméno tagu, použité buď v path nebo speciální setting
		 * status/tag_status - status tagu
		 */
		Placeholders placeholders = new Placeholders();
		placeholders.addPlayer("player", player)
			.replace("tag", getTagFormat(tag)!=null?getTagFormat(tag):"" ).replace("tag_format", getTagFormat(tag)!=null?getTagFormat(tag):"" )
			.replace("tag_name", getTagName(tag)).replace("name", getTagName(tag))
			.replace("info", getTagInfo(tag)).replace("tag_info", getTagInfo(tag))
			.replace("status", getStatus(tag, player)).replace("tag_status", getStatus(tag, player))
			;
		
		if(item instanceof HeadItemMaker &&	((HeadItemMaker)item).getHeadOwnerType()==0 ) {
				((HeadItemMaker)item).skinName(player.getName());
		}
		
		return applyPlaceholders(item, placeholders).build();
	}
	
	/** Returns preview item used to inform player which tag he has selected
	 * @param player The player opening menu
	 * @return {@link ItemStack}
	 */
	public static ItemStack getPreviewItem(Player player) {
		ItemMaker item = ItemMaker.of(ItemMaker.loadFromConfig(Loader.gui, "gui.items.preview"));

		/*
		 * %amazingtags_...%
		 * 
		 * tag/tag_format - tagformát z Tags.yml
		 * info/tag_info - informace o tagu
		 * name/tag_name - jméno tagu, použité buď v path nebo speciální setting
		 * status/tag_status - status tagu
		 */
		String tag = API.getSelectedTag(player);
		Placeholders placeholders = new Placeholders();
		placeholders.addPlayer("player", player)
			.replace("tag", getTagFormat(tag)!=null?getTagFormat(tag):"" ).replace("tag_format", getTagFormat(tag)!=null?getTagFormat(tag):"" )
			.replace("tag_name", getTagName(tag)).replace("name", getTagName(tag))
			.replace("info", getTagInfo(tag)).replace("tag_info", getTagInfo(tag))
			.replace("status", getStatus(tag, player)).replace("tag_status", getStatus(tag, player))
			;
		
		if(item instanceof HeadItemMaker &&	((HeadItemMaker)item).getHeadOwnerType()==0 ) {
				((HeadItemMaker)item).skinName(player.getName());
		}
		
		return applyPlaceholders(item, placeholders).build();
	}
	
	/** This method will replace all possible placeholders from item's name and lore
	 * @param item the item, where placeholders should be replaced
	 * @param placeholders all placeholders you want to be replaced 
	 * @return {@link ItemMaker}
	 * @see {@link Placeholders}
	 */
	private static ItemMaker applyPlaceholders(ItemMaker item, Placeholders placeholders) {
		item.displayName(placeholders.apply(item.getDisplayName()));
		
		List<String> lore = item.getLore();
		if(lore.isEmpty()) {
			List<String> newLore = new ArrayList<String>();
			
			for(String line : lore)
				newLore.add(placeholders.apply(line));
			
			item.lore(newLore);
		}
		return item;
	}
	
}
