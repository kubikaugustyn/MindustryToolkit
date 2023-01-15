package MindustryToolkit.settings;

import MindustryToolkit.dialogs.AutoFillDialog;
import arc.struct.ObjectMap;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class IdentitySettings {
    private static final String settingsPrefix = Settings.getSettingsNamePrefix() + "identity-";
    public static boolean enabled = IdentitySettingsDefault.enabled;

    public static void init() {
        // This method must be called only once!
        IdentitySettingsDefault.init();
        readSettings();
    }

    public static void readSettings() {
        enabled = AutoFillDialog.readBoolSetting("autofill.enabled", IdentitySettingsDefault.enabled);
    }

    private static String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
