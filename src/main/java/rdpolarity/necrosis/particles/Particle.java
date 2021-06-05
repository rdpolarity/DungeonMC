package rdpolarity.necrosis.particles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Particle {
    Vector location;
    Vector velocity;
    int lifetime;

    public Particle(Vector location, int lifetime, int angle, int speed) {
        this.lifetime = lifetime;
        double angleInRadians = angle * Math.PI / 180;
        this.location = location;
        this.velocity = new Vector(
                speed * Math.cos(angleInRadians),
                speed * Math.sin(angleInRadians),
                speed * Math.tan(angleInRadians)
        );
    }

    public void Update(int deltatime) {
        lifetime -= deltatime;
        if (lifetime > 0) {
            location.add(velocity.multiply(deltatime));
        }
    }
}
