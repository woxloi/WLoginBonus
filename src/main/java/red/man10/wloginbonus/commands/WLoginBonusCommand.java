package red.man10.wloginbonus.commands;

import com.shojabon.scommandrouter.SCommandRouter.SCommandArgumentType;
import com.shojabon.scommandrouter.SCommandRouter.SCommandObject;
import com.shojabon.scommandrouter.SCommandRouter.SCommandRouter;
import red.man10.wloginbonus.Main;
import red.man10.wloginbonus.commands.subCommands.*;


public class WLoginBonusCommand extends SCommandRouter {

    Main plugin;

    public WLoginBonusCommand(Main plugin) {
        super(plugin, "loginbonus");
        this.plugin = plugin;
        registerCommands();
        registerEvents();
        pluginPrefix = Main.prefix;
    }

    public void registerEvents() {
        setNoPermissionEvent(e -> e.sender.sendMessage(Main.prefix + "§c§lあなたは権限がありません"));
        setOnNoCommandFoundEvent(e -> e.sender.sendMessage(Main.prefix + "§c§lコマンドが存在しません"));
    }

    public void registerCommands() {
        //loginbonus command
        addCommand(
                new SCommandObject()
                        .prefix("test")
                        .permission("wloginbonus.test")
                        .explanation("テスト")
                        .executor(new TestCommand(plugin))
        );

        addCommand(
                new SCommandObject()
                        .prefix("edit")
                        .argument("内部名")
                        .permission("wloginbonus.edit")
                        .explanation("ログインボーナスの編集を行う")
                        .executor(new EditCommand(plugin))
        );
        addCommand(
                new SCommandObject()
                        .prefix("create")
                        .argument("内部名")
                        .permission("wloginbonus.create")
                        .explanation("新しくログインボーナスを作成する")
                        .executor(new CreateCommand(plugin))
        );
        addCommand(
                new SCommandObject()
                        .prefix("deposit")
                        .argument("player")
                        .argument("金額")
                        .permission("wloginbonus.deposit")
                        .explanation("指定したプレイヤーのお金を増やす(内部用)")
                        .executor(new DepositCommand(plugin))
        );
        addCommand(
                new SCommandObject()
                        .prefix("withdraw")
                        .argument("player")
                        .argument("金額")
                        .permission("wloginbonus.withdraw")
                        .explanation("指定したプレイヤーのお金を引く(内部用)")
                        .executor(new WithdrawCommand(plugin))
        );
    }
}