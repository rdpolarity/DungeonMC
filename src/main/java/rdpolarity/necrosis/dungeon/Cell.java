package rdpolarity.necrosis.dungeon;

public class Cell {

    public enum Type {
        NONE,
        START,
        DEADEND,
        CHEST,
        BOSS
    }

    public enum Size {
        Large,
        Regular,
        Curved
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    Type type = Type.NONE;
    Size size = Size.Regular;
    Difficulty difficulty = Difficulty.EASY;
    boolean visited = false;
}
