package rdpolarity.necrosis.math;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Range;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;

public class MathHelper {
    public static int RandRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static boolean VectorIsInRange(Vector vec, int min, int max) {
        return Range.between(min, max).contains((int) vec.getX()) && Range.between(min, max).contains((int) vec.getY());
    }
}
