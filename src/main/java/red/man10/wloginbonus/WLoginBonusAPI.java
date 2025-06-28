package red.man10.wloginbonus;

import java.util.HashMap;
import java.util.Map;

public class WLoginBonusAPI {

    // ボーナス名 → ボーナスデータ
    private static final Map<String, LoginBonusData> bonuses = new HashMap<>();

    /**
     * 新しいログインボーナスを作成
     */
    public static boolean createBonus(String name) {
        if (bonuses.containsKey(name)) return false;
        bonuses.put(name, new LoginBonusData(name));
        return true;
    }

    /**
     * ログインボーナスの存在を確認
     */
    public static boolean exists(String name) {
        return bonuses.containsKey(name);
    }

    /**
     * ログインボーナスの削除
     */
    public static boolean deleteBonus(String name) {
        return bonuses.remove(name) != null;
    }

    /**
     * ボーナスの取得
     */
    public static LoginBonusData getBonus(String name) {
        return bonuses.get(name);
    }

    /**
     * ボーナスの更新（上書き）
     */
    public static void updateBonus(String name, LoginBonusData data) {
        bonuses.put(name, data);
    }

    /**
     * 連続ログイン日数を設定
     */
    public static boolean setConsecutiveDays(String name, int days) {
        LoginBonusData data = bonuses.get(name);
        if (data == null) return false;
        data.setConsecutiveDays(days);
        return true;
    }

    /**
     * 日数を設定（通常の日数）
     */
    public static boolean setDays(String name, int days) {
        LoginBonusData data = bonuses.get(name);
        if (data == null) return false;
        data.setDays(days);
        return true;
    }

    /**
     * 現在のすべてのボーナスを取得
     */
    public static Map<String, LoginBonusData> getAllBonuses() {
        return bonuses;
    }

    // 今後: ファイル保存/読み込みなど追加予定
}
