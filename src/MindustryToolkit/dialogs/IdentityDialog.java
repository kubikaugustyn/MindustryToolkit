package MindustryToolkit.dialogs;

import MindustryToolkit.identity.User;
import MindustryToolkit.identity.Users;
import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.Button;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import java.util.concurrent.atomic.AtomicBoolean;

public class IdentityDialog extends FeatureDialog {
    public static String title = Settings.getText("identity.title");

    private final AtomicBoolean enabled = new AtomicBoolean(IdentitySettings.enabled);

    public IdentityDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                IdentityDialog.onClose();
            return false;
        });
        this.enabled.set(IdentitySettings.enabled);
        this.main.checkPref(Settings.getText("identity.enabled"), IdentitySettings.enabled, this.enabled::set);
        this.main.add(new Label(Settings.getText("identity.users-category"))).row();
        for (User user : IdentitySettings.users.users()) {
            Table editTable = new Table();
            editTable.center();
            TextField userName = new TextField();
            userName.update(() -> {
                userName.setText(user.username());
            });
            userName.changed(() -> {
                user.username(userName.getText());
            });
            editTable.add(userName);
            TextField uuid = new TextField();
            uuid.update(() -> {
                uuid.setText(user.uuid());
            });
            uuid.changed(() -> {
                user.uuid(uuid.getText());
            });
            editTable.add(uuid);
            editTable.button(Icon.trash, Styles.emptyi, () -> {
                IdentitySettings.users.removeUser(user);
            });
            editTable.pack();
            this.main.add(editTable).row();
        }
        this.main.button(Icon.add, Styles.emptyi, () -> {
            IdentitySettings.users.addUser(new User());
        }).row();
        this.main.pref(new Settings.ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        IdentitySettings.enabled = this.enabled.get();
        // Users
        IdentitySettings.saveSettings();
    }

    private static void onClose() {
        IdentitySettings.readSettings();
    }
}
