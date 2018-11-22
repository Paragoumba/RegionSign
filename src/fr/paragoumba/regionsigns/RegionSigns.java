package fr.paragoumba.regionsigns;

import fr.paragoumba.regionsigns.commands.RegionSignCommand;
import fr.paragoumba.regionsigns.events.BlockBreakEventHandler;
import fr.paragoumba.regionsigns.events.PlayerInteractEventHandler;
import fr.paragoumba.regionsigns.events.SignChangeEventHandler;
import fr.paragoumba.regionsigns.objects.RegionSign;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class RegionSigns extends JavaPlugin {

    public static RegionSigns plugin;

    public static String toSellInfo;
    public static String reloadedInfo;
    public static String youBoughtInfo;
    public static String youSoldInfo;
    public static String errorError;
    public static String notANumberError;
    public static String doesntExistsError;

    @Override
    public void onEnable() {

        plugin = this;

        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {

            getConfig().options().copyDefaults(true);
            saveConfig();

        }

        loadConfig();

        Database.init();

        PluginManager pm = this.getServer().getPluginManager();

        //Events
        pm.registerEvents(new BlockBreakEventHandler(), this);
        pm.registerEvents(new SignChangeEventHandler(), this);
        pm.registerEvents(new PlayerInteractEventHandler(), this);

        getCommand("regionsign").setExecutor(new RegionSignCommand());

        populateSigns();

    }

    public void loadConfig(){

        reloadConfig();

        Configuration config = getConfig();

        toSellInfo = config.getString("messages.infos.toSell");
        reloadedInfo = config.getString("messages.infos.reloaded");
        youBoughtInfo = config.getString("messages.infos.youBought");
        youSoldInfo = config.getString("messages.infos.youSold");
        errorError = config.getString("messages.errors.error");
        notANumberError = config.getString("messages.errors.notANumber");
        doesntExistsError = config.getString("messages.errors.doesntExists");

    }

    public void populateSigns(){

        ArrayList<RegionSign> regionSigns = Database.getAllRegionSigns();

        for (RegionSign regionSign : regionSigns){

            Sign sign = regionSign.getSign();

            regionSign.display(sign.getLines(), "signs.sellingSign");
            sign.update(true);

        }
    }
}
