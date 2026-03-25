package com.wolfyxon.firetell.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wolfyxon.firetell.android.lib.Util;

public class LoginActivity extends AppCompatActivity {
    TextView errorLbl;
    EditText emailInput;
    EditText passwordInput;
    Button loginBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        errorLbl = findViewById(R.id.error);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(l -> {
            logIn();
        });
    }

    void logIn() {
        errorLbl.setText("");
        loginBtn.setEnabled(false);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this::processLoginResult);
    }

    void processLoginResult(Task<AuthResult> result) {
        if(result.isSuccessful()) {
            Util.changeActivity(this, ChatActivity.class);
        } else {
            Exception ex = result.getException();

            if(ex == null) {
                return;
            }

            String err;
            String localizedErr = ex.getLocalizedMessage();
            String normalErr = ex.getMessage();

            if(localizedErr != null) {
                err = localizedErr;
            } else {
                err = normalErr;
            }

            errorLbl.setText(err);
        }
    }
}
