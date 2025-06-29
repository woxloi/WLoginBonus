package red.man10.wloginbonus;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LoginBonusData {

    private final String name;
    private int consecutiveDays;
    private int totalDays;
    private String rewardDescription;
    private boolean isStreak = false; // デフォルトは通常ボーナス


    // 日数ごとの報酬説明リスト
    private List<String> rewardDescriptionList;

    // 日数ごとのコマンド報酬リスト（1日ごとに複数コマンド想定）
    private List<List<String>> commandRewardList;
    private int days;  // ログインボーナスの最大日数（例：10日）

    // 日数ごとのアイテム報酬リスト（1日ごとに複数アイテム想定）
    private List<List<ItemStack>> itemRewardList;
    private final Map<Integer, List<String>> commandRewardMap = new HashMap<>();


    public LoginBonusData(String name) {
        this.name = name;
        this.consecutiveDays = 0;
        this.totalDays = 0;
        this.rewardDescription = "未設定";
        this.rewardDescriptionList = new ArrayList<>();

        this.commandRewardList = new ArrayList<>();
        this.itemRewardList = new ArrayList<>();
    }
    // ========== 基本情報 ==========

    public String getName() {
        return name;
    }

    public int getConsecutiveDays() {
        return consecutiveDays;
    }

    public void setConsecutiveDays(int consecutiveDays) {
        this.consecutiveDays = consecutiveDays;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.consecutiveDays = days;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public List<String> getRewardDescriptionList() {
        return rewardDescriptionList;
    }

    public void setRewardDescriptionList(List<String> rewardDescriptionList) {
        this.rewardDescriptionList = rewardDescriptionList;
    }

    // --- ここから追加部分 ---

    /**
     * 指定日数のコマンド報酬リストを取得
     * @param day 1日目が1
     * @return コマンドリスト。設定なしなら空リスト
     */
    public List<String> getCommandRewardList(int day) {
        if(day <= 0) return new ArrayList<>();
        if(day > commandRewardList.size()) return new ArrayList<>();
        List<String> commands = commandRewardList.get(day -1);
        return commands != null ? commands : new ArrayList<>();
    }

    /**
     * 指定日数のコマンド報酬リストを設定
     * @param day 1日目が1
     * @param commands コマンドリスト
     */
    public void setCommandRewardList(int day, List<String> commands) {
        if(day <= 0) return;
        // 必要に応じてリスト拡張
        while(commandRewardList.size() < day){
            commandRewardList.add(new ArrayList<>());
        }
        commandRewardList.set(day - 1, commands);
    }

    /**
     * 指定日数のアイテム報酬リストを取得
     * @param day 1日目が1
     * @return アイテムリスト。設定なしなら空リスト
     */
    public List<ItemStack> getItemRewardList(int day) {
        if(day <= 0) return new ArrayList<>();
        if(day > itemRewardList.size()) return new ArrayList<>();
        List<ItemStack> items = itemRewardList.get(day -1);
        return items != null ? items : new ArrayList<>();
    }

    /**
     * 指定日数のアイテム報酬リストを設定
     * @param day 1日目が1
     * @param items アイテムリスト
     */
    public void setItemRewardList(int day, List<ItemStack> items) {
        if(day <= 0) return;
        while(itemRewardList.size() < day){
            itemRewardList.add(new ArrayList<>());
        }
        itemRewardList.set(day - 1, items);
    }

    // --- コマンド報酬追加用の便利メソッド ---
    public void addCommandReward(int day, String command){
        if(day <= 0) return;
        while(commandRewardList.size() < day){
            commandRewardList.add(new ArrayList<>());
        }
        commandRewardList.get(day -1).add(command);
    }

    // --- アイテム報酬追加用の便利メソッド ---
    public void addItemReward(int day, ItemStack item){
        if(day <= 0) return;
        while(itemRewardList.size() < day){
            itemRewardList.add(new ArrayList<>());
        }
        itemRewardList.get(day -1).add(item);
    }

    /**
     * 指定した日数の報酬説明を取得（1日目はindex0）
     */
    public String getRewardDescriptionByDay(int day) {
        List<String> list = getRewardDescriptionList();
        if(day <= 0 || day > list.size()) return null;
        return list.get(day - 1);
    }

    /**
     * 指定した日数の報酬説明を設定（1日目はindex0）
     */
    public void setRewardDescriptionByDay(int day, String description) {
        if(day <= 0) return;
        if(rewardDescriptionList == null){
            rewardDescriptionList = new ArrayList<>();
        }
        while(rewardDescriptionList.size() < day){
            rewardDescriptionList.add("");
        }
        rewardDescriptionList.set(day - 1, description);
    }

    public List<ItemStack> getItemRewards(int day) {
        return getItemRewardList(day);
    }

    public List<String> getCommandRewards(int day) {
        return getCommandRewardList(day);
    }
    public void setCommandRewards(int day, List<String> commands) {
        commandRewardMap.put(day, commands);
    }

    public boolean isStreak() {
        return isStreak;
    }

    public void setStreak(boolean streak) {
        this.isStreak = streak;
    }

    public boolean hasCommandRewards(int day) {
        List<String> list = getCommandRewardList(day);
        return list != null && !list.isEmpty();
    }

    public boolean hasItemRewards(int day) {
        List<ItemStack> list = getItemRewardList(day);
        return list != null && !list.isEmpty();
    }

}
