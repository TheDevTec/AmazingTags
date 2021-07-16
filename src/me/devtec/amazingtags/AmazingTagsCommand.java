package me.devtec.amazingtags;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.devtec.amazingtags.utils.API;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;

public class AmazingTagsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if(Loader.has(s, Loader.config.getString("Options.Command.Permission")) ) {
			if(args.length==0) {
				TagsGUI.open(Bukkit.getPlayer(s.getName()));
				return true;
			}
			if(args[0].equalsIgnoreCase("reload") && Loader.has(s, "amazingtags.reload")) {
				Loader.reload(s);
				return true;
			}
			if(args[0].equalsIgnoreCase("create") && Loader.has(s, "amazingtags.create")) {
				if(args.length>2) {
					String name = args[1];
					String format = StringUtils.buildString(2, args);
					Bukkit.broadcastMessage("1");
					API.createTag(name, format);
					TheAPI.msg(Loader.config.getString("Translation.created_new_tag").replace("%tagname%", name).replace("%tag%", format)
							.replace("%prefix%", Loader.prefix), s);
					return true;
				}
				Bukkit.broadcastMessage("0");
				TheAPI.msg("/Tags Create <name> <format> &7&l- &7Create new tag", s);
				return true;
				// /tag create <name> <format>
			}
			return true;
		}
		return true;
	}

}
