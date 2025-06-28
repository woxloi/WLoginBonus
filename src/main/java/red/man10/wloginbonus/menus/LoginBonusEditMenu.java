package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.menus.RewardEditMenu;

public class LoginBonusEditMenu extends SInventory {

    private final String bonusName;

    public LoginBonusEditMenu(JavaPlugin plugin, String bonusName){
        super("§eログインボーナス編集: " + bonusName, 3, plugin);
        this.bonusName = bonusName;

        // 日数設定ボタン
        setItem(10, new SInventoryItem(createItem(Material.CLOCK, "§b日数設定", "クリックして日数を設定"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    p.sendMessage(Main.prefix + "§e日数をチャットで入力してください（未実装）");
                    // チャット入力待ちは EditCommand 側でやる
                }));

        // 報酬設定ボタン
        setItem(12, new SInventoryItem(createItem(Material.CHEST, "§a報酬設定", "クリックして報酬を編集"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    new RewardEditMenu((Main) plugin, bonusName).open(p);
                }));

        // 削除ボタン
        setItem(14, new SInventoryItem(createItem(Material.BARRIER, "§cログインボーナス削除", "クリックで削除"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    boolean success = WLoginBonusAPI.deleteBonus(bonusName);
                    if(success){
                        p.sendMessage(Main.prefix + "§cログインボーナス「" + bonusName + "」を削除しました");
                    } else {
                        p.sendMessage(Main.prefix + "§c削除に失敗しました");
                    }
                    p.closeInventory();
                }));

        // 戻るボタン（一覧GUIがあれば開く）
        setItem(16, new SInventoryItem(createItem(Material.ARROW, "§7戻る", "一覧に戻る"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    // 必要に応じてここで一覧メニューを開く
                }));
    }

    @Override
    public void renderMenu(){
        renderInventory();
    }

    private org.bukkit.inventory.ItemStack createItem(Material material, String name, String... lore){
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.setDisplayName(name);
            meta.setLore(java.util.Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
