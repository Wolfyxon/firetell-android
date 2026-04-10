package com.wolfyxon.firetell.android.lib;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpApi {
    public interface GenericListener<T> {
        public void onData(T data);
    }

    public static String URL = "https://firetell.vercel.app/api/v1/";

    RequestQueue queue;
    HashMap<String, UserData> userCache;

    public HttpApi(RequestQueue queue) {
        this.queue = queue;
        this.userCache = new HashMap<>();
    }

    public void authRequest(
            String endpoint,
            int method,
            JSONObject body,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener
        )
    {
        FirebaseAuth auth = getAuth();

        assert auth.getCurrentUser() != null;
        auth.getCurrentUser().getIdToken(false).addOnSuccessListener(res -> {
            String token = res.getToken();

            JsonObjectRequest req = new JsonObjectRequest(method, URL + endpoint, body, successListener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);

                    return headers;
                }
            };

            queue.add(req);
        });
    }

    public void fetchUser(String uid, GenericListener<UserData> listener) {
        if(userCache.containsKey(uid)) {
            listener.onData(userCache.get(uid));
            return;
        }

        authRequest("users/" + uid, Request.Method.GET, null,
            json -> {
                try {
                    UserData user = new UserData(json);
                    userCache.put(uid, user);

                    listener.onData(user);
                } catch (JSONException ignored) {

                }
            },
            err -> {}
        );
    }

    public void sendMessage(
            String chatId,
            String content,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onError
    ) {
        String url = "chats/" + chatId + "/messages";
        JSONObject body = new JSONObject();

        try {
            body.put("content", content);
        } catch (JSONException ignored) {

        };

        authRequest(url, Request.Method.POST, body, onSuccess, onError);
    }

    FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
