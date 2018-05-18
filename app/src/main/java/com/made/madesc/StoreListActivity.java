package com.made.madesc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.made.madesc.MainActivity.USER_ID;

public class StoreListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String userEmail = "";
        try {
            userEmail = currentUser.getEmail();
        }
        catch (NullPointerException e) {
            // User is logged out
            goToLoginScreen();
        }
        TextView textViewUserId = findViewById(R.id.tv_user_id);
        textViewUserId.setText(getResources().getString(R.string.hello_text, userEmail));

    }

    private void goToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
