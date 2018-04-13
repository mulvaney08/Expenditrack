package com.expenditrack.expenditrack;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText usernameIn, pWordIn, secAnswer;
    Spinner secQuestion;

    String username, pWord, secAns, secQuest;

    User newUser;

    int positionOfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


        register = findViewById(R.id.actualRegisterButton);
        usernameIn = findViewById(R.id.username_input_register);
        pWordIn = findViewById(R.id.passwd_input_register);
        secAnswer = findViewById(R.id.secAnswer);
        secQuestion = findViewById(R.id.securityQuestionSelect);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seQuestionArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        secQuestion.setAdapter(adapter);
        secQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position > 0){
                    secQuest = secQuestion.getSelectedItem().toString();
                    positionOfSpinner = position;
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
            }
        });

    }

    public void generateToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }

    public void register(int positionOfSpinner) {
        try {
            if(usernameIn.getText().toString().matches("") || pWordIn.getText().toString().matches("")|| positionOfSpinner == 0){
                generateToast("Please enter valid information");
            }
            else {
                username = usernameIn.getText().toString();
                pWord = pWordIn.getText().toString();
                secAns = secAnswer.getText().toString();
            }
        }catch (Exception e){
            generateToast(getString(R.string.error_inputs));
        }


        newUser = new User(username,pWord,secQuest,secAns);

        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    try {
                        if (ds.child("username").getValue().toString().equalsIgnoreCase(usernameIn.getText().toString())) {
                            generateToast(getString(R.string.already_reg));
                        }
                        else {
                            Utils.writeUser(newUser);
                            generateToast(getString(R.string.user_add_success));
                        }
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
