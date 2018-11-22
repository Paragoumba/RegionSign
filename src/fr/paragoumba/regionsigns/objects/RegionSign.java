package fr.paragoumba.regionsigns.objects;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.paragoumba.minediversity.ecodiversity.Ecodiversity;
import fr.paragoumba.regionsigns.Database;
import fr.paragoumba.regionsigns.RegionSigns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;

import java.util.List;

import static com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager;
import static fr.paragoumba.minediversity.ecodiversity.Ecodiversity.sendErrorMessage;
import static fr.paragoumba.minediversity.ecodiversity.Ecodiversity.tooPoorError;
import static fr.paragoumba.regionsigns.RegionSigns.plugin;
import static fr.paragoumba.regionsigns.RegionSigns.youBoughtInfo;
import static fr.paragoumba.regionsigns.RegionSigns.youSoldInfo;

public class RegionSign {

    public RegionSign(String regionId, OfflinePlayer owner, Location signLoc, BlockFace direction, double price){

        Block block = signLoc.getWorld().getBlockAt(signLoc);

        block.setType(Material.WALL_SIGN);

        BlockState state = block.getState();

        ((Directional) state.getData()).setFacingDirection(direction);

        if (block.getType() == Material.SIGN_POST && block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, -1, 0)).getType() != Material.AIR || block.getType() == Material.WALL_SIGN && block.getRelative(direction).getType() != Material.AIR) state.update(true);

        this.regionId = regionId;
        this.owner = owner;
        this.sign = (Sign) state;
        this.price = price;

    }

    public RegionSign(String regionId, OfflinePlayer owner, Sign sign, double price){

        this.regionId = regionId;
        this.owner = owner;
        this.sign = sign;
        this.price = price;

    }

    private String regionId;
    private OfflinePlayer owner;
    private Sign sign;
    private double price;

    public void sellTo(Player player){

        ProtectedRegion protectedRegion = getRegionManager(sign.getLocation().getWorld()).getRegion(regionId);

        if (!player.equals(owner)) {

            if (fr.paragoumba.minediversity.ecodiversity.Database.getPlayerFunds(player) >= price && protectedRegion != null) {

                if (!fr.paragoumba.minediversity.ecodiversity.Database.subPlayerFunds(player, price)) {

                    sendErrorMessage(player, tooPoorError);
                    return;

                }

                if (!fr.paragoumba.minediversity.ecodiversity.Database.addPlayerFunds(owner, price)) {

                    fr.paragoumba.minediversity.ecodiversity.Database.addPlayerFunds(player, price);
                    return;

                }

                DefaultDomain owners = protectedRegion.getOwners();

                owners.removeAll();
                owners.addPlayer(player.getUniqueId());

                String ownerName = owner.getName();

                if (owner.isOnline()) {

                    Player owner = Bukkit.getPlayer(this.owner.getUniqueId());
                    ownerName = owner.getDisplayName();

                    owner.sendMessage(youSoldInfo.replace("{regionId}", regionId).replace("{price}", String.valueOf(price)).replace("{moneySymbol}", Ecodiversity.moneySymbol).replace("{playerD}", player.getDisplayName()).replace("{player}", player.getName()));

                }

                player.sendMessage(youBoughtInfo.replace("{regionId}", regionId).replace("{price}", String.valueOf(price)).replace("{moneySymbol}", Ecodiversity.moneySymbol).replace("{ownerD}", ownerName).replace("{owner}", owner.getName()));

                owner = player;

            } else sendErrorMessage(player, tooPoorError);

        }
    }

    public void display(String[] lines, String sign){

        List<String> linesList = plugin.getConfig().getStringList(sign);

        for (int i = 0; i < lines.length; ++i){

            lines[i] = linesList.get(i).replace("{toSell}", RegionSigns.toSellInfo).replace("{error}", RegionSigns.errorError).replace("{regionId}", regionId).replace("{price}", String.valueOf(price)).replace("{moneySymbol}", Ecodiversity.moneySymbol).replace("{owner}", owner.getName());

        }
    }

    public static void displayError(String[] lines, String sign){

        List<String> linesList = plugin.getConfig().getStringList(sign);

        for (int i = 0; i < lines.length; ++i){

            lines[i] = linesList.get(i).replace("{toSell}", RegionSigns.toSellInfo).replace("{error}", RegionSigns.errorError).replace("{moneySymbol}", Ecodiversity.moneySymbol);

        }
    }

    public void save() {

        if (Database.regionSignExists(regionId, sign.getLocation())) Database.updateRegionSign(regionId, owner, sign, price);
        else Database.createRegionSign(regionId, sign, owner, price);

    }

    public Sign getSign() {

        return sign;

    }
}
