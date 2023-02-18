package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.Settings;
import arc.scene.event.VisibilityEvent;
import arc.scene.ui.Label;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoFillDialog extends FeatureDialog {
    public static String title = Settings.getText("autofill.title");

    private AtomicBoolean enabled;
    private AtomicBoolean allowHomingAmmo;
    private AtomicBoolean allowFireAmmo;
    private AtomicInteger minTurretCoreItems;

    public AutoFillDialog() {
        super(title);
        this.addListener(sceneEvent -> {
            if (sceneEvent instanceof VisibilityEvent && ((VisibilityEvent) sceneEvent).isHide())
                AutoFillDialog.onClose();
            return false;
        });
        this.enabled = new AtomicBoolean(AutoFillSettings.enabled);
        this.allowHomingAmmo = new AtomicBoolean(AutoFillSettings.allowHomingAmmo);
        this.allowFireAmmo = new AtomicBoolean(AutoFillSettings.allowFireAmmo);
        this.minTurretCoreItems = new AtomicInteger(AutoFillSettings.minTurretCoreItems);
        this.main.checkPref(Settings.getText("autofill.enabled"), AutoFillSettings.enabled, this.enabled::set);
        this.main.add(new Label(Settings.getText("autofill.ammo-category"))).row();
        this.main.checkPref(Settings.getText("autofill.allow-homing-ammo"), AutoFillSettings.allowHomingAmmo, this.allowHomingAmmo::set);
        this.main.checkPref(Settings.getText("autofill.allow-fire-ammo"), AutoFillSettings.allowFireAmmo, this.allowFireAmmo::set);
        this.main.sliderPref(Settings.getText("autofill.min-turret-core-items"), AutoFillSettings.minTurretCoreItems, 0, 500, 10, i -> {
            this.minTurretCoreItems.set(i);
            return i + "";
        });
        this.main.pref(new Settings.ButtonSetting(Settings.getText("save"), this::onSaveClick));
    }

    private void onSaveClick() {
        AutoFillSettings.enabled = this.enabled.get();
        AutoFillSettings.allowHomingAmmo = this.allowHomingAmmo.get();
        AutoFillSettings.allowFireAmmo = this.allowFireAmmo.get();
        AutoFillSettings.minTurretCoreItems = this.minTurretCoreItems.get();
        AutoFillSettings.saveSettings();
    }

    private static void onClose() {
        AutoFillSettings.readSettings();
    }
}
