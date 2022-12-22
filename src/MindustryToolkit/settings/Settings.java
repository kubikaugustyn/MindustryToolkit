package MindustryToolkit.settings;

import MindustryToolkit.dialogs.AutoFillDialog;
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
            settings.pref(new ButtonSetting(AutoFillDialog.title, autoFillDialog::show));
            settings.pref(new ButtonSetting("Save", () -> {
                showDialog("Save", "Save!");
            }));

            settings.checkPref(getText("enabled"), true, e -> this.saveSetting("mindustry-toolkit-kubikaugustyn-enabled", e));

            settingsTable.add(settings);
        };
        ui.settings.getCategories().add(new SettingsMenuDialog.SettingsCategory(bundle.get("mindustry-toolkit-kubikaugustyn.settings.title"), new TextureRegionDrawable(Core.atlas.find("mindustry-toolkit-kubikaugustyn-logo")), builder));
    }

    public String getSettingsNamePrefix() {
        return "mindustry-toolkit-kubikaugustyn-";
    }

    private void showDialog(String title, String message) {
        FullTextDialog baseDialog = new FullTextDialog();

        baseDialog.show(title, message);
    }

    private static class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting {
        String name;
        Runnable clicked;

        public ButtonSetting(String name, Runnable clicked) {
            super(name);
            this.name = name;
            this.clicked = clicked;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            table.button(name, clicked).margin(14).width(240f).pad(6);
            table.row();
        }
    }

    private void saveSetting(String name, Object object) {
        Core.settings.put(name, object);
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
