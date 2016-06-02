package finder.com.flock_client.api;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Daniel on 2/6/16.
 */

public class HttpClient {

    private String token;
    private final OkHttpClient client = new OkHttpClient();
    private MediaType contentType = MediaType.parse("application/json");
    private String baseUrl = "";

    public HttpClient(String token) {
        this.token = token;
    }

    public JSONObject makeGetRequest(String suffix) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .get();
        if (token != null) {
            builder.addHeader("Authorization", token);
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        JSONObject responseJSON = new JSONObject(response.body().string());
        return responseJSON.getJSONObject("data");
    }

    public JSONObject makePostRequest(String suffix, JSONObject datObj) throws Exception {
        RequestBody reqBody = RequestBody.create(contentType, datObj.toString());
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .post(reqBody);
        if (this.token != null) {
            builder.addHeader("Authorization", token);
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        JSONObject responseJSON = new JSONObject(response.body().string());
        return responseJSON.getJSONObject("data");
    }
}
