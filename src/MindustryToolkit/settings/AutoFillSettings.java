package MindustryToolkit.settings;

import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class AutoFillSettings {
    private static final String settingsPrefix = Settings.getSettingsNamePrefix() + "autofill-";
    public static boolean enabled = AutoFillSettingsDefault.enabled;
    public static boolean allowHomingAmmo = AutoFillSettingsDefault.allowHomingAmmo;
    public static boolean allowFireAmmo = AutoFillSettingsDefault.allowFireAmmo;
    public static int minTurretCoreItems = AutoFillSettingsDefault.minTurretCoreItems;
    public static ObjectMap<ItemTurret, Item[]> turretAmmo = AutoFillSettingsDefault.turretAmmo;

    public static void init() {
        // This method must be called only once!
        AutoFillSettingsDefault.init();
        readSettings();
    }

    public static void readSettings() {
        // enabled = Core.settings.getBool(namePrefix("enabled"), true);
        enabled = Settings.readBoolSetting(AutoFillSettings.namePrefix("enabled"), AutoFillSettingsDefault.enabled);
        allowHomingAmmo = Settings.readBoolSetting(AutoFillSettings.namePrefix("allow-homing-ammo"), AutoFillSettingsDefault.allowHomingAmmo);
        allowFireAmmo = Settings.readBoolSetting(AutoFillSettings.namePrefix("allow-fire-ammo"), AutoFillSettingsDefault.allowFireAmmo);
        minTurretCoreItems = Settings.readIntSetting(AutoFillSettings.namePrefix("min-turret-core-items"), AutoFillSettingsDefault.minTurretCoreItems);
    }

    public static void saveSettings() {
        Log.info((AutoFillSettings.enabled ? "Enabled" : "Disabled") + " Autofill!");
        Settings.saveBoolSetting(AutoFillSettings.namePrefix("enabled"), AutoFillSettings.enabled);
        Settings.saveBoolSetting(AutoFillSettings.namePrefix("allow-homing-ammo"), AutoFillSettings.allowHomingAmmo);
        Settings.saveBoolSetting(AutoFillSettings.namePrefix("allow-fire-ammo"), AutoFillSettings.allowFireAmmo);
        Settings.saveIntSetting(AutoFillSettings.namePrefix("min-turret-core-items"), AutoFillSettings.minTurretCoreItems);
    }

    private static String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
