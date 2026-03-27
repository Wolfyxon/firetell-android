package com.wolfyxon.firetell.android.lib;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpApi {
    public static String URL = "https://firetell.vercel.app/api/v1/";

    public static void authRequest(
            RequestQueue queue,
            FirebaseAuth auth,
            String endpoint,
            int method,
            JSONObject body,
            Response.Listener<JSONObject> successListener,
            Response.ErrorListener errorListener
        )
    {
        assert auth.getCurrentUser() != null;
        String token = auth.getCurrentUser().getIdToken(false).getResult().getToken();

        JsonObjectRequest req = new JsonObjectRequest(method, URL + endpoint, body, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);

                return headers;
            }
        };

        queue.add(req);
    }
}
