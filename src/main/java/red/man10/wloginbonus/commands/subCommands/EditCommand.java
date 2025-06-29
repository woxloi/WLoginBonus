package red.man10.wloginbonus.commands.subCommands;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.WLoginBonusAPI;
import red.man10.wloginbonus.LoginBonusData;
import red.man10.wloginbonus.menus.RewardEditMenu;
import red.man10.wloginbonus.menus.LoginBonusEditMenu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class EditCommand implements CommandExecutor, Listener {

    private final Main plugin;
    private final Map<UUID, InputSession> waitingForInput = new ConcurrentHashMap<>();

    public EditCommand(JavaPlugin plugin){
        this.plugin = (Main) plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void startCommandInputSession(Player player, String bonusName, int day){
        waitingForInput.put(player.getUniqueId(), new InputSession(InputType.SET_COMMAND, bonusName, day));
        player.sendMessage(Main.prefix + "§e§l" + day + "日目のコマンド報酬をチャットで入力してください。複数ある場合は改行（\\n）で区切ってください。キャンセルするには 'cancel' と入力。");
    }
    public void startDaysInputSession(Player player, String bonusName){
        waitingForInput.put(player.getUniqueId(), new InputSession(InputType.SET_DAYS, bonusName));
        player.sendMessage(Main.prefix + "§e§l日数をチャットで入力してください。キャンセルは 'cancel'");
    }

    public void startDisplayNameInputSession(Player player, String bonusName){
        waitingForInput.put(player.getUniqueId(), new InputSession(InputType.SET_DISPLAY_NAME, bonusName));
        player.sendMessage(Main.prefix + "§e§l表示名をチャットで入力してください。キャンセルは 'cancel'");
    }


    private enum InputType {
        SET_DAYS,
        SET_CONSECUTIVE_DAYS,
        SET_COMMAND,
        SET_REWARD,
        SET_DISPLAY_NAME  // 追加
    }

    private static class InputSession {
        InputType type;
        String bonusName;
        int rewardDay = -1;

        InputSession(InputType type, String bonusName){
            this.type = type;
            this.bonusName = bonusName;
        }

        InputSession(InputType type, String bonusName, int rewardDay){
            this.type = type;
            this.bonusName = bonusName;
            this.rewardDay = rewardDay;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(Main.prefix + "このコマンドはプレイヤーのみ実行可能です");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 2){
            player.sendMessage(Main.prefix + "§c§lログインボーナスの名前を指定してください");
            return true;
        }

        String bonusName = args[1];

        if(!WLoginBonusAPI.exists(bonusName)){
            player.sendMessage(Main.prefix + "§c§lログインボーナスの取得に失敗しました");
            return true;
        }

        new LoginBonusEditMenu(plugin, bonusName).open(player);
        return true;
    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!waitingForInput.containsKey(uuid)) return;

        e.setCancelled(true);
        InputSession session = waitingForInput.get(uuid);
        String msg = e.getMessage();

        if(msg.equalsIgnoreCase("cancel")){
            player.sendMessage(Main.prefix + "§c§l入力をキャンセルしました。");
            waitingForInput.remove(uuid);
            return;
        }

        switch(session.type){
            case SET_DAYS:
                try {
                    int days = Integer.parseInt(msg);
                    if(days <= 0){
                        player.sendMessage(Main.prefix + "§c§l1以上の正しい数値を入力してください");
                        return;
                    }
                    if(WLoginBonusAPI.setDays(session.bonusName, days)){
                        player.sendMessage(Main.prefix + "§a§lログインボーナス「" + session.bonusName + "」の日数を " + days + " に設定しました");
                    } else {
                        player.sendMessage(Main.prefix + "§c§l日数の設定に失敗しました");
                    }
                } catch(NumberFormatException ex){
                    player.sendMessage(Main.prefix + "§c§l数字を入力してください");
                }
                waitingForInput.remove(uuid);
                break;
            case SET_COMMAND:
                LoginBonusData commandData = WLoginBonusAPI.getBonus(session.bonusName);
                if (commandData == null) {
                    player.sendMessage(Main.prefix + "§c§lログインボーナスが見つかりません。");
                    waitingForInput.remove(uuid);
                    return;
                }

                // \n で分割（\\n はバックスラッシュがエスケープされるため）
                List<String> commandList = List.of(msg.split("\\\\n"));
                commandData.setCommandRewards(session.rewardDay, new ArrayList<>(commandList));
                WLoginBonusAPI.updateBonus(session.bonusName, commandData);

                player.sendMessage(Main.prefix + "§a§l" + session.rewardDay + "日目のコマンド報酬を設定しました：");
                for (String cmd : commandList) {
                    player.sendMessage(" §f- " + cmd);
                }

                waitingForInput.remove(uuid);
                break;

            case SET_CONSECUTIVE_DAYS:
                try {
                    int cDays = Integer.parseInt(msg);
                    if(cDays <= 0){
                        player.sendMessage(Main.prefix + "§c§l1以上の正しい数値を入力してください");
                        return;
                    }
                    LoginBonusData data = WLoginBonusAPI.getBonus(session.bonusName);
                    if(data != null){
                        data.setConsecutiveDays(cDays);
                        WLoginBonusAPI.updateBonus(session.bonusName, data);
                        player.sendMessage(Main.prefix + "§a§lログインボーナス「" + session.bonusName + "」の連続ログイン日数を " + cDays + " に設定しました");
                    } else {
                        player.sendMessage(Main.prefix + "§c§l設定に失敗しました");
                    }
                } catch(NumberFormatException ex){
                    player.sendMessage(Main.prefix + "§c§l数字を入力してください");
                }
                waitingForInput.remove(uuid);
                break;

            case SET_REWARD:
                LoginBonusData rewardData = WLoginBonusAPI.getBonus(session.bonusName);
                if(rewardData == null){
                    player.sendMessage(Main.prefix + "§c§lログインボーナスが見つかりません。");
                    waitingForInput.remove(uuid);
                    return;
                }
                rewardData.setRewardDescriptionByDay(session.rewardDay, msg);
                WLoginBonusAPI.updateBonus(session.bonusName, rewardData);
                player.sendMessage(Main.prefix + "§a§l" + session.rewardDay + "日目の報酬を「" + msg + "」に設定しました。");
                waitingForInput.remove(uuid);
                break;
        }
    }

    public void startRewardInputSession(Player player, String bonusName, int day){
        waitingForInput.put(player.getUniqueId(), new InputSession(InputType.SET_REWARD, bonusName, day));
    }
    /**
     * 旧LoginBonusEditmenu
     */    public class LoginBonusEditMenukyuu extends SInventory {

        private final String bonusName;

        public LoginBonusEditMenukyuu(JavaPlugin plugin, String bonusName){
            super("§eログインボーナス編集: " + bonusName, 3, plugin);
            this.bonusName = bonusName;

            setItem(10, new SInventoryItem(createItem(Material.CLOCK, "§b日数設定", "クリックして日数を設定"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        waitingForInput.put(p.getUniqueId(), new InputSession(InputType.SET_DAYS, bonusName));
                        p.sendMessage(Main.prefix + "§e§l日数を入力してください");
                    }));

            setItem(11, new SInventoryItem(createItem(Material.PAPER, "§d連続ログイン日数設定", "クリックして連続ログイン日数を設定"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        waitingForInput.put(p.getUniqueId(), new InputSession(InputType.SET_CONSECUTIVE_DAYS, bonusName));
                        p.sendMessage(Main.prefix + "§e§l連続ログイン日数を入力してください");
                    }));

            setItem(12, new SInventoryItem(createItem(Material.CHEST, "§a報酬設定", "クリックして報酬を編集"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        new RewardEditMenu((Main) plugin, bonusName).open(p);
                    }));

            setItem(14, new SInventoryItem(createItem(Material.BARRIER, "§cログインボーナス削除", "クリックで削除"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        boolean success = WLoginBonusAPI.deleteBonus(bonusName);
                        if(success){
                            p.sendMessage(Main.prefix + "§c§lログインボーナス「" + bonusName + "」を削除しました");
                        } else {
                            p.sendMessage(Main.prefix + "§c§l削除に失敗しました");
                        }
                        p.closeInventory();
                    }));

            setItem(16, new SInventoryItem(createItem(Material.ARROW, "§7戻る", "一覧に戻る"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
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
}
