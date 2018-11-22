package fr.paragoumba.regionsigns.events;

import fr.paragoumba.regionsigns.Database;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event){

        Block block = event.getBlock();

        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){

            if (Database.isRegionSign(block.getLocation())) event.setCancelled(true);

        }
    }
}
