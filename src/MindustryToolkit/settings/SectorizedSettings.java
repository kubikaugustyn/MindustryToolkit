package MindustryToolkit.settings;

import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class SectorizedSettings implements FeatureSettings {
    private static final String settingsPrefix = Settings.getSettingsNamePrefix() + "sectorized-";
    public static boolean enabled = SectorizedSettingsDefault.enabled;

    public static void init() {
        // This method must be called only once!
        SectorizedSettingsDefault.init();
        readSettings();
    }

    public static void readSettings() {
        enabled = Settings.readBoolSetting(SectorizedSettings.namePrefix("enabled"), SectorizedSettingsDefault.enabled);
    }

    public static void saveSettings() {
        Log.info((SectorizedSettings.enabled ? "Enabled" : "Disabled") + " Sectorized!");
        Settings.saveBoolSetting(SectorizedSettings.namePrefix("enabled"), SectorizedSettings.enabled);
    }

    private static String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
