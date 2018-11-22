package fr.paragoumba.regionsigns.commands;

import fr.paragoumba.regionsigns.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static fr.paragoumba.regionsigns.RegionSigns.*;

public class RegionSignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings[0].equalsIgnoreCase("remove") && commandSender.hasPermission("regionsign.remove")){

            if (strings.length > 4) {

                double x, y, z;

                try {

                    x = Double.parseDouble(strings[2]);

                } catch (NumberFormatException e) {

                    commandSender.sendMessage(notANumberError.replace("{var}", "x"));
                    return true;

                }

                try {

                    y = Double.parseDouble(strings[3]);

                } catch (NumberFormatException e) {

                    commandSender.sendMessage(notANumberError.replace("{var}", "y"));
                    return true;

                }

                try {

                    z = Double.parseDouble(strings[4]);

                } catch (NumberFormatException e) {

                    commandSender.sendMessage(notANumberError.replace("{var}", "z"));
                    return true;

                }

                Location loc = new Location(Bukkit.getWorld(strings[1]), x, y, z);

                if (Database.removeRegionSign(loc)) loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                else commandSender.sendMessage(doesntExistsError);

                return true;

            }

        } else if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission("regionsign.reload")){

            plugin.loadConfig();
            plugin.populateSigns();
            commandSender.sendMessage(reloadedInfo);

            return true;

        }

        return false;

    }
}
