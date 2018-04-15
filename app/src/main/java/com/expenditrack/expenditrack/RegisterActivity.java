
package com.expenditrack.expenditrack;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText usernameIn, pWordIn, secAnswer;
    Spinner secQuestion;

    ProgressBar loading;

    String username, pWord, secAns, secQuest;

    User newUser;

    Intent login_intent;

    int positionOfSpinner;

    String found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);

        try {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        } catch (Exception e) {

        }
        register = findViewById(R.id.actualRegisterButton);
        usernameIn = findViewById(R.id.username_input_register);
        pWordIn = findViewById(R.id.passwd_input_register);
        loading = findViewById(R.id.loading_reg);
        secAnswer = findViewById(R.id.secAnswer);
        secQuestion = findViewById(R.id.securityQuestionSelect);
        login_intent = new Intent(this, LoginActivity.class);

        loading.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seQuestionArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        secQuestion.setAdapter(adapter);
        secQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    secQuest = secQuestion.getSelectedItem().toString();
                    positionOfSpinner = position;
                    secAnswer.setFocusable(true);
                    secAnswer.requestFocus();
                } else {
                    positionOfSpinner = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(positionOfSpinner);
                Utils.hideSoftKeyboard(getCurrentFocus(), getSystemService(INPUT_METHOD_SERVICE));
                if (found.equals("imhere")) {
                    generateToast(getString(R.string.already_reg));
                    loading.setVisibility(View.GONE);
                    found = "nothere";
                } else {

                    loading.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                newUser = new User(username, pWord, secQuest, secAns);
                                Utils.writeUser(newUser);
                                generateToast(getString(R.string.user_add_success));
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Utils.loadUserInfo();
                                loading.setVisibility(View.GONE);
                            } catch (Exception e) {
                                generateToast(getString(R.string.unable_add_user));
                                loading.setVisibility(View.GONE);
                            }
                        }

                    }, 2000);
                }
            }
        });

    }

    public void generateToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void register(int positionOfSpinner) {

        try {
            if (usernameIn.getText().toString().matches("") || pWordIn.getText().toString().matches("") || positionOfSpinner == 0 || secAnswer.getText().toString().matches("")) {
                generateToast(getString(R.string.all_fields));
            } else {
                username = usernameIn.getText().toString();
                pWord = pWordIn.getText().toString();
                secAns = secAnswer.getText().toString();
            }

        } catch (Exception e) {
            generateToast(getString(R.string.error_inputs));
        }

        try {
            if (Utils.usernames.contains(username)) {
                found = "imhere";
            } else if (!Utils.usernames.contains(username)) {
                found = "nothere";
            }


        } catch (Exception e) {
        }

    }
}
