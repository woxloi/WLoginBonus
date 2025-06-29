package red.man10.wloginbonus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.VaultAPI;
import red.man10.wloginbonus.commands.WLoginBonusCommand;
import red.man10.wloginbonus.commands.subCommands.EditCommand;  // import追加
import red.man10.wloginbonus.listeners.RewardEditMenuClickBlocker;

public final class Main extends JavaPlugin {
    public static VaultAPI vault;
    public static String version = "2025/6/25";
    public static String prefix = "§6[§e§lLoginBonus§6]§f";

    private WLoginBonusAPI api;
    private EditCommand editCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        vault = new VaultAPI();
        api = new WLoginBonusAPI();
        this.editCommand = new EditCommand(this);
        getServer().getPluginManager().registerEvents(new RewardEditMenuClickBlocker(), this);
        // WLoginBonusCommand初期化（もしEditCommandもこの中でやっていたら調整が必要）
        new WLoginBonusCommand(this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.sendMessage(Main.prefix + p.getName() + "さんログインボーナスは受け取りましたか?");
    }

    public WLoginBonusAPI getApi() {
        return api;
    }

    // これを追加
    public EditCommand getEditCommand() {
        return editCommand;
    }
}
