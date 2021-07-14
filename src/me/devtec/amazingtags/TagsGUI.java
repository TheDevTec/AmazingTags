package me.devtec.amazingtags;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.utils.Pagination;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.guiapi.GUI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.guiapi.ItemGUI;

public class TagsGUI {

	public static void open(Player p) {
		openTags(p, 0);
	}
	
	private static void openTags(Player p, int page) {
		GUI a = prepare( new GUI(Loader.gui.getString("GUI.Title"), 54) );
		
		Pagination<String> pagination = new Pagination<String>(45);
		
		for(String tag: Loader.tags.getKeys("Tags")) {
			if(Tags.isTag(tag))
				if(Tags.canSee(p, tag))
					pagination.add(tag);
		}

		if(pagination!=null && !pagination.isEmpty()) {
			
			for(String tag: pagination.getPage(page)) {
				
				a.addItem(new ItemGUI( ItemCreatorAPI.create(Tags.getType(tag), 1, Tags.getName(tag), Tags.getLore(tag, p))) {
					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						if(Tags.hasPermission(p, tag)) {
							Tags.select(p, tag);
							a.close();
						}
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
				// TODO Auto-generated method stub
				
			}
		});
	
		a.open(p);
	}
	
	private static GUI prepare(GUI a) {
		for (int i=0; i<=8; i++) {
			a.setItem(i, new ItemGUI( ItemCreatorAPI.create(Material.BLACK_STAINED_GLASS_PANE, 1, null)) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		for (int i=45; i<=53; i++) {
			a.setItem(i, new ItemGUI( ItemCreatorAPI.create(Material.BLACK_STAINED_GLASS_PANE, 1, null)) {
				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
				} });
		}
		return a;
	}
}
