package red.man10.wloginbonus.commands.subCommands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;

public class EditCommand implements CommandExecutor, Listener {

    private final Main plugin;

    public EditCommand(JavaPlugin plugin) {
        this.plugin = (Main) plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.prefix + "このコマンドはプレイヤーのみ実行可能です");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(Main.prefix + "§cログインボーナスの名前を指定してください");
            return true;
        }

        String Name = args[1];

        // ここでapiからログインボーナス情報を取得したり編集メニューを開いたりする処理を書くイメージ
            player.sendMessage(Main.prefix + "§cログインボーナス「" + Name + "」は存在しません");
            return true;
        }

    }
