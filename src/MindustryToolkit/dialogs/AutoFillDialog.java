package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.Label;

public class AutoFillDialog extends FeatureDialog {
    public static String title = Settings.getText("autofill.title");

    public AutoFillDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                AutoFillDialog.onClose();
            return false;
        });
        this.main.checkPref(Settings.getText("autofill.enabled"), AutoFillSettings.enabled, null);
        this.main.add(new Label(Settings.getText("autofill.ammo-category"))).row();
        this.main.checkPref(Settings.getText("autofill.allow-homing-ammo"), AutoFillSettings.allowHomingAmmo, null);
        this.main.checkPref(Settings.getText("autofill.allow-fire-ammo"), AutoFillSettings.allowFireAmmo, null);
        this.main.sliderPref(Settings.getText("autofill.min-turret-core-items"), AutoFillSettings.minTurretCoreItems, 0, 500, i -> i + "");
    }

    private static void onClose() {
        AutoFillSettings.readSettings();
    }
}
