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
	 *     Tag - Tag's format
	 *     Info - Tag's info text
	 *     Enabled - If tag is enabled
	 *     Permission - Tag's special permission
	 *     Name - Special tag's name. (if missing, using path name as tag's name)
	 *     item:... ItemMaker thingies
	 */
	
	/* Placeholders (placeholders in command description are without 'amazingtags_')
	 * %amazingtags_...%
	 * 
	 * tag/tag_format - Tag's format from Tags.yml
	 * info/tag_info - Tag's information text
	 * name/tag_name - Tag's name, used path name or special 'name' setting
	 * status/tag_status - Tag's status
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
		if(Loader.config.getBoolean("tags.settings.seeAll")) {
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
			return player.hasPermission(getTagPermission(tag));
		/*	if(player.hasPermission(getTagPermission(tag)))
				return true;
			else return false;*/
		}
		return player.hasPermission(getDefaultPermission(tag));
		/*if(player.hasPermission(getDefaultPermission(tag)))
			return true;
		return false;*/
	}
	//Default permission of all tags
	//Default permission value is amazingtags.tag.%tagname%
	public static String getDefaultPermission(String tag) {
		return Loader.config.getString("tags.default.permission").replace("%tagname%", tag);
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
				return Loader.config.getString("status.active");
			else
				return Loader.config.getString("status.available");
		}
		return Loader.config.getString("status.noPerm");
	}
	
	/** Loads and prepare item. </br>
	 * If there is custom item in Tags.yml that do not have lore or displayName, this will replace it with default ones.
	 * @param tag - The name used in the file to access the tag data
	 * @return {@link ItemMaker}
	 */
	private static ItemMaker getTagItemFromConfig(String tag) {
		// Default item preparation
		ItemMaker item = ItemMaker.loadMakerFromConfig(Loader.gui, "gui.items.default");
		// If there is custom item in Tags.yml -> Then we try and use that item 
		if(Loader.tags.exists("tags."+tag+".item"))
			item = ItemMaker.loadMakerFromConfig(Loader.tags, "tags."+tag+".item");
		if(item==null) //if there is missing type -> then back to default item (sorry folks :D)
			item = ItemMaker.loadMakerFromConfig(Loader.gui, "gui.items.default");
		
		// displayName and lore check
		if(item.getDisplayName() == null || item.getDisplayName().isEmpty())
			item.displayName(Loader.gui.getString("gui.items.default.displayName"));
		if(item.getLore()==null)
			item.lore(Loader.gui.getStringList("gui.items.default.lore"));
			
		return item;
		
	}
	/** Returns tag item. Server admin can edit items in Tags.yml or the default one in GUI.yml
	 * @param player - The player opening menu
	 * @param tag - The name used in the file to access the tag data
	 * @return {@link ItemStack}
	 */
	public static ItemStack getTagItem(Player player, String tag) {
		// Getting item
		ItemMaker item = getTagItemFromConfig(tag);
		
		// Loading placeholders in item lore and name
		Placeholders placeholders = new Placeholders();
		placeholders.addPlayer("player", player)
			.replace("tag", getTagFormat(tag)!=null?getTagFormat(tag):"" ).replace("tag_format", getTagFormat(tag)!=null?getTagFormat(tag):"" )
			.replace("tag_name", getTagName(tag)).replace("name", getTagName(tag))
			.replace("info", getTagInfo(tag)).replace("tag_info", getTagInfo(tag))
			.replace("status", getStatus(tag, player)).replace("tag_status", getStatus(tag, player))
			;
		// If item is head -> Load custom player head if needed
		if(item instanceof HeadItemMaker /*&&	((HeadItemMaker)item).getHeadOwnerType()==0*/) {
			// this player head
			if(Loader.tags.getString("tags."+tag+".item.head.type").equalsIgnoreCase("PLAYER") &&
					Loader.tags.getString("tags."+tag+".item.head.owner").equalsIgnoreCase("%player%"))
				((HeadItemMaker)item).skinName(player.getName());
			// values - because of some minecraft shenanigans we need to fix values heads like this
			if(Loader.tags.getString("tags."+tag+".item.head.type").equalsIgnoreCase("VALUES"))
				((HeadItemMaker)item).skinValues(Loader.tags.getString("tags."+tag+".item.head.owner"));
		}
		// Applying placeholders -> Replacing them
		return applyPlaceholders(item, placeholders).build();
	}
	
	/** Returns preview item used to inform player which tag he has selected
	 * @param player The player opening menu
	 * @return {@link ItemStack}
	 */
	public static ItemStack getPreviewItem(Player player) {
		ItemMaker item = ItemMaker.of(ItemMaker.loadFromConfig(Loader.gui, "gui.items.preview"));
		// Loading placeholders
		String tag = API.getSelectedTag(player);
		Placeholders placeholders = new Placeholders();
		placeholders.addPlayer("player", player)
			.replace("tag", getTagFormat(tag)!=null?getTagFormat(tag):"" ).replace("tag_format", getTagFormat(tag)!=null?getTagFormat(tag):"" )
			.replace("tag_name", getTagName(tag)).replace("name", getTagName(tag))
			.replace("info", getTagInfo(tag)).replace("tag_info", getTagInfo(tag))
			.replace("status", getStatus(tag, player)).replace("tag_status", getStatus(tag, player))
			;

		// If item is head -> Load custom player head if needed
		if(item instanceof HeadItemMaker /*&&	((HeadItemMaker)item).getHeadOwnerType()==0*/) {
			//this player head
			if(Loader.gui.getString("gui.items.preview.head.type").equalsIgnoreCase("PLAYER") &&
					Loader.gui.getString("gui.items.preview.head.owner").equalsIgnoreCase("%player%"))
				((HeadItemMaker)item).skinName(player.getName());
			// values - because of some minecraft shenanigans we need to fix values heads like this
			if(Loader.gui.getString("gui.items.preview.head.type").equalsIgnoreCase("VALUES"))
				((HeadItemMaker)item).skinValues(Loader.tags.getString("gui.items.preview.head.owner"));
		}

		// Applying placeholders -> Replacing them
		return applyPlaceholders(item, placeholders).build();
	}
	
	/** This method will replace all possible placeholders from item's name and lore
	 * @param item the item, where placeholders should be replaced
	 * @param placeholders all placeholders you want to be replaced 
	 * @return {@link ItemMaker}
	 * @see {@link Placeholders}
	 */
	private static ItemMaker applyPlaceholders(ItemMaker item, Placeholders placeholders) {
		
		if(item==null || placeholders == null) return item;
		// replacing diplayName
		item.displayName(placeholders.apply(item.getDisplayName()));

		// replacing lore
		List<String> lore = item.getLore();
		if(lore!=null && !lore.isEmpty()) {
			List<String> newLore = new ArrayList<String>();
			
			for(String line : lore)
				newLore.add(placeholders.apply(line));
			
			item.lore(newLore);
		}
		
		return item;
	}
	
}
