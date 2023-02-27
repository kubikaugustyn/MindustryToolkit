package MindustryToolkit.identity;

import arc.Core;
import mindustry.Vars;
import mindustry.core.NetClient;

public class User {
    public String username;
    public String usid;
    public String addressTCP;

    public User() {
        this("Player");
    }

    public User(String username) {
        this(username, "666");
    }

    public User(String username, String usid) {
        this.username(username);
        this.usid(usid);
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public boolean blank() {
        return this.username() != null && this.usid() != null;
    }

    public boolean rejoinAs() {
        if (!this.switchTo()) return false;
        // Return false if you're not connected to any server, to prevent errors while Vars.ui.join.reconnect()
        if (!Vars.net.client() || !Vars.net.active()) return false;
        Vars.ui.join.reconnect();
        return true;
    }

    public boolean switchTo() {
        // if (Vars.player.con() == null) return false;
        Vars.player.name(this.username());
//        this.setUsid(Vars.netClient);
        Vars.netClient = new NetClient();
        return true;
    }

    public void setUsid(String ip) {
        if (ip.contains("/")) {
            ip = ip.substring(ip.indexOf("/") + 1);
        }

        Core.settings.put("usid-" + ip, this.usid());
    }

    public User usid(String usid) {
        this.usid = usid;
        return this;
    }

    public User addressTCP(String addressTCP) {
        this.addressTCP = addressTCP;
        return this;
    }

    public String username() {
        return this.username;
    }

    public String usid() {
        // To get your Sectorized uuid you do this:
        // Core.settings.getString("usid-89.58.37.204:6567", "No USID found :-(")
        // For OmniDustry:
        // Core.settings.getString("usid-109.94.209.233:6567", "No USID found :-(")
        return this.usid;
    }

    public String addressTCP() {
        return this.addressTCP;
    }

    @Override
    public String toString() {
        return "User:" + this.usid() + ":" + this.username();
    }

    public static User fromString(String source) {
        String[] parts = source.split(":");
        // Ehm IDEA whatever
        // Basically to prevent java.lang.ArrayIndexOutOfBoundsException
        return switch (parts.length) {
            case 2 -> new User(null, parts[1]);
            case 3 -> new User(parts[2], parts[1]);
            default -> new User();
        };
    }
}
