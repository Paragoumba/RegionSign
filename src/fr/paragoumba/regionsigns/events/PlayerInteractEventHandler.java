package fr.paragoumba.regionsigns.events;

import fr.paragoumba.regionsigns.Database;
import fr.paragoumba.regionsigns.objects.RegionSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractEventHandler implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){

            Block block = event.getClickedBlock();

            if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){

                Sign sign = (Sign) block.getState();
                RegionSign regionSign = Database.getRegionSign(sign.getLocation());

                if (regionSign != null) regionSign.sellTo(event.getPlayer());

            }
        }
    }
}
