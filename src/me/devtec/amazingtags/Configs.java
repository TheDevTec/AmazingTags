package me.devtec.amazingtags;

import java.util.Arrays;
import java.util.List;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.utility.StreamUtils;

public class Configs {
	
	private static Config temp_data = new Config();
	static List<String> datas = Arrays.asList("Config.yml","GUI.yml","Tags.yml");
	
	/**
	 * Creates and loads all config files
	 */
	public static void load() {
		Loader.config = loadAndMerge("Config.yml", "Config.yml");
		Loader.config = loadAndMerge("Tags.yml", "Tags.yml");
		Loader.config = loadAndMerge("GUI.yml", "GUI.yml");
		
		convertTags();
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
	
	private static void convertTags() { //TODO - dokončit
		
	}
}
