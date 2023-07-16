package me.devtec.amazingtags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.devtec.amazingtags.utils.MessageUtils;
import me.devtec.amazingtags.utils.MessageUtils.Placeholders;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.theapi.bukkit.game.ItemMaker;

public class Configs {
	
	private static Config temp_data = new Config();
	static List<String> datas = Arrays.asList("Config.yml","GUI.yml","Tags.yml");
	
	/**
	 * Creates and loads all config files. </br>
	 * Also loading next and previous button.
	 */
	public static void load() {
		Loader.config = loadAndMerge("Config.yml", "Config.yml");
		Loader.tags = loadAndMerge("Tags.yml", "Tags.yml");
		Loader.gui = loadAndMerge("GUI.yml", "GUI.yml");
		
		convertTags();
		
		TagsGUI.next = ItemMaker.loadFromConfig(Loader.gui, "gui.items.next");
		TagsGUI.prev = ItemMaker.loadFromConfig(Loader.gui, "gui.items.previous");
	}
	
	/**
	 * This method will copy and load all content in file
	 * 
	 * @param sourcePath - path to .yml (source file)
	 * @param filePath - path to .yml in plugins/AmazingTags directory (server file)
	 * @return {@link Config}
	 */
	private static Config loadAndMerge(String sourcePath, String filePath) {
		temp_data.reload(StreamUtils.fromStream(Loader.plugin.getResource("Configs/" + sourcePath)));
		Config result = new Config("plugins/AmazingTags/" + filePath);
		if (result.merge(temp_data))
			result.save(DataType.YAML);
		temp_data.clear();
		return result;
	}
	
	private static void convertTags() {
		if(Loader.config.getInt("file_version")==1) {
			int converted = 0;
			
			for(String tag: Loader.tags.getKeys("Tags")) {
				
				List<String> list = new ArrayList<String>(Arrays.asList("Tag", "Info", "Enabled", 
						"Permission", "Name"));
				for(String path: list)
					if(Loader.tags.exists("Tags."+tag+"."+path))
						Loader.tags.set("tags."+tag+"."+path.toLowerCase(), 
							Loader.tags.get("Tags."+tag+"."+path));

				if(Loader.tags.exists("Tags."+tag+".Select.Commands") )
						Loader.tags.set("tags."+tag+".select.commands", Loader.tags.get("Tags."+tag+".Select.Commands") );
				if(Loader.tags.exists("Tags."+tag+".Select.Messages") )
						Loader.tags.set("tags."+tag+".select.messages", Loader.tags.get("Tags."+tag+".Select.Messages") );
				
				if(Loader.tags.exists("Tags."+tag+".Type") )
						Loader.tags.set("tags."+tag+".item.type", Loader.tags.get("Tags."+tag+".Type") );
				if(Loader.tags.exists("Tags."+tag+".Type") )
					Loader.tags.set("tags."+tag+".item.type", Loader.tags.get("Tags."+tag+".Type") );
				if(Loader.tags.exists("Tags."+tag+".Lore") )
					Loader.tags.set("tags."+tag+".item.lore", Loader.tags.get("Tags."+tag+".Lore") );

				Loader.tags.save();
				converted++;	
			}
			Loader.tags.remove("Tags");
			Loader.tags.save();
			
			MessageUtils.msgConsole("[AmazinggTags] &4Converted &c&l%c% &4tags to new format!", Placeholders.c().add("c", converted));
			
			Loader.config.set("file_version", 2);
		}
		
	}
	/*
	private static void keys(Config config, String path, String newpath) {
		boolean looped = false;
		for(String keypath: config.getKeys(path)) {
			if(keypath == null || keypath.isEmpty())
				break;
			keys(config, path+"."+keypath, newpath+"."+keypath);
			looped = true;
			continue;
		}
		if(!looped) {
			config.set(newpath,config.get(path));
			config.save();
		}
	}
	*/
}
