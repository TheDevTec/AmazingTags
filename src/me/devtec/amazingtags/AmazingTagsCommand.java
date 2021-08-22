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
			if(args[0].equalsIgnoreCase("help")) {
				if(s.hasPermission(Loader.config.getString("Options.Command.Permission"))) TheAPI.msg("&7/Tags &7&l- &7Open GUI", s);
				if(s.hasPermission("amazingtags.reload")) TheAPI.msg("&7/Tags Reload &7&l- &7Reload configs", s);
				if(s.hasPermission("amazingtags.create")) TheAPI.msg("&7/Tags Create <name> <format> &7&l- &7Create new tag", s);
				return true;
			}
			if(args[0].equalsIgnoreCase("reload") && Loader.has(s, "amazingtags.reload")) {
				Loader.reload(s);
				return true;
			}//také mi nešlo nic posílat na git :( ani nově vytvořený... vůbec.. prostě eclipse chcípla
			//a dával sis třeba update eclipse? právě on to posral xd
			// já nemám update :D tak
			// tuto verzi mám tak měsíc -2 starou.. tak nevím :( Asi nějaký bug.... 
			//eee :(
			//ale jinak co se týče source codu co tu máš, neboj se! stačí otevřít složku a vložit do nového repa (AF) a ono to najde co jsi udělal za tměnay :P
			
			
			
			//ADasdasd
			// Ty tagy jsem dal znovu... pořád t ochce login a pak nič
			
			if(args[0].equalsIgnoreCase("create") && Loader.has(s, "amazingtags.create")) {
				if(args.length>2) {
					String name = args[1];
					String format = StringUtils.buildString(2, args);
					API.createTag(name, format);
					TheAPI.msg(Loader.config.getString("Translation.created_new_tag").replace("%tagname%", name).replace("%tag%", format)
							.replace("%prefix%", Loader.prefix), s);
					return true;
				}
				TheAPI.msg("&7/Tags Create <name> <format> &7&l- &7Create new tag", s);
				return true;
				// /tag create <name> <format>
			}
			return true;
		}
		return true;
	}

}
