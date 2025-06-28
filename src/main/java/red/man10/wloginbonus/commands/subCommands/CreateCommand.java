package red.man10.wloginbonus.commands.subCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
public class CreateCommand implements CommandExecutor {

    private final Main plugin;

    public CreateCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + "このコマンドはプレイヤーのみ実行可能です");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Main.prefix + "§c§lログインボーナスの名前を指定してください");
            return true;
        }

        String name = args[1]; // ログインボーナスの名前

        // ここに保存処理を追加予定
        // WLoginBonusAPIからとってくる予定
        WLoginBonusAPI.addBonus(name);
        sender.sendMessage(Main.prefix + "§a§lログインボーナス「" + name + "」を作成しました");
        sender.sendMessage(Main.prefix + "§a§l/loginbonus edit 「" + name + "」で編集可能");

        return true;
    }
}
