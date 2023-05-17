package MindustryToolkit.settings;

import MindustryToolkit.dialogs.AutoFillDialog;
import MindustryToolkit.dialogs.SectorizedDialog;
import MindustryToolkit.dialogs.FeatureDialog;
import MindustryToolkit.dialogs.IdentityDialog;
import arc.Core;
import arc.func.Cons;
import arc.scene.style.TextureRegionDrawable;
import arc.util.*;
import mindustry.ui.dialogs.FullTextDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.bundle;
import static mindustry.Vars.ui;

public class Settings {
    public Settings() {
    }

    public void init() {
        Log.info("[cyan]Init Mindustry Toolkit settings");
        Cons<SettingsMenuDialog.SettingsTable> builder = settingsTable -> {
            SettingsMenuDialog.SettingsTable settings = new SettingsMenuDialog.SettingsTable();
            AutoFillDialog autoFillDialog = new AutoFillDialog();
            settings.pref(new FeatureDialog.ButtonSetting(AutoFillDialog.title, autoFillDialog::show));
            IdentityDialog identityDialog = new IdentityDialog();
            settings.pref(new FeatureDialog.ButtonSetting(IdentityDialog.title, identityDialog::show));
            SectorizedDialog sectorizedDialog = new SectorizedDialog();
            settings.pref(new FeatureDialog.ButtonSetting(SectorizedDialog.title, sectorizedDialog::show));
            /*settings.pref(new ButtonSetting("Save", () -> {
                showDialog("Save", "Save!");
            }));*/

//            settings.checkPref(getText("enabled"), true, e -> this.saveSetting(getSettingsNamePrefix() + "enabled", e));

            settingsTable.add(settings);
        };
        ui.settings.getCategories().add(new SettingsMenuDialog.SettingsCategory(Settings.getText("title"), new TextureRegionDrawable(Core.atlas.find("mindustry-toolkit-kubikaugustyn-logo")), builder));
    }

    public static String getSettingsNamePrefix() {
        return "mindustry-toolkit-kubikaugustyn-";
    }

    private void showDialog(String title, String message) {
        FullTextDialog baseDialog = new FullTextDialog();

        baseDialog.show(title, message);
    }

    public static void saveSetting(String name, Object object) {
        Core.settings.put(name, object);
    }

    public static void saveStringSetting(String name, String value) {
        Settings.saveSetting(name, value);
    }

    public static void saveBoolSetting(String name, boolean value) {
        Settings.saveSetting(name, value);
    }

    public static void saveIntSetting(String name, int value) {
        Settings.saveSetting(name, value);
    }

    public static String readStringSetting(String name, String def) {
        return Core.settings.getString(name, def);
    }

    public static boolean readBoolSetting(String name, boolean def) {
        return Core.settings.getBool(name, def);
    }

    public static int readIntSetting(String name, int def) {
        return Core.settings.getInt(name, def);
    }

    public static String getText(String name) {
        String key = "mindustry-toolkit-kubikaugustyn.settings." + name;
        return bundle.get(key);
    }

    public static String getText(String name, String def) {
        String key = "mindustry-toolkit-kubikaugustyn.settings." + name;
        return bundle.get(key, def);
    }
}
