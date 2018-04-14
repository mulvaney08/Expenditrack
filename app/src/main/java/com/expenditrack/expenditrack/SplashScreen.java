package com.expenditrack.expenditrack;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
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

public class SplashScreen extends AppCompatActivity {

    ImageView image;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ProgressBar loading;

    final int extendRun = 500;

    Handler handler = new Handler();

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        intent = new Intent(SplashScreen.this, LoginActivity.class);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            signInAnonymously();

        }

        Utils.loadUserInfo();

        loading = findViewById(R.id.loading_reg);

        image = findViewById(R.id.loadImage);

        loading.setVisibility(View.VISIBLE);
        runInBG(extendRun);
    }


    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("TAG", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    public void runInBG(final int extendRun) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Utils.usernames.size() > 0) {
                    loading.setVisibility(View.GONE);
                    startActivity(intent);
                } else {
                    runInBG(extendRun);
                }

            }
        }, 500);
    }
}
