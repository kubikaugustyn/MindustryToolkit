package MindustryToolkit.settings;

import MindustryToolkit.identity.Users;
import arc.util.Log;

public class IdentitySettings implements FeatureSettings {
    private static final String settingsPrefix = Settings.getSettingsNamePrefix() + "identity-";
    public static boolean enabled = IdentitySettingsDefault.enabled;
    public static Users users = IdentitySettingsDefault.users;
    public static String quickKey = IdentitySettingsDefault.quickKey;
    public static String originalUUID = IdentitySettingsDefault.originalUUID;

    public static void init() {
        // This method must be called only once!
        IdentitySettingsDefault.init();
        /*try { // Test for Issue #2
            Log.warn(IdentitySettingsDefault.users.toString()); // "[]"
            Log.warn(Users.fromString(IdentitySettingsDefault.users.toString()).toString()); // "[]"
        } catch (Exception e) {
            Log.err(e);
        }*/
        readSettings();
    }

    public static void readSettings() {
        enabled = Settings.readBoolSetting(IdentitySettings.namePrefix("enabled"), IdentitySettingsDefault.enabled);
        users = Users.fromString(Settings.readStringSetting(IdentitySettings.namePrefix("users"), IdentitySettingsDefault.users.toString()));
        quickKey = Settings.readStringSetting(IdentitySettings.namePrefix("quickKey"), IdentitySettingsDefault.quickKey);
        originalUUID = Settings.readStringSetting(IdentitySettings.namePrefix("originalUUID"), IdentitySettingsDefault.originalUUID);
    }

    public static void saveSettings() {
        Log.info((IdentitySettings.enabled ? "Enabled" : "Disabled") + " Identity!");
        Settings.saveBoolSetting(IdentitySettings.namePrefix("enabled"), IdentitySettings.enabled);
        Settings.saveStringSetting(IdentitySettings.namePrefix("users"), IdentitySettings.users.toString());
        Settings.saveStringSetting(IdentitySettings.namePrefix("quickKey"), IdentitySettings.quickKey);
        Settings.saveStringSetting(IdentitySettings.namePrefix("originalUUID"), IdentitySettings.originalUUID);
    }

    public static void saveOriginalUsid(String usid, String ip) {
        if (Settings.readStringSetting(IdentitySettings.namePrefix("originalUSID-" + ip), null) != null)
            return;
        Settings.saveStringSetting(IdentitySettings.namePrefix("originalUSID-" + ip), usid);
    }

    public static String readOriginalUsid(String ip) {
        return Settings.readStringSetting(IdentitySettings.namePrefix("originalUSID-" + ip), null);
    }

    public static void saveOriginalUsername(String username, String ip) {
        if (Settings.readStringSetting(IdentitySettings.namePrefix("originalUsername-" + ip), null) != null)
            return;
        Settings.saveStringSetting(IdentitySettings.namePrefix("originalUsername-" + ip), username);
    }

    public static String readOriginalUsername(String ip) {
        return Settings.readStringSetting(IdentitySettings.namePrefix("originalUsername-" + ip), null);
    }

    private static String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
