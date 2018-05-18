package com.made.madesc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void onLoginButtonPress() {
        Context context = MainActivity.this;
        String textToShow = "Login Clicked!";
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
    }
}
