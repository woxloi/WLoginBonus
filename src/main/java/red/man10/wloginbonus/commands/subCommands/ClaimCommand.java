package red.man10.wloginbonus.commands.subCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.menus.LoginBonusClaimMenu;

public class ClaimCommand implements CommandExecutor {

    private final Main plugin;

    public ClaimCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage("§cプレイヤー専用コマンドです。");
            return true;
        }

        Player player = (Player) sender;

        // GUIを開く
        new LoginBonusClaimMenu(plugin, player).open(player);
        return true;
    }
}
