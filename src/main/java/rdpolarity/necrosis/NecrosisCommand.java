package rdpolarity.necrosis;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Command;

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

    @Subcommand("new")
    public static void onNew(Player player) {
        player.sendMessage("Creating new necrosis game!");
    }

    @Subcommand("debug")
    public static void onDebug(Player player) {
        FireballStick.giveStick(player);
    }
}
