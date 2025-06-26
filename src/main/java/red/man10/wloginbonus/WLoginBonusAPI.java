package red.man10.wloginbonus;

//サンプル用に作成
import java.util.HashMap;
import java.util.Map;

public class WLoginBonusAPI {

    // 仮にボーナスの名前と情報を管理するMap（後でDBとかファイルに変える）
    private static Map<String, String> bonuses = new HashMap<>();

    // ボーナスを作成する
    public static void createBonus(String name) {
        bonuses.put(name, "初期データ"); // 実際は詳細データを保存
    }

    // ボーナスが存在するかチェック
    public static boolean exists(String name) {
        return bonuses.containsKey(name);
    }

    // ボーナス情報を取得
    public static String getBonus(String name) {
        return bonuses.get(name);
    }

    // ボーナス情報を更新
    public static void updateBonus(String name, String data) {
        bonuses.put(name, data);
    }
}
