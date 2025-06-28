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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class EditCommand implements CommandExecutor, Listener {

    private final Main plugin;

    // チャット入力待ちの状態管理
    private final Map<UUID, InputSession> waitingForInput = new ConcurrentHashMap<>();

    public EditCommand(JavaPlugin plugin){
        this.plugin = (Main) plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // 入力セッションタイプ
    private enum InputType {
        SET_DAYS,
        SET_CONSECUTIVE_DAYS,
        SET_REWARD    // ← 追加：報酬設定用
    }

    // 入力セッションデータクラス
    private static class InputSession {
        InputType type;
        String bonusName;
        int rewardDay = -1; // 何日目の報酬か（報酬用のみ）

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
            player.sendMessage(Main.prefix + "§cログインボーナスの名前を指定してください");
            return true;
        }

        String bonusName = args[1];

        if(!WLoginBonusAPI.exists(bonusName)){
            player.sendMessage(Main.prefix + "§cログインボーナス「" + bonusName + "」は存在しません");
            return true;
        }

        // 編集用GUIを開く
        new LoginBonusEditMenu(plugin, bonusName).open(player);

        return true;
    }

    // チャットでの入力待ち処理
    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!waitingForInput.containsKey(uuid)) return;

        e.setCancelled(true);

        InputSession session = waitingForInput.get(uuid);
        String msg = e.getMessage();

        if(msg.equalsIgnoreCase("cancel")){
            player.sendMessage(Main.prefix + "§c入力をキャンセルしました。");
            waitingForInput.remove(uuid);
            return;
        }

        switch(session.type){
            case SET_DAYS:
                int days;
                try {
                    days = Integer.parseInt(msg);
                    if(days <= 0){
                        player.sendMessage(Main.prefix + "§c1以上の正しい数値を入力してください");
                        return;
                    }
                } catch(NumberFormatException ex){
                    player.sendMessage(Main.prefix + "§c数字を入力してください");
                    return;
                }

                boolean success = WLoginBonusAPI.setDays(session.bonusName, days);
                if(success){
                    player.sendMessage(Main.prefix + "§aログインボーナス「" + session.bonusName + "」の日数を " + days + " に設定しました");
                } else {
                    player.sendMessage(Main.prefix + "§c日数の設定に失敗しました");
                }
                waitingForInput.remove(uuid);
                break;

            case SET_CONSECUTIVE_DAYS:
                int cDays;
                try {
                    cDays = Integer.parseInt(msg);
                    if(cDays <= 0){
                        player.sendMessage(Main.prefix + "§c1以上の正しい数値を入力してください");
                        return;
                    }
                } catch(NumberFormatException ex){
                    player.sendMessage(Main.prefix + "§c数字を入力してください");
                    return;
                }

                LoginBonusData data = WLoginBonusAPI.getBonus(session.bonusName);
                if(data != null){
                    data.setConsecutiveDays(cDays);
                    WLoginBonusAPI.updateBonus(session.bonusName, data);
                    player.sendMessage(Main.prefix + "§aログインボーナス「" + session.bonusName + "」の連続ログイン日数を " + cDays + " に設定しました");
                } else {
                    player.sendMessage(Main.prefix + "§c設定に失敗しました");
                }
                waitingForInput.remove(uuid);
                break;

            case SET_REWARD:
                LoginBonusData rewardData = WLoginBonusAPI.getBonus(session.bonusName);
                if(rewardData == null){
                    player.sendMessage(Main.prefix + "§cログインボーナスが見つかりません。");
                    waitingForInput.remove(uuid);
                    return;
                }

                List<String> rewards = rewardData.getRewardDescriptionList();
                if(rewards == null){
                    rewards = new ArrayList<>();
                }

                // 5日分確保
                while(rewards.size() < 5){
                    rewards.add("");
                }

                rewards.set(session.rewardDay - 1, msg);
                rewardData.setRewardDescriptionList(rewards);
                WLoginBonusAPI.updateBonus(session.bonusName, rewardData);

                player.sendMessage(Main.prefix + "§a" + session.rewardDay + "日目の報酬を「" + msg + "」に設定しました。");
                waitingForInput.remove(uuid);
                break;
        }
    }

    // 報酬設定用チャット入力待ちを開始するメソッド
    public void startRewardInputSession(Player player, String bonusName, int day){
        waitingForInput.put(player.getUniqueId(), new InputSession(InputType.SET_REWARD, bonusName, day));
    }

    // ログインボーナス編集GUIクラス
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
                        waitingForInput.put(p.getUniqueId(), new InputSession(InputType.SET_DAYS, bonusName));
                        p.sendMessage(Main.prefix + "§e日数を入力してください");
                    }));

            // 連続ログイン日数設定ボタン（新規追加）
            setItem(11, new SInventoryItem(createItem(Material.PAPER, "§d連続ログイン日数設定", "クリックして連続ログイン日数を設定"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        waitingForInput.put(p.getUniqueId(), new InputSession(InputType.SET_CONSECUTIVE_DAYS, bonusName));
                        p.sendMessage(Main.prefix + "§e連続ログイン日数を入力してください");
                    }));

            // 報酬設定ボタン
            setItem(12, new SInventoryItem(createItem(Material.CHEST, "§a報酬設定", "クリックして報酬を編集"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        // RewardEditMenuへ遷移する際、EditCommandのstartRewardInputSessionメソッドを使うため
                        new RewardEditMenu(plugin, bonusName).open(p);
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

            // 戻るボタン
            setItem(16, new SInventoryItem(createItem(Material.ARROW, "§7戻る", "一覧に戻る"))
                    .setEvent(e -> {
                        Player p = (Player) e.getWhoClicked();
                        p.closeInventory();
                        // ここで一覧GUIを開く処理あれば呼ぶ
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
