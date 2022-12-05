package MindustryToolkit.settings;

import arc.Core;
import arc.func.Cons;
import arc.scene.style.TextureRegionDrawable;
import arc.util.*;
import mindustry.ui.dialogs.FullTextDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import static arc.Core.bundle;
import static mindustry.Vars.ui;

public class InitSettings {
    public InitSettings() {
    }

    public void init() {
        Log.info("[cyan]Init Mindustry Toolkit settings");
        Cons<SettingsMenuDialog.SettingsTable> builder = settingsTable -> {
            AtomicReference<String> authKey = new AtomicReference<>(Core.settings.getString("auth-key", ""));
            AtomicReference<String> targetLanguage = new AtomicReference<>(Core.settings.getString("target-language", "en-GB"));

            SettingsMenuDialog.SettingsTable settings = new SettingsMenuDialog.SettingsTable();
            settings.areaTextPref(bundle.get("mindustry-toolkit-kubikaugustyn.settings.auth-key"), "", authKey::set);
            settings.textPref(bundle.get("mindustry-toolkit-kubikaugustyn.settings.target-language"), "en-GB", targetLanguage::set);

            settings.pref(new ButtonSetting("Save", () -> {
                showDialog("Save", "Save!");
            }));

            settings.checkPref(bundle.get("mindustry-toolkit-kubikaugustyn.settings.enabled"), true, e -> Core.settings.put("mindustry-toolkit-kubikaugustyn-enabled", e));

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
}
