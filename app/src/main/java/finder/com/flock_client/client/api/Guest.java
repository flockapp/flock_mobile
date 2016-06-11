package finder.com.flock_client.client.api;

/**
 * Created by Daniel on 11/6/16.
 */

public class Guest {

    private String username;
    private String fullName;

    public Guest(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
