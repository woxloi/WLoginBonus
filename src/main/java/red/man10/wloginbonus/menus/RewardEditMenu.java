package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.LoginBonusData;
import red.man10.wloginbonus.commands.subCommands.EditCommand;

import java.util.ArrayList;
import java.util.List;

public class RewardEditMenu extends SInventory {

    private final String bonusName;
    private final Main plugin;  // プラグインインスタンスを保持

    public RewardEditMenu(JavaPlugin plugin, String bonusName){
        super("§a報酬設定: " + bonusName, 3, plugin);
        this.bonusName = bonusName;
        this.plugin = (Main) plugin;
    }


    @Override
    public void renderMenu(){
        clear();

        LoginBonusData data = WLoginBonusAPI.getBonus(bonusName);
        if(data == null){
            return;
        }

        // 現在設定されている報酬をリストで取得 (5日分)
        List<String> rewards = data.getRewardDescriptionList();
        if(rewards == null) rewards = new ArrayList<>();

        // 左下スロット（18番）に現在の報酬を表示
        List<String> lore = new ArrayList<>();
        if(rewards.isEmpty()){
            lore.add("§7報酬は未設定です");
        } else {
            for(int i = 0; i < rewards.size(); i++){
                lore.add("§e" + (i + 1) + "日目: §f" + rewards.get(i));
            }
        }
        setItem(18, new SInventoryItem(createItem(Material.BOOK, "§6現在の報酬一覧", lore)));

        // 1〜5日目の報酬設定ボタン
        for(int day=1; day<=5; day++){
            int slot = 9 + day;
            int finalDay = day;
            setItem(slot, new SInventoryItem(createItem(Material.DIAMOND, "§b" + day + "日目の報酬設定", "クリックして編集"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();

                        // チャット入力待ち登録
                        plugin.getEditCommand().startRewardInputSession(p, bonusName, finalDay);

                        p.sendMessage(Main.prefix + "§e" + finalDay + "日目の報酬をチャットで入力してください。キャンセルする場合は「cancel」と入力してください。");
                    }));
        }

        // 戻るボタン
        setItem(26, new SInventoryItem(createItem(Material.ARROW, "§7戻る", "編集メニューへ戻る"))
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    plugin.getEditCommand().new LoginBonusEditMenu(plugin, bonusName).open(p);
                }));

        renderInventory();
    }

    private org.bukkit.inventory.ItemStack createItem(Material material, String name, List<String> lore){
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private org.bukkit.inventory.ItemStack createItem(Material material, String name, String... lore){
        return createItem(material, name, java.util.Arrays.asList(lore));
    }
}
