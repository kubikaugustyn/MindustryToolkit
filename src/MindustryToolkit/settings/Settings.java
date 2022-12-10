package MindustryToolkit.settings;

import arc.Core;
import arc.func.Cons;
import arc.scene.style.TextureRegionDrawable;
import arc.util.*;
import mindustry.ui.dialogs.FullTextDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.util.concurrent.atomic.AtomicReference;

import static arc.Core.bundle;
import static mindustry.Vars.defaultEnv;
import static mindustry.Vars.ui;

public class Settings {
    public Settings() {
    }

    public void init() {
        Log.info("[cyan]Init Mindustry Toolkit settings");
        Cons<SettingsMenuDialog.SettingsTable> builder = settingsTable -> {
            AtomicReference<String> authKey = new AtomicReference<>(Core.settings.getString("auth-key", ""));
            AtomicReference<String> targetLanguage = new AtomicReference<>(Core.settings.getString("target-language", "en-GB"));

            SettingsMenuDialog.SettingsTable settings = new SettingsMenuDialog.SettingsTable();
            settings.areaTextPref(this.getText("auth-key"), "", authKey::set);
            settings.textPref(this.getText("target-language"), "en-GB", targetLanguage::set);

            settings.pref(new ButtonSetting("Save", () -> {
                showDialog("Save", "Save!");
            }));

            settings.checkPref(this.getText("enabled"), true, e -> this.saveSetting("mindustry-toolkit-kubikaugustyn-enabled", e));

            settingsTable.add(settings);
        };
        ui.settings.getCategories().add(new SettingsMenuDialog.SettingsCategory(bundle.get("mindustry-toolkit-kubikaugustyn.settings.title"), new TextureRegionDrawable(Core.atlas.find("mindustry-toolkit-kubikaugustyn-logo")), builder));
    }

    private void showDialog(String title, String message) {
        FullTextDialog baseDialog = new FullTextDialog();

        baseDialog.show(title, message);
    }

    private class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting {
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

    private String getText(String name) {
        String key = "mindustry-toolkit-kubikaugustyn.settings." + name;
        String text = bundle.get(key);
        return text;
    }

    private String getText(String name, String def) {
        String key = "mindustry-toolkit-kubikaugustyn.settings." + name;
        String text = bundle.get(key, def);
        return text;
    }
}
