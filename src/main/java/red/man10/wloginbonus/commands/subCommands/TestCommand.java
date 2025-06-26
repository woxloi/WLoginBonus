package red.man10.wloginbonus.commands.subCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.man10.wloginbonus.Main;

public class TestCommand implements CommandExecutor {

    private final Main plugin;

    public TestCommand(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + "このコマンドはプレイヤーのみ実行可能です");
            return true;
        }
        Player player = (Player) sender;

        // コマンドが許可されている場合の処理を書く
        Bukkit.broadcastMessage(Main.prefix + "§a§l" + player.getName() + "がテストコマンドを打ちました");
        sender.sendMessage(Main.prefix + "テストコマンドの実行に成功しました");

        return true;
    }
}
