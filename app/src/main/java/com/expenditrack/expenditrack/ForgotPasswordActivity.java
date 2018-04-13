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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button forgotPword, retrieveQuestion;
    EditText usernameIn, secAnswer;
    TextView passOut, question;
    Intent login_intent;

    String quest, pass;

    int i, j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        forgotPword = findViewById(R.id.retrievePassButton);
        retrieveQuestion = findViewById(R.id.retrieveQuestionButton);
        usernameIn = findViewById(R.id.username_input_forgot);
        passOut = findViewById(R.id.passwordRetrive);
        question = findViewById(R.id.secQuestion);
        secAnswer = findViewById(R.id.secAnswer);
        login_intent = new Intent(this, LoginActivity.class);

        forgotPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    retrievePassword();
                } catch (Exception e) {
                    generateToast(getString(R.string.unable_to_load));
                }
            }
        });

        retrieveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    retrieveQ();
                } catch (Exception e) {
                    generateToast(getString(R.string.question_retrieve));
                }

            }
        });
    }

    public void generateToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void retrieveQ() {
        Utils.usersRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    try {
                        if (ds.child("username").getValue().toString().matches(usernameIn.getText().toString())) {
                            i = 1;
                            quest = ds.child("secQuestion").getValue().toString();
                        } else {
                            i = 0;
                        }
                    } catch (Exception e) {
                    }

                }

                if (i == 1) {
                    generateToast("Security Question retrieved");
                    question.setText(quest);
                } else if (i == 0) {
                    generateToast("No user found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void retrievePassword() {

        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.usersRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    try {
                        if (datasnap.child("secAnswer").getValue().toString().matches(secAnswer.getText().toString())) {
                            j = 1;
                            pass = datasnap.child("passwd").getValue().toString();
                        } else {
                            j = 0;
                        }
                    } catch (Exception e) {
                    }

                }

                if (j == 1) {
                    generateToast("Password retrieved");
                    passOut.setText(pass);
//                    startActivity(login_intent);
                } else if (j == 0) {
                    generateToast("Details incorrect");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
