package MindustryToolkit.dialogs;

import MindustryToolkit.settings.AutoFillSettings;
import MindustryToolkit.settings.Settings;
import arc.Core;
import arc.scene.event.VisibilityEvent;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class FeatureDialog extends BaseDialog {
    public SettingsMenuDialog.SettingsTable main;

    public FeatureDialog(String title) {
        super(title);
        this.addCloseButton();
        this.cont.add(this.main = new SettingsMenuDialog.SettingsTable());
        this.main.center();
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
