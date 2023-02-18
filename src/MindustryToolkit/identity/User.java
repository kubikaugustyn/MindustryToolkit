package MindustryToolkit.identity;

import mindustry.Vars;

public class User {
    public String username;
    public String uuid;

    public User() {
        this("Player");
    }

    public User(String username) {
        this(username, "666");
    }

    public User(String username, String uuid) {
        this.username(username);
        this.uuid(uuid);
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public boolean rejoinAs() {
        if (!this.switchTo()) return false;
        // Return false if you're not connected to any server, to prevent errors while Vars.ui.join.reconnect()
        if (!Vars.net.client()) return false;
        Vars.ui.join.reconnect();
        return true;
    }

    public boolean switchTo() {
        if (Vars.player.con() == null) return false;
        Vars.player.name(this.username());
        Vars.player.con().uuid = this.uuid();
        return true;
    }

    public User uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String username() {
        return this.username;
    }

    public String uuid() {
        // To get your Sectorized uuid you do this:
        // Core.settings.getString("usid-89.58.37.204:6567", "No USID found :-(")
        // For OmniDustry:
        // Core.settings.getString("usid-109.94.209.233:6567", "No USID found :-(")
        return this.uuid;
    }

    @Override
    public String toString() {
        return "User:" + this.uuid() + ":" + this.username();
    }

    public static User fromString(String source) {
        String[] parts = source.split(":");
        String[] uuidPart = new String[parts.length - 2];
        System.arraycopy(parts, 2, uuidPart, 0, parts.length - 2);
        return new User(parts[1], String.join(":", uuidPart));
    }
}