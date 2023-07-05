package me.devtec.amazingtags.utils.sql;

import java.sql.SQLException;

import me.devtec.amazingtags.Loader;
import me.devtec.shared.database.DatabaseAPI;
import me.devtec.shared.database.DatabaseAPI.DatabaseType;
import me.devtec.shared.database.DatabaseAPI.SqlDatabaseSettings;
import me.devtec.shared.database.DatabaseHandler;
import me.devtec.shared.database.DatabaseHandler.Row;

public class MySQL {
	
	public MySQL() {}
	
	public DatabaseHandler connection;
	
	public static String enabled_path = "MySQL.Use";
	private String hostname_path = "MySQL.hostname";
	private String port_path = "MySQL.port";
	private String username_path = "MySQL.username";
	private String password_path = "MySQL.password";
	private String database_path = "MySQL.database";
	private String tablePrefix_path = "MySQL.table_prefix";
	
	/**
	 * Creates all connections
	 */
	public MySQL prepare() {
		connect();
		createTable();
		return this;
	}
	
	private boolean connect() {
		try {
			this.connection = Database(getHost(), getPort(), getDatabase(), getUser(), getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		this.connection = Database(getHost(), getPort(), getDatabase(), getUser(), getPassword());
		return true;
	}
	
	/**
	 * Ends connection
	 * @return true - if succesfull </br> false - if not
	 */
	public boolean close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private DatabaseHandler Database(String host, int port, String db, String usr, String psw){
		synchronized (Loader.plugin){
			try{
				return DatabaseAPI.openConnection(DatabaseType.MYSQL, new SqlDatabaseSettings(DatabaseType.MYSQL, host, port, db, usr, psw));
 			} catch (Exception e){e.printStackTrace();}
		}

		return null;
	}
	
	private void createTable() {
		try {
			connection.createTable(getTablePrefix()+"users", new Row[]{new Row("name", "TEXT", false, "", "", ""), new Row("tag", "TEXT", false, "", "", "")});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getHost() {
		return Loader.config.getString(hostname_path);
	}
	private int getPort() {
		return Loader.config.getInt(port_path);
	}
	private String getUser() {
		return Loader.config.getString(username_path);
	}
	private String getPassword() {
		return Loader.config.getString(password_path);
	}
	private String getDatabase() {
		return Loader.config.getString(database_path);
	}
	public String getTablePrefix() {
		return Loader.config.getString(tablePrefix_path);
	}

}
