package finder.com.flock_client;

/**
 * Created by Daniel on 3/6/16.
 */

public class FlockApplication extends android.app.Application {
    private String currToken;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setCurrToken(String token) {
        currToken = token;
    }

    public String getCurrToken() {
        return currToken;
    }
}
