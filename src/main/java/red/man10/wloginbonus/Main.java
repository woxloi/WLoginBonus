package red.man10.wloginbonus;

import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.VaultAPI;

public final class Main extends JavaPlugin {
    public static VaultAPI vault;
    public static String version = "2025/6/25";
    public static String prefix = "§6[§e§lLoginBonus§6]§f";

    private WLoginBonusAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        vault = new VaultAPI();
        api = new WLoginBonusAPI();

    }

    @Override
    public void onDisable() {
    }

    public WLoginBonusAPI getApi() {
        return api;
    }
}
