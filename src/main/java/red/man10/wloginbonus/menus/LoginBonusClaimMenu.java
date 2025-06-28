package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import red.man10.wloginbonus.LoginBonusData;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.Main;

import java.util.List;

public class LoginBonusClaimMenu extends SInventory {

    private final Player player;

    public LoginBonusClaimMenu(JavaPlugin plugin, Player player){
        super("§aログインボーナス受け取り", 3, plugin);
        this.player = player;
    }

    @Override
    public void renderMenu(){

        List<String> bonuses = WLoginBonusAPI.getBonusList();

        int slot = 0;
        for(String bonusName : bonuses){
            LoginBonusData data = WLoginBonusAPI.getBonus(bonusName);
            if(data == null) continue;

            int day = data.getConsecutiveDays();
            String rewardText = data.getRewardDescriptionByDay(day);

            boolean canClaim = !WLoginBonusAPI.hasClaimedToday(player.getUniqueId(), bonusName); // 実装が必要

            ItemStack icon = new ItemStack(canClaim ? Material.CHEST : Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = icon.getItemMeta();
            if(meta != null){
                meta.setDisplayName("§e" + bonusName + " §7- §f" + day + "日目");
                meta.setLore(List.of(
                        "§7報酬: §f" + (rewardText == null ? "なし" : rewardText),
                        "",
                        canClaim ? "§aクリックで受け取る" : "§7本日分は受け取り済み"
                ));
                icon.setItemMeta(meta);
            }

            setItem(slot++, new SInventoryItem(icon).setEvent(e -> {
                if(!canClaim){
                    player.sendMessage(Main.prefix + "§7このボーナスは既に受け取り済みです。");
                    return;
                }

                boolean success = WLoginBonusAPI.giveReward(player, bonusName); // 実装が必要
                if(success){
                    player.sendMessage(Main.prefix + "§a「" + bonusName + "」の報酬を受け取りました！");
                } else {
                    player.sendMessage(Main.prefix + "§c報酬の受け取りに失敗しました。");
                }
                player.closeInventory();
            }));
        }

        renderInventory();
    }
}
