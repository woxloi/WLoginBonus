package red.man10.wloginbonus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class RewardEditMenuClickBlocker implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        String title = e.getView().getTitle();

        // 報酬編集メニューならキャンセル（タイトルで判定）
        if (title.startsWith("§e報酬編集: ")) {
            e.setCancelled(true);
        }
    }
}
