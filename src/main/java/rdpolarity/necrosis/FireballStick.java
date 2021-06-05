package rdpolarity.necrosis;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rdpolarity.necrosis.blenderstands.Blenderstand;
import rdpolarity.necrosis.particles.ParticleManager;

import java.util.ArrayList;
import java.util.List;

public class FireballStick implements Listener {

    Necrosis plugin;

    public FireballStick(Necrosis plugin) {
        this.plugin = plugin;
    }

    public static void giveStick(Player player) {
        ItemStack stick = new ItemStack(Material.STICK);
        // Item Meta
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.setDisplayName("Fireball Stick");
        List<String> lore = new ArrayList<>();
        lore.add("A simple fireball stick");
        stickMeta.setLore(lore);
        stick.setItemMeta(stickMeta);
        // NBT Data
        net.minecraft.server.v1_16_R3.ItemStack itemNMS = CraftItemStack.asNMSCopy(stick);
        NBTTagCompound tag = itemNMS.getTag() != null ? itemNMS.getTag() : new NBTTagCompound();
        tag.setString("NecrosisData", Constants.FIREBALL_ITEM);
        itemNMS.setTag(tag);
        stick = CraftItemStack.asCraftMirror(itemNMS);
        player.getInventory().addItem(stick);
    }

    public boolean isStick(ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack itemNMS = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemNMS.getTag() != null ? itemNMS.getTag() : null;
        if (tag != null) {
            return tag.getString("NecrosisData").equals(Constants.FIREBALL_ITEM);
        }
        return false;
    }

    @EventHandler
    private void getRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (isStick(player.getInventory().getItemInMainHand())) {
                fire(player);
            }
        }
    }

    public void fire(Player player) {
        Blenderstand bs = new Blenderstand();
        bs.Place(bs.Read("untitled"), player.getLocation());
        player.sendMessage("FIRED!!!!");
//        ParticleManager pm = new ParticleManager(plugin, player.getLocation());
//        pm.Start(player.getLocation());
    }
}
