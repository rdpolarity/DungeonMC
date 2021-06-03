package rdpolarity.necrosis.dungeon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Range;
import org.bukkit.util.Vector;
import rdpolarity.necrosis.math.MathHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DungeonBuilder {

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private final HashMap<Direction, Vector> Directions = new HashMap<Direction, Vector>() {
        {
            put(Direction.UP, new Vector(0, 1, 0));
            put(Direction.DOWN, new Vector(0, -1, 0));
            put(Direction.LEFT, new Vector(-1, 0, 0));
            put(Direction.RIGHT, new Vector(1, 0, 0));
        }
    };

    private Cell[][] GenerateFloor(int size, int iterations) {
        Cell[][] floor = new Cell[size][size];
        Vector current = new Vector(MathHelper.RandRange(0, size), MathHelper.RandRange(0, size), 0); // Randomize starting position
        for (int i = 0; i < iterations; i++) {
            ArrayList<Vector> paths =  new ArrayList<>(); // Temp store of current path options
            floor[(int)current.getX()][(int)current.getY()] = new Room();
            for (Direction dir : Directions.keySet()) { // Find all available spaces, make them walls then add them to temp paths list
                Vector locDir = Directions.get(dir);
                Vector loc = new Vector(locDir.getX() + current.getX(), locDir.getY() + current.getY(), 0);
                if (MathHelper.VectorIsInRange(loc, 0, size - 1)) {
                    Cell currentCell = floor[(int)loc.getX()][(int)loc.getY()];
                    if (currentCell == null) {
                        floor[(int)loc.getX()][(int)loc.getY()] = new Wall();
                        paths.add(loc);
                    }
                }
            }
            if (!paths.isEmpty()) {
                Vector next = paths.get(MathHelper.RandRange(0, paths.size()));
                current = next; // Move to the next room square
            }
        }
        return floor;
    }

    public void GeneratePreviewAt(Location loc, int size, int iterations) {
        Cell[][] floor = GenerateFloor(size, iterations);

        // Create Outline
        int extend = 1;
        int max = floor.length + extend;
        int min = 0 - extend;
        for (int x = min; x < max; x++) {
            for (int y = min; y < max; y++) {
                if ((Range.between(min, max).contains(x) && (y == min || y == max - 1)) || (Range.between(min, max).contains(y) && (x == min || x == max - 1))) {
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ());
                    newLoc.getBlock().setType(Material.RED_WOOL);
                }
            }
        }

        // Create Level
        for (int x = 0; x < floor[0].length; x++) {
            for (int y = 0; y < floor.length; y++) {
                Cell currentRoom = floor[x][y];
                if (currentRoom instanceof Room) {
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ());
                    newLoc.getBlock().setType(Material.STONE);
                } else if (currentRoom instanceof Wall) {
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ());
                    newLoc.getBlock().setType(Material.BLACK_WOOL);
                }
            }
        }
    }
}

