package finder.com.flock_client.client.api;

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

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType contentType = MediaType.parse("application/json");

    private String token;
    private String baseUrl = "";

    public HttpClient() {

    }

    public HttpClient(String token) {
        this.token = token;
    }

    private JSONObject makeRequest(String method, String suffix, JSONObject datObj) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+suffix)
                .get();
        if (token != null) {
            builder.addHeader("Authorization", token);
        }
        RequestBody reqBody = (datObj != null) ? RequestBody.create(contentType, datObj.toString()): null;
        switch (method) {
            case "get":
                builder = builder.get();
            case "delete":
                builder = builder.delete(reqBody);
            case "post":
                builder = builder.post(reqBody);
            case "put":
                builder = builder.put(reqBody);

        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    public JSONObject makeGetRequest(String suffix) throws Exception {
        return makeRequest("get", suffix, null);
    }

    public JSONObject makeDeleteRequest(String suffix, JSONObject datObj) throws Exception {
        return makeRequest("delete", suffix, datObj);
    }

    public JSONObject makePutRequest(String suffix, JSONObject datObj) throws Exception {
        return makeRequest("put", suffix, datObj);
    }

    public JSONObject makePostRequest(String suffix, JSONObject datObj) throws Exception {
        return makeRequest("post", suffix, datObj);
    }
}
