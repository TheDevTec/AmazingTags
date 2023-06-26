package me.devtec.amazingtags;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.utils.API;
import me.devtec.amazingtags.utils.MessageUtils;
import me.devtec.amazingtags.utils.MessageUtils.Placeholders;
import me.devtec.amazingtags.utils.Tags;
import me.devtec.shared.utility.StringUtils;

public class AmazingTagsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if(Loader.has(s, Loader.config.getString("Options.Command.Permission")) ) { //if player can use command
			if(args.length==0) { // /tag -> opens GUI
				TagsGUI.open(Bukkit.getPlayer(s.getName()));
				return true;
			}
			// /tag help
			if(args[0].equalsIgnoreCase("help")) {
				if(s.hasPermission(Loader.config.getString("Options.Command.Permission"))) Loader.msg("&7/Tags &7&l- &7Open GUI", s);
				if(s.hasPermission("amazingtags.reload")) Loader.msg("&7/Tags Reload &7&l- &7Reload configs", s);
				if(s.hasPermission("amazingtags.create")) Loader.msg("&7/Tags Create <name> <format> &7&l- &7Create new tag", s);
				if(s.hasPermission("amazingtags.set")) Loader.msg("&7/Tags Set <Player> <tag_name> &7&l- &7Set tag to player", s);
				return true;
			}
			// /tag reload
			if(args[0].equalsIgnoreCase("reload") && Loader.has(s, "amazingtags.reload")) {
				Loader.reload(s);
				return true;
			}
			// /tag set ....
			if(args[0].equalsIgnoreCase("set") && Loader.has(s, "amazingtags.set")) {
				// /tag set <player> <tag_name>
	            if (args.length != 3) {
	                Loader.msg("&7/Tags Set <Player> <tag_name> &7&l- &7Set tag to player", s);
	                return true;
	            }
	            Player p = Bukkit.getPlayer(args[1]);
	            if (p == null) {
	            	MessageUtils.message(s, "Translation.offline", Placeholders.c().addPlayer("player", s));
	                //Loader.msg(Loader.config.getString("Translation.offline").replace("%player%", args[1]).replace("%prefix%", Loader.prefix), s);
	                return true;
	            }
	            String tag = args[2];
	            if(Tags.exist(tag)) {
	                API.selectTag(p, tag);
	                MessageUtils.message(s,"Translation.set_tag", Placeholders.c().add("tag_name", tag)
	                		.add("%tag%", API.getTagFormat(tag)));
	                //Loader.msg(Loader.config.getString("Translation.set_tag").replace("%tag_name%", tag).replace("%tag%", API.getTagFormat(tag)).replace("%player%", p.getName()).replace("%prefix%", Loader.prefix), s);
	                return true;
	            }
	            MessageUtils.message(s, "Translation.wrong_tag", Placeholders.c().add("tag", args[2]) );
	            //Loader.msg(Loader.config.getString("Translation.wrong_tag").replace("%tag%", args[2]).replace("%prefix%", Loader.prefix), s);
	            return true;
	        }

			// /tag create <name> <format>
			if(args[0].equalsIgnoreCase("create") && Loader.has(s, "amazingtags.create")) {
				if(args.length>2) {
					String name = args[1];
					String format = StringUtils.buildString(2, args);
					API.createTag(name, format);
					MessageUtils.message(s, "Translation.created_new_tag", Placeholders.c().add("%tagname%", name)
							.add("%tag%", format));
					//Loader.msg(Loader.config.getString("Translation.created_new_tag").replace("%tagname%", name).replace("%tag%", format)
					//		.replace("%prefix%", Loader.prefix), s);
					return true;
				}
				Loader.msg("&7/Tags Create <name> <format> &7&l- &7Create new tag", s);
				return true;
			}
			return true;
		}
		return true;
	}

}
