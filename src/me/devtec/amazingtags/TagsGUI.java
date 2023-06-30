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

	public static void open(Player p) {
		if(Loader.tags.exists("categories"))
			openCategories(p, 0);
		else
			openTags(p, 0);
	}
	
	private static void openTags(Player p, int page) {
		//GUI preparation (title, size, frame
		GUI a = prepare( new GUI(Loader.gui.getString("gui.title"), 54) );
		//Loading pagination, 36 slots available
		Pagination<String> pagination = new Pagination<String>(36);
		//Loading all available tags (that player can see) into pagination
		for(String tag: Loader.tags.getKeys("tags")) {
			if(Tags.isTag(tag))
				if(Tags.canSee(p, tag))
					pagination.add(tag);
		}
		//If there are some tags available
		if(pagination!=null && !pagination.isEmpty()) {
			//Looping all tags (page) and adding them into GUI
			for(String tag: pagination.getPage(page)) {
				
				a.addItem(new ItemGUI(Tags.getTagItem(p, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(p, tag)) {
							API.selectTag(p, tag);
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
							a.close();
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
						openTags(player, page+1);
						
					}
				});
			}
			if(page>0) { //If this is not first page, then add previous button
				a.setItem(47, new ItemGUI(Loader.prev) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page-1);
						
					}
				});
			}
		}
		//PREVIEW ITEM
		addPreviewButton(p, a);
		/*a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.selectTag(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
				
			}
		});*/
	
		a.open(p);
	}
	
	private static void openCategories(Player p, int page) {
		//GUI preparation (title, size, frame
		GUI a = prepare( new GUI(Loader.gui.getString("gui.title"), 54) );
		//Loading pagination, 36 slots available
		Pagination<Category> pagination = new Pagination<Category>(36);
		//Adding categories into pagination
		for(String category: Loader.tags.getKeys("categories")) {
			if(Category.canSee(p, category)) //if player can SEE category in GUI
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
							
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
							openCategory(p, 0, category);
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
		addPreviewButton(p, a);
		/*a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.selectTag(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
			}
		});*/
		
		//Open GUI
		a.open(p);
	}


	private static void openCategory(Player p, int page, Category category) {
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
						if(Tags.hasPermission(p, tag))
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
							if(Tags.hasPermission(p, tag))
								pagination.add(tag);
						continue;
					} else //If ther is none special setting
						if(Tags.canSee(p, tag)) //If player can see tag in GUI
							pagination.add(tag);
			}
		}
		
		
		if(pagination!=null && !pagination.isEmpty()) {
			
			for(String tag: pagination.getPage(page)) {
				a.addItem(new ItemGUI(Tags.getTagItem(p, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(p, tag)) {
							API.selectTag(p, tag);
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
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
		/*a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.selectTag(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
				
			}
		});*/
		addPreviewButton(p, a);
	
		a.open(p);
	}
	
	private static void addPreviewButton(Player player, GUI gui) {
		gui.setItem(4, new ItemGUI(Tags.getPreviewItem(player)) {
			@Override
			public void onClick(Player player, HolderGUI hgui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.selectTag(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("options.tags.select.sound")), SoundCategory.MASTER, 10, 5);
					gui.close();
				}
			}
		});
	}
	
	private static GUI prepare(GUI a) {
		for (int i=0; i<=8; i++) {
			a.setItem(i, new ItemGUI( ItemMaker.of(Material.BLACK_STAINED_GLASS_PANE).amount(1).displayName("&7").build()) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		for (int i=45; i<=53; i++) {
			a.setItem(i, new ItemGUI( ItemMaker.of(Material.BLACK_STAINED_GLASS_PANE).amount(1).displayName("&7").build()) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		return a;
	}
}
