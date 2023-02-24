package MindustryToolkit.dialogs;

import MindustryToolkit.identity.User;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import java.util.concurrent.atomic.AtomicBoolean;

public class IdentityDialog extends FeatureDialog {
    public static String title = Settings.getText("identity.title");

    private final AtomicBoolean enabled = new AtomicBoolean(IdentitySettings.enabled);
    private UserInputs users = new UserInputs();

    public IdentityDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                IdentityDialog.onClose();
            return false;
        });
    }

    public void rebuild() {
        this.enabled.set(IdentitySettings.enabled);
        this.main.checkPref(Settings.getText("identity.enabled"), IdentitySettings.enabled, this.enabled::set);
        this.main.pref(new DescriptionSetting(Settings.getText("identity.users-category")));
        this.main.pref(new DividerSetting());
        int usersNum = IdentitySettings.users.users().length;
        TextField[] userNames = new TextField[usersNum], uuids = new TextField[usersNum];
        for (int i = 0; i < usersNum; i++) {
            User user = IdentitySettings.users.users()[i];
            Table editTable = new Table();
            editTable.center();
            TextField userName = new TextField();
            userNames[i] = userName;
            userName.setMaxLength(Vars.maxNameLength);
            userName.update(() -> userName.setText(user.username()));
            userName.changed(() -> {
                if (!userName.getText().contains(":")) user.username(userName.getText());
            });
            editTable.add(userName);
            TextField uuid = new TextField();
            uuids[i] = uuid;
            uuid.setMaxLength(100);
            uuid.update(() -> uuid.setText(user.uuid()));
            uuid.changed(() -> {
                if (!uuid.getText().contains(":")) user.uuid(uuid.getText());
            });
            editTable.add(uuid);
            editTable.button(Icon.trash, Styles.emptyi, () -> {
                IdentitySettings.users.removeUser(user);
                this.show();
            });
            if (Vars.net.active()) editTable.button(Icon.ok, Styles.emptyi, user::rejoinAs);
            editTable.pack();
            this.main.pref(new ElementSetting<>(editTable));
        }
        this.users = new UserInputs(userNames, uuids);
        this.main.pref(new ElementSetting<>(this.main.button(Icon.add, Styles.emptyi, () -> {
            User[] users = IdentitySettings.users.users();
            if (users.length == 0 || users[users.length - 1].blank()) IdentitySettings.users.addUser(new User());
            this.show();
        }).get()));
        this.main.pref(new DividerSetting());
        this.main.pref(new ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        IdentitySettings.enabled = this.enabled.get();
        int usersNum = IdentitySettings.users.users().length;
        for (int i = 0; i < usersNum; i++) {
            User user = IdentitySettings.users.users()[i];
            user.username(this.users.userNames[i].getText());
            user.uuid(this.users.uuids[i].getText());
        }
        IdentitySettings.saveSettings();
    }

    private static void onClose() {
        IdentitySettings.readSettings();
    }

    private static class UserInputs {
        public TextField[] userNames;
        public TextField[] uuids;

        public UserInputs() {
        }

        public UserInputs(TextField[] userNames, TextField[] uuids) {
            this.userNames = userNames;
            this.uuids = uuids;
        }
    }
}
