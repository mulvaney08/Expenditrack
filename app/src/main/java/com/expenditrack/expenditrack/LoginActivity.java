package com.expenditrack.expenditrack;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button login, register, forgotPword;
    EditText usernameIn, pWordIn;

    Intent register_intent;
    Intent main_intent;
    Intent forgotP_intent;

    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);

        try {
            Utils.setUserReference();
        } catch (Exception e) {
            generateToast(getString(R.string.access_down));
        }


        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);
        forgotPword = findViewById(R.id.forgotPword);
        usernameIn = findViewById(R.id.username_input);
        pWordIn = findViewById(R.id.passwd_input);
        register_intent = new Intent(this, RegisterActivity.class);
        main_intent = new Intent(this, Main.class);
        forgotP_intent = new Intent(this, ForgotPasswordActivity.class);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login();
                } catch (Exception e) {
                    generateToast(getString(R.string.unable_to_load));
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
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


    public void login() {

        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.usersRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    try {
                        if (ds.child("username").getValue().toString().matches(usernameIn.getText().toString()) && ds.child("passwd").getValue().toString().matches(pWordIn.getText().toString())) {
                            i = 1;

                        } else{
                            i = 0;
                        }
                    } catch (Exception e) {
                        generateToast("Cannot access database");
                    }

                }
                if(i == 1){
                    generateToast(getString(R.string.success_login));
                    Utils.initialiseFBase(usernameIn.getText().toString());
                    startActivity(main_intent);
                }else if(i == 0)
                {
                    generateToast("Login details incorrect");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
