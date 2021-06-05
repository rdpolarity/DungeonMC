package rdpolarity.necrosis.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import rdpolarity.necrosis.Necrosis;

import java.util.Arrays;

public class ParticleManager {

    Particle[] particles = new Particle[2];
    Necrosis plugin;

    public ParticleManager(Necrosis plugin, Location loc) {
        this.plugin = plugin;
        for (Particle particle : this.particles) {
            particle = new Particle(loc.toVector(), 200, 45, 1);
        }
    }

    public void Start(Location location) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Particle particle : particles) {
                location.getWorld().spawnParticle(org.bukkit.Particle.FLAME, location, 1, 0, 0, 0, 0, null, false);
            }
        }, 0, 1);
    }
}
