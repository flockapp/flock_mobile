package finder.com.flock_client.client.api;

import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Daniel on 2/6/16.
 */
public class HttpClient {
    public static final String baseHost = "192.168.0.104";
    public static final String baseUrl = "http://"+baseHost+":3000";

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType contentType = MediaType.parse("application/json");

    private String token = "";

    public HttpClient() {}

    public HttpClient(String token) {
        this.token = token;
    }

    private JSONObject makeRequest(Request.Builder reqBuilder) throws Exception {
        Request request = reqBuilder.build();
        Response response = client.newCall(request).execute();
        JSONObject respObj = new JSONObject(response.body().string());
        Log.d("debug response", respObj.toString());
        return respObj;
    }

    public JSONObject makeGetRequest(String suffix) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .addHeader("Authorization", token)
                .get();
        return makeRequest(builder);
    }

    public JSONObject makeGetRequest(HttpUrl url) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .get();
        return makeRequest(builder);
    }

    public JSONObject makeDeleteRequest(String suffix, JSONObject datObj) throws Exception {
        RequestBody reqBody = (datObj != null) ? RequestBody.create(contentType, datObj.toString()): null;
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .addHeader("Authorization", token)
                .delete(reqBody);
        return makeRequest(builder);
    }

    public JSONObject makePutRequest(String suffix, JSONObject datObj) throws Exception {
        RequestBody reqBody = (datObj != null) ? RequestBody.create(contentType, datObj.toString()): null;
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .addHeader("Authorization", token)
                .put(reqBody);
        return makeRequest(builder);
    }

    public JSONObject makePostRequest(String suffix, JSONObject datObj) throws Exception {
        RequestBody reqBody = (datObj != null) ? RequestBody.create(contentType, datObj.toString()): null;
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .addHeader("Authorization", token)
                .post(reqBody);
        return makeRequest(builder);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
