package com.made.madesc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check firebase status
        mAuth = FirebaseAuth.getInstance();

        // Sets login button listener
        Button loginButton = (Button) findViewById(R.id.bt_login);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                String textToShow = "Login Clicked!";
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void onLoginButtonPress() {
        Context context = MainActivity.this;
        String textToShow = "Login Clicked!";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
    }
}
