package red.man10.wloginbonus.menus;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.LoginBonusData;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LoginBonusClaimMenu extends SInventory {

    private final Main plugin;
    private final Player player;

    private List<String> bonuses;
    private int currentBonusIndex = 0;
    private int currentDayIndex = 0;

    public LoginBonusClaimMenu(Main plugin, Player player) {
        super("§aログインボーナス", 6, plugin);
        this.plugin = plugin;
        this.player = player;
        this.bonuses = WLoginBonusAPI.getBonusList();
        renderMenu();
    }

    @Override
    public void renderMenu() {
        clear();
        decorateBackground();

        if (bonuses.isEmpty()) {
            setItem(22, createCenterItem(Material.BARRIER, "§cログインボーナスがありません"));
            setTitle("§cログインボーナスなし");
            renderInventory();
            return;
        }

        String bonusName = bonuses.get(currentBonusIndex);
        LoginBonusData data = WLoginBonusAPI.getBonus(bonusName);
        if (data == null) return;

        int maxDay = data.getConsecutiveDays();
        int dayToShow = Math.min(currentDayIndex + 1, maxDay);

        boolean canClaim = !WLoginBonusAPI.hasClaimedToday(player.getUniqueId(), bonusName);
        String rewardText = data.getRewardDescriptionByDay(dayToShow);
        if (rewardText == null) rewardText = "???";

        Material mainMaterial = canClaim ? Material.CHEST : Material.GRAY_STAINED_GLASS_PANE;

        SItemStack mainItemStack = new SItemStack(mainMaterial)
                .setDisplayName("§e" + bonusName + " §7- §f" + dayToShow + "日目")
                .addLore("§7報酬: §f" + rewardText)
                .addLore("")
                .addLore(canClaim ? "§aクリックで受け取る" : "§7本日分は受け取り済み");
        SInventoryItem mainItem = new SInventoryItem(mainItemStack.build())
                .setEvent(e -> {
                    if (!canClaim) {
                        player.sendMessage(Main.prefix + "§7このボーナスは既に受け取り済みです。");
                        return;
                    }
                    if (WLoginBonusAPI.giveReward(player, bonusName)) {
                        player.sendMessage(Main.prefix + "§a報酬を受け取りました！");
                    } else {
                        player.sendMessage(Main.prefix + "§c報酬の受け取りに失敗しました。");
                    }
                    player.closeInventory();
                });

        setItem(22, mainItem);

        // タイトルに残り時間を表示
        String remain = getTimeUntilNextClaim(player.getUniqueId(), bonusName);
        setTitle("§aログボ - 次まで " + remain);

        // ページ切替
        setItem(20, createControl(Material.ARROW, "§7前の日", () -> {
            if (currentDayIndex > 0) {
                currentDayIndex--;
                renderMenu();
            }
        }));

        setItem(24, createControl(Material.ARROW, "§7次の日", () -> {
            if (currentDayIndex + 1 < maxDay) {
                currentDayIndex++;
                renderMenu();
            }
        }));

        setItem(30, createControl(Material.PAPER, "§7前のボーナス", () -> {
            if (currentBonusIndex > 0) {
                currentBonusIndex--;
                currentDayIndex = 0;
                renderMenu();
            }
        }));

        setItem(32, createControl(Material.PAPER, "§7次のボーナス", () -> {
            if (currentBonusIndex + 1 < bonuses.size()) {
                currentBonusIndex++;
                currentDayIndex = 0;
                renderMenu();
            }
        }));

        renderInventory();
    }

    private void decorateBackground() {
        int size = 6 * 9;
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                setItem(i, createFrameItem());
            } else {
                setItem(i, createBackgroundItem());
            }
        }
    }

    private SInventoryItem createBackgroundItem() {
        return new SInventoryItem(new SItemStack(Material.BLACK_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .build())
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                });
    }

    private SInventoryItem createFrameItem() {
        return new SInventoryItem(new SItemStack(Material.OBSIDIAN)
                .setDisplayName(" ")
                .build())
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
        });
    }

    private SInventoryItem createCenterItem(Material material, String name) {
        return new SInventoryItem(new SItemStack(material)
                .setDisplayName(name)
                .build())
                .setEvent(e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                });
   }

    private SInventoryItem createControl(Material material, String name, Runnable action) {
        return new SInventoryItem(new SItemStack(material)
                .setDisplayName(name)
                .build())
                .setEvent(e -> action.run());
    }

    private String getTimeUntilNextClaim(UUID uuid, String bonusName) {
        LocalDate last = WLoginBonusAPI.getLastClaimDate(uuid.toString() + ":" + bonusName);
        if (last == null) return "未受取";

        LocalDateTime nextMidnight = LocalDate.now().plusDays(1).atStartOfDay();
        Duration d = Duration.between(LocalDateTime.now(), nextMidnight);

        return String.format("%02d:%02d:%02d", d.toHours(), d.toMinutes() % 60, d.getSeconds() % 60);
    }
}
