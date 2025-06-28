package red.man10.wloginbonus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginBonusData {

    private final String name;
    private int consecutiveDays;         // 連続ログイン日数
    private int totalDays;               // 累計ログイン日数（未使用なら削除可）
    private String rewardDescription;    // 単一の報酬説明（旧仕様向け）
    private List<String> rewardDescriptionList; // 日数ごとの報酬説明リスト

    public LoginBonusData(String name) {
        this.name = name;
        this.consecutiveDays = 0;
        this.totalDays = 0;
        this.rewardDescription = "未設定";
        this.rewardDescriptionList = new ArrayList<>();
    }

    // ========== Getter & Setter ==========

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

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    // ===== 日数ごとの報酬管理 =====

    /**
     * 日数ごとの報酬説明リストを取得
     * @return 報酬説明のリスト
     */
    public List<String> getRewardDescriptionList(){
        if(rewardDescription == null || rewardDescription.isEmpty()){
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(rewardDescription.split("\n")));
    }


    /**
     * 日数ごとの報酬説明リストをセット
     * @param rewardDescriptionList 報酬説明のリスト
     */
    public void setRewardDescriptionList(List<String> rewardDescriptionList) {
        this.rewardDescriptionList = rewardDescriptionList;
    }

    /**
     * 指定した日数の報酬説明を取得（1日目はindex0）
     * @param day 日数（1〜）
     * @return 報酬説明
     */
    public String getRewardDescriptionByDay(int day) {
        if(day <= 0 || day > rewardDescriptionList.size()) return null;
        return rewardDescriptionList.get(day - 1);
    }

    /**
     * 指定した日数の報酬説明を設定（1日目はindex0）
     * @param day 日数（1〜）
     * @param description 報酬説明
     */
    public void setRewardDescriptionByDay(int day, String description) {
        if(day <= 0) return;
        // 必要に応じてリストを拡張
        while(rewardDescriptionList.size() < day){
            rewardDescriptionList.add("");
        }
        rewardDescriptionList.set(day - 1, description);
    }

    /**
     * 日数の設定（consecutiveDaysへ反映）
     * もし意味が違うなら適宜修正してください
     */
    public void setDays(int days) {
        this.consecutiveDays = days;
    }
}
