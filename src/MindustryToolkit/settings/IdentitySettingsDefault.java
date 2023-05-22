package MindustryToolkit.settings;

import MindustryToolkit.identity.Users;
import mindustry.Vars;

public class IdentitySettingsDefault implements FeatureSettingsDefault {
    public static boolean enabled = true; // Identity enabled
    public static Users users = Users.blank; // User profiles
    public static String quickKey = "L"; // Key to quickly switch between users in multiplayer
    public static String originalUUID = Vars.platform.getUUID(); // Original UUID

    public static void init() {

    }
}
