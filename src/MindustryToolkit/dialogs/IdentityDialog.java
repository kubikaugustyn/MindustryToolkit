package MindustryToolkit.dialogs;

import MindustryToolkit.identity.User;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.Core;
import arc.math.Rand;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import arc.util.serialization.Base64Coder;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class IdentityDialog extends FeatureDialog {
    public static String title = Settings.getText("identity.title");

    private final AtomicBoolean enabled = new AtomicBoolean(IdentitySettings.enabled);
    private UserInputs users = new UserInputs();
    private final TextField.TextFieldValidator usidValidator = (String text) -> {
        if (text.contains(":")) return false;
        try {
            byte[] bytes = Base64Coder.decode(text);
            if (bytes.length != 8) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    };
    private final TextField.TextFieldValidator uuidValidator = usidValidator;
    private final FatTextField serverIP = new FatTextField();
    private final DescriptionSetting serverUSID = new DescriptionSetting("");
    private int serverUSIDI = 0;

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
        TextField[] userNames = new TextField[usersNum], usids = new TextField[usersNum], uuids = new TextField[usersNum];
        for (int i = 0; i < usersNum; i++) {
            User user = IdentitySettings.users.users()[i];
            Table editTable = new Table();
            editTable.center();
            FatTextField userName = new FatTextField();
            userName.setPrefWidth(Vars.maxNameLength * 20f);
            userNames[i] = userName;
            userName.setMaxLength(Vars.maxNameLength);
            userName.update(() -> userName.setText(user.username()));
            userName.changed(() -> {
                if (!userName.getText().contains(":")) user.username(userName.getText());
            });
            editTable.add(userName);
            FatTextField usid = new FatTextField();
            usid.setPrefWidth(300f);
            usids[i] = usid;
            usid.setValidator(usidValidator);
            usid.setMaxLength(100);
            usid.setText(user.usidStr());
            usid.changed(() -> {
                if (usid.isValid()) user.usid(usid.getText());
            });
            editTable.add(usid);
            FatTextField uuid = new FatTextField();
            uuid.setPrefWidth(300f);
            uuids[i] = uuid;
            uuid.setValidator(uuidValidator);
            uuid.setMaxLength(100);
            uuid.setText(user.uuidStr());
            uuid.changed(() -> {
                if (uuid.isValid()) user.uuid(uuid.getText());
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
        this.users = new UserInputs(userNames, usids, uuids);
        this.main.pref(new ElementSetting<>(this.main.button(Icon.add, Styles.emptyi, () -> {
            User[] users = IdentitySettings.users.users();
            if (users.length == 0 || users[users.length - 1].blank()) IdentitySettings.users.addUser(new User());
            this.show();
        }).get()));
        this.main.pref(new DividerSetting());
        this.main.pref(new DescriptionSetting(Settings.getText("identity.server-category")));
        serverIP.setPrefWidth(400f);
        serverIP.changed(() -> {
            int currI = ++serverUSIDI;
            Time.runTask(60.0F, () -> {
                if (currI < serverUSIDI) return; // Delays the IP search by 1 second
                serverUSID.setDesc(Settings.getText("identity.server-usid.loading"));
                try {
                    if (!serverIP.getText().trim().isEmpty()) {
                        String ip = serverIP.getText().trim();
                        if (ip.contains("/")) {
                            ip = ip.substring(ip.indexOf("/") + 1);
                        }
                        InetAddress address = InetAddress.getByName(ip);
                        ip = address.toString();

                        serverUSID.setDesc(Settings.getText("identity.server-usid") + " " + Core.settings.getString("usid-" + ip, ""));
                    }
                } catch (UnknownHostException ignored) {

                }
            });
        });
        this.main.pref(new ElementSetting<>(serverIP));
        serverUSID.setDesc(Settings.getText("identity.server-usid.blank"));
        this.main.pref(serverUSID);
        this.main.pref(new DividerSetting());
        this.main.pref(new ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        IdentitySettings.enabled = this.enabled.get();
        int usersNum = IdentitySettings.users.users().length;
        for (int i = 0; i < usersNum; i++) {
            if (!this.users.usids[i].isValid() || !this.users.uuids[i].isValid()) {
                Vars.ui.showInfo(Settings.getText("identity.invalid-user-info"));
                return;
            }
        }
        for (int i = 0; i < usersNum; i++) {
            User user = IdentitySettings.users.users()[i];
            user.username(this.users.userNames[i].getText());
            user.usid(this.users.usids[i].getText());
            user.uuid(this.users.uuids[i].getText());
        }
        IdentitySettings.saveSettings();
    }

    private static void onClose() {
        IdentitySettings.readSettings();
    }

    private static class UserInputs {
        public TextField[] userNames;
        public TextField[] usids;
        public TextField[] uuids;

        public UserInputs() {
        }

        public UserInputs(TextField[] userNames, TextField[] usids, TextField[] uuids) {
            this.userNames = userNames;
            this.usids = usids;
            this.uuids = uuids;
        }
    }

    private static class FatTextField extends TextField {
        private float prefWidth = 150f;

        public float getPrefWidth() {
            return this.prefWidth;
        }

        public void setPrefWidth(float prefWidth) {
            this.prefWidth = prefWidth;
        }
    }
}
