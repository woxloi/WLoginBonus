package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.LoginBonusData;

import java.util.ArrayList;
import java.util.List;

public class RewardEditMenu extends SInventory {

    private final Main plugin;
    private final String bonusName;
    private int currentDayIndex = 0;

    public RewardEditMenu(Main plugin, String bonusName) {
        super("§e報酬編集: " + bonusName + " - 日目 " + (1 + 0), 6, plugin);
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
        int day = currentDayIndex + 1;

        List<ItemStack> items = data.getItemRewards(day);
        if (items == null) items = new ArrayList<>();
        int slot = 0;
        // ここでアイテム単体をfinalにしてコピーして使う
        for (ItemStack item : new ArrayList<>(items)) {
            final ItemStack finalItem = item.clone();
            List<ItemStack> finalItems = items;
            setItem(slot++, new SInventoryItem(finalItem).setEvent(e -> {
                Player p = (Player) e.getWhoClicked();
                finalItems.remove(finalItem);
                data.setItemRewardList(day, finalItems);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "§cアイテム報酬を削除しました");
                renderMenu();
            }));
            if (slot >= 27) break;
        }

        List<ItemStack> finalItems1 = items;
        setItem(30, new SInventoryItem(createItem(Material.HOPPER, "§aアイテム追加", "手に持ったアイテムを追加")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                p.sendMessage(Main.prefix + "§c手にアイテムを持ってください");
                return;
            }
            finalItems1.add(hand.clone());
            data.setItemRewardList(day, finalItems1);
            WLoginBonusAPI.updateBonus(bonusName, data);
            p.sendMessage(Main.prefix + "§aアイテムを追加しました");
            renderMenu();
        }));

        List<String> commands = data.getCommandRewards(day);
        if (commands == null) commands = new ArrayList<>();
        int cmdSlot = 27;
        for (String cmd : new ArrayList<>(commands)) {
            final String finalCmd = cmd; // ここをfinalに
            List<String> finalCommands = commands;
            setItem(cmdSlot++, new SInventoryItem(createItem(Material.PAPER, "§eコマンド: " + finalCmd, "クリックで削除")).setEvent(e -> {
                Player p = (Player) e.getWhoClicked();
                finalCommands.remove(finalCmd);
                data.setCommandRewards(day, finalCommands);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "§cコマンドを削除しました");
                renderMenu();
            }));
            if (cmdSlot >= 36) break;
        }

        setItem(31, new SInventoryItem(createItem(Material.WRITABLE_BOOK, "§aコマンド追加", "チャットで入力")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            plugin.getEditCommand().startCommandInputSession(p, bonusName, day);
            p.sendMessage(Main.prefix + "§eコマンドをチャットで入力してください");
        }));

        setItem(48, new SInventoryItem(createItem(Material.ARROW, "§7前の日")).setEvent(e -> {
            if (currentDayIndex > 0) {
                currentDayIndex--;
                setTitle("§e報酬編集: " + bonusName + " - 日目 " + (currentDayIndex + 1));
                renderMenu();
            }
        }));

        setItem(50, new SInventoryItem(createItem(Material.ARROW, "§7次の日")).setEvent(e -> {
            if (currentDayIndex + 1 < maxDay) {
                currentDayIndex++;
                setTitle("§e報酬編集: " + bonusName + " - 日目 " + (currentDayIndex + 1));
                renderMenu();
            }
        }));

        setItem(49, new SInventoryItem(createItem(Material.EMERALD, "§a+1日追加")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            if (maxDay < 10) {
                data.setConsecutiveDays(maxDay + 1);
                WLoginBonusAPI.updateBonus(bonusName, data);
                p.sendMessage(Main.prefix + "§a日数を+1しました");
                renderMenu();
            } else {
                p.sendMessage(Main.prefix + "§c最大10日までです");
            }
        }));

        setItem(53, new SInventoryItem(createItem(Material.BARRIER, "§c戻る")).setEvent(e -> {
            Player p = (Player) e.getWhoClicked();
            p.closeInventory();
            new LoginBonusEditMenu(plugin, bonusName).open(p);
        }));

        renderInventory();
    }


    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
