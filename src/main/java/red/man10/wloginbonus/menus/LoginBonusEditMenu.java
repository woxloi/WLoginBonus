package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.LoginBonusData;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.commands.subCommands.EditCommand;  // ← EditCommandをimport

import java.util.Arrays;

public class LoginBonusEditMenu extends SInventory {

    private final Main plugin;
    private final String bonusName;
    private EditCommand editCommand = null; // EditCommandのインスタンスを保持

    public LoginBonusEditMenu(JavaPlugin plugin, String bonusName) {
        super("§eログインボーナス編集: " + bonusName, 3, plugin);
        this.bonusName = bonusName;
        this.plugin = (Main) plugin;
        renderMenu();
    }

    @Override
    public void renderMenu() {
        clear();

        LoginBonusData data = WLoginBonusAPI.getBonus(bonusName);
        if (data == null) return;

        int day = 1; // 例として1日目の報酬表示

        setItem(4, new SInventoryItem(createItem(Material.BOOK, "§f現在の設定",
                "§7最大日数: §f" + data.getDays(),
                "§7連続日数: §f" + data.getConsecutiveDays(),
                "§7タイプ: §f" + (data.isStreak() ? "連続型" : "毎日型"),
                "§7コマンド報酬: §f" + (data.hasCommandRewards(day) ? "あり" : "なし"),
                "§7アイテム報酬: §f" + (data.hasItemRewards(day) ? "あり" : "なし")
        )));

        // 日数設定（チャット入力開始）
        setItem(10, new SInventoryItem(createItem(Material.CLOCK, "§b日数設定", "§7クリックして日数を設定"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    editCommand.startDaysInputSession(p, bonusName);
                    p.sendMessage(Main.prefix + "§e日数をチャットで入力してください。キャンセルは 'cancel'");
                }));

        // タイプ切替
        setItem(11, new SInventoryItem(createItem(Material.LEVER, "§6タイプ切替",
                "§7現在: §f" + (data.isStreak() ? "連続型" : "毎日型"),
                "§eクリックで切り替え"))
                .setEvent(e -> {
                    data.setStreak(!data.isStreak());
                    WLoginBonusAPI.updateBonus(bonusName, data);
                    renderMenu();
                }));

        // 表示名編集（チャット入力開始）
        setItem(13, new SInventoryItem(createItem(Material.NAME_TAG, "§b表示名編集", "§7クリックしてチャットで入力"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    editCommand.startDisplayNameInputSession(p, bonusName);
                    p.sendMessage(Main.prefix + "§e表示名をチャットで入力してください。キャンセルは 'cancel'");
                }));

        // 報酬編集
        setItem(12, new SInventoryItem(createItem(Material.CHEST, "§a報酬編集", "§7クリックして報酬を編集"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    new RewardEditMenu((Main) plugin, bonusName).open(p);
                }));

        // 保存して閉じる
        setItem(15, new SInventoryItem(createItem(Material.EMERALD_BLOCK, "§a保存して閉じる"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.sendMessage(Main.prefix + "§a設定を保存しました");
                    p.closeInventory();
                }));

        // 削除
        setItem(14, new SInventoryItem(createItem(Material.BARRIER, "§cログインボーナス削除", "§7クリックで削除"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    boolean success = WLoginBonusAPI.deleteBonus(bonusName);
                    if (success) {
                        p.sendMessage(Main.prefix + "§cログインボーナス「" + bonusName + "」を削除しました");
                    } else {
                        p.sendMessage(Main.prefix + "§c削除に失敗しました");
                    }
                    p.closeInventory();
                }));

        // 戻る
        setItem(16, new SInventoryItem(createItem(Material.ARROW, "§7戻る", "§7一覧に戻る"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                }));

        renderInventory();
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
