package MindustryToolkit.identity;

import MindustryToolkit.Utils;
import arc.Core;
import arc.func.Cons;
import arc.net.Connection;
import arc.net.NetListener;
import arc.util.Log;
import arc.util.Strings;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.net.Packet;
import mindustry.net.Packets;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Users {
    public static Users blank = new Users(new User[]{});
    private User[] users;

    public Users(User[] users) {
        this.users = users;

        Vars.platform.getNet();
    }

    public User[] users() {
        return this.users;
    }

    public Users removeUser(User user) {
        // I hope you will always input user that's included in this Users class
        // Or it will throw too big index exception
        User[] newUsers = new User[this.users.length - 1];
        boolean foundRemovedUser = false;
        for (int i = 0; i < this.users.length; i++) {
            User currUser = this.users[i];
            if (!foundRemovedUser && currUser == user) {
                foundRemovedUser = true;
            } else {
                newUsers[i - (foundRemovedUser ? 1 : 0)] = currUser;
            }
        }
        this.users(newUsers);
        return this;
    }

    public Users addUser(User user) {
        User[] newUsers = new User[this.users.length + 1];
        for (int i = 0; i < this.users.length; i++)
            newUsers[i] = this.users[i];

        newUsers[this.users.length] = user;
        this.users(newUsers);
        return this;
    }


    public Users users(User[] users) {
        this.users = users;
        return this;
    }

    @Override
    public String toString() {
        User[] users = this.users();
        String[] userStrings = new String[users.length];
        for (int i = 0; i < users.length; i++) {
            String userString = users[i].toString();
            userString = userString.replaceAll("\"", "\\\""); // We replace " with \"
            userString = userString.replaceAll(",", "\\,"); // We replace , with \,
            userStrings[i] = '"' + userString + '"'; // Surround it with " like: "<User>"
        }
        return '[' + String.join(",", userStrings) + ']';
    }

    public static Users fromString(String source) {
        // Log.info("Parsing users of: " + source);
        source = source.substring(2, source.length() - 2);
        String[] userStrings = source.split("\",\"");
        // String[] userStrings = Utils.advancedSplit(source, "\",\"", "\\,\""); Not working function
        User[] users = new User[userStrings.length];
        for (int i = 0; i < userStrings.length; i++) {
            String userString = userStrings[i];
            userString = userString.replaceAll("\\\"", "\"");
            users[i] = User.fromString(userString);
        }
        return new Users(users);
    }
}
