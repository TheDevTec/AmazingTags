package me.devtec.amazingtags.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.devtec.amazingtags.Loader;
import me.devtec.theapi.sqlapi.SQLAPI;

public class SQL {

	public static boolean isEnabled() {
		return Loader.config.getBoolean("Options.MySQL.Use");
	}
	
	public static SQLAPI connect() {
		return Database(getHost(), getPort(), getDatabase(), getUser(), getPassword());
	}
	
	private static SQLAPI Database(String host, int port, String db, String usr, String psw){
		synchronized (Loader.plugin){
			try{
				//Class.forName("com.mysql.jdbc.Driver");
				//return java.sql.DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db,usr,psw);
				return new SQLAPI(host, db, usr, psw, port);
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
		Loader.connection.execute("CREATE TABLE IF NOT EXISTS "+getTablePrefix()+"users (name TEXT NOT NULL, tag TEXT NOT NULL)");
		//Loader.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS "+getTablePrefix()+"users (name TEXT NOT NULL, tag TEXT NOT NULL)");
	}

	/*public static void selectTag(Player player, String tag) {
		if(tag!=null) {
			try {
				ResultSet set = Loader.connection.query("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
				//ResultSet set = Loader.connection.createStatement().executeQuery("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
				if(set!=null && set.next()) {
					Loader.connection.execute("update "+getTablePrefix()+"users set tag='"+tag+"' where name='"+player.getName()+"'");
					//Loader.connection.createStatement().execute("update "+getTablePrefix()+"users set tag='"+tag+"' where name='"+player.getName()+"'");
				}
				else {
					//PreparedStatement preparedStmt = Loader.connection.getPreparedStatement("insert into "+getTablePrefix()+"users (name, tag)"+ " values (?, ?)");
					//PreparedStatement preparedStmt = Loader.connection.prepareStatement("insert into "+getTablePrefix()+"users (name, tag)"+ " values (?, ?)");

					//preparedStmt.setString(1, player.getName());
					//preparedStmt.setString(2, tag);
					//preparedStmt.execute();
					
					Loader.connection.execute("insert into "+getTablePrefix()+"users (name, tag)"+ " values ("+ player.getName()+", "+tag+")");
				}
			
			} catch (Exception e) {}
			//Bukkit.broadcastMessage("tag uložen");
		}else {
			Loader.connection.execute("delete from "+getTablePrefix()+"users where name='"+player.getName()+"'");
			//Loader.connection.createStatement().execute("delete from "+getTablePrefix()+"users where name='"+player.getName()+"'");
		}
	}*/
	 // TODO - NOVÝ THEAPI IMPORT
	 public static void selectTag(Player player, String tag) {
        if(tag!=null) {
            try {
                ResultSet set = Loader.connection.query("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
                if(set!=null && set.next()) {
                    Loader.connection.execute("update "+getTablePrefix()+"users set tag='"+tag+"' where name='"+player.getName()+"'");
                }
                else {
                    Loader.connection.execute("insert into "+getTablePrefix()+"users values ('"+player.getName()+"', '"+tag+"')");
                    //Loader.connection.execute("insert into "+getTablePrefix()+"users (name, tag)"+ " values ("+player.getName()+", "+tag+")");
                    }
            
            } catch (Exception e) {}
        }else {
            Loader.connection.execute("delete from "+getTablePrefix()+"users where name='"+player.getName()+"'");
        }
    }
	 
	
	public static String getTag(Player player) {
		if(Loader.connection==null || !Loader.connection.isConnected())
			return null;
		try {
			ResultSet rs = Loader.connection.query("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
			//ResultSet rs = Loader.connection.createStatement().executeQuery("select * from "+getTablePrefix()+"users where name='"+player.getName()+"'");
		if(rs!=null && rs.next()) {
			//while (rs.next()) {
				return rs.getString("tag");
			//}
		}
		return null;
		} catch (SQLException e) {
		}
		return null;
	}
}
