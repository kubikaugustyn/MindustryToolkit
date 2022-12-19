package MindustryToolkit.settings;

import arc.Core;

public class AutoFillSettings {
    private static final String settingsPrefix = new Settings().getSettingsNamePrefix() + "autofill-";
    public static boolean enabled = true;

    public void init() {
        enabled = Core.settings.getBool(namePrefix("enabled"), true);
    }

    private String namePrefix(String name) {
        return settingsPrefix + name;
    }
}
