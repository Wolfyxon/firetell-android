package com.wolfyxon.firetell.android;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wolfyxon.firetell.android.lib.HttpApi;
import com.wolfyxon.firetell.android.lib.Util;

public class MainActivity extends AppCompatActivity {

    void goToLogin() {
        Util.changeActivity(this, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        HttpApi api = new HttpApi(Volley.newRequestQueue(this));

        if(user == null) {
            goToLogin();
            return;
        }

        api.authRequest("auth", Request.Method.GET,null,
                json -> {
                    Util.changeActivity(this, ChatActivity.class);
                },
                err -> {
                    if(err.networkResponse == null) {
                        Util.showAlert(this, "Unable to connect. Please check your internet connection.");
                        return;
                    }

                    int code = err.networkResponse.statusCode;

                    if(code == 500) {
                        Util.showAlert(this, "Server has a skill issue (code 500)");
                        return;
                    }

                    if(code == 401) {
                        Util.showToast(this, "Your session expired, please log back in");
                        goToLogin();
                        return;
                    }

                    Util.showAlert(this, "Unknown error. Response: " + code);
                }
        );


    }
}