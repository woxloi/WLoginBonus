package red.man10.wloginbonus.commands.subCommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import red.man10.wloginbonus.Main;

public class WithdrawCommand implements CommandExecutor {

    private final Main plugin;

    public WithdrawCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage(Main.prefix + "§c§lコマンドが不正です");
            return true;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (target == null || target.getName() == null) {
            sender.sendMessage(Main.prefix + "§c§lプレイヤーが見つかりませんでした");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Main.prefix + "§c§l金額が正しくありません");
            return true;
        }

        // VaultAPI 経由で引き出し
        boolean result = Main.vault.withdraw(target.getUniqueId(), amount);
        if (result) {
            sender.sendMessage(Main.prefix + "§a§l" + target.getName() + " から " + amount + " 円を引き出しました");
        } else {
            sender.sendMessage(Main.prefix + "§c§l引き出しに失敗しました");
        }

        return true;
    }
}
