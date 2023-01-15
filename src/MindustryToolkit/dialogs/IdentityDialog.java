package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;

public class IdentityDialog extends FeatureDialog {
    public static String title = Settings.getText("autofill.title");

    public IdentityDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                IdentityDialog.onClose();
            return false;
        });
    }

    private static void onClose() {
        AutoFillSettings.readSettings();
    }
}
