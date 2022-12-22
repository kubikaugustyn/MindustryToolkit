package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.AutoFillSettingsDefault;
import MindustryToolkit.settings.Settings;
import arc.Core;
import arc.func.Cons;
import arc.scene.ui.Label;
import arc.util.Log;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class AutoFillDialog extends BaseDialog {
    public SettingsMenuDialog.SettingsTable main;
    public static String title = Settings.getText("autofill.title");

    public AutoFillDialog() {
        super(title);
        this.addCloseButton();
        this.closeOnBack(this::onClose);
        this.cont.add(this.main = new SettingsMenuDialog.SettingsTable());
        this.main.center();
        this.main.checkPref(Settings.getText("autofill.enabled"), AutoFillSettings.enabled, null);
        this.main.add(new Label(Settings.getText("autofill.ammo-category"))).row();
        this.main.checkPref(Settings.getText("autofill.allow-homing-ammo"), AutoFillSettings.allowHomingAmmo, null);
        this.main.checkPref(Settings.getText("autofill.allow-fire-ammo"), AutoFillSettings.allowFireAmmo, null);
        this.main.sliderPref(Settings.getText("autofill.min-turret-core-items"), AutoFillSettings.minTurretCoreItems, 0, 500, i -> i + "");
    }

    private void onClose() {
        AutoFillSettings.readSettings();
    }

    public static String readStringSetting(String name) {
        return Core.settings.getString(Settings.getText(name));
    }

    public static String readStringSetting(String name, String def) {
        return Core.settings.getString(Settings.getText(name), def);
    }

    public static boolean readBoolSetting(String name) {
        return Core.settings.getBool(Settings.getText(name));
    }

    public static boolean readBoolSetting(String name, boolean def) {
        return Core.settings.getBool(Settings.getText(name), def);
    }

    public static int readIntSetting(String name) {
        return Core.settings.getInt(Settings.getText(name));
    }

    public static int readIntSetting(String name, int def) {
        return Core.settings.getInt(Settings.getText(name), def);
    }
}
