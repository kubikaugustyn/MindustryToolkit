package MindustryToolkit.settings;

import MindustryToolkit.identity.Users;
import mindustry.Vars;

public class IdentitySettingsDefault {
    public static boolean enabled = false; // Identity enabled, false until I get it working
    public static Users users = Users.blank; // User profiles
    public static String quickKey = "L"; // Key to quickly switch between users in multiplayer
    public static String originalUUID = Vars.platform.getUUID(); // Original UUID

    public static void init() {

    }
}
