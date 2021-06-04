package rdpolarity.necrosis.dungeon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.MCEditSchematicReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.DataException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Range;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.util.Vector;
import rdpolarity.necrosis.Necrosis;
import rdpolarity.necrosis.math.MathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DungeonBuilder {

    private enum Direction {
        NORTH,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTH,
        SOUTHWEST,
        WEST,
        NORTHWEST
    }

    private final HashMap<Direction, Vector> Directions = new HashMap<Direction, Vector>() {
        {
            put(Direction.NORTH, new Vector(0, 1, 0));
            put(Direction.EAST, new Vector(1, 0, 0));
            put(Direction.SOUTH, new Vector(0, -1, 0));
            put(Direction.WEST, new Vector(-1, 0, 0));
        }
    };

    private final HashMap<Direction, Vector> Diagonals = new HashMap<Direction, Vector>() {
        {
            put(Direction.NORTHEAST, new Vector(1, 1, 0));
            put(Direction.SOUTHEAST, new Vector(1, -1, 0));
            put(Direction.SOUTHWEST, new Vector(-1, -1, 0));
            put(Direction.NORTHWEST, new Vector(-1, 1, 0));
        }
    };

    private final HashMap<Cell.Type, Material> RoomMaterials = new HashMap<Cell.Type, Material>() {
        {
            put(Cell.Type.DEADEND, Material.WHITE_WOOL);
            put(Cell.Type.START, Material.BLUE_WOOL);
            put(Cell.Type.CHEST, Material.GOLD_BLOCK);
            put(Cell.Type.NONE, Material.STONE);
            put(Cell.Type.BOSS, Material.GRAY_WOOL);
        }
    };

    private class Level {
        public Cell[][] data;

        public Level(int size) {
            this.data = new Cell[size][size];
            for (int row = 0; row < data.length; row++) {
                for (int col = 0; col < data[row].length; col++) {
                    data[row][col] = new Wall(); //Whatever value you want to set them to
                }
            }
        }

        public Cell get(Vector vec) {
            return data[(int) vec.getX()][(int) vec.getY()];
        }

        public Cell get(int x, int y) {
            return data[x][y];
        }

        public Cell get(double x, double y) {
            return data[(int) x][(int) y];
        }

        public void set(Vector vec, Cell cell) {
            data[(int) vec.getX()][(int) vec.getY()] = cell;
        }

        public void set(int x, int y, Cell cell) {
            data[x][y] = cell;
        }

        public void set(double x, double y, Cell cell) {
            data[(int) x][(int) y] = cell;
        }

        public ArrayList<Vector> adjacents(Vector vec) {
            ArrayList<Vector> neighbours = new ArrayList<>();
            for (Direction dir : Directions.keySet()) {
                Vector locDir = Directions.get(dir);
                Vector loc = new Vector(locDir.getX() + vec.getX(), locDir.getY() + vec.getY(), 0);
                if (MathHelper.VectorIsInRange(loc, 0, data.length - 1)) {
                    neighbours.add(loc);
                }
            }
            return neighbours;
        }

        public ArrayList<Vector> diagonals(Vector vec) {
            ArrayList<Vector> neighbours = new ArrayList<>();
            for (Direction dir : Diagonals.keySet()) {
                Vector locDir = Diagonals.get(dir);
                Vector loc = new Vector(locDir.getX() + vec.getX(), locDir.getY() + vec.getY(), 0);
                if (MathHelper.VectorIsInRange(loc, 0, data.length - 1)) {
                    neighbours.add(loc);
                }
            }
            return neighbours;
        }
    }

    private Cell[][] GenerateFloor(int size, int iterations, int turtles) {
        Level floor = new Level(size);
        Vector start = new Vector(MathHelper.RandRange(0, size), MathHelper.RandRange(0, size), 0); // Randomize starting position
        floor.set(start, Room.As(Cell.Type.START));

        // Generate Level Using Turtles
        ArrayList<Vector> rooms = new ArrayList<>();
        for (int i = 0; i < turtles; i++) {
            Vector current = start;
            for (int j = 0; j < iterations; j++) {
                ArrayList<Vector> Frontiers = floor.adjacents(current);
                current = Frontiers.get(MathHelper.RandRange(0, Frontiers.size()));
                if (floor.get(current) != null && floor.get(current).type != Cell.Type.START) {
                    rooms.add(current);
                    floor.set(current, new Room());
                }
            }
        }

        // Find All Dead Ends
        ArrayList<Vector> deadends = new ArrayList<>();
        rooms.forEach(room -> {
            int adjacentRooms = (int) floor.adjacents(room).stream().filter(adjacent -> floor.get(adjacent) instanceof Room).count();
            if (adjacentRooms == 1) {
                floor.set(room, Room.As(Cell.Type.DEADEND));
                deadends.add(room);
            }
        });

        if (!deadends.isEmpty()) {
            Vector bossroom = deadends.get(MathHelper.RandRange(0, deadends.size()));
            floor.set(bossroom, Room.As(Cell.Type.BOSS));
            deadends.remove(bossroom);
        }

        if (!deadends.isEmpty()) {
            Vector chestroom = deadends.get(MathHelper.RandRange(0, deadends.size()));
            floor.set(chestroom, Room.As(Cell.Type.CHEST));
            deadends.remove(chestroom);
        }

        return floor.data;
    }

    private Cell[][] GenerateFloorWith(int size, int iterations, int turtles, int rooms) {
        Cell[][] floor;
        while (true) { // Ensure minimum room size
            int roomCount = 0;
            int bossCount = 0;
            floor = GenerateFloor(size, iterations, turtles);
            for (int x = 0; x < floor[0].length; x++) {
                for (int y = 0; y < floor.length; y++) {
                    if (floor[x][y] instanceof Room) roomCount++;
                    if (floor[x][y].type == Cell.Type.BOSS) bossCount++;
                }
            }
            if (roomCount > rooms && bossCount >= 1) {
                break;
            }
        }
        return floor;
    }

    public void GenerateAt(Location loc) {
        Cell[][] floor = GenerateFloorWith(10, 10, 3, 5);
        File roomSchem = new File(Necrosis.getPlugin(Necrosis.class).getDataFolder() + File.separator + "/levels/room.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(roomSchem);
        try (ClipboardReader reader = format.getReader(new FileInputStream(roomSchem));) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().build()) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void GeneratePreviewAt(Location loc, int size, int iterations, int turtles, int rooms) {
        Cell[][] floor = GenerateFloorWith(size, iterations, turtles, rooms);

        // Create Outline
        int extend = 1;
        int max = floor.length + extend;
        int min = -extend;
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
                    newLoc.getBlock().setType(RoomMaterials.get(currentRoom.type));
                } else if (currentRoom instanceof Wall) {
                    Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ());
                    newLoc.getBlock().setType(Material.BLACK_WOOL);
                }
            }
        }
    }
}

