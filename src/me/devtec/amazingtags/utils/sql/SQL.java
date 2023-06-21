package me.devtec.amazingtags.utils.sql;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.shared.database.DatabaseHandler.InsertQuery;
import me.devtec.shared.database.DatabaseHandler.RemoveQuery;
import me.devtec.shared.database.DatabaseHandler.Result;
import me.devtec.shared.database.DatabaseHandler.SelectQuery;
import me.devtec.shared.database.DatabaseHandler.UpdateQuery;

public class SQL {
	
	public static boolean isEnabled() {
		if(Loader.config.exists(MySQL.enabled_path))
			return Loader.config.getBoolean(MySQL.enabled_path);
		return false;
	}
	
	 public static void selectTag(Player player, String tag) {
       if(tag!=null) {
           try {
               if(Loader.sql.connection.exists(SelectQuery.table(Loader.sql.getTablePrefix()+"users").where("name", player.getName()))) {
                   Loader.sql.connection.update(UpdateQuery.table(Loader.sql.getTablePrefix()+"users").value("tag", tag).where("name", player.getName()));
               }
               else {
                   Loader.sql.connection.insert(InsertQuery.table(Loader.sql.getTablePrefix()+"users", player.getName(), tag));
               }
           } catch (Exception e) {}
       }else {
           try {
			Loader.sql.connection.remove(RemoveQuery.table(Loader.sql.getTablePrefix()+"users").where("name", player.getName()).limit(0));
		} catch (SQLException e) {
			e.printStackTrace();
		}
       }
   }
	
	public static String getTag(Player player) {
		try {
			Result rs = Loader.sql.connection.get(SelectQuery.table(Loader.sql.getTablePrefix()+"users", "tag").where("name", player.getName()));
		if(rs.getValue()!=null)
			return rs.getValue()[0];
		return null;
		} catch (SQLException e) {
		}
		return null;
	}
	
	
}
