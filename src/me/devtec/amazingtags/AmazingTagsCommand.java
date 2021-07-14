package me.devtec.amazingtags;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AmazingTagsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if(Loader.has(s, Loader.config.getString("Options.Command.Permission")) ) {
			if(args.length==0) {
				
			}
			return true;
		}
		return true;
	}

}
