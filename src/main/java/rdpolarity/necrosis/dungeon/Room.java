package rdpolarity.necrosis.dungeon;

public class Room extends Cell {
    public static Room As(Type newType) {
        return new Room() {{ type = newType; }};
    }
}



