package MindustryToolkit.dialogs;

import MindustryToolkit.identity.User;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class IdentityServerDialog extends BaseDialog {
    public static String title = Settings.getText("identity.server-dialog.title");

    public IdentityServerDialog() {
        super(title);
    }

    public Dialog show() {
        this.cont.clearChildren();
        this.rebuild();
        return super.show();
    }

    public void rebuild() {
        this.addCloseButton();
        this.cont.labelWrap(Settings.getText("identity.server-dialog.header")).fillX().width(250f).center().get().setWrap(false);
        this.cont.row();
        for (User user : IdentitySettings.users.users()) {
            Table userTable = new Table();
            userTable.center();
            userTable.labelWrap(user.username()).fillX().center().get().setWrap(false);
            userTable.button(Icon.ok, Styles.emptyi, () -> {
                this.hide();
                user.rejoinAs();
            });
            this.cont.add(userTable);
            this.cont.row();
        }
    }
}
