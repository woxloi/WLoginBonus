package red.man10.wloginbonus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WLoginBonusAPI {

    private static final Map<String, LoginBonusData> bonuses = new ConcurrentHashMap<>();

    // プレイヤーUUID + ボーナス名 → 最後に受け取った日付を保存（仮にメモリ管理）
    private static final Map<String, LocalDate> claimedDates = new ConcurrentHashMap<>();

    public static boolean addBonus(String name){
        if(bonuses.containsKey(name)) return false;
        bonuses.put(name, new LoginBonusData(name));
        return true;
    }

    public static boolean exists(String name){
        return bonuses.containsKey(name);
    }

    public static LoginBonusData getBonus(String name){
        return bonuses.get(name);
    }

    public static boolean updateBonus(String name, LoginBonusData data){
        if(!bonuses.containsKey(name)) return false;
        bonuses.put(name, data);
        return true;
    }

    public static boolean deleteBonus(String name){
        return bonuses.remove(name) != null;
    }

    public static boolean setDays(String name, int days){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return false;
        data.setDays(days);
        return true;
    }

    public static boolean setCommandReward(String name, int day, String command){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return false;

        List<String> list = data.getCommandRewardList(day);
        if(list == null) list = new ArrayList<>();

        list.add(command);
        data.setCommandRewardList(day, list);
        return true;
    }

    public static List<String> getCommandReward(String name, int day){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return null;

        return data.getCommandRewardList(day);
    }

    public static boolean setItemReward(String name, int day, List<ItemStack> items){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return false;

        data.setItemRewardList(day, items);
        return true;
    }

    public static List<ItemStack> getItemReward(String name, int day){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return null;

        return data.getItemRewardList(day);
    }

    public static List<String> getAllBonusNames(){
        return new ArrayList<>(bonuses.keySet());
    }

    // 追加1: getBonusList()（getAllBonusNames()のエイリアス）
    public static List<String> getBonusList(){
        return getAllBonusNames();
    }

    public static boolean setTotalDays(String name, int days){
        LoginBonusData data = bonuses.get(name);
        if(data == null) return false;
        data.setTotalDays(days);
        return true;
    }

    // 追加2: 指定ボーナスの指定日目の報酬説明を取得
    public static String getRewardDescriptionByDay(String bonusName, int day){
        LoginBonusData data = bonuses.get(bonusName);
        if(data == null) return null;
        return data.getRewardDescriptionByDay(day);
    }

    // 追加3: 当日受け取り済みか判定（UUID + bonusNameで管理）
    public static boolean hasClaimedToday(UUID playerUUID, String bonusName){
        String key = playerUUID.toString() + ":" + bonusName;
        LocalDate lastClaimDate = claimedDates.get(key);
        LocalDate today = LocalDate.now();
        return today.equals(lastClaimDate);
    }

    // 追加4: 報酬をプレイヤーに付与し、受取記録を更新
    public static boolean giveReward(Player player, String bonusName){
        LoginBonusData data = bonuses.get(bonusName);
        if(data == null) return false;

        int day = data.getConsecutiveDays();
        if(day <= 0) return false;

        // アイテム報酬付与
        List<ItemStack> items = data.getItemRewardList(day);
        if(items != null){
            for(ItemStack item : items){
                player.getInventory().addItem(item);
            }
        }

        // コマンド報酬実行（コンソールとして）
        List<String> commands = data.getCommandRewardList(day);
        if(commands != null){
            for(String cmd : commands){
                String replacedCmd = cmd.replace("<player>", player.getName());
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacedCmd);
            }
        }

        // 受取記録更新
        String key = player.getUniqueId().toString() + ":" + bonusName;
        claimedDates.put(key, LocalDate.now());

        return true;
    }
}
