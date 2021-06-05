package rdpolarity.necrosis.blenderstands;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import rdpolarity.necrosis.Necrosis;

import java.io.*;

public class Blenderstand {

    public ArmourstandObject[] Read(String name) {
        String path = Necrosis.getPlugin(Necrosis.class).getDataFolder() + File.separator + "/armourstands/" + name + ".json";
        File file = new File(path);
        try {
            Gson g = new Gson();
            ArmourstandObject[] ao = g.fromJson(new FileReader(path), ArmourstandObject[].class);
            return ao;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Place(ArmourstandObject[] armourstands, Location loc) {
        for (ArmourstandObject armourstand : armourstands) {
            Location newLoc = armourstand.location.add(loc.toVector()).toLocation(loc.getWorld());
            double asHeight = 1.375;
            double blockDimensions = 0.625;
            newLoc.setY(newLoc.getY() - asHeight);
            ArmorStand as = newLoc.getWorld().spawn(newLoc, ArmorStand.class);
            as.setGravity(false);
            as.setCanPickupItems(false);
            as.setVisible(false);
//            as.setCustomName(armourstand.name);
//            as.setCustomNameVisible(true);
            as.getEquipment().setHelmet(new ItemStack(Material.STONE));
        }
    }
}
