package com.expenditrack.expenditrack;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    Button login, register, forgotPword;
    EditText usernameIn, pWordIn;

    Intent register_intent;
    Intent main_intent;
    Intent forgotP_intent;

    static String username;
    String password;

    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        Toolbar toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);

        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);
        forgotPword = findViewById(R.id.forgotPword);
        usernameIn = findViewById(R.id.username_input);
        pWordIn = findViewById(R.id.passwd_input);
        loading = findViewById(R.id.loading_login);
        loading.setVisibility(View.GONE);
        register_intent = new Intent(this, RegisterActivity.class);
        main_intent = new Intent(this, Main.class);
        forgotP_intent = new Intent(this, ForgotPasswordActivity.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    username = usernameIn.getText().toString();
                    password = pWordIn.getText().toString();
                    login(username, password);
                } catch (Exception e) {
                    generateToast(getString(R.string.unable_to_load));
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(register_intent);
            }
        });

        forgotPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(forgotP_intent);
            }
        });
    }

    public void generateToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void login(String username, String password) {

        boolean found = false;

        for (int i = 0; i < Utils.usernames.size(); i++) {
            if (Utils.usernames.get(i).equals(username) && Utils.pWords.get(i).equals(password)) {
                found = true;
            }
        }

        if (found) {
            Utils.hideSoftKeyboard(getCurrentFocus(), getSystemService(INPUT_METHOD_SERVICE));
            loading.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    loading.setVisibility(View.GONE);
                    generateToast(getString(R.string.success_login));
                    startActivity(main_intent);
                }
            }, 2000);
        } else {
            generateToast("Wrong information");
        }

    }

}
