package me.devtec.amazingtags;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.Category;
import me.devtec.amazingtags.utils.ItemCreatorAPI;
import me.devtec.amazingtags.utils.Pagination;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.theapi.bukkit.gui.GUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class TagsGUI {

	public static void open(Player p) {
		if(Loader.tags.exists("categories") && p.getName().equalsIgnoreCase("Houska02"))
			openCategories(p, 0);
		else
			openTags(p, 0);
	}
	
	private static void openTags(Player p, int page) {
		GUI a = prepare( new GUI(Loader.gui.getString("GUI.Title"), 54) );
		
		Pagination<String> pagination = new Pagination<String>(36);
		
		for(String tag: Loader.tags.getKeys("Tags")) {
			if(Tags.isTag(tag))
				if(Tags.canSee(p, tag))
					pagination.add(tag);
		}

		if(pagination!=null && !pagination.isEmpty()) {
			
			for(String tag: pagination.getPage(page)) {
				
				a.addItem(new ItemGUI(Tags.getTagItem(p, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(p, tag)) {
							API.select(p, tag);
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
							a.close();
						}
						else
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
					}
				});
				
			}
		
			if(pagination.totalPages()>page+1) {
				a.setItem(51, new ItemGUI(Loader.next) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page+1);
						
					}
				});
			}
			if(page>0) {
				a.setItem(47, new ItemGUI(Loader.prev) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page-1);
						
					}
				});
			}
		}
		
		a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.select(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
				
			}
		});
	
		a.open(p);
	}
	
	private static void openCategories(Player p, int page) {
		GUI a = prepare( new GUI(Loader.gui.getString("GUI.Title"), 54) );
		
		Pagination<String> pagination = new Pagination<String>(36);
		
		for(String category: Loader.tags.getKeys("categories")) {
			pagination.add(category);
		}

		if(pagination!=null && !pagination.isEmpty()) {
			
			for(String cat: pagination.getPage(page)) {
				Category category = new Category(cat);
				if(category.getSlot()==-1) {
					a.addItem(new ItemGUI( category.getItem()) {
						@Override
						public void onClick(Player player, HolderGUI gui, ClickType click) {
							if(category.hasPermission(player)) {
								
								player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
								openCategory(p, 0, category);
							}else
								player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
						}
					});
				}
				else {
					a.setItem(category.getSlot(), new ItemGUI( category.getItem()) {
						@Override
						public void onClick(Player player, HolderGUI gui, ClickType click) {
							if(category.hasPermission(player)) {
								
								player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
								openCategory(p, 0, category);
							}else
								player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
						}
					});
				}
			}
		
			if(pagination.totalPages()>page+1) {
				a.setItem(51, new ItemGUI(Loader.next) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page+1);
						
					}
				});
			}
			if(page>0) {
				a.setItem(47, new ItemGUI(Loader.prev) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openTags(player, page-1);
						
					}
				});
			}
		}
		
		a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.select(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
			}
		});
	
		a.open(p);
	}


	private static void openCategory(Player p, int page, Category category) {
		GUI a = prepare( new GUI(Loader.gui.getString("GUI.Title"), 54) );
		
		Pagination<String> pagination = new Pagination<String>(36);
		
		String special = category.getSpecial();
		
		for(String tag: category.getContent()) {
			if(Tags.isTag(tag))
				if(special!=null) {
					if(special.equalsIgnoreCase("ALL"))
						pagination.add(tag);
					if(special.equalsIgnoreCase("PERM"))
						if(Tags.hasPermission(p, tag))
							pagination.add(tag);
					continue;
				}
				if(Tags.canSee(p, tag))
					pagination.add(tag);
		}

		if(pagination!=null && !pagination.isEmpty()) {
			
			for(String tag: pagination.getPage(page)) {
				a.addItem(new ItemGUI(Tags.getTagItem(p, tag)) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(p, tag)) {
							API.select(p, tag);
							player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
							a.close();
						}
						else
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.MASTER, 10, 5);
					}
				});
				
			}
		
			if(pagination.totalPages()>page+1) {
				a.setItem(51, new ItemGUI(Loader.next) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategory(player, page+1, category);
						
					}
				});
			}
			if(page>0) {
				a.setItem(47, new ItemGUI(Loader.prev) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						openCategory(player, page-1, category);
						
					}
				});
			}
		}
		
		a.setItem(4, new ItemGUI(Tags.getPreviewItem(p)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				if(click==ClickType.RIGHT_PICKUP||click==ClickType.RIGHT_DROP) {
					API.select(player, null);
					player.playSound(player.getLocation(), Sound.valueOf(Loader.config.getString("Options.Tags.Select.Sound")), SoundCategory.MASTER, 10, 5);
					a.close();
				}
				
			}
		});
	
		a.open(p);
	}
	
	private static GUI prepare(GUI a) {
		for (int i=0; i<=8; i++) {
			a.setItem(i, new ItemGUI( ItemCreatorAPI.create(Material.BLACK_STAINED_GLASS_PANE, 1, "&7")) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		for (int i=45; i<=53; i++) {
			a.setItem(i, new ItemGUI( ItemCreatorAPI.create(Material.BLACK_STAINED_GLASS_PANE, 1, "&7")) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		return a;
	}
}
