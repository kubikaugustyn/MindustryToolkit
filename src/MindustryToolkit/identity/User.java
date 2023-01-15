package MindustryToolkit.identity;

import mindustry.Vars;
import mindustry.net.NetConnection;
import mindustry.net.Packets;

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

    /*public boolean rejoinAs() {
        if (!this.switchTo()) return false;
        NetConnection con=Vars.player.con();
        String ip=con.address;
        int port=???;
        Vars.net.disconnect();
        Vars.net.connect(ip, 1,null);
        return true;
    }*/

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
        return this.uuid;
    }
}
