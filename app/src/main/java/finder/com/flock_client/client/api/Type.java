package finder.com.flock_client.client.api;

/**
 * Created by Daniel on 10/6/16.
 */

public class Type {
    private int id;
    private String name;

    public Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
