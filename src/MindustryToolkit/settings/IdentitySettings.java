package MindustryToolkit.settings;

import MindustryToolkit.dialogs.AutoFillDialog;
import MindustryToolkit.dialogs.IdentityDialog;
import MindustryToolkit.identity.Users;
import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class IdentitySettings {
    private static final String settingsPrefix = Settings.getSettingsNamePrefix() + "identity-";
    public static boolean enabled = IdentitySettingsDefault.enabled;
    public static Users users = IdentitySettingsDefault.users;

    public static void init() {
        // This method must be called only once!
        IdentitySettingsDefault.init();
        readSettings();
    }

    public static void readSettings() {
        enabled = Settings.readBoolSetting(IdentitySettings.namePrefix("enabled"), IdentitySettingsDefault.enabled);
        users = Users.fromString(Settings.readStringSetting(IdentitySettings.namePrefix("users"), IdentitySettingsDefault.users.toString()));
    }

    public static void saveSettings() {
        Log.info((IdentitySettings.enabled ? "Enabled" : "Disabled") + " Identity!");
        Settings.saveBoolSetting(IdentitySettings.namePrefix("enabled"), IdentitySettings.enabled);
        Settings.saveStringSetting(IdentitySettings.namePrefix("users"), IdentitySettings.users.toString());
    }

    private static String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
