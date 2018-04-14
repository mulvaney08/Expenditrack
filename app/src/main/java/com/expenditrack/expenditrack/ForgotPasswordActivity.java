package com.expenditrack.expenditrack;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button forgotPword, retrieveQuestion;
    EditText usernameIn, secAnswer;
    TextView passOut, question;
    Intent login_intent;

    String user, secAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        retrieveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    user = usernameIn.getText().toString();
                    retrieval(Utils.usernames, Utils.secQs, user, question);
                } catch (Exception e) {
                    generateToast(getString(R.string.question_retrieve));
                }

            }
        });

        forgotPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    secAns = secAnswer.getText().toString();
                    retrieval(Utils.secAnswers
                            , Utils.pWords, secAns, passOut);
                } catch (Exception e) {
                    generateToast(getString(R.string.unable_to_load));
                }
            }
        });


    }

    public void generateToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void retrieval(ArrayList<String> array1, ArrayList<String> array2, String var1, TextView var2) {
        boolean found = false;

        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i).equals(var1) && Utils.usernames.get(i).equals(user)) {
                var2.setText(array2.get(i));
                found = true;
            }
        }

        if (found) {
            generateToast("Successful Retrieval");
        } else {
            generateToast("Wrong information");
        }
    }
}
