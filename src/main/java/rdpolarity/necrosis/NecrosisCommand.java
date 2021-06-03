package rdpolarity.necrosis;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Command;
import rdpolarity.necrosis.dungeon.DungeonBuilder;

@CommandAlias("necrosis|necro|nc")
@Command(
        name = "Necrosis",
        desc = "Core command for necrosis plugin",
        aliases = {"necrosis", "necro", "nc"},
        usage = "/<command>"
)
public class NecrosisCommand extends BaseCommand {
    @Default
    public static void onExecute(Player player) {
        player.sendMessage("This is the necrosis command");
    }

    @Default
    @Subcommand("new")
    public static void onNew(Player player) {
        player.sendMessage("Creating new necrosis game!");
        DungeonBuilder dungeon = new DungeonBuilder();
        dungeon.GeneratePreviewAt(player.getLocation(), 10, 50);
    }

    @Subcommand("new")
    public static void onNew(Player player, int size, int iterations) {
        player.sendMessage("Creating new necrosis game!");
        DungeonBuilder dungeon = new DungeonBuilder();
        dungeon.GeneratePreviewAt(player.getLocation(), size, iterations);
    }

    @Subcommand("debug")
    public static void onDebug(Player player) {
        FireballStick.giveStick(player);
    }
}
