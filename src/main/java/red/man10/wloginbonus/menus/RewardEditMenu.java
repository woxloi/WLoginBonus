package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.LoginBonusData;
import red.man10.wloginbonus.commands.subCommands.EditCommand;

import java.util.List;

public class RewardEditMenu extends SInventory {

    private final Main plugin;
    private final String bonusName;
    private int currentDayIndex = 0;

    public RewardEditMenu(Main plugin, String bonusName) {
        super("\u00a7e\u5831\u916c\u7de8\u96c6: " + bonusName + " - \u65e5\u76ee " + (1 + 0), 6, plugin);
        this.plugin = plugin;
        this.bonusName = bonusName;
        renderMenu();
    }

    @Override
    public void renderMenu() {
        clear();
        LoginBonusData data = WLoginBonusAPI.getBonus(bonusName);
        if (data == null) return;

        int maxDay = data.getConsecutiveDays();

        // アイテム報酬
        int slot = 0;
        for (ItemStack item : data.getItemRewards(currentDayIndex + 1)) {
            setItem(slot++, new SInventoryItem(item.clone()).setEvent(e -> {
                Player p = (Player) e.getWhoClicked();
                data.getItemRewards(currentDayIndex + 1).remove(item);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "\u00a7c\u30a2\u30a4\u30c6\u30e0\u5831\u916c\u3092\u524a\u9664\u3057\u307e\u3057\u305f");
                renderMenu();
            }));
            if (slot >= 27) break;
        }

        // アイテム追加
        setItem(30, new SInventoryItem(createItem(Material.HOPPER, "\u00a7a\u30a2\u30a4\u30c6\u30e0\u8ffd\u52a0", "\u624b\u306b\u6301\u3063\u305f\u30a2\u30a4\u30c6\u30e0\u3092\u8ffd\u52a0")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                p.sendMessage(Main.prefix + "\u00a7c\u624b\u306b\u30a2\u30a4\u30c6\u30e0\u3092\u6301\u3063\u3066\u304f\u3060\u3055\u3044");
                return;
            }
            data.addItemReward(currentDayIndex + 1, hand.clone());
            WLoginBonusAPI.updateBonus(bonusName, data);
            p.sendMessage(Main.prefix + "\u00a7a\u30a2\u30a4\u30c6\u30e0\u3092\u8ffd\u52a0\u3057\u307e\u3057\u305f");
            renderMenu();
        }));

        // コマンド報酬
        int cmdSlot = 27;
        for (String cmd : data.getCommandRewards(currentDayIndex + 1)) {
            setItem(cmdSlot++, new SInventoryItem(createItem(Material.PAPER, "\u00a7e\u30b3\u30de\u30f3\u30c9: " + cmd, "\u30af\u30ea\u30c3\u30af\u3067\u524a\u9664")).setEvent(e -> {
                Player p = (Player) e.getWhoClicked();
                data.getCommandRewards(currentDayIndex + 1).remove(cmd);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "\u00a7c\u30b3\u30de\u30f3\u30c9\u3092\u524a\u9664\u3057\u307e\u3057\u305f");
                renderMenu();
            }));
            if (cmdSlot >= 36) break;
        }

        // コマンド追加
        setItem(31, new SInventoryItem(createItem(Material.WRITABLE_BOOK, "\u00a7a\u30b3\u30de\u30f3\u30c9\u8ffd\u52a0", "\u30c1\u30e3\u30c3\u30c8\u3067\u5165\u529b")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            plugin.getEditCommand().startCommandInputSession(p, bonusName, currentDayIndex + 1);
            p.sendMessage(Main.prefix + "\u00a7e\u30b3\u30de\u30f3\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044");
        }));

        // ページング
        setItem(48, new SInventoryItem(createItem(Material.ARROW, "\u00a77\u524d\u306e\u65e5")).setEvent(e -> {
            if (currentDayIndex > 0) {
                currentDayIndex--;
                setTitle("\u00a7e\u5831\u916c\u7de8\u96c6: " + bonusName + " - \u65e5\u76ee " + (currentDayIndex + 1));
                renderMenu();
            }
        }));

        setItem(50, new SInventoryItem(createItem(Material.ARROW, "\u00a77\u6b21\u306e\u65e5")).setEvent(e -> {
            if (currentDayIndex + 1 < maxDay) {
                currentDayIndex++;
                setTitle("\u00a7e\u5831\u916c\u7de8\u96c6: " + bonusName + " - \u65e5\u76ee " + (currentDayIndex + 1));
                renderMenu();
            }
        }));

        // 日数追加
        setItem(49, new SInventoryItem(createItem(Material.EMERALD, "\u00a7a+1\u65e5\u8ffd\u52a0")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            if (data.getConsecutiveDays() < 10) {
                data.setConsecutiveDays(data.getConsecutiveDays() + 1);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "\u00a7a\u65e5\u6570\u3092+1\u3057\u307e\u3057\u305f");
                renderMenu();
            } else {
                p.sendMessage(Main.prefix + "\u00a7c\u6700\u592710\u65e5\u307e\u3067\u3067\u3059");
            }
        }));

        // 戻る
        setItem(53, new SInventoryItem(createItem(Material.BARRIER, "\u00a7c\u623b\u308b")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            plugin.getEditCommand().new LoginBonusEditMenu(plugin, bonusName).open(p);
        }));

        renderInventory();
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(java.util.Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
