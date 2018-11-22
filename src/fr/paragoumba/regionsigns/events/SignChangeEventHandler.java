package fr.paragoumba.regionsigns.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.paragoumba.regionsigns.objects.RegionSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager;

public class SignChangeEventHandler implements Listener {

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event){

        Block block = event.getBlock();

        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {

            Sign sign = (Sign) block.getState();

            if (event.getLine(0).trim().equalsIgnoreCase("[plot]") && event.getPlayer().hasPermission("regionsign.create")) {

                Player owner = event.getPlayer();
                String regionId = event.getLine(1);
                ProtectedRegion protectedRegion = getRegionManager(sign.getWorld()).getRegion(regionId);
                String[] lines = event.getLines();

                if (protectedRegion != null) {

                    try {

                        double price = Double.parseDouble(event.getLine(2));
                        RegionSign regionSign = new RegionSign(regionId, owner, sign, price);

                        regionSign.save();
                        regionSign.display(lines, "signs.sellingSign");

                    } catch (NumberFormatException e){

                        RegionSign.displayError(lines, "signs.priceErrorSign");

                    }

                } else RegionSign.displayError(lines, "signs.idErrorSign");

            }
        }
    }
}
