package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.IdentitySettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.Label;

import java.util.concurrent.atomic.AtomicBoolean;

public class IdentityDialog extends FeatureDialog {
    public static String title = Settings.getText("identity.title");

    private AtomicBoolean enabled;

    public IdentityDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                IdentityDialog.onClose();
            return false;
        });
        this.enabled = new AtomicBoolean(AutoFillSettings.enabled);
        // Users
        this.main.checkPref(Settings.getText("identity.enabled"), AutoFillSettings.enabled, this.enabled::set);
        this.main.add(new Label(Settings.getText("identity.users-category"))).row();
        // Users
        this.main.pref(new Settings.ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        IdentitySettings.enabled = this.enabled.get();
        // Users
        IdentitySettings.saveSettings();
    }

    private static void onClose() {
        AutoFillSettings.readSettings();
    }
}
