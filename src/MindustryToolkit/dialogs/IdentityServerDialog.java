package MindustryToolkit.dialogs;

import MindustryToolkit.identity.User;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;


public class IdentityServerDialog extends BaseDialog {
    public static String title = Settings.getText("identity.server-dialog.title");

    public IdentityServerDialog() {
        super(title);

        Vars.ui.paused.shown(this::fixPausedDialog);
    }

    public Dialog show() {
        this.cont.clearChildren();
        this.buttons.clearChildren();
        this.rebuild();
        return super.show();
    }

    public void rebuild() {
        this.addCloseButton();
        this.cont.labelWrap(Settings.getText("identity.server-dialog.header")).fillX().width(250f).center().get().setWrap(false);
        this.cont.row();
        String serverIp = null;
        try {
            serverIp = User.getCurrentServerIp();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        String origUsid = serverIp == null ? null : IdentitySettings.readOriginalUsid(serverIp);
        Log.info("Server IP: " + serverIp);
        if (Vars.net.client() && Vars.net.active() && origUsid != null) {
            Table origTable = new Table();
            origTable.center();
            origTable.labelWrap(Settings.getText("identity.server-dialog.rollback-to-original")).fillX().center().get().setWrap(false);
            origTable.button(Icon.ok, Styles.emptyi, () -> {
                this.hide();
                User origUser = new User(null, origUsid, IdentitySettings.originalUUID);
                if (origUser.rejoinAs())
                    Vars.ui.hudfrag.showToast(Settings.getText("identity.server-dialog.switched-to.rollback"));
                else Vars.ui.hudfrag.showToast(Settings.getText("identity.server-dialog.switched-to.fail"));
            });
            this.cont.add(origTable);
        }
        this.cont.row();
        for (User user : IdentitySettings.users.users()) {
            Table userTable = new Table();
            userTable.center();
            userTable.labelWrap(user.username() == null ? Settings.getText("identity.server-dialog.keep-name") : user.username()).fillX().center().get().setWrap(false);
            userTable.button(Icon.ok, Styles.emptyi, () -> {
                this.hide();
                if (user.rejoinAs())
                    Vars.ui.hudfrag.showToast(Settings.getText("identity.server-dialog.switched-to"));
                else Vars.ui.hudfrag.showToast(Settings.getText("identity.server-dialog.switched-to.fail"));
            });
            this.cont.add(userTable);
            this.cont.row();
        }
    }

    private void fixPausedDialog() {
        if (!IdentitySettings.enabled) return;
        if (!Vars.net.client() || !Vars.net.active()) return;
        Table root = Vars.ui.paused.cont;

        if (Vars.mobile) {
            root.row().buttonRow(Settings.getText("identity.server-dialog.button"), Icon.admin, this::show).colspan(3);
            return;
        }

        root.row();
        root.button(Settings.getText("identity.server-dialog.button"), Icon.admin, this::show).colspan(2).width(450f).row();

        int index = Vars.state.isCampaign() || Vars.state.isEditor() ? 5 : 7;
        root.getCells().insert(index, root.getCells().remove(index + 1));
    }
}
