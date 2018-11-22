package fr.paragoumba.regionsigns;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import fr.paragoumba.regionsigns.objects.RegionSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import static fr.paragoumba.regionsigns.RegionSigns.plugin;

public class Database {

    private static String database, url, login, password, regionSignTable;

    public static void createRegionSign(String regionId, Sign sign, OfflinePlayer owner, double price){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + database + ".`" + regionSignTable + "` VALUES (?, ?, ?, ?, ?)")){

            BlockFace blockFace = ((Directional) sign.getData()).getFacing();

            statement.setString(1, regionId);
            statement.setString(2, serialize(sign.getLocation()));
            statement.setString(3, blockFace.name());
            statement.setString(4, owner.getUniqueId().toString());
            statement.setDouble(5, price);
            statement.execute();

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static RegionSign getRegionSign(Location signLoc){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + database + ".`" + regionSignTable + "` WHERE `signLoc`=?")){

            statement.setString(1, serialize(signLoc));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new RegionSign(resultSet.getString("regionId"), Bukkit.getPlayer(UUID.fromString(resultSet.getString("owner"))), deserialize(resultSet.getString("signLoc")), BlockFace.valueOf(resultSet.getString("signDirection")), resultSet.getDouble("price"));

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return null;

    }

    static ArrayList<RegionSign> getAllRegionSigns(){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + database + ".`" + regionSignTable + "`")){

            ArrayList<RegionSign> regionSigns = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) regionSigns.add(new RegionSign(resultSet.getString("regionId"), Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("owner"))), deserialize(resultSet.getString("signLoc")), BlockFace.valueOf(resultSet.getString("signDirection")), resultSet.getDouble("price")));

            return regionSigns;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return new ArrayList<>();

    }

    public static boolean removeRegionSign(Location signLoc){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + database + ".`" + regionSignTable + "` WHERE `signLoc`=?")){

            System.out.println(serialize(signLoc));

            statement.setString(1, serialize(signLoc));

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static boolean regionSignExists(String regionId, Location signLoc){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("SELECT price FROM " + database + ".`" + regionSignTable + "` WHERE `regionId`=? AND `signLoc`=?")){

            statement.setString(1, regionId);
            statement.setString(2, serialize(signLoc));

            return statement.executeQuery().next();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void updateRegionSign(String regionId, OfflinePlayer owner, Sign sign, double price){

        try(Connection connection = DriverManager.getConnection(url, login, password);
            PreparedStatement statement = connection.prepareStatement("UPDATE `" + regionSignTable + "` SET `regionId`=?, `owner`=?, `signLoc`=?, `signDirection`=?, `price`=?")){

            statement.setString(1, regionId);
            statement.setString(2, owner.getUniqueId().toString());
            statement.setString(3, serialize(sign.getLocation()));
            statement.setString(4, ((Directional) sign.getData()).getFacing().name());
            statement.setDouble(5, price);
            statement.execute();

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static boolean isRegionSign(Location signLoc) {

        try (Connection connection = DriverManager.getConnection(url, login, password)) {

            PreparedStatement statement = connection.prepareStatement("SELECT price FROM " + database + ".`" + regionSignTable + "` WHERE `signLoc`=?");

            statement.setString(1, serialize(signLoc));

            return statement.executeQuery().next();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    static void init(){

        Configuration config = plugin.getConfig();
        database = config.getString("database");
        url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + database;
        login = config.getString("login");
        password = config.getString("password");
        regionSignTable = config.getString("regionSignTable");

        try(Connection connection = DriverManager.getConnection(url, login, password)){

            // RegionSignsTable
            try(Statement state = connection.createStatement()){

                state.execute("SELECT min(price) FROM " + database + ".`" + regionSignTable + "`");

            } catch (MySQLSyntaxErrorException e){

                Statement state = connection.createStatement();

                state.executeUpdate("CREATE TABLE " + database + ".`" + regionSignTable + "` (" +
                        "regionId TINYTEXT," +
                        "signLoc TINYTEXT," +
                        "signDirection VARCHAR(16)," +
                        "owner TINYTEXT," +
                        "price DOUBLE)");

                state.close();
                Bukkit.getLogger().log(Level.INFO, "Ecodiversity: " + regionSignTable + " table created.");

            }

            Bukkit.getLogger().log(Level.INFO, "Ecodiversity: " + regionSignTable + " table works.");

        } catch (SQLException e) {

            Bukkit.getLogger().log(Level.SEVERE, "Ecodiversity: Error when initializing database. Check credentials in the config.");

        }
    }

    private static String serialize(Location loc){
        
        return loc == null ? null : loc.getWorld().getUID().toString() + ',' + loc.getX() + ',' + loc.getY() + ',' + loc.getZ();

    }

    private static Location deserialize(String loc){

        if (loc == null) return null;

        String[] params = loc.split(",");

        if (params.length < 4) return null;

        return new Location(Bukkit.getWorld(UUID.fromString(params[0])), Double.parseDouble(params[1]), Double.parseDouble(params[2]), Double.parseDouble(params[3]));

    }
}
