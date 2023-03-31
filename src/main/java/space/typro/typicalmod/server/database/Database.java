package space.typro.typicalmod.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection(Database database) throws SQLException{
        return DriverManager.getConnection(database.getUrl(), database.getUsername(), database.getPassword());
    }
    private final String url;
    private final String username;
    private final String password;
    public Database(String url1, String username1, String password1){
        this.url = url1;
        this.username = username1;
        this.password = password1;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Database{");
        sb.append("url='").append(url).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
