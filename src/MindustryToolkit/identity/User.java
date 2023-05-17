package MindustryToolkit.identity;

import arc.Core;
import arc.math.Rand;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import mindustry.Vars;
import mindustry.ui.dialogs.JoinDialog;

import java.lang.reflect.Field;

public class User {
    public String username;
    public byte[] uuid;
    public byte[] usid;
    public String addressTCP;

    public User() {
        this("Player");
    }

    public User(String username) {
        this(username, "666");
    }

    public User(String username, String usid) {
        this(username, usid, "666");
    }

    public User(String username, String usid, String uuid) {
        this.username(username);
        this.usid(usid);
        this.uuid(uuid);
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public boolean blank() {
        return this.username() != null && this.usid() != null;
    }

    public boolean rejoinAs() {
        // Return false if you're not connected to any server, to prevent errors while Vars.ui.join.reconnect()
        if (!Vars.net.client() || !Vars.net.active()) return false;
        if (!this.switchTo()) return false;
        Vars.ui.join.reconnect();
        return true;
    }

    public boolean switchTo() {
        // if (Vars.player.con() == null) return false;
        Vars.player.name(this.username());
        try {
            /*Field providerField = Net.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            ArcNetProvider provider = (ArcNetProvider) providerField.get(Vars.net);

            Field clientField = ArcNetProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            Client client = (Client) clientField.get(provider);

            Field addressField = Client.class.getDeclaredField("connectHost");
            addressField.setAccessible(true);
            InetAddress address = (InetAddress) addressField.get(client);
            String ip = address.toString();*/
            Field ipField = JoinDialog.class.getDeclaredField("lastIp");
            ipField.setAccessible(true);
            String ip = (String) ipField.get(Vars.ui.join);
            Field portField = JoinDialog.class.getDeclaredField("lastPort");
            portField.setAccessible(true);
            int port = (int) portField.get(Vars.ui.join);

            Log.info("Reconnect as " + this.username() + " with USID " + this.usidStr() + " and UUID " + this.uuidStr());

            this.setUsid(ip + ":" + port);
            this.setUuid();
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setUsid(String ip) {
        if (ip.contains("/")) {
            ip = ip.substring(ip.indexOf("/") + 1);
        }

        // Core.settings.put("usid-" + ip, this.usid());
        String result = new String(Base64Coder.encode(this.usid()));
        Core.settings.put("usid-" + ip, result);
    }

    public User usid(byte[] usid) {
        this.usid = usid;
        return this;
    }

    public User usid(String usid) {
        try {
            byte[] bytes = Base64Coder.decode(usid);
            if (bytes.length != 8) return this;
            this.usid(bytes);
        } catch (Exception e) {
            return this;
        }
        return this;
    }

    public void setUuid() {
        // Core.settings.getString("uuid", "");
        String result = new String(Base64Coder.encode(this.uuid()));
        Core.settings.put("uuid", result);
    }

    public User uuid(byte[] uuid) {
        this.uuid = uuid;
        return this;
    }

    public User uuid(String uuid) {
        try {
            byte[] bytes = Base64Coder.decode(uuid);
            if (bytes.length != 8) return this;
            this.uuid(bytes);
        } catch (Exception e) {
            return this;
        }
        return this;
    }

    public User addressTCP(String addressTCP) {
        this.addressTCP = addressTCP;
        return this;
    }

    public String username() {
        return this.username;
    }

    public byte[] usid() {
        // To get your Sectorized uuid you do this:
        // Core.settings.getString("usid-89.58.37.204:6567", "No USID found :-(")
        // For OmniDustry:
        // Core.settings.getString("usid-109.94.209.233:6567", "No USID found :-(")
        if (this.usid == null) {
            byte[] bytes = new byte[8];
            (new Rand()).nextBytes(bytes);
            this.usid = bytes;
            return bytes;
        }
        return this.usid;
    }

    public String usidStr() {
        return new String(Base64Coder.encode(this.usid()));
    }

    public byte[] uuid() {
        // Vars.platform.getUUID()
        if (this.uuid == null) {
            // byte[] bytes = Base64Coder.decode(Vars.platform.getUUID());
            byte[] bytes = new byte[8];
            (new Rand()).nextBytes(bytes);
            this.uuid = bytes;
            return bytes;
        }
        return this.uuid;
    }

    public String uuidStr() {
        return new String(Base64Coder.encode(this.uuid()));
    }

    public String addressTCP() {
        return this.addressTCP;
    }

    @Override
    public String toString() {
        return "User:" + this.usidStr() + ":" + this.uuidStr() + ":" + this.username();
    }

    public static User fromString(String source) {
        String[] parts = source.split(":");
        // Log.info("Parsing user of " + parts.length + " parts: " + source);
        // Ehm IDEA whatever
        // Basically to prevent java.lang.ArrayIndexOutOfBoundsException
        return switch (parts.length) {
            case 2 -> new User(null, parts[1]);
            case 3 -> new User(parts[2], parts[1]);
            case 4 -> new User(parts[3], parts[1], parts[2]);
            default -> new User();
        };
    }
}
