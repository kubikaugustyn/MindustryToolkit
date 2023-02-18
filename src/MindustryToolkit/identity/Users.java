package MindustryToolkit.identity;

import java.util.Arrays;
import java.util.List;

public class Users {
    public static Users blank = new Users(new User[]{});
    private User[] users;

    public Users(User[] users) {
        this.users = users;
    }

    public User[] users() {
        return this.users;
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
            userStrings[i] = '"' + users[i].toString().replaceAll("\"", "\\\"") + '"'; // We replace " with \" and surround it with " like: "<User>"
        }
        return '[' + String.join(",", userStrings) + ']';
    }

    public static Users fromString(String source) {
        return Users.blank;
    }
}
