package MindustryToolkit.identity;

import arc.util.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Users {
    public static Users blank = new Users(new User[]{});
    private User[] users;

    public Users(User[] users) {
        this.users = users;
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
            userStrings[i] = '"' + users[i].toString().replaceAll("\"", "\\\"") + '"'; // We replace " with \" and surround it with " like: "<User>"
        }
        return '[' + String.join(",", userStrings) + ']';
    }

    public static Users fromString(String source) {
        return Users.blank;
    }
}
