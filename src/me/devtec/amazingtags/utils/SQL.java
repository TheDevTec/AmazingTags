package me.devtec.amazingtags.utils;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.shared.database.DatabaseAPI;
import me.devtec.shared.database.DatabaseAPI.DatabaseType;
import me.devtec.shared.database.DatabaseAPI.SqlDatabaseSettings;
import me.devtec.shared.database.DatabaseHandler;
import me.devtec.shared.database.DatabaseHandler.InsertQuery;
import me.devtec.shared.database.DatabaseHandler.RemoveQuery;
import me.devtec.shared.database.DatabaseHandler.Result;
import me.devtec.shared.database.DatabaseHandler.Row;
import me.devtec.shared.database.DatabaseHandler.SelectQuery;
import me.devtec.shared.database.DatabaseHandler.UpdateQuery;

public class SQL {

	public static boolean isEnabled() {
		return Loader.config.getBoolean("Options.MySQL.Use");
	}
	
	public static DatabaseHandler connect() {
		return Database(getHost(), getPort(), getDatabase(), getUser(), getPassword());
	}
	
	private static DatabaseHandler Database(String host, int port, String db, String usr, String psw){
		synchronized (Loader.plugin){
			try{
				return DatabaseAPI.openConnection(DatabaseType.MYSQL, new SqlDatabaseSettings(DatabaseType.MYSQL, host, port, db, usr, psw));
 			} catch (Exception e){e.printStackTrace();}
		}

		return null;
	}
	
	private static String getHost() {
		return Loader.config.getString("Options.MySQL.hostname");
	}
	private static int getPort() {
		return Loader.config.getInt("Options.MySQL.port");
	}
	private static String getUser() {
		return Loader.config.getString("Options.MySQL.username");
	}
	private static String getPassword() {
		return Loader.config.getString("Options.MySQL.password");
	}
	private static String getDatabase() {
		return Loader.config.getString("Options.MySQL.database");
	}
	private static String getTablePrefix() {
		return Loader.config.getString("Options.MySQL.table_prefix");
	}
	
	public static void createTable() {
		try {
			Loader.connection.createTable(getTablePrefix()+"users", new Row[]{new Row("name", "TEXT", false, "", "", ""), new Row("tag", "TEXT", false, "", "", "")});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	 public static void selectTag(Player player, String tag) {
       if(tag!=null) {
           try {
               if(Loader.connection.exists(SelectQuery.table(getTablePrefix()+"users").where("name", player.getName()))) {
                   Loader.connection.update(UpdateQuery.table(getTablePrefix()+"users").value("tag", tag).where("name", player.getName()));
               }
               else {
                   Loader.connection.insert(InsertQuery.table(getTablePrefix()+"users", player.getName(), tag));
               }
           } catch (Exception e) {}
       }else {
           try {
			Loader.connection.remove(RemoveQuery.table(getTablePrefix()+"users").where("name", player.getName()).limit(0));
		} catch (SQLException e) {
			e.printStackTrace();
		}
       }
   }
	
	public static String getTag(Player player) {
		try {
			Result rs = Loader.connection.get(SelectQuery.table(getTablePrefix()+"users", "tag").where("name", player.getName()));
		if(rs.getValue()!=null)
			return rs.getValue()[0];
		return null;
		} catch (SQLException e) {
		}
		return null;
	}
}
