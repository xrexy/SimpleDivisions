package me.xrexy.simpledivisions.utils.mysql;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.players.DivPlayer;
import me.xrexy.simpledivisions.utils.SerializableUtils;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLHandler {
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();
    private final FileConfiguration config = plugin.getConfig();
    private String username, password, dbName, dbAddress, dbPort, dbSSL, url = "";
    private final String prefix = "[MySQL] ";
    private Connection connection;
    private DivPlayerDummy[] topPlayers;

    public boolean isOnline = false;

    public MySQLHandler updateSettings() {
        this.username = config.getString("mysql.username");
        this.password = config.getString("mysql.password");
        this.dbName = config.getString("mysql.database-name");
        this.dbPort = config.getString("mysql.server.port");
        this.dbAddress = config.getString("mysql.server.address");
        this.dbSSL = config.getString("mysql.server.SSL");
        this.topPlayers = new DivPlayerDummy[config.getInt("mysql.top-count")];
        return this;
    }

    public void startSQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Utils.exception(e, Level.SEVERE, prefix + "Couldn't find MySQL Driver, disabling MySQL support");
            return;
        }
        try {
            this.url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s", this.dbAddress, this.dbPort, this.dbName, this.dbSSL);
            connection = DriverManager.getConnection(this.url, this.username, this.password);
            createTable();
            isOnline = true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("28000")) {
                Utils.exception(e, Level.WARNING, prefix + "Login credentials are incorrect. Check your config.yml");
            } else if (e.getSQLState().equals("08S01")) {
                Utils.exception(e, Level.WARNING, prefix + "Couldn't establish a connection!");
            } else {
                Utils.exception(e, Level.WARNING, prefix + "Unhandled error happened while connecting to database!");
            }
        }
    }

    public void updateTopPlayersAndStartTimer() {
        Bukkit.getOnlinePlayers().forEach(player -> plugin.getSqlHandler().savePlayerInDatabase(plugin.getPlayerAPI().getPlayer(player)));

        String query = "SELECT username, score FROM divisions ORDER BY score desc LIMIT ?";
        int delaySeconds = config.getInt("mysql.top-timer") * 20;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, config.getInt("mysql.top-count"));

            int i = 0;
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                topPlayers[i] = new DivPlayerDummy(result.getString("username"), result.getInt("score"), i);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.log(Level.WARNING, "Couldn't update top players! Retrying after " + delaySeconds + " seconds.");
        }

         Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                updateTopPlayersAndStartTimer();
            }
        }, delaySeconds).getTaskId();
    }

    public DivPlayerDummy[] getTopPlayers() {
        if (topPlayers == null || topPlayers.length == 0) updateTopPlayersAndStartTimer();

        return topPlayers;
    }

    public void savePlayerInDatabase(DivPlayer player) {
        String query = "INSERT INTO `divisions` (`uuid`, `username`, `divisionID`, `score`, `maxDivision`, `claimed`) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "`uuid` = ?," +
                "`username` = ?," +
                "`divisionID` = ?," +
                "`score` = ?," +
                "`maxDivision` = ?," +
                "`claimed` = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String serializedArr = SerializableUtils.toString(player.getClaimed());
            stmt.setString(1, player.getUuid());
            stmt.setString(2, player.getUsername());
            stmt.setInt(3, player.getDivisionIndex());
            stmt.setInt(4, player.getScore());
            stmt.setInt(5, player.getMaxDivision());
            stmt.setString(6, serializedArr);

            stmt.setString(7, player.getUuid());
            stmt.setString(8, player.getUsername());
            stmt.setInt(9, player.getDivisionIndex());
            stmt.setInt(10, player.getScore());
            stmt.setInt(11, player.getMaxDivision());
            stmt.setString(12, serializedArr);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.log(Level.WARNING, "Couldn't save information for player with UUID of: " + player.getUuid());
        }
    }

    @Nullable
    public DivPlayer loadPlayer(OfflinePlayer player) {
        return getDivPlayer(player.getUniqueId());
    }

    @Nullable
    public DivPlayer loadPlayer(Player player) {
        return getDivPlayer(player.getUniqueId());
    }

    private DivPlayer getDivPlayer(UUID uuid) {
        String query = "SELECT * FROM divisions d WHERE d.uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());

            ResultSet result = stmt.executeQuery();
            ArrayList<Integer> claimed;
            while (result.next()) {
                claimed = (ArrayList<Integer>) SerializableUtils.fromString(result.getString("claimed"));
                return new DivPlayer(result.getInt("divisionID"), result.getString("username"), result.getInt("score"), uuid.toString(), result.getInt("maxDivision"), claimed); // returns first one it finds, would be one anyways since uuid is unique
            }
        } catch (Exception e) {
            Utils.log(Level.WARNING, "Couldn't load information for a player");
        }
        return null;
    }

    private void createTable() {
        String table = "CREATE TABLE IF NOT EXISTS `divisions` (" +
                "`uuid` VARCHAR(40) NOT NULL," +
                "`username` VARCHAR(100) NOT NULL," +
                "`divisionID` INT NOT NULL," +
                "`score` INT NOT NULL," +
                "`maxDivision` INT NOT NULL," +
                "`claimed` VARCHAR(5000) NOT NULL," +
                "PRIMARY KEY (`uuid`))";
        try (PreparedStatement stmt = connection.prepareStatement(table)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            Utils.exception(e, Level.SEVERE, prefix + "Something went wrong while creating divisions DB");
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
            isOnline = false;
        } catch (Exception e) {
            Utils.log(Level.SEVERE, prefix + "An error occurred while closing the connection");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbSSL() {
        return dbSSL;
    }

    public String getUrl() {
        return url;
    }

    public Connection getConnection() {
        return connection;
    }
}
