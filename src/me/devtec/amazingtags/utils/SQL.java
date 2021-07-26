package me.devtec.amazingtags.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;

public class SQL {

	public static boolean isEnabled() {
		return Loader.config.getBoolean("Options.MySQL.Use");
	}
	
	public static Connection connect() {
		return Database(getHost(), getPort(), getDatabase(), getUser(), getPassword());
	}
	
	private static Connection Database(String host, String port, String db, String usr, String psw){
		synchronized (Loader.plugin){
			try{
				return DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db,usr,psw);
			} catch (Exception e){e.printStackTrace();}
		}

		return null;
	}
	
	private static String getHost() {
		return Loader.config.getString("Options.MySQL.hostname");
	}
	private static String getPort() {
		return Loader.config.getString("Options.MySQL.port");
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
	

	public static void selectTag(Player player, String tag) {
		if(tag!=null) {
			try {
				ResultSet set = Loader.connection.createStatement().executeQuery("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
				Bukkit.broadcastMessage("1");
				if(set!=null && set.next()) {
					Bukkit.broadcastMessage("2");
					Loader.connection.createStatement().execute("update "+getTablePrefix()+"users set tag='"+tag+"' where name='"+player.getName()+"'");
				}
				else {
					PreparedStatement preparedStmt = Loader.connection.prepareStatement("insert into "+getTablePrefix()+"users (name, tag)"+ " values (?, ?)");
					preparedStmt.setString(1, player.getName());
					preparedStmt.setString(2, tag);
					preparedStmt.execute();
				}
			
			} catch (Exception e) {
		}
		}else {
			try {
				Loader.connection.createStatement().execute("delete from "+getTablePrefix()+"users where name='"+player.getName()+"'");
			} catch (SQLException e) {
				
			}
		}
	}
	
	public static String getTag(Player player) {
		try {
			ResultSet rs = Loader.connection.createStatement().executeQuery("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
			Bukkit.broadcastMessage("1");
		if(rs!=null && rs.next()) {
			Bukkit.broadcastMessage("2");
			//while (rs.next()) {
				Bukkit.broadcastMessage("3");
				return rs.getString("tag");
			//}
		}
		return null;
		} catch (SQLException e) {
			Bukkit.broadcastMessage(e.getMessage());
		}
		return null;
	}
}
