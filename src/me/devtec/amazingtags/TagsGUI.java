package me.devtec.amazingtags;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.Category;
import me.devtec.amazingtags.utils.Pagination;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.GUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class TagsGUI {

	/** Only option to open Tags menu
	 * @param player - Player that is opening menu
	 * @apiNote If there are any created categories, this will open categories menu
	 */
	public static void open(Player player) {
		if(Loader.tags.exists("categories"))
			openCategories(player, 0);
		else
			openTags(player, 0);
	}
	
	/** Opening all tags menu
	 * @param player - Player that is opening menu
	 * @param page - current page (0 is first page; if you do not know use 0)
	 */
	private static void openTags(Player player, int page) {
		//GUI preparation (title, size, frame
		GUI a = prepare( new GUI(Loader.gui.getString("gui.title"), 54) );
		//Loading pagination, 36 slots available
		Pagination<String> pagination = new Pagination<String>(36);
		//Loading all available tags (that player can see) into pagination
		for(String tag: Loader.tags.getKeys("tags")) {
			if(Tags.isTag(tag))
				if(Tags.canSee(player, tag))
					pagination.add(tag);
		}
		//If there are some tags available
		if(pagination!=null && !pagination.isEmpty()) {
			//Looping all tags (page) and adding them into GUI
			for(String tag: pagination.getPage(page)) {
				
				a.addItem(new ItemGUI(Tags.getTagItem(player, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(player, tag)) { //if player can click (have Permission)
							API.selectTag(player, tag); //select this tag
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("tags.select.sound")), SoundCategory.MASTER, 10, 5);
							a.close(); //closing menu
						}
						else
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
					}
				});
				
			}
			//NEXT AND PREVIOUS PAGE BUTTONS
			if(pagination.totalPages()>page+1) {
				a.setItem(51, new ItemGUI(Loader.next) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page+1); //page+1 -> next page
						
					}
				});
			}
			if(page>0) { //If this is not first page, then add previous button
				a.setItem(47, new ItemGUI(Loader.prev) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page-1); //page-1 -> previous page
						
					}
				});
			}
		}
		//PREVIEW ITEM
		addPreviewButton(player, a);
	
		a.open(player);
	}
	
	/** Opening list of categories available
	 * @param player - Player that is opening menu
	 * @param page - current page (0 is first page; if you do not know use 0)
	 * @apiNote Player can select category and open exact category
	 */
	private static void openCategories(Player player, int page) {
		//GUI preparation (title, size, frame
		GUI a = prepare( new GUI(Loader.gui.getString("gui.title"), 54) );
		//Loading pagination, 36 slots available
		Pagination<Category> pagination = new Pagination<Category>(36);
		//Adding categories into pagination
		for(String category: Loader.tags.getKeys("categories")) {
			if(Category.canSee(player, category)) //if player can SEE category in GUI
				pagination.add(new Category(category));
		}
		//If there are some categories that player acn see
		if(pagination!=null && !pagination.isEmpty()) {
			//Looping all available categories (and putting them into GUI)
			for(Category category: pagination.getPage(page)) {
				//Loading item
				ItemGUI item = new ItemGUI(category.getItem()) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(category.hasPermission(player)) { //If player can OPEN category
							
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("tags.select.sound")), SoundCategory.MASTER, 10, 5);
							openCategory(player, 0, category);
						}else
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
					}
				};
				
				//Adding item
				if(category.getSlot()==-1)
					a.addItem(item);
				else
					a.setItem(category.getSlot(), item); //Setting item on the exact position
			}
			
			//NEXT AND PREVIOUS BUTTON
			if(pagination.totalPages()>page+1) { //If there is next page
				a.setItem(51, new ItemGUI(Loader.next) { //Adding next page button
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategories(player, page+1); //Recursion (calls itself) +1 page
					}
				});
			}
			if(page>0) { //If this is not first page
				a.setItem(47, new ItemGUI(Loader.prev) { //Adding previous page button
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategories(player, page-1); //Recursion (calls itself) -1 page
					}
				});
			}
		}
		//PREWIEW ITEM
		addPreviewButton(player, a);
		
		//Open GUI
		a.open(player);
	}


	/** Opening selected category
	 * @param player - player that is opening this category
	 * @param page - current page (0 is first page; if you do not know use 0)
	 * @param category - category you want to be opened
	 */
	private static void openCategory(Player player, int page, Category category) {
		//GUI preparation (title, size, frame
		GUI a = prepare( new GUI(Loader.gui.getString("gui.title"), 54) );
		//Loading pagination, 36 slots available
		Pagination<String> pagination = new Pagination<String>(36);
		//Getting special option
		String special = category.getSpecial();
		//ALL - všechny možné tagy, které může vidět v GUI
		//PERM - všechny tagy na které má hráč permise (když některý tag může vidět, ale ne ho použít, tak se neukáže)
		
		/*KOMBINACE
		 * EMPTY+
		 * 		ALL - všechny tagy na serveru (vynecháno canSee, takže uvidí i ty, co by neměl :D )
		 * 		PERM - všechny na které má permise
		 * CONTENT+
		 * 		ALL - všechny ze seznamu
		 * 		PERM - tagy ze seznamu, na které má hráč permise
		 * EMPTY+EMPTY - všechny tagy ze seznamu, které hráč může vidět
		 */
		
		//if content is empty and there is some special setting
		if(category.getContent().isEmpty() && !special.equalsIgnoreCase("NONE")) {
			//looping all tags
			for(String tag: Loader.tags.getKeys("tags")) {
				if(Tags.isTag(tag) && Tags.isEnabled(tag)) { //if tag is available (enabled and valid)
					if(special.equalsIgnoreCase("ALL")) //EMPTY+ALL - ALL tags
						pagination.add(tag);
					if(special.equalsIgnoreCase("PERM")) //EMPTY+PERM - selecting from ALL tags only which one can use
						if(Tags.hasPermission(player, tag))
							pagination.add(tag);
					
				}
			}
		}else {
			//looping tags from content list
			for(String tag: category.getContent()) {
				if(Tags.isTag(tag) && Tags.isEnabled(tag)) //if tag is valid and enabled
					if(!special.equalsIgnoreCase("NONE")) { //if there is ALL or PERM setting
						if(special.equalsIgnoreCase("ALL")) //CONTENT+ALL - Adding ALL tags from list (bypassing perm check)
							pagination.add(tag);
						if(special.equalsIgnoreCase("PERM")) //CONTENT+PERN - Adding tags from the list to which the player has the permission
							if(Tags.hasPermission(player, tag))
								pagination.add(tag);
						continue;
					} else //If ther is none special setting
						if(Tags.canSee(player, tag)) //If player can see tag in GUI
							pagination.add(tag);
			}
		}
		
		//if there is any tag available in list
		if(pagination!=null && !pagination.isEmpty()) {
			//looping all tags from pagination list
			for(String tag: pagination.getPage(page)) {
				a.addItem(new ItemGUI(Tags.getTagItem(player, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(player, tag)) { //if player can click (have Permission)
							API.selectTag(player, tag); //select this tag
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("tags.select.sound")), SoundCategory.MASTER, 10, 5);
							a.close();
						}
						else
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
					}
				});
				
			}

			//NEXT AND PREVIOUS BUTTON
			if(pagination.totalPages()>page+1) { //If there is next page available
				a.setItem(51, new ItemGUI(Loader.next) { //Adding next page button
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategory(player, page+1, category);
					}
				});
			}
			if(page>0) { //If this is not the first page
				a.setItem(47, new ItemGUI(Loader.prev) { //Adding previous button
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategory(player, page-1, category);	
					}
				});
			}
		}
		//PREVIEW BUTTON
		addPreviewButton(player, a);
	
		a.open(player); //opens GUI
	}
	
	/**
	 * Adding preview button to GUI
	 */
	private static void addPreviewButton(Player player, GUI gui) {
		gui.setItem(4, new ItemGUI(Tags.getPreviewItem(player)) {
			@Override
			public void onClick(Player player, HolderGUI hgui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.selectTag(player, null); // select none tag, will delete player data from database
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("tags.select.sound")), SoundCategory.MASTER, 10, 5);
					gui.close();
				}
			}
		});
	}
	
	/** GUI preparation
	 * @param gui -> {@link GUI}
	 * @return {@link GUI}
	 */
	private static GUI prepare(GUI gui) {
		for (int i=0; i<=8; i++) {
			gui.setItem(i, new ItemGUI( ItemMaker.of(Material.BLACK_STAINED_GLASS_PANE).amount(1).displayName("&7").build()) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		for (int i=45; i<=53; i++) {
			gui.setItem(i, new ItemGUI( ItemMaker.of(Material.BLACK_STAINED_GLASS_PANE).amount(1).displayName("&7").build()) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		return gui;
	}
}
